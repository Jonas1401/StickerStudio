package com.stickerstudio.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stickerstudio.app.ui.HelpScreen
import com.stickerstudio.app.ui.HomeScreen
import com.stickerstudio.app.ui.MainViewModel
import com.stickerstudio.app.ui.PacksScreen
import com.stickerstudio.app.ui.ProjectsScreen
import com.stickerstudio.app.ui.StickersScreen
import com.stickerstudio.app.ui.theme.StickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { StickerTheme { AppRoot() } }
    }
}

@Composable
private fun AppRoot(vm: MainViewModel = viewModel()) {
    val nav = rememberNavController()
    val snackbar = remember { SnackbarHostState() }
    val packs by vm.packs.collectAsStateWithLifecycle()
    val projects by vm.projects.collectAsStateWithLifecycle()
    val stickers by vm.stickers.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { vm.messages.collect { snackbar.showSnackbar(it) } }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) vm.importPhoto(uri)
    }
    val pickPhoto = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
    val back: () -> Unit = { nav.popBackStack() }

    NavHost(nav, startDestination = "home") {
        composable("home") {
            HomeScreen(
                packs, projects, snackbar,
                onPickPhoto = pickPhoto,
                onOpenPacks = { nav.navigate("packs") },
                onOpenStickers = { nav.navigate("stickers") },
                onOpenProjects = { nav.navigate("projects") },
                onOpenHelp = { nav.navigate("help") },
                onCreatePack = vm::createPack
            )
        }
        composable("packs") { PacksScreen(packs, back, vm::createPack, vm::renamePack, vm::deletePack) }
        composable("projects") { ProjectsScreen(projects, back, pickPhoto, vm::deleteProject) }
        composable("stickers") { StickersScreen(stickers, back) }
        composable("help") { HelpScreen(back) }
    }
}
