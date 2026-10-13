package com.stickerstudio.app

import android.app.Application
import com.stickerstudio.app.data.AppDatabase
import com.stickerstudio.app.media.ImageImporter

class StickerApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.create(this) }
    val imageImporter: ImageImporter by lazy { ImageImporter(this) }
}
