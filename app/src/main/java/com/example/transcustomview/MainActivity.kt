package com.example.transcustomview

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.widget.ImageView
import com.vnpay.vietcredit.utils.launch.VnpayLaunch

class MainActivity : AppCompatActivity() {
    val customView:RecordAudioView by lazy {
        findViewById(R.id.customview)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        customView.setOnClickListener {

        }
    }

}