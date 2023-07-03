package com.example.assignmentpart2

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.CalendarView.OnDateChangeListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignmentpart2.databinding.ActivityCreateBinding
import com.example.assignmentpart2.databinding.ActivityDisplayEntryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class DisplayEntry : AppCompatActivity() {
    
    private lateinit var binding: ActivityDisplayEntryBinding

    private lateinit var btnTimer: Button
    private lateinit var btnCalendar: Button
    private lateinit var tvDisplayEntry: TextView

    private var firebaseDatabase: FirebaseDatabase? = null
    private var databaseReference: DatabaseReference? = null
    private var adaptar: EntryAdaptar? = null
    private var list = mutableListOf<Entry>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDisplayEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnTimer = findViewById(R.id.btnTimer)
        btnCalendar = findViewById(R.id.btnCalender)
        //tvDisplayEntry = findViewById(R.id.tvEntryDisplay)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase?.getReference("entries")

        initRecycleView()
        getEntries()

        val userId = intent.getStringExtra("useremail") //You need this in order to access data for a specif userID

        //******************************************************************************************
        //Timer button
        btnTimer.setOnClickListener {
            val intent = Intent(this, CreateEntry::class.java)
            intent.putExtra("useremail",userId)
            startActivity(intent)
        }
        //******************************************************************************************
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
    private fun initRecycleView(){
        adaptar = EntryAdaptar()
        binding.apply {
            tvEntryDisplay.layoutManager = LinearLayoutManager(this@DisplayEntry)
            tvEntryDisplay.adapter = adaptar
        }
    }
    //******************************************************************************************
    //Get Values in Database
    private fun getEntries(){
        databaseReference?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // Log.e("00000", "onDataChange: $snapshot")
                list.clear()
                for (i in snapshot.children){
                    val itEntryName = i.key
                    //val itEntryName = i.child("itEntryName").value.toString()
                    val itEntryDesc = i.child("itEntryDesc").value.toString()
                    val itEntryCategory = i.child("itEntryCategory").value.toString()
                    val itEntryTime = i.child("itEntryTime").value.toString()
                    val itEntryDate = i.child("itEntryDate").value.toString()
                    val itEntryImage = i.child("itEntryImage").value.toString()

                    val entry = Entry(itEntryName = "Entry Name: "+itEntryName, itEntryDesc ="Description: "+itEntryDesc, itEntryCategory = "Category: "+itEntryCategory, itEntryTime = "Time: "+itEntryTime, itEntryDate = itEntryDate, itEntryImage = itEntryImage)
                    list.add(entry)
                }
                Log.e("0000", "size: ${list.size}")
                adaptar?.setItem(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("0000", "onCancelled: ${error.toException()}")
            }
        })
    }
}
//--------------------------------------ooo000EndOfFile000ooo---------------------------------------