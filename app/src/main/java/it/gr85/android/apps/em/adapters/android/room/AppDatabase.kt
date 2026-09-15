package it.gr85.android.apps.em.adapters.android.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * AppDatabase
 *
 * Room database per l'app ExpenseManager.
 * Entities: ARCategoryEntity, ARTransactionEntity
 * Version: 1 (aggiorna quando modifichi lo schema)
 */
@Database(
    entities = [ARCategoryEntity::class, ARTransactionEntity::class],
    version = 1,
    exportSchema = false  // Set to true in production per migrazioni
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * DAO: CategoryRepository adapter
     */
    abstract fun categoryDao(): ARCategoryDao

    /**
     * DAO: TransactionRepository adapter
     */
    abstract fun transactionDao(): ARTransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Singleton: istanzia il database una sola volta.
         * Thread-safe con double-checked locking.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expensemanager_database"
                )
                    // Development: ricreare il DB se lo schema cambia
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}