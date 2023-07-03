package com.example.assignmentpart2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.assignmentpart2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //replaceFragment(HomeFragment())

        //After user clicks on 'get started' button they are taken to add goals screen where they can set their goals.
        //val setGoal = findViewById<Button>(R.id.btnSetGoals)
        val createNew = findViewById<Button>(R.id.btnCreateNew)
        val userId = intent.getStringExtra("useremail")

        createNew.setOnClickListener{
            val intent = Intent(this, Create::class.java)
            intent.putExtra("useremail",userId)
            startActivity(intent)
        }

        binding.bottomNavigationView.setOnItemSelectedListener {item ->
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