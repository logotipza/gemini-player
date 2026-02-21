package com.gravitymusic.data.local.metadata

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.gravitymusic.domain.model.Track
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class MetadataManager @Inject constructor(
    private val context: Context
) {
    fun extractMetadata(uri: Uri, folderId: Long): Track? {
        var tempFile: File? = null
        return try {
            val contentResolver = context.contentResolver
            
            // JAudioTagger needs a File object, so we copy stream to temp cache if not a "file://"
            val isFileScheme = uri.scheme == "file"
            val fileToRead = if (isFileScheme) {
                File(uri.path!!)
            } else {
                tempFile = File(context.cacheDir, "temp_audio_extract")
                contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }
                tempFile
            }

            if (!fileToRead.exists() || fileToRead.length() == 0L) return null

            val audioFile = AudioFileIO.read(fileToRead)
            val tag = audioFile.tag
            val header = audioFile.audioHeader

            val title = tag?.getFirst(FieldKey.TITLE).takeIf { !it.isNullOrBlank() } ?: "Unknown Title"
            val artist = tag?.getFirst(FieldKey.ARTIST).takeIf { !it.isNullOrBlank() } ?: "Unknown Artist"
            val album = tag?.getFirst(FieldKey.ALBUM).takeIf { !it.isNullOrBlank() } ?: "Unknown Album"
            val duration = header?.trackLength?.toLong()?.times(1000) ?: 0L

            Track(
                id = 0, // Assigned by Room
                title = title,
                artist = artist,
                album = album,
                duration = duration,
                uri = uri.toString(),
                folderId = folderId
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            tempFile?.delete()
        }
    }

    fun updateMetadata(uri: Uri, title: String?, artist: String?, album: String?): Boolean {
        var tempFile: File? = null
        return try {
            val isFileScheme = uri.scheme == "file"
            val fileToEdit = if (isFileScheme) {
                File(uri.path!!)
            } else {
                tempFile = File(context.cacheDir, "temp_audio_edit")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }
                tempFile
            }

            if (!fileToEdit.exists()) return false

            val audioFile = AudioFileIO.read(fileToEdit)
            val tag = audioFile.tagOrCreateAndSetDefault

            title?.let { tag.setField(FieldKey.TITLE, it) }
            artist?.let { tag.setField(FieldKey.ARTIST, it) }
            album?.let { tag.setField(FieldKey.ALBUM, it) }

            audioFile.commit()

            // If it was a content URI, we need to copy the changes back to the original URI
            if (!isFileScheme) {
                context.contentResolver.openOutputStream(uri, "wt")?.use { output ->
                    fileToEdit.inputStream().use { input ->
                        input.copyTo(output)
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            tempFile?.delete()
        }
    }

    // Logic for returning cover art bytes can be added here
}
