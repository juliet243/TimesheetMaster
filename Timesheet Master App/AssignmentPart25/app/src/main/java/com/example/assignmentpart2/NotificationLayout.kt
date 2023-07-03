package com.example.assignmentpart2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class NotificationLayout : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_layout)

        val image = findViewById<ImageView>(R.id.imageView3)
        val cont= findViewById<TextView>(R.id.tvContinue)

        //what will allow the image to animate
        image.setOnClickListener{
            image.animate().apply{
                duration = 1000
                rotationYBy(360f)
            }.withEndAction{
                image.animate().apply{
                    duration=1000
                    rotationYBy(3600f)
                }.start()
            }

        }

        cont.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}