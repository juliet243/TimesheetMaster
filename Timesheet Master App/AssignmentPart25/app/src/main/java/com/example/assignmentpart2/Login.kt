package com.example.assignmentpart2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*
        This class will perform 'user authentication locally. If a user has been signed up,
        they can use their credentials to log in to the app.
        */

        val email = findViewById<EditText>(R.id.edtSignUpEmail)
        val password = findViewById<EditText>(R.id.edtSignUpPassword)
        val login = findViewById<Button>(R.id.btnSignIn)
        val signUp = findViewById<TextView>(R.id.txtSignUp)
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Users")

        // After retrieving data from the frontend, we use if statements to authenticate the user.
        login.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            // Check if the child key and password exist
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.hasChild(emailText))
                    {
                        //Getting values from real time database of email and password
                        val childSnapshot = dataSnapshot.child(emailText)
                        val storedPassword = childSnapshot.child("password").getValue(String::class.java)

                        //comparing password submitted by user and password inside database
                        if (passwordText == storedPassword)
                        {
                            //If password match user will log in and be taken to user page
                            val intent = Intent(this@Login, MainActivity::class.java)
                            intent.putExtra("useremail",emailText)
                            startActivity(intent)
                            Toast.makeText(applicationContext, "Login successful", Toast.LENGTH_LONG).show()

                        }
                        else
                        {
                            // Password does not match
                            Toast.makeText(applicationContext, "Incorrect password", Toast.LENGTH_LONG).show()
                        }
                    }
                    else
                    {
                        // Child key does not exist
                        Toast.makeText(applicationContext, "User not found", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Exception handling
                    Toast.makeText(applicationContext, "Error: ${databaseError.message}", Toast.LENGTH_LONG).show()

                }
            })
        }


        // If the user selects the sign up TextView, they will be redirected to the sign-up UI
        signUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}
//--------------------------------------ooo000EndOfFile000ooo---------------------------------------