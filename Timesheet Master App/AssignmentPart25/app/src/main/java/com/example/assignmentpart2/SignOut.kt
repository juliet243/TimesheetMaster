package com.example.assignmentpart2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
//import com.example.assignmentpart2.databinding.ActivityDisplayAllProjectsBinding
import com.example.assignmentpart2.databinding.ActivitySignOutBinding

class SignOut : AppCompatActivity() {

    private lateinit var binding: ActivitySignOutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignOutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId = intent.getStringExtra("useremail")

        var btnSignOut = findViewById<Button>(R.id.btnSignOut)

        //When user selects logout button they will be redirected to the login section
        btnSignOut.setOnClickListener{
            Toast.makeText(applicationContext, "Logging out...", Toast.LENGTH_LONG).show()
            val intent = Intent(this,   Welcome::class.java)
            startActivity(intent)
        }

        //This will account for event clicking of the navigation bar (similar to if statement format)
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
                    val intent = Intent(this, Login::class.java)
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