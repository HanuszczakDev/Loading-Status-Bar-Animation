package com.hanuszczak.loadingstatusbaranimation

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity() {
    private var downloadID: Long = 0

    private lateinit var loadingButton: LoadingButton
    private lateinit var radioGroup: RadioGroup
    private lateinit var glideRadio: RadioButton
    private lateinit var loadAppRadio: RadioButton
    private lateinit var retrofitRadio: RadioButton

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        loadingButton = findViewById(R.id.custom_button)

        radioGroup = findViewById(R.id.radio_group)
        glideRadio = findViewById(R.id.glide_radio)
        loadAppRadio = findViewById(R.id.load_app_radio)
        retrofitRadio = findViewById(R.id.retrofit_radio)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        loadingButton.setOnClickListener {
            if (radioGroup.checkedRadioButtonId == -1) {
                Toast.makeText(
                    this, getString(R.string.no_radio_group_selected),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                if (glideRadio.isChecked) url = URL_GLIDE
                if (loadAppRadio.isChecked) url = URL_UDACITY
                if (retrofitRadio.isChecked) url = URL_RETROFIT
                download(url)
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.d("MainActivity", "downloadId: $id")
        }
    }

    private fun download(url: String?) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL_UDACITY = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_GLIDE = "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_RETROFIT = "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }
}