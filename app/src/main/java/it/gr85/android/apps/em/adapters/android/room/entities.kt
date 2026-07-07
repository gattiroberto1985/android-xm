package it.gr85.android.apps.em.adapters.android.room

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// le entities "mappano" gli oggetti di dominio come li vede il database.
// questi poi devono avere i layer di accesso, cioè i DAOs.


// time to long -> System.currentTimeMillis()
// time from long ->
//        long currentMillis = System.currentTimeMillis();
//        java.time.Instant instant = java.time.Instant.ofEpochMilli(currentMillis);
//        java.time.LocalDateTime localDateTime = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
data class Timestamps(
    @ColumnInfo(name = "created_at") val created_at: Long,
    @ColumnInfo(name = "updated_at") val updated_at: Long,
)



@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class ARCategoryEntity (
    @PrimaryKey @ColumnInfo(name = "id") val id        : String,
    @ColumnInfo(name = "name")      val name      : String,
    @ColumnInfo(name = "hex_color") val hexColor  : String,
    @Embedded                       val timestamps: Timestamps
)


@Entity(
    tableName = "transactions",
    indices = [
        Index( value = ["category_id"] )
    ]
)
data class ARTransactionEntity (
    @PrimaryKey @ColumnInfo( name = "id"  )        val id         : String,
    @ColumnInfo( name = "type")        val type       : String,
    @ColumnInfo( name = "amount")      val amount     : Long,
    @ColumnInfo( name = "date")        val date       : Long,
    @ColumnInfo( name = "description") val description: String,
    @ColumnInfo( name = "category_id") val categoryId : String,
    @Embedded val timestamps : Timestamps
)

data class CategoryExpenseRow(
    @ColumnInfo("id") val categoryId: String,
    @ColumnInfo("name") val categoryName: String,
    @ColumnInfo("color") val categoryColor: String,
    @ColumnInfo("total_amount") val totalAmount: Long,  // in cents
    @ColumnInfo("transaction_count") val transactionCount: Int
)