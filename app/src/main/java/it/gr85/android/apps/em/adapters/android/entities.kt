package it.gr85.android.apps.em.adapters.android

import it.gr85.android.apps.em.domain.model.Category
import it.gr85.android.apps.em.domain.model.Transaction
import it.gr85.android.apps.em.domain.model.idFrom

// le entities "mappano" gli oggetti di dominio come li vede il database.
// questi poi devono avere i layer di accesso, cioè i DAOs.
interface CategoryEntity {
    fun toDomain(): Category
}

interface TransactionEntity {
    fun toDomain(): Transaction
}


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
    table_name = "categories",
    indices = [
        Index(value = ["name"], unique = true )
    ]
)
data class AndroidRoomCategoryEntity (
    @PrimaryKey @ColumnInfo(name = "id")        val id        : String,
                @ColumnInfo(name = "name")      val name      : String,
                @ColumnInfo(name = "hex_color") val hexColor  : String,
                @Embedded                       val timestamps: Timestamps
) : CategoryEntity {

    override fun toDomain(): Category {
        return Category(
            id = idFrom( this.id ),
            name = this.name,
            colorHexCode = this.hexColor
        )
    }
}


@Entity(
    table_name = "transactions",
    indices = [
        Index( value = "category_id" )
    ]
)
data class AndroidRoomTransactionEntity (
    @PrimaryKey @ColumnInfo( name = "id"  )        val id         : String,
                @ColumnInfo( name = "type")        val type       : String,
                @ColumnInfo( name = "amount")      val amount     : Float,
                @ColumnInfo( name = "date")        val date       : Long,
                @ColumnInfo( name = "description") val description: String,
                @ColumnInfo( name = "category_id") val category_id: String,
                @Embedded                          val timestamps : Timestamps
) : TransactionEntity