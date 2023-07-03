package com.example.assignmentpart2

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.assignmentpart2.databinding.ActivitySignUpBinding
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userName = binding.edtSignUpFirstName
        val userSurname = binding.edtSignUpLastName
        val userEmail = binding.edtSignUpEmail
        val userPassword = binding.edtSignUpPassword
        val confirmPassword = binding.edtSignUpConfirmPassword
        val signUp = binding.btnSignUp
        val login = binding.txtSignUp
        database = FirebaseDatabase.getInstance().reference.child("Users")

        signUp.setOnClickListener {
            if (TextUtils.isEmpty(userName.text.toString()) || TextUtils.isEmpty(userSurname.text.toString()) || TextUtils.isEmpty(userEmail.text.toString()) || TextUtils.isEmpty(userPassword.text.toString()) || TextUtils.isEmpty(confirmPassword.text.toString()))
            {
                Toast.makeText(applicationContext, "Please fill in all fields before continuing", Toast.LENGTH_LONG).show()
            }
            else
            {
                if (userPassword.text.toString().equals(confirmPassword.text.toString())) {
                    database.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.hasChild(userEmail.text.toString()))
                            {
                                Toast.makeText(applicationContext, "Username already exists", Toast.LENGTH_LONG).show()
                            }
                            else
                            {
                                val name = userName.text.toString()
                                val surname = userSurname.text.toString()
                                val email = userEmail.text.toString()
                                val password = userPassword.text.toString()

                                val user = User(email, name, surname, password)
                                database.child(email).setValue(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(applicationContext, "Registration successful", Toast.LENGTH_LONG)
                                            .show()
                                        val intent = Intent(this@SignUp, Login::class.java)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(applicationContext, "Registration failed", Toast.LENGTH_LONG)
                                            .show()
                                    }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(applicationContext, "Error: ${databaseError.message}", Toast.LENGTH_LONG).show()
                        }
                    })
                }
                else
                {
                    Toast.makeText(applicationContext, "Passwords do not match", Toast.LENGTH_LONG).show()
                }
            }
        }

        login.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}
//--------------------------------------ooo000EndOfFile000ooo---------------------------------------