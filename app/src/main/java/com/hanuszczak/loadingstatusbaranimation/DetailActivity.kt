package com.hanuszczak.loadingstatusbaranimation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hanuszczak.loadingstatusbaranimation.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(findViewById(R.id.toolbar))
    }

}
