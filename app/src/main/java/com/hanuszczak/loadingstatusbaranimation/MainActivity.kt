package com.hanuszczak.loadingstatusbaranimation

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar

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

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private var url: String? = null

    private var radioSelection: RadioSelection? = null
    private var downloadedUrl: String? = null
    private var downloadStatus: String? = null

    enum class RadioSelection {
        GLIDE, LOADAPP, RETROFIT
    }

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

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                sendNotification(
                    getString(R.string.notification_description),
                    this
                )
            } else {
                Toast.makeText(this, "no Grants", Toast.LENGTH_SHORT).show()
            }
        }

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        loadingButton.setOnClickListener {
            loadingButton.buttonState = ButtonState.Clicked
            if (radioGroup.checkedRadioButtonId == -1) {
                Toast.makeText(
                    this, getString(R.string.no_radio_group_selected),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                loadingButton.buttonState = ButtonState.Loading
                if (glideRadio.isChecked) {
                    url = URL_GLIDE
                    radioSelection = RadioSelection.GLIDE
                }
                if (loadAppRadio.isChecked) {
                    url = URL_UDACITY
                    radioSelection = RadioSelection.LOADAPP
                }
                if (retrofitRadio.isChecked) {
                    url = URL_RETROFIT
                    radioSelection = RadioSelection.RETROFIT
                }
                radioGroup.clearCheck()
                radioGroup.setChildrenEnabled(false)
                loadingButton.isEnabled = false
                download(url)
            }
        }
    }

    fun RadioGroup.setChildrenEnabled(enabled: Boolean) {
        for (i in 0 until childCount) {
            getChildAt(i).isEnabled = enabled
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.d("MainActivity", "downloadId: $id")
            downloadStatus = when (
                intent?.getIntExtra(DownloadManager.COLUMN_STATUS, -1)
            ) {
                DownloadManager.STATUS_SUCCESSFUL -> getString(R.string.download_successful)
                else -> getString(R.string.download_failed)
            }
            radioGroup.setChildrenEnabled(true)
            loadingButton.isEnabled = true
            loadingButton.buttonState = ButtonState.Completed
            downloadedUrl = when (radioSelection) {
                RadioSelection.GLIDE -> getString(R.string.glide_url)
                RadioSelection.LOADAPP -> getString(R.string.loadapp_url)
                RadioSelection.RETROFIT -> getString(R.string.retrofit_url)
                else -> ""
            }
            prepareNotification(context)
            when {
                ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    Log.e("MainActivity", "User accepted the notifications!")
                    sendNotification(
                        getString(R.string.notification_description),
                        context
                    )
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Snackbar.make(
                        findViewById(R.id.main_layout),
                        "The user denied the notifications ):",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Settings") {
                            val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            val uri: Uri =
                                Uri.fromParts(
                                    "com.hanuszczak.loadingstatusbaranimation",
                                    packageName,
                                    null
                                )
                            settingsIntent.data = uri
                            startActivity(settingsIntent)
                        }
                        .show()
                }
                else -> {
                    if (Build.VERSION.SDK_INT >= 33) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
        }
    }

    private fun prepareNotification(context: Context?) {
        val intentToDetailActivity = Intent(context, DetailActivity::class.java)
        intentToDetailActivity.putExtra(INTENT_DOWNLOAD_URL, downloadedUrl)
        intentToDetailActivity.putExtra(INTENT_DOWNLOAD_STATUS, downloadStatus)
        pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            intentToDetailActivity,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        action = NotificationCompat.Action(
            R.drawable.ic_assistant_black_24dp,
            getString(R.string.notification_button),
            pendingIntent
        )
    }

    private fun sendNotification(messageBody: String, context: Context) {
        val builder = NotificationCompat.Builder(
            context,
            getString(R.string.notification_channel_id)
        )
            .setContentTitle(
                context
                    .getString(R.string.notification_title)
            )
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(action)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = resources.getString(R.string.notification_channel_description)

            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
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
        private const val NOTIFICATION_ID = 0
        const val INTENT_DOWNLOAD_URL = "downloadedUrl"
        const val INTENT_DOWNLOAD_STATUS = "downloadStatus"
    }
}