package com.gravitymusic.data.local.scanner

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gravitymusic.data.local.metadata.MetadataManager
import com.gravitymusic.domain.model.Playlist
import com.gravitymusic.domain.model.Track
import com.gravitymusic.domain.repository.FolderRepository
import com.gravitymusic.domain.repository.PlaylistRepository
import com.gravitymusic.domain.repository.TrackRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class MediaScannerWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val folderRepository: FolderRepository,
    private val trackRepository: TrackRepository,
    private val playlistRepository: PlaylistRepository,
    private val metadataManager: MetadataManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val folders = folderRepository.getAllFolders().first()
            val existingTracks = trackRepository.getAllTracks().first().map { it.uri }.toSet()

            val artistsSet = mutableSetOf<String>()
            val albumsSet = mutableSetOf<String>()

            for (folder in folders) {
                val uri = Uri.parse(folder.path)
                val documentFile = DocumentFile.fromTreeUri(context, uri)
                if (documentFile != null && documentFile.isDirectory) {
                    scanDirectory(documentFile, folder.id, existingTracks, artistsSet, albumsSet)
                }
            }
            
            generateAutoPlaylists(artistsSet, albumsSet)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private suspend fun scanDirectory(
        dir: DocumentFile,
        folderId: Long,
        existingTracks: Set<String>,
        artistsSet: MutableSet<String>,
        albumsSet: MutableSet<String>
    ) {
        val files = dir.listFiles()
        for (file in files) {
            if (file.isDirectory) {
                scanDirectory(file, folderId, existingTracks, artistsSet, albumsSet)
            } else {
                val uriString = file.uri.toString()
                if (file.type?.startsWith("audio/") == true || file.type?.startsWith("video/") == true) {
                    if (!existingTracks.contains(uriString)) {
                        val track = metadataManager.extractMetadata(file.uri, folderId)
                        if (track != null) {
                            trackRepository.insertTrack(track)
                            artistsSet.add(track.artist)
                            albumsSet.add(track.album)
                        }
                    }
                }
            }
        }
    }

    private suspend fun generateAutoPlaylists(artists: Set<String>, albums: Set<String>) {
        val existingPlaylists = playlistRepository.getAllPlaylists().first().map { it.name }.toSet()

        artists.forEach { artist ->
            val playlistName = "Artist: $artist"
            if (artist != "Unknown Artist" && !existingPlaylists.contains(playlistName)) {
                playlistRepository.createPlaylist(playlistName)
            }
        }

        albums.forEach { album ->
            val playlistName = "Album: $album"
            if (album != "Unknown Album" && !existingPlaylists.contains(playlistName)) {
                playlistRepository.createPlaylist(playlistName)
            }
        }
        // Linking specific tracks to these playlists iteratively is omitted for brevity but would happen here
    }
}
