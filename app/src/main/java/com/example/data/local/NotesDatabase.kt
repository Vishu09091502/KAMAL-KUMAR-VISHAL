package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AdminUser
import com.example.data.model.Branch
import com.example.data.model.Note
import com.example.data.model.Semester
import com.example.data.model.Subject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Branch::class, Semester::class, Subject::class, Note::class, AdminUser::class],
    version = 1,
    exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

    companion object {
        @Volatile
        private var INSTANCE: NotesDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NotesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesDatabase::class.java,
                    "diploma_notes_db"
                )
                    .addCallback(NotesDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class NotesDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.notesDao())
                    }
                }
            }

            suspend fun populateDatabase(dao: NotesDao) {
                // Populate Admin User
                dao.insertAdmin(PrepopulatedData.defaultAdmin)

                // Populate Branches
                dao.insertBranches(PrepopulatedData.branches)

                // Populate Semesters (1-6 for all branches)
                dao.insertSemesters(PrepopulatedData.generateSemesters())

                // Populate Subjects
                dao.insertSubjects(PrepopulatedData.subjects)

                // Populate Notes
                dao.insertNotes(PrepopulatedData.sampleNotes)
            }
        }
    }
}
