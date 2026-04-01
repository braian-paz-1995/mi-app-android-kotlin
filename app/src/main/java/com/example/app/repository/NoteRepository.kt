package com.example.app.repository

import com.example.app.data.NoteDao
import com.example.app.data.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    fun notes(): Flow<List<NoteEntity>> = noteDao.getAll()

    suspend fun addNote(content: String) {
        if (content.isNotBlank()) {
            noteDao.insert(NoteEntity(content = content.trim()))
        }
    }
}
