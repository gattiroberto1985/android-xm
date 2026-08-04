# ExpenseManager - Architecture & Development Skill

**Project**: `it.gr85.android.apps.em`  
**Architecture**: Hexagonal/Ports-and-Adapters (Enterprise-Grade, 6-layer model)  
**Language**: Kotlin  
**Target**: Android minSdk 30, compileSdk 35  
**UI Framework**: Jetpack Compose  
**State Management**: StateFlow (ViewModel layer only)  
**Persistence**: Room + SQLite  
**DI Pattern**: Manual wiring via `ViewModelProvider.Factory` (no Hilt)

---

## Table of Contents

1. [Layered Architecture](#layered-architecture)
2. [Domain Layer](#domain-layer)
3. [Ports (Use Cases & Interfaces)](#ports-use-cases--interfaces)
4. [Adapter Layer](#adapter-layer)
5. [ViewModel Layer](#viewmodel-layer)
6. [Compose UI Layer](#compose-ui-layer)
7. [Key Patterns & Principles](#key-patterns--principles)
8. [Anti-Patterns to Avoid](#anti-patterns-to-avoid)
9. [Testing Strategy](#testing-strategy)
10. [File Organization & Naming](#file-organization--naming)

---

## Layered Architecture

The application follows a **six-layer hexagonal architecture**:

```
┌─────────────────────────────────────┐
│        Compose UI Layer             │  ← User-facing UI (stateless composables)
├─────────────────────────────────────┤
│        ViewModel Layer              │  ← State management (MutableStateFlow/StateFlow)
├─────────────────────────────────────┤
│        Application/Use Cases        │  ← Orchestration (pure business logic)
├─────────────────────────────────────┤
│        Domain Layer                 │  ← Entities, Value Objects, Domain Rules
├─────────────────────────────────────┤
│        Ports (Interfaces)           │  ← Data access contracts
├─────────────────────────────────────┤
│        Adapter Layer                │  ← Room DAO, Mappers, Repository Impls
└─────────────────────────────────────┘
```

Each layer has **clear responsibilities** and **unidirectional dependencies** (inner layers don't know about outer layers).

---

## Domain Layer

### Purpose
Represents **pure business logic** independent of any framework or platform. Domain objects are immutable value objects and entities that express real-world concepts.

### Key Types

#### **Value Objects**
All IDs are **Kotlin type aliases to UUID**:
```kotlin
package it.gr85.android.apps.em.domain.model

typealias CategoryId = String
typealias MovimentoId = String

// Money represented in cents, not Double
typealias MoneyAmount = Long  // 1500 = €15.00
```

#### **Entities**

**Category**
```kotlin
data class Category(
    val id: CategoryId,
    val name: String,
    val color: String  // Hex color code (#RRGGBB)
)
```

**Movimento (Transaction)**
```kotlin
data class Movimento(
    val id: MovimentoId,
    val amount: MoneyAmount,  // cents
    val type: MovimentoType,  // EXPENSE, INCOME, TRANSFER
    val category: Category,
    val description: String,
    val date: LocalDate  // java.time.LocalDate
)

enum class MovimentoType {
    EXPENSE,
    INCOME,
    TRANSFER
}
```

#### **Domain Aggregates**
Real business concepts, not presentation models:

**CategoryExpenseBreakdown** - Represents spending by category
```kotlin
data class CategoryExpenseBreakdown(
    val category: Category,
    val totalAmount: MoneyAmount,
    val percentageOfTotal: Double,
    val movementCount: Int
)
```

**BalanceSummary** - High-level account snapshot
```kotlin
data class BalanceSummary(
    val totalIncome: MoneyAmount,
    val totalExpense: MoneyAmount,
    val netBalance: MoneyAmount
)
```

### Domain Rules
- **No nullability for core fields** – category is always present
- **Immutable**: All domain objects are `data class` or `value class`
- **No framework dependencies** – no Room annotations, no Android imports
- **Type safety over primitives** – Use type aliases for semantic clarity

---

## Ports (Use Cases & Interfaces)

### Purpose
Define **contracts** for data access and business orchestration. Ports live in the domain layer logically but are implemented in the adapter layer.

### Use Case Signature Pattern

All use cases follow this signature:
```kotlin
suspend fun operationName(params: ParamObject): Result<ReturnType>
// or
suspend fun operationName(params: ParamObject): DomainType
```

**Never return `Flow` from domain ports** – reactive composition happens at the ViewModel layer.

### Example Ports

#### **Read Ports**
```kotlin
package it.gr85.android.apps.em.domain.port

interface TransactionRepository {
    suspend fun getMovimenti(query: TransactionSearchQuery): Result<List<Movimento>>
    suspend fun getMovimentoById(id: MovimentoId): Result<Movimento>
    suspend fun getByIds(ids: List<MovimentoId>): Result<List<Movimento>>
}

interface CategoryRepository {
    suspend fun getAllCategories(): Result<List<Category>>
    suspend fun getCategoryById(id: CategoryId): Result<Category>
}
```

#### **Aggregation Ports** (belong in adapter layer, invoked by use cases)
```kotlin
// Repository aggregation queries (implemented in DAO)
suspend fun getExpenseBreakdownByCategory(
    dateStart: LocalDate,
    dateEnd: LocalDate
): Result<List<CategoryExpenseBreakdown>>

suspend fun getBalanceSummary(
    dateStart: LocalDate? = null,
    dateEnd: LocalDate? = null
): Result<BalanceSummary>
```

#### **Mutation Ports**
```kotlin
interface TransactionRepository {
    suspend fun createMovimento(movimento: Movimento): Result<Movimento>
    suspend fun updateMovimento(movimento: Movimento): Result<Unit>
    suspend fun deleteMovimento(id: MovimentoId): Result<Unit>
}
```

### Use Case Classes (Application Layer)

Use cases orchestrate repositories and business logic:

```kotlin
package it.gr85.android.apps.em.application.usecase

class GetCategoryBreakdown(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(
        dateStart: LocalDate,
        dateEnd: LocalDate
    ): Result<List<CategoryExpenseBreakdown>> =
        transactionRepository.getExpenseBreakdownByCategory(dateStart, dateEnd)
}

class GetBalanceSummary(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(
        dateStart: LocalDate? = null,
        dateEnd: LocalDate? = null
    ): Result<BalanceSummary> =
        transactionRepository.getBalanceSummary(dateStart, dateEnd)
}
```

---

## Adapter Layer

### Purpose
Implements ports: database access (Room), data mapping, and repository logic.

### Responsibility Breakdown

#### **1. Room DAO Layer**
- **Direct SQL queries via `@Query`**
- **No N+1 queries** – bulk reads and aggregation at query level
- **Nullable query parameters for bounded filters** (YAGNI over Query Builder)

```kotlin
package it.gr85.android.apps.em.adapter.persistence.dao

@Dao
interface TransactionDao {
    @Query("SELECT * FROM movimenti WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): TransactionEntity?

    @Query("""
        SELECT * FROM movimenti
        WHERE (:categoryId IS NULL OR category_id = :categoryId)
          AND (:typeFilter IS NULL OR type = :typeFilter)
          AND (:dateStart IS NULL OR date >= :dateStart)
          AND (:dateEnd IS NULL OR date <= :dateEnd)
        ORDER BY date DESC
    """)
    suspend fun searchMovimenti(
        categoryId: String?,
        typeFilter: String?,
        dateStart: LocalDate?,
        dateEnd: LocalDate?
    ): List<TransactionEntity>

    @Query("SELECT id FROM movimenti WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<TransactionEntity>

    @Query("""
        SELECT 
            c.id, c.name, c.color,
            SUM(m.amount) as total_amount,
            COUNT(m.id) as movement_count
        FROM movimenti m
        JOIN categories c ON m.category_id = c.id
        WHERE m.type = 'EXPENSE'
          AND (:dateStart IS NULL OR m.date >= :dateStart)
          AND (:dateEnd IS NULL OR m.date <= :dateEnd)
        GROUP BY c.id
        ORDER BY total_amount DESC
    """)
    suspend fun getExpenseBreakdownByCategory(
        dateStart: LocalDate?,
        dateEnd: LocalDate?
    ): List<CategoryExpenseBreakdownEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovimento(entity: TransactionEntity)

    @Delete
    suspend fun deleteMovimento(entity: TransactionEntity)
}
```

#### **2. Entity Mapping**

Room entities live in the adapter layer. Map to/from domain cleanly using **extension functions**:

```kotlin
package it.gr85.android.apps.em.adapter.persistence.entity

@Entity(
    tableName = "movimenti",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey val id: String,
    val amount: Long,
    val type: String,
    @ColumnInfo(name = "category_id") val categoryId: String,
    val description: String,
    val date: LocalDate
)

// Mapper extension functions (in adapter.persistence.mapper package)
suspend fun TransactionEntity.toDomain(categoryRepository: CategoryRepository): Movimento {
    val category = categoryRepository.getCategoryById(categoryId)
        .getOrThrow()
    return Movimento(
        id = id,
        amount = amount,
        type = MovimentoType.valueOf(type),
        category = category,
        description = description,
        date = date
    )
}

fun Movimento.toEntity(): TransactionEntity =
    TransactionEntity(
        id = id,
        amount = amount,
        type = type.name,
        categoryId = category.id,
        description = description,
        date = date
    )
```

#### **3. Repository Implementation**

Repository is an **orchestrator**, not a logic dumping ground:

```kotlin
package it.gr85.android.apps.em.adapter.persistence.repository

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val categoryRepository: CategoryRepository
) : TransactionRepository {

    override suspend fun getMovimenti(query: TransactionSearchQuery): Result<List<Movimento>> =
        runCatching {
            val entities = transactionDao.searchMovimenti(
                categoryId = query.categoryId,
                typeFilter = query.type?.name,
                dateStart = query.dateStart,
                dateEnd = query.dateEnd
            )
            entities.map { it.toDomain(categoryRepository) }
        }

    override suspend fun getMovimentoById(id: MovimentoId): Result<Movimento> =
        runCatching {
            transactionDao.getById(id)
                ?.toDomain(categoryRepository)
                ?: throw IllegalArgumentException("Movimento not found: $id")
        }

    override suspend fun getByIds(ids: List<MovimentoId>): Result<List<Movimento>> =
        runCatching {
            val entities = transactionDao.getByIds(ids)
            entities.map { it.toDomain(categoryRepository) }
        }

    override suspend fun getExpenseBreakdownByCategory(
        dateStart: LocalDate,
        dateEnd: LocalDate
    ): Result<List<CategoryExpenseBreakdown>> =
        runCatching {
            transactionDao.getExpenseBreakdownByCategory(dateStart, dateEnd)
                .map { it.toDomain() }
        }

    override suspend fun createMovimento(movimento: Movimento): Result<Movimento> =
        runCatching {
            transactionDao.insertMovimento(movimento.toEntity())
            movimento
        }
}
```

### Key Adapter Principles

1. **No N+1 queries** – Use `getByIds()` + `associateBy()` for bulk operations
2. **Aggregation in SQL, not in ViewModel** – `GROUP BY`, `SUM`, `COUNT` belong in DAO
3. **Repository is orchestration** – Handles mapping and coordination, not business logic
4. **Mappers are extensions** – `toEntity()` as extension functions, scoped to mapper files
5. **YAGNI over abstraction** – Nullable query parameters for bounded, stable filters

---

## ViewModel Layer

### Purpose
Manage UI state reactively. **Only layer that exposes `Flow`/`StateFlow`**.

### State Design Pattern

**Single source of truth**: One `MutableStateFlow<UiState>` backing a public `StateFlow<UiState>`.

```kotlin
package it.gr85.android.apps.em.presentation.viewmodel

data class HomeUiState(
    val isLoading: Boolean = false,
    val categoryBreakdown: List<CategoryExpenseBreakdown> = emptyList(),
    val balanceSummary: BalanceSummary? = null,
    val selectedDateRange: DateRange = DateRange.currentMonth(),
    val snackbarMessage: String? = null  // Nullable for snackbar display
)

class HomeViewModel(
    private val getCategoryBreakdown: GetCategoryBreakdown,
    private val getBalanceSummary: GetBalanceSummary
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Invalidation signal: emit Unit to trigger fresh data load
    private val _refreshSignal = MutableSharedFlow<Unit>()

    init {
        viewModelScope.launch {
            _refreshSignal
                .onStart { emit(Unit) }  // Load on init
                .flatMapLatest {
                    val state = _uiState.value
                    loadData(state.selectedDateRange)
                }
                .collect()
        }
    }

    private suspend fun loadData(dateRange: DateRange): Flow<Unit> = flow {
        _uiState.update { it.copy(isLoading = true) }
        
        getCategoryBreakdown(dateRange.start, dateRange.end)
            .onSuccess { breakdown ->
                _uiState.update { it.copy(categoryBreakdown = breakdown) }
            }
            .onFailure { error ->
                _uiState.update { it.copy(snackbarMessage = error.message) }
            }

        getBalanceSummary(dateRange.start, dateRange.end)
            .onSuccess { summary ->
                _uiState.update { it.copy(balanceSummary = summary, isLoading = false) }
            }
            .onFailure { error ->
                _uiState.update { it.copy(snackbarMessage = error.message, isLoading = false) }
            }
        
        emit(Unit)
    }

    fun onDateRangeChanged(dateRange: DateRange) {
        _uiState.update { it.copy(selectedDateRange = dateRange) }
        viewModelScope.launch {
            _refreshSignal.emit(Unit)
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
```

### ViewModel Principles

1. **One `UiState` data class per screen** – Immutable, contains all state needed to render
2. **Private `MutableStateFlow`, public `StateFlow`** – Prevents accidental external mutations
3. **`flatMapLatest` for reactive reloads** – Handles invalidation cleanly
4. **`MutableSharedFlow<Unit>` for invalidation** – Emitted when data should refresh
5. **`onSuccess`/`onFailure` chaining** – Use Result extension functions, not try/catch
6. **Nullable fields for optional UI states** – e.g., `snackbarMessage: String?` for transient messages

---

## Compose UI Layer

### Purpose
Display state reactively. **Composables are stateless** (except root screen composable which receives full `UiState`).

### Composition Pattern

#### **Root Screen Composable** (receives full `UiState`)
```kotlin
package it.gr85.android.apps.em.presentation.screen

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle snackbar display
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Decompose state for children
            DateRangeControl(
                selectedRange = uiState.selectedDateRange,
                onRangeChanged = viewModel::onDateRangeChanged
            )

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                CategoryBreakdownSection(
                    breakdown = uiState.categoryBreakdown,
                    modifier = Modifier.fillMaxWidth()
                )
                BalanceSummaryCard(
                    summary = uiState.balanceSummary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
```

#### **Child Composables** (receive decomposed primitives or domain objects)
```kotlin
@Composable
fun CategoryBreakdownSection(
    breakdown: List<CategoryExpenseBreakdown>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(breakdown) { item ->
            CategoryExpenseRow(
                category = item.category,
                amount = item.totalAmount,
                percentage = item.percentageOfTotal
            )
        }
    }
}

@Composable
fun CategoryExpenseRow(
    category: Category,
    amount: MoneyAmount,
    percentage: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(category.name)
        Text("${(amount / 100.0)}€ (${(percentage * 100).toInt()}%)")
    }
}
```

#### **Stateless Date Range Control**
```kotlin
@Composable
fun DateRangeControl(
    selectedRange: DateRange,
    onRangeChanged: (DateRange) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DatePickerButton(
            label = "From",
            selectedDate = selectedRange.start,
            onDateSelected = { newStart ->
                onRangeChanged(selectedRange.copy(start = newStart))
            }
        )

        DatePickerButton(
            label = "To",
            selectedDate = selectedRange.end,
            onDateSelected = { newEnd ->
                onRangeChanged(selectedRange.copy(end = newEnd))
            }
        )
    }
}

@Composable
fun DatePickerButton(
    label: String,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Button(
        onClick = { showDialog = true },
        modifier = modifier
    ) {
        Text(label + ": " + selectedDate.format(DateTimeFormatter.ISO_DATE))
    }

    if (showDialog) {
        DatePickerDialog(
            selectedDate = selectedDate,
            onDateSelected = {
                onDateSelected(it)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}
```

### Compose Principles

1. **Root composable receives full `UiState`** – Single source of state binding
2. **Child composables receive decomposed primitives or domain objects** – Not full `UiState`
3. **Snackbar centralized at root** – Avoid scattered error handling
4. **Local state only for UI chrome** (e.g., dialog visibility) – Never for business state
5. **Domain objects acceptable to pass down** – `Category`, `CategoryExpenseBreakdown` are semantic and stable

---

## Key Patterns & Principles

### 1. **Hexagonal Architecture Benefits**

- **Testability**: Mock repositories, swap implementations
- **Independence**: Domain doesn't depend on frameworks
- **Scalability**: Add new adapters (REST API, WebSocket) without touching domain
- **Clarity**: Each layer has one reason to change

### 2. **Type Safety Over Primitives**

```kotlin
// ❌ Confusing
fun getTransactions(categoryId: String, userId: String): List<Transaction>

// ✅ Clear
typealias CategoryId = String
typealias UserId = String

fun getTransactions(categoryId: CategoryId, userId: UserId): List<Transaction>
```

### 3. **N+1 Prevention**

```kotlin
// ❌ N+1
val ids = listOf("1", "2", "3")
ids.map { repository.getById(it) }  // 3 queries!

// ✅ Correct
val ids = listOf("1", "2", "3")
repository.getByIds(ids)  // 1 query
```

### 4. **Aggregation at Query Level**

```kotlin
// ❌ Wrong layer
val all = dao.getAllMovimenti()
val breakdown = all.groupBy { it.category }
    .mapValues { it.value.sumOf { m -> m.amount } }

// ✅ Correct
val breakdown = dao.getExpenseBreakdownByCategory(startDate, endDate)
```

### 5. **State Invalidation Pattern**

```kotlin
private val _refreshSignal = MutableSharedFlow<Unit>()

init {
    viewModelScope.launch {
        _refreshSignal
            .onStart { emit(Unit) }  // Initial load
            .flatMapLatest { loadData() }
            .collect()
    }
}

// Anywhere you need to refresh:
fun refresh() {
    viewModelScope.launch {
        _refreshSignal.emit(Unit)
    }
}
```

### 6. **Result Handling**

```kotlin
// Always use Result<T> from domain; map/chain with extensions
useCase.invoke(params)
    .onSuccess { data -> updateState(data) }
    .onFailure { error -> updateError(error) }
    .getOrNull()  // Returns T? safely
```

---

## Anti-Patterns to Avoid

### ❌ **Leaking `Flow` from Domain**
```kotlin
// WRONG - Domain shouldn't expose Flow
interface TransactionRepository {
    fun getMovimenti(): Flow<List<Movimento>>
}
```
**Correct**: Use `suspend fun` in domain, introduce `Flow` at ViewModel.

### ❌ **Business Logic in Composables**
```kotlin
// WRONG
@Composable
fun CategoryList() {
    val categories = remember { mutableStateOf(emptyList<Category>()) }
    LaunchedEffect(Unit) {
        categories.value = fetchCategories()  // Business logic!
    }
}
```
**Correct**: Fetch in ViewModel, pass data to stateless composable.

### ❌ **N+1 Queries**
```kotlin
// WRONG
val transactions = getAllTransactions()
val enriched = transactions.map { t ->
    t.copy(category = getCategoryById(t.categoryId))  // N queries!
}
```
**Correct**: Use `getByIds()` + `associateBy()` or SQL JOIN.

### ❌ **Aggregation in Application Layer**
```kotlin
// WRONG
val all = repository.getAllTransactions()
val total = all.filter { it.type == EXPENSE }.sumOf { it.amount }
```
**Correct**: Implement aggregation at DAO level with SQL `GROUP BY`.

### ❌ **Passing Entire `UiState` to Children**
```kotlin
// WRONG
@Composable
fun CategoryRow(uiState: HomeUiState) {  // Only needs category!
    Text(uiState.categoryBreakdown[0].category.name)
}
```
**Correct**: Pass decomposed value:
```kotlin
@Composable
fun CategoryRow(category: Category) {
    Text(category.name)
}
```

### ❌ **Generic Repository Interfaces**
```kotlin
// WRONG - Over-engineered
interface CrudRepository<T, ID> {
    suspend fun create(entity: T): Result<T>
    suspend fun read(id: ID): Result<T>
    suspend fun update(entity: T): Result<T>
    suspend fun delete(id: ID): Result<Unit>
}
```
**Correct**: Domain-driven interfaces specific to your use cases.

---

## Testing Strategy

### **Unit Tests: Use Case & Repository**

Use **fixture-based fakes** instead of mocks:

```kotlin
class FakeTransactionRepository : TransactionRepository {
    private val transactions = mutableMapOf<MovimentoId, Movimento>()

    override suspend fun getMovimenti(query: TransactionSearchQuery): Result<List<Movimento>> =
        Result.success(transactions.values.toList())

    suspend fun withMovimento(movimento: Movimento) = apply {
        transactions[movimento.id] = movimento
    }

    override suspend fun createMovimento(movimento: Movimento): Result<Movimento> {
        transactions[movimento.id] = movimento
        return Result.success(movimento)
    }
}

// Test
@Test
fun testGetCategoryBreakdown() = runTest {
    val repo = FakeTransactionRepository()
        .withMovimento(
            Movimento(
                id = "1",
                amount = 1500,
                type = EXPENSE,
                category = Category("cat1", "Groceries", "#FF0000"),
                description = "Weekly shopping",
                date = LocalDate.now()
            )
        )

    val useCase = GetCategoryBreakdown(repo)
    val result = useCase(LocalDate.now(), LocalDate.now())

    assertTrue(result.isSuccess)
    assertEquals(1, result.getOrNull()?.size)
}
```

### **ViewModel Tests**

Test state transitions:

```kotlin
@Test
fun testLoadDataSuccess() = runTest {
    val breakdown = listOf(
        CategoryExpenseBreakdown(
            Category("1", "Food", "#FF0000"),
            5000,
            0.5,
            3
        )
    )
    val mockUseCase = mockk<GetCategoryBreakdown>()
    coEvery { mockUseCase.invoke(any(), any()) } returns Result.success(breakdown)

    val viewModel = HomeViewModel(mockUseCase, mockk())
    val state = viewModel.uiState.value

    assertEquals(breakdown, state.categoryBreakdown)
    assertFalse(state.isLoading)
}
```

### **Compose Tests**

Test composable rendering and interactions:

```kotlin
@Test
fun testDatePickerButtonDisplay() {
    composeTestRule.setContent {
        DatePickerButton(
            label = "From",
            selectedDate = LocalDate.of(2024, 12, 15),
            onDateSelected = {}
        )
    }

    composeTestRule
        .onNodeWithText("From: 2024-12-15")
        .assertExists()
}
```

---

## File Organization & Naming

### Directory Structure

```
it/gr85/android/apps/em/
├── domain/
│   ├── model/
│   │   ├── Category.kt
│   │   ├── Movimento.kt
│   │   ├── CategoryExpenseBreakdown.kt
│   │   └── BalanceSummary.kt
│   ├── port/
│   │   ├── TransactionRepository.kt
│   │   ├── CategoryRepository.kt
│   │   └── TransactionSearchQuery.kt
│   └── exception/
│       └── DomainException.kt
│
├── application/
│   └── usecase/
│       ├── GetCategoryBreakdown.kt
│       ├── GetBalanceSummary.kt
│       ├── CreateMovimento.kt
│       └── DeleteMovimento.kt
│
├── adapter/
│   ├── persistence/
│   │   ├── database/
│   │   │   └── AppDatabase.kt
│   │   ├── dao/
│   │   │   ├── TransactionDao.kt
│   │   │   └── CategoryDao.kt
│   │   ├── entity/
│   │   │   ├── TransactionEntity.kt
│   │   │   └── CategoryEntity.kt
│   │   ├── mapper/
│   │   │   ├── TransactionMapper.kt
│   │   │   └── CategoryMapper.kt
│   │   └── repository/
│   │       ├── TransactionRepositoryImpl.kt
│   │       └── CategoryRepositoryImpl.kt
│   └── navigation/
│       └── NavigationGraph.kt
│
├── presentation/
│   ├── viewmodel/
│   │   ├── HomeViewModel.kt
│   │   ├── TransactionDetailViewModel.kt
│   │   └── ViewModelFactory.kt
│   ├── screen/
│   │   ├── HomeScreen.kt
│   │   ├── TransactionDetailScreen.kt
│   │   └── AddTransactionScreen.kt
│   ├── composable/
│   │   ├── DateRangeControl.kt
│   │   ├── DatePickerButton.kt
│   │   ├── CategoryExpenseRow.kt
│   │   ├── BalanceSummaryCard.kt
│   │   └── common/
│   │       ├── LoadingIndicator.kt
│   │       └── ErrorBanner.kt
│   ├── model/
│   │   ├── HomeUiState.kt
│   │   ├── DateRange.kt
│   │   └── *UiState.kt
│   └── theme/
│       ├── Color.kt
│       ├── Typography.kt
│       └── Theme.kt
│
├── MainActivity.kt
└── ExpenseManagerApp.kt
```

### Naming Conventions

| Artifact | Convention | Example |
|----------|-----------|---------|
| **Domain Entity** | PascalCase | `Category`, `Movimento` |
| **Value Object** | PascalCase | `CategoryExpenseBreakdown`, `BalanceSummary` |
| **Type Alias** | PascalCase | `CategoryId`, `MoneyAmount` |
| **Port Interface** | `<Entity>Repository` or `<Operation>Port` | `TransactionRepository`, `PersistencePort` |
| **Use Case Class** | `<Operation><Entity>` or `<Verb><Noun>` | `GetCategoryBreakdown`, `CreateMovimento` |
| **DAO Interface** | `<Entity>Dao` | `TransactionDao`, `CategoryDao` |
| **Entity** | `<Entity>Entity` | `TransactionEntity`, `CategoryEntity` |
| **ViewModel** | `<Screen>ViewModel` | `HomeViewModel`, `TransactionDetailViewModel` |
| **UiState** | `<Screen>UiState` | `HomeUiState`, `TransactionDetailUiState` |
| **Screen Composable** | `<Name>Screen` | `HomeScreen`, `TransactionDetailScreen` |
| **Component Composable** | `<Name><Type>` | `DateRangeControl`, `CategoryExpenseRow`, `BalanceSummaryCard` |
| **Mapper Function** | `to<Target>()` | `toDomain()`, `toEntity()` |
| **Repository Impl** | `<Entity>RepositoryImpl` | `TransactionRepositoryImpl`, `CategoryRepositoryImpl` |
| **Database** | `AppDatabase` | `AppDatabase` |

### Package Naming

- `domain.model` – Entities, value objects, aggregates
- `domain.port` – Repository interfaces, query objects
- `application.usecase` – Use case classes
- `adapter.persistence.dao` – Room DAOs
- `adapter.persistence.entity` – Room entities
- `adapter.persistence.mapper` – Mapping extension functions
- `adapter.persistence.repository` – Repository implementations
- `presentation.viewmodel` – ViewModels
- `presentation.screen` – Full-screen composables
- `presentation.composable` – Reusable UI components
- `presentation.model` – UiState, presentation-only models

---

## Development Workflow

### **Adding a New Feature (Vertical Slice)**

1. **Define domain entity** → `Movimento`, `Category`
2. **Define domain ports** → `TransactionRepository.getByIds()`
3. **Create DAO query** → `@Query` with aggregation if needed
4. **Implement repository** → Map entities, compose queries
5. **Create use case** → Orchestrate repository
6. **Design ViewModel** → Define `UiState`, wire use cases
7. **Build Compose UI** → Root screen receives `UiState`, children receive primitives

### **Code Review Checklist**

- [ ] Domain layer has zero framework dependencies
- [ ] No `Flow` leaking from domain ports
- [ ] DAO queries avoid N+1 (uses `getByIds()` or SQL aggregation)
- [ ] Repository is orchestrator, not logic dump
- [ ] ViewModel properly manages invalidation signals
- [ ] Child composables are stateless, receive decomposed values
- [ ] Snackbar errors handled at screen root
- [ ] Type safety used (type aliases for IDs)
- [ ] `Result<T>` chaining with `onSuccess`/`onFailure`
- [ ] No business logic in composables

---

## Key Decisions & Rationale

| Decision | Rationale |
|----------|-----------|
| **Hexagonal Architecture** | Clear separation of concerns; testable domain layer; framework-agnostic |
| **Type Aliases for IDs** | Semantic clarity; prevents accidental misuse of primitive strings |
| **MoneyAmount in cents (Long)** | Avoids floating-point precision issues; represents real-world money accurately |
| **No Flow from domain** | Domain purity; reactive composition at ViewModel boundary only |
| **StateFlow at ViewModel only** | Centralized state, single source of truth; clear data flow |
| **Manual DI (no Hilt)** | Full control; simpler for monolithic app; easy to understand object graph |
| **Fixture-based testing** | Better readability; mutable state builders; less ceremony than mocks |
| **SQL aggregation** | Efficient; prevents N+1; belongs at data layer per CQRS principles |
| **Decomposed Compose state** | Children remain stateless and reusable; easier testing; clear data contracts |

---

## Resources & References

- **Ports & Adapters**: https://www.alistair.cockburn.us/hexagonal-architecture/
- **Clean Architecture (Android)**: *Clean Architecture: A Craftsman's Guide to Software Structure and Design* by Robert C. Martin
- **Jetpack Compose State**: https://developer.android.com/jetpack/compose/state
- **Room Best Practices**: https://developer.android.com/training/data-storage/room
- **Testing Best Practices**: https://developer.android.com/training/testing

---

## Contact / Maintainer

**Project Owner**: Roberto  
**Package**: `it.gr85.android.apps.em`  
**Architecture Review**: Engage before major refactoring or architectural decisions