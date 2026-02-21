package com.gravitymusic.data.sync

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gravitymusic.core.util.Result as AppResult
import com.gravitymusic.domain.repository.CloudRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File

@HiltWorker
class CloudSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val cloudRepository: CloudRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): androidx.work.ListenableWorker.Result {
        val yandexPath = inputData.getString("YANDEX_PATH") ?: return androidx.work.ListenableWorker.Result.failure()

        return try {
            val resourcesResult = cloudRepository.getFolderContents(yandexPath)
            if (resourcesResult is AppResult.Success) {
                val items = resourcesResult.data
                
                // create target local folder
                val localDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "GravityMusic_Cloud")
                if (!localDir.exists()) localDir.mkdirs()

                // Filter for audio files
                val audioItems = items.filter { item ->
                    item.type == "file" && (item.mimeType?.startsWith("audio/") == true ||
                            item.name.endsWith(".mp3") || item.name.endsWith(".flac"))
                }

                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                for (item in audioItems) {
                    // Check if file already exists locally
                    val localFile = File(localDir, item.name)
                    if (localFile.exists()) continue

                    // Get transient download link from Yandex
                    val linkResult = cloudRepository.getDownloadLink(item.path)
                    if (linkResult is AppResult.Success) {
                        val downloadUri = Uri.parse(linkResult.data)
                        
                        val request = DownloadManager.Request(downloadUri)
                            .setTitle("Downloading ${item.name}")
                            .setDescription("Downloading from Yandex Disk")
                            .setDestinationUri(Uri.fromFile(localFile))
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true)

                        downloadManager.enqueue(request)
                    }
                }
                
                // Future Note: WorkManager doesn't wait for DownloadManager to finish. 
                // The MediaScannerWorker handles parsing successfully stored file tags.
                androidx.work.ListenableWorker.Result.success()
            } else {
                androidx.work.ListenableWorker.Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            androidx.work.ListenableWorker.Result.failure()
        }
    }
}
