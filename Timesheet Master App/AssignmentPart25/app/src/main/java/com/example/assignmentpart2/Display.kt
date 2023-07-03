package com.example.assignmentpart2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.assignmentpart2.databinding.ActivityDisplayBinding

class Display : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_display)
        //This class will allow users to navigate between the DisplayEntry and Report UI

        binding = ActivityDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("useremail") //You need this in order to access data for a specif userID
            var displayEnt = findViewById<ImageButton>(R.id.imgBtnDsplyEntries)
            var displayGraph = findViewById<ImageButton>(R.id.imgBtnDsplyGraph)

        displayEnt.setOnClickListener{
            val intent = Intent(this, DisplayEntry::class.java)
            intent.putExtra("useremail",userId)
            startActivity(intent)
        }

        displayGraph.setOnClickListener{
            intent.putExtra("useremail",userId)
            val intent = Intent(this, Reports::class.java)
            startActivity(intent)
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("useremail",userId)
                    startActivity(intent)
                }
                R.id.create -> {
                    val intent = Intent(this, Create::class.java)
                    intent.putExtra("useremail",userId)
                    startActivity(intent)
                }
                R.id.display -> {
                    val intent = Intent(this, Display::class.java)
                    intent.putExtra("useremail",userId)
                    startActivity(intent)
                }
                R.id.signOut -> {
                    val intent = Intent(this, SignOut::class.java)
                    intent.putExtra("useremail",userId)
                    startActivity(intent)
                }
                else -> {}
            }
            true

        }
    }
}
//--------------------------------------ooo000EndOfFile000ooo---------------------------------------