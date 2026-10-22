package id.heula.data.repository

import id.heula.domain.model.FastingSession
import id.heula.domain.model.Mood
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeAll(): Flow<List<FastingSession>>
    suspend fun add(session: FastingSession): Long
    suspend fun delete(id: Long): Boolean
    suspend fun setMoodNote(id: Long, mood: Mood?, note: String?): Boolean
    suspend fun clearAll()
}
