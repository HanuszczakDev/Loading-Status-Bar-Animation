package com.hanuszczak.loadingstatusbaranimation

import android.app.NotificationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hanuszczak.loadingstatusbaranimation.MainActivity.Companion.INTENT_DOWNLOAD_STATUS
import com.hanuszczak.loadingstatusbaranimation.MainActivity.Companion.INTENT_DOWNLOAD_URL

class DetailActivity : AppCompatActivity() {
    private lateinit var fileNameTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(findViewById(R.id.toolbar))

        fileNameTextView = findViewById(R.id.repository_name_text_view)
        statusTextView = findViewById(R.id.download_status_text_view)
        confirmButton = findViewById(R.id.confirm_button)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()

        fileNameTextView.text = intent.getStringExtra(INTENT_DOWNLOAD_URL)
        statusTextView.text = intent.getStringExtra(INTENT_DOWNLOAD_STATUS)

        confirmButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

}
