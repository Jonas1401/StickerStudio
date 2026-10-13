package com.stickerstudio.app.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class ImportException(message: String) : Exception(message)

data class ImportedImage(val sourcePath: String, val thumbPath: String)

/**
 * Importa uma imagem escolhida no Photo Picker: valida o conteúdo real (não a extensão),
 * reduz para no máximo [MAX_SIDE] px e salva como PNG no armazenamento privado do app.
 */
class ImageImporter(private val context: Context) {

    private val sourcesDir get() = File(context.filesDir, "sources").apply { mkdirs() }
    private val thumbsDir get() = File(context.filesDir, "thumbs").apply { mkdirs() }

    suspend fun import(uri: Uri): ImportedImage = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        (resolver.openInputStream(uri) ?: throw ImportException("Não foi possível abrir o arquivo."))
            .use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw ImportException("O arquivo não é uma imagem válida ou está corrompido.")
        }

        val opts = BitmapFactory.Options().apply {
            inSampleSize = ImageMath.sampleSize(bounds.outWidth, bounds.outHeight, MAX_SIDE)
        }
        val decoded = try {
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
        } catch (e: OutOfMemoryError) {
            throw ImportException("A imagem é grande demais para este aparelho.")
        } ?: throw ImportException("Não foi possível decodificar a imagem.")

        val id = UUID.randomUUID().toString()
        val sourceFile = File(sourcesDir, "$id.png")
        val thumbFile = File(thumbsDir, "$id.png")
        var scaled: Bitmap? = null
        var thumb: Bitmap? = null
        try {
            val scale = ImageMath.fitScale(decoded.width, decoded.height, MAX_SIDE)
            scaled = if (scale < 1f) {
                Bitmap.createScaledBitmap(
                    decoded,
                    (decoded.width * scale).toInt().coerceAtLeast(1),
                    (decoded.height * scale).toInt().coerceAtLeast(1),
                    true
                )
            } else decoded
            sourceFile.outputStream().use { scaled.compress(Bitmap.CompressFormat.PNG, 100, it) }

            val ts = ImageMath.fitScale(scaled.width, scaled.height, THUMB_SIDE)
            thumb = Bitmap.createScaledBitmap(
                scaled,
                (scaled.width * ts).toInt().coerceAtLeast(1),
                (scaled.height * ts).toInt().coerceAtLeast(1),
                true
            )
            thumbFile.outputStream().use { thumb.compress(Bitmap.CompressFormat.PNG, 100, it) }
        } catch (e: Exception) {
            sourceFile.delete(); thumbFile.delete()
            throw ImportException("Falha ao salvar a imagem: ${e.message}")
        } finally {
            if (thumb != null && thumb !== scaled) thumb.recycle()
            if (scaled != null && scaled !== decoded) scaled.recycle()
            decoded.recycle()
        }
        ImportedImage(sourceFile.absolutePath, thumbFile.absolutePath)
    }

    companion object {
        const val MAX_SIDE = 2048
        const val THUMB_SIDE = 256
    }
}
