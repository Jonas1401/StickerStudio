package com.stickerstudio.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PackDao {
    @Query(
        """
        SELECT p.id AS id, p.name AS name, p.author AS author, p.animated AS animated,
               (SELECT s.thumbPath FROM stickers s WHERE s.id = p.iconStickerId) AS coverPath,
               (SELECT COUNT(*) FROM stickers s WHERE s.packId = p.id) AS stickerCount
        FROM packs p
        ORDER BY p.updatedAt DESC
        """
    )
    fun observeSummaries(): Flow<List<PackSummary>>

    @Insert
    suspend fun insert(pack: PackEntity)

    @Query("UPDATE packs SET name = :name, author = :author, updatedAt = :now WHERE id = :id")
    suspend fun update(id: String, name: String, author: String, now: Long)

    @Query("DELETE FROM packs WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface StickerDao {
    @Query("SELECT * FROM stickers ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<StickerEntity>>

    @Query("SELECT * FROM stickers WHERE packId = :packId ORDER BY position")
    fun observeForPack(packId: String): Flow<List<StickerEntity>>
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<ProjectEntity>>

    @Insert
    suspend fun insert(project: ProjectEntity): Long

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun get(id: Long): ProjectEntity?

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun delete(id: Long)
}
