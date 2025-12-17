package com.myapp.greetingcard

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.serialization.Serializable
import android.content.Context
import androidx.room.RawQuery
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteQuery

@Entity(tableName = "FlashCards",
    indices = [Index(value = ["english_card","vietnamese_card"], unique = true)])
@Serializable
    data class FlashCard(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "english_card") val englishCard: String?,
    @ColumnInfo(name = "vietnamese_card") val vietnameseCard: String?
    )

@Dao
interface FlashCardDao {
    @RawQuery
    fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int
    @Query("SELECT * FROM FlashCards")
    suspend fun getAll(): List<FlashCard>

//    @Query("SELECT * FROM FlashCards WHERE uid IN (:flashCardIds)")
//    suspend fun loadAllByIds(flashCardIds: IntArray): List<FlashCard>

    @Query("SELECT * FROM FlashCards ORDER BY RANDOM() LIMIT :size")
    suspend fun getLesson(size: Int): List<FlashCard>

    @Query("SELECT * FROM FlashCards WHERE english_card LIKE :english AND " +
            "vietnamese_card LIKE :vietnamese LIMIT 1")
    suspend fun findByCards(english: String, vietnamese: String): FlashCard?

//    @Query("SELECT * FROM FlashCards WHERE uid = :id")
//    suspend fun getCardById(id: Int): FlashCard?

    @Insert
    suspend fun insertAll(vararg flashCard: FlashCard)

//    @Delete
//    suspend fun delete(flashCard: FlashCard)

    @Query("DELETE FROM FlashCards WHERE english_card = :english AND vietnamese_card = :vietnamese")
    suspend fun deleteFlashCard(english: String, vietnamese: String)
//    @Update
//    suspend fun update(flashCard: FlashCard)

    @Query("UPDATE FlashCards SET english_card = :newEnglish, vietnamese_card = :newVietnamese WHERE english_card = :oldEnglish AND vietnamese_card = :oldVietnamese")
    suspend fun updateByContent(oldEnglish: String, oldVietnamese: String, newEnglish: String, newVietnamese: String)
}

@Database(entities = [FlashCard::class], version = 1)
abstract class AnNamDatabase : RoomDatabase() {
    abstract fun flashCardDao(): FlashCardDao
    companion object {
        @Volatile // Ensures visibility to all threads
        private var INSTANCE: AnNamDatabase? = null

        fun getDatabase(context: Context): AnNamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Use application context to prevent memory leaks
                    AnNamDatabase::class.java,
                    "FlashCardDatabase"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}