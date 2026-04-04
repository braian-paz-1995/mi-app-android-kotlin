package com.example.app.core.data.repository

import com.example.app.core.data.local.menu.NoteDao
import com.example.app.core.data.local.room.entity.menu.menu.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl(private val noteDao: NoteDao) {
    fun notes(): Flow<List<NoteEntity>> = noteDao.getAll()

    suspend fun addNote(content: String) {
        if (content.isNotBlank()) {
            noteDao.insert(NoteEntity(content = content.trim()))
        }
    }
}