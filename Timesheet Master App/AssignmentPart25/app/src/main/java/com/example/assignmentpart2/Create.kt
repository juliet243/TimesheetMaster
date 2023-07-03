package com.example.assignmentpart2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.assignmentpart2.databinding.ActivityCreateBinding


class Create : AppCompatActivity() {

    private lateinit var binding: ActivityCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        This activity will prompt user between creating a new project/category
        OR update an already existing category by creating a new entry
         */

        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_create_categories)

        var newCateg = findViewById<ImageButton>(R.id.imgBtnNewProj)
        var newEntry = findViewById<ImageButton>(R.id.imgBtnNewEntry)
        val userId = intent.getStringExtra("useremail")

        //If user opts to create a new category
        newCateg.setOnClickListener {
            val intent = Intent(this, CreateCategory::class.java)
            intent.putExtra("useremail",userId)
            startActivity(intent)
        }

        //If user opts to create a new entry in already existing category
        newEntry.setOnClickListener {
            val intent = Intent(this, CreateEntry::class.java)
            intent.putExtra("useremail",userId)
            startActivity(intent)
        }

        //If statement to move from one activity after menu nav bar has been clicked
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