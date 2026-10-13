package com.stickerstudio.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.stickerstudio.app.data.PackSummary
import com.stickerstudio.app.data.ProjectEntity
import com.stickerstudio.app.data.StickerEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubScreen(
    title: String,
    onBack: () -> Unit,
    fab: (@Composable () -> Unit)? = null,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar") }
                }
            )
        },
        floatingActionButton = { fab?.invoke() }
    ) { padding -> content(Modifier.padding(padding)) }
}

@Composable
private fun EmptyState(text: String) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun PacksScreen(
    packs: List<PackSummary>,
    onBack: () -> Unit,
    onCreate: (String, String) -> Unit,
    onRename: (String, String, String) -> Unit,
    onDelete: (String) -> Unit
) {
    var showCreate by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<PackSummary?>(null) }
    var deleting by remember { mutableStateOf<PackSummary?>(null) }

    SubScreen(
        "Meus pacotes", onBack,
        fab = {
            ExtendedFloatingActionButton(
                onClick = { showCreate = true },
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text("Novo pacote") }
            )
        }
    ) { mod ->
        if (packs.isEmpty()) {
            Box(mod) { EmptyState("Nenhum pacote criado ainda.") }
        } else {
            Column(mod.verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                packs.forEach { p ->
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            FileImage(p.coverPath, Modifier.padding(end = 12.dp).aspectRatio(1f).weight(0.25f).clip(RoundedCornerShape(12.dp)))
                            Column(Modifier.weight(0.75f)) {
                                Text(p.name, style = MaterialTheme.typography.titleMedium)
                                Text("por ${p.author} · ${p.stickerCount} figurinhas", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { editing = p }) { Icon(Icons.Filled.Edit, "Renomear") }
                            IconButton(onClick = { deleting = p }) { Icon(Icons.Filled.Delete, "Excluir") }
                        }
                    }
                }
            }
        }
    }

    if (showCreate) {
        PackDialog("Novo pacote", confirmLabel = "Criar", onDismiss = { showCreate = false }) { n, a ->
            onCreate(n, a); showCreate = false
        }
    }
    editing?.let { p ->
        PackDialog("Renomear pacote", p.name, p.author, "Salvar", onDismiss = { editing = null }) { n, a ->
            onRename(p.id, n, a); editing = null
        }
    }
    deleting?.let { p ->
        ConfirmDialog(
            "Excluir pacote?",
            "“${p.name}” e suas figurinhas serão removidos deste aparelho. Isso não pode ser desfeito.",
            "Excluir",
            onDismiss = { deleting = null }
        ) { onDelete(p.id); deleting = null }
    }
}

@Composable
fun ProjectsScreen(
    projects: List<ProjectEntity>,
    onBack: () -> Unit,
    onPickPhoto: () -> Unit,
    onDelete: (ProjectEntity) -> Unit
) {
    var deleting by remember { mutableStateOf<ProjectEntity?>(null) }
    SubScreen(
        "Projetos recentes", onBack,
        fab = {
            ExtendedFloatingActionButton(
                onClick = onPickPhoto,
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text("Importar foto") }
            )
        }
    ) { mod ->
        if (projects.isEmpty()) {
            Box(mod) { EmptyState("Nenhum projeto salvo. Importe uma foto para começar.") }
        } else {
            LazyVerticalGrid(
                GridCells.Adaptive(120.dp),
                mod.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(projects, key = { it.id }) { pr ->
                    Box {
                        FileImage(pr.thumbPath, Modifier.aspectRatio(1f).clip(RoundedCornerShape(16.dp)).clickable { deleting = pr })
                    }
                }
            }
        }
    }
    deleting?.let { pr ->
        ConfirmDialog(
            "Excluir projeto?",
            "A foto importada será removida deste aparelho.",
            "Excluir",
            onDismiss = { deleting = null }
        ) { onDelete(pr); deleting = null }
    }
}

@Composable
fun StickersScreen(stickers: List<StickerEntity>, onBack: () -> Unit) {
    SubScreen("Minhas figurinhas", onBack) { mod ->
        if (stickers.isEmpty()) {
            Box(mod) { EmptyState("Nenhuma figurinha exportada ainda. O editor e a exportação WebP chegam na Fase 2.") }
        } else {
            LazyVerticalGrid(
                GridCells.Adaptive(100.dp),
                mod.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stickers, key = { it.id }) { FileImage(it.thumbPath, Modifier.aspectRatio(1f)) }
            }
        }
    }
}

@Composable
fun HelpScreen(onBack: () -> Unit) {
    SubScreen("Ajuda e configurações", onBack) { mod ->
        Column(mod.verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Como usar", style = MaterialTheme.typography.titleMedium)
            Text("1. Toque em “Figurinha com foto” e escolha uma imagem.\n2. A foto fica salva em Projetos recentes (o editor chega na Fase 2).\n3. Crie pacotes em “Novo pacote”.")
            Text("Estado atual (Fase 1)", style = MaterialTheme.typography.titleMedium)
            Text("Importação de fotos, projetos e pacotes locais já funcionam. Editor, exportação WebP, vídeo, GIF e a inclusão no WhatsApp ainda não foram implementados e aparecem como “Em breve” ou ficam fora do app.")
            Text("Privacidade", style = MaterialTheme.typography.titleMedium)
            Text("Tudo é processado e guardado neste aparelho. O app não pede conta, não usa internet e não solicita permissão de armazenamento: as fotos vêm do seletor de fotos do Android.")
        }
    }
}
