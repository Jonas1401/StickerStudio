package com.stickerstudio.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Pacote de figurinhas. O id é um UUID, usado depois como identifier no WhatsApp. */
@Entity(tableName = "packs")
data class PackEntity(
    @PrimaryKey val id: String,
    val name: String,
    val author: String,
    val animated: Boolean = false,
    val iconStickerId: Long? = null,
    val createdAt: Long,
    val updatedAt: Long
)

/** Figurinha exportada, pertencente a um pacote (fases 2+). */
@Entity(
    tableName = "stickers",
    foreignKeys = [
        ForeignKey(
            entity = PackEntity::class,
            parentColumns = ["id"],
            childColumns = ["packId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("packId")]
)
data class StickerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packId: String,
    val filePath: String,
    val thumbPath: String,
    val position: Int,
    val emojis: String = "😀",
    val animated: Boolean = false,
    val createdAt: Long
)

/** Projeto em edição: guarda a imagem de origem e (nas próximas fases) o estado do editor. */
@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val sourcePath: String,
    val thumbPath: String,
    val editorState: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

/** Resumo de pacote para a galeria. */
data class PackSummary(
    val id: String,
    val name: String,
    val author: String,
    val animated: Boolean,
    val coverPath: String?,
    val stickerCount: Int
)
