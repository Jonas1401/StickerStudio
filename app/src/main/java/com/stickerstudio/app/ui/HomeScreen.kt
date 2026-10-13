package com.stickerstudio.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.stickerstudio.app.data.PackSummary
import com.stickerstudio.app.data.ProjectEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    packs: List<PackSummary>,
    projects: List<ProjectEntity>,
    snackbar: SnackbarHostState,
    onPickPhoto: () -> Unit,
    onOpenPacks: () -> Unit,
    onOpenStickers: () -> Unit,
    onOpenProjects: () -> Unit,
    onOpenHelp: () -> Unit,
    onCreatePack: (String, String) -> Unit
) {
    var showCreate by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Sticker Studio") }) },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreate = true },
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text("Novo pacote") }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionCard(Icons.Filled.AddPhotoAlternate, "Figurinha com foto", null, true, Modifier.weight(1f), onPickPhoto)
                ActionCard(Icons.Filled.Videocam, "Animada com vídeo", "Em breve", false, Modifier.weight(1f)) {}
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionCard(Icons.Filled.Gif, "Figurinha com GIF", "Em breve", false, Modifier.weight(1f)) {}
                ActionCard(Icons.Filled.Inventory2, "Meus pacotes", null, true, Modifier.weight(1f), onOpenPacks)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionCard(Icons.Filled.EmojiEmotions, "Minhas figurinhas", null, true, Modifier.weight(1f), onOpenStickers)
                ActionCard(Icons.Filled.HelpOutline, "Ajuda e configurações", null, true, Modifier.weight(1f), onOpenHelp)
            }

            SectionHeader("Meus pacotes", "Ver todos", onOpenPacks)
            if (packs.isEmpty()) {
                Text(
                    "Você ainda não criou pacotes. Toque em “Novo pacote” para começar.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(end = 8.dp)) {
                    items(packs, key = { it.id }) { PackCard(it, Modifier.width(140.dp)) }
                }
            }

            SectionHeader("Projetos recentes", "Ver todos", onOpenProjects)
            if (projects.isEmpty()) {
                Text("Nenhum projeto ainda. Importe uma foto para criar o primeiro.", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(projects.take(10), key = { it.id }) {
                        FileImage(it.thumbPath, Modifier.size(96.dp).then(Modifier), ContentScale.Crop)
                    }
                }
            }
            Column(Modifier.height(72.dp)) {}
        }
    }

    if (showCreate) {
        PackDialog(
            title = "Novo pacote",
            confirmLabel = "Criar",
            onDismiss = { showCreate = false },
            onConfirm = { n, a -> onCreatePack(n, a); showCreate = false }
        )
    }
}

@Composable
private fun SectionHeader(title: String, action: String, onAction: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        Text(
            action,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.clickable(onClick = onAction).padding(4.dp)
        )
    }
}

@Composable
private fun ActionCard(
    icon: ImageVector,
    label: String,
    note: String?,
    enabled: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(110.dp).clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(14.dp).fillMaxWidth(), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(icon, null, Modifier.size(32.dp), tint = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.outline)
            Column {
                Text(label, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (note != null) Text(note, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
fun PackCard(pack: PackSummary, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    Card(modifier = modifier.let { if (onClick != null) it.clickable(onClick = onClick) else it }, shape = RoundedCornerShape(16.dp)) {
        Column {
            FileImage(pack.coverPath, Modifier.fillMaxWidth().height(110.dp))
            Column(Modifier.padding(10.dp)) {
                Text(pack.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${pack.stickerCount} figurinhas", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
