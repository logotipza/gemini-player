package com.gravitymusic.presentation.screens.folders

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.media3.common.MediaMetadata
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.gravitymusic.domain.repository.FolderRepository
import com.gravitymusic.media.controller.MusicController
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FoldersViewModel @Inject constructor(
    private val folderRepository: FolderRepository,
    private val musicController: MusicController,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val folders = folderRepository.getAllFolders()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addFolderUri(uriString: String) {
        viewModelScope.launch {
            folderRepository.addFolder(uriString)
        }
    }

    fun playFolder(uriString: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = Uri.parse(uriString)
                val documentFile = DocumentFile.fromTreeUri(context, uri)
                if (documentFile != null && documentFile.isDirectory) {
                    val mediaItems = mutableListOf<MediaItem>()
                    val retriever = MediaMetadataRetriever()
                    // Получаем аудио файлы в этой папке
                    documentFile.listFiles().forEach { file ->
                        if (!file.isDirectory) {
                            val mime = file.type ?: ""
                            val name = file.name ?: ""
                            if (mime.startsWith("audio/") || name.endsWith(".mp3") || name.endsWith(".flac")) {
                                try {
                                    retriever.setDataSource(context, file.uri)
                                    val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: name
                                    val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"
                                    val artwork = retriever.embeddedPicture

                                    val metadata = MediaMetadata.Builder()
                                        .setTitle(title)
                                        .setArtist(artist)
                                        .setArtworkData(artwork, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
                                        .build()

                                    val item = MediaItem.Builder()
                                        .setUri(file.uri)
                                        .setMediaId(file.uri.toString())
                                        .setMediaMetadata(metadata)
                                        .build()
                                        
                                    mediaItems.add(item)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    mediaItems.add(MediaItem.fromUri(file.uri))
                                }
                            }
                        }
                    }
                    try { retriever.release() } catch (e: Exception) {}

                    if (mediaItems.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            musicController.mediaController?.clearMediaItems()
                            musicController.mediaController?.setMediaItems(mediaItems)
                            musicController.mediaController?.prepare()
                            musicController.mediaController?.play()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Здесь лучше выводить Toast или Error state
            }
        }
    }
}
