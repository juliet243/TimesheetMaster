package com.example.assignmentpart2

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.*
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.util.Pair
import com.example.assignmentpart2.databinding.ActivityCreateEntryBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CreateEntry : AppCompatActivity() {

    private lateinit var binding: ActivityCreateEntryBinding
    private lateinit var database: DatabaseReference

    private lateinit var spinner: Spinner

    private lateinit var btnCalendar: Button
    private lateinit var tvDisplayDate: TextView
    private var timerStared = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0
    var stringImage: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("useremail") //You need this in order to access data for a specif userID
        binding.btnStart.setOnClickListener{ startStopTimer()}
        binding.btnReset.setOnClickListener { resetTimer() }

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TimerUpdated))

        val saveButton = findViewById<Button>(R.id.btnSave)
        tvDisplayDate = findViewById(R.id.tvDisplayedDate)
        btnCalendar = findViewById(R.id.btnCalender)

        database = FirebaseDatabase.getInstance().reference.child("Categories")

        //Spinner
        spinner = findViewById(R.id.spinCatDescription)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                // Handle the selected item
                Log.d("Selected Item", selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle when nothing is selected
            }
        }
        populateSpinner()

        // saveButton.setOnClickListener { setEntryValues(view) }
        //******************************************************************************************
        //Clendar Method
        //******************************************************************************************
        btnCalendar.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTheme(R.style.ThemeMaterialCalendar)
                .setTitleText("Select Date For Entry")
                .setSelection(Pair(null, null))
                .build()

            datePicker.show(this.supportFragmentManager, "TAG")

            datePicker.addOnPositiveButtonClickListener {
                tvDisplayDate.setText(convertTimeToDate(it.first) + " - " + convertTimeToDate(it.second))
            }
            datePicker.addOnNegativeButtonClickListener{
                datePicker.dismiss()
            }

        }
        //******************************************************************************************

        //Method to Take and Store Image
        //***************************************************************************
        val getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null)
            {
                val bitmap = it.data!!.extras?.get("data") as Bitmap
                binding.camImage.setImageBitmap(bitmap)
            }
        }

        binding.btnCamera.setOnClickListener() {
            //var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //getResult.launch(intent)
            //picture
            var myfileintent = Intent(Intent.ACTION_GET_CONTENT)
            myfileintent.setType("image/*")
            ActivityResultLauncher.launch(myfileintent)
        }
        //******************************************************************************
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
    //******************************************************************************************
    //Set Values in Database
    fun setEntryValues(view: View){
        val intent = Intent(this, Create::class.java)
        val itEntryName = binding.edtxtCateName.text.toString()
        val itEntryTime = binding.tvDispTimer.text.toString()
        val itEntryDate = binding.tvDisplayedDate.text.toString()
        val itEntryDesc = binding.edtxtEntryDesc.text.toString()
        val itEntryImage = binding.camImage
        var itSelectedCategory: String = ""

        // Get selected item from spinner
        val selectedCategory = spinner.selectedItem as? String
        if (selectedCategory != null) {
            itSelectedCategory = selectedCategory
        }


        database = FirebaseDatabase.getInstance().getReference("entries")
        val entrys = Entry(itEntryName, itEntryDesc,itSelectedCategory.toString(), itEntryTime, itEntryDate, stringImage)
        val databaseReference = FirebaseDatabase.getInstance().reference
       // val id = databaseReference.push().key
        database.child(itEntryName).setValue(entrys).addOnSuccessListener {
            binding.edtxtCateName.text.clear()
            binding.edtxtEntryDesc.text.clear()
            stringImage=""
            Toast.makeText(this, "Well Done You Have Done An Entry!!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Entry Failed, Try Again :__(", Toast.LENGTH_SHORT).show()
        }
        startActivity(intent)
    }
    //*************************************************************************************
    //Input Image to Database
    private val ActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ){result:ActivityResult ->
        if (result.resultCode== RESULT_OK){
            val uri = result.data!!.data
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val myBitmap =BitmapFactory.decodeStream(inputStream)
                val stream =ByteArrayOutputStream()
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100,stream)
                val bytes = stream.toByteArray()
                stringImage = Base64.encodeToString(bytes, Base64.DEFAULT)
                binding.camImage.setImageBitmap(myBitmap)
                inputStream!!.close()
            } catch (ex: Exception) {
                Toast.makeText(this, ex.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
    //*************************************************************************************
    //Spinner
    private fun populateSpinner() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val childKeys: MutableList<String> = mutableListOf()

                for (childSnapshot in dataSnapshot.children) {
                    childKeys.add(childSnapshot.key.toString())
                }

                val adapter = ArrayAdapter(this@CreateEntry, android.R.layout.simple_spinner_item, childKeys)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                Log.e("DatabaseError", databaseError.message)
            }
        })
    }
    //*************************************************************************************
    //Calendar
    private fun convertTimeToDate(time: Long) : String {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utc.timeInMillis = time
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return format.format(utc.time)
    }
    //Timer Methods Bellow
    //*************************************************************************
    private fun resetTimer() {
        stopTimer()
        time = 0.0
        binding.tvDispTimer.text = getTimeStringFromDouble(time)
    }

    private fun startStopTimer() {
        if(timerStared)
            stopTimer()
        else
            startTimer()
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TimeExtra, time)
        startService(serviceIntent )
        binding.btnStart.text = "Stop"
        timerStared = true
    }

    private fun stopTimer() {
        stopService(serviceIntent)
        binding.btnStart.text = "Start"
        timerStared = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TimeExtra, 0.0)
            binding.tvDispTimer.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String
    {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hrs: Int,min: Int,sec: Int): String = String.format("%02d:%02d:%02d", hrs, min, sec)

}
//--------------------------------------ooo000EndOfFile000ooo---------------------------------------