package com.stickerstudio.app.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stickerstudio.app.StickerApp
import com.stickerstudio.app.data.PackEntity
import com.stickerstudio.app.data.PackSummary
import com.stickerstudio.app.data.ProjectEntity
import com.stickerstudio.app.data.StickerEntity
import com.stickerstudio.app.media.ImportException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val graph = app as StickerApp
    private val db = graph.database

    val packs: StateFlow<List<PackSummary>> = db.packDao().observeSummaries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val projects: StateFlow<List<ProjectEntity>> = db.projectDao().observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val stickers: StateFlow<List<StickerEntity>> = db.stickerDao().observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _messages = Channel<String>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    fun importPhoto(uri: Uri) {
        viewModelScope.launch {
            try {
                val img = graph.imageImporter.import(uri)
                val now = System.currentTimeMillis()
                db.projectDao().insert(
                    ProjectEntity(
                        title = "Projeto ${projects.value.size + 1}",
                        sourcePath = img.sourcePath,
                        thumbPath = img.thumbPath,
                        createdAt = now,
                        updatedAt = now
                    )
                )
                _messages.send("Foto importada e salva em Projetos recentes.")
            } catch (e: ImportException) {
                _messages.send(e.message ?: "Falha ao importar a foto.")
            } catch (e: Exception) {
                _messages.send("Erro inesperado ao importar: ${e.message}")
            }
        }
    }

    fun createPack(name: String, author: String) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            db.packDao().insert(
                PackEntity(
                    id = UUID.randomUUID().toString(),
                    name = name.trim(),
                    author = author.trim(),
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    fun renamePack(id: String, name: String, author: String) {
        viewModelScope.launch {
            db.packDao().update(id, name.trim(), author.trim(), System.currentTimeMillis())
        }
    }

    fun deletePack(id: String) {
        viewModelScope.launch { db.packDao().delete(id) }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            db.projectDao().delete(project.id)
            File(project.sourcePath).delete()
            File(project.thumbPath).delete()
        }
    }
}
