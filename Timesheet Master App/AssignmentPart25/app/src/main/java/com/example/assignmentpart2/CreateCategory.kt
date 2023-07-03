package com.example.assignmentpart2

import android.Manifest
import android.app.DatePickerDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.assignmentpart2.databinding.ActivityCreateCategoryBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*


class CreateCategory : AppCompatActivity() {

    private lateinit var binding: ActivityCreateCategoryBinding
    private lateinit var database: DatabaseReference
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = "package com.example.assignmentpart2"
    private val description = "Test notification"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        /*
        This activity will allow the user to create a category that will be stored in the database and allow a user to add
        entries dependent on the specific category selected in CreateEntry UI
         */

       // val simpleCalendarView = findViewById<View>(R.id.calendar1) as CalendarView // get the reference of CalendarView
        val simpleCalendarView = binding.calendar1
        val selectedDate = simpleCalendarView.date // get selected date in milliseconds
        val saveButton = findViewById<Button>(R.id.btnCreate)
        val title = findViewById<EditText>(R.id.edtxtCateName)
        val descriptionCat = findViewById<EditText>(R.id.edtxtCatDescription)
        val minGoal = findViewById<EditText>(R.id.edtxtMinGoal)
        val maxGoal = findViewById<EditText>(R.id.edtxtMaxGoal)
        val email = intent.getStringExtra("useremail")
        var dpc: String = ""


        simpleCalendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Format the selected date as a string
            val formattedMonth = month + 1 // Increment the month by 1 to match the correct value
            val todayDate = "$dayOfMonth/$formattedMonth/$year"
            Log.d("date", todayDate)

            dpc = todayDate // Store the selected date in the dpc variable
            // date.setText(todayDate)
        }


        // After the user clicks the save button, data will be sent to the database
        saveButton.setOnClickListener {

            val name = title.text.toString()
            val description = descriptionCat.text.toString()
            val minimumGoal = minGoal.text.toString().toIntOrNull()
            val maximumGoal = maxGoal.text.toString().toIntOrNull()
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Setting the format of the date
            val dateString = simpleDateFormat.format(Date(selectedDate))
            //val selectedDate = returnDate(binding.calendar1)

            if (name.isNotEmpty() && description.isNotEmpty() && minimumGoal != null && maximumGoal != null) {
                val cat = Categories(name, description, minimumGoal, maximumGoal, dpc, email.toString())
                database = FirebaseDatabase.getInstance().getReference("Categories")
                database.child(name).setValue(cat)
                    .addOnSuccessListener {
                        //Calling the send notification function
                    sendNotification()

                        //clearing the text fields.
                        Toast.makeText(applicationContext, "Category: $name created", Toast.LENGTH_LONG).show()
                        binding.edtxtCateName.text.clear()
                        binding.edtxtCatDescription.text.clear()
                        binding.edtxtMinGoal.text.clear()
                        binding.edtxtMaxGoal.text.clear()
                    }
                    .addOnFailureListener{
                        Toast.makeText(applicationContext, "Failed to create category", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(applicationContext, "Please fill in all fields before continuing", Toast.LENGTH_LONG).show()
            }
        }

        // Handle event clicking of the navigation bar
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("useremail", email)
                    startActivity(intent)
                }
                R.id.create -> {
                    val intent = Intent(this, Create::class.java)
                    intent.putExtra("useremail", email)
                    startActivity(intent)
                }
                R.id.display -> {
                    val intent = Intent(this, Display::class.java)
                    intent.putExtra("useremail", email)
                    startActivity(intent)
                }
                R.id.signOut -> {
                    val intent = Intent(this, SignOut::class.java)
                    intent.putExtra("useremail", email)
                    startActivity(intent)
                }
                else -> {}
            }
            true
        }
    }

fun sendNotification ()
{
    val intent = Intent(this,NotificationLayout::class.java)
    val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)

    //val contentView = RemoteViews(packageName,R.layout.notification_layout)
   // contentView.setTextViewText(R.id.tv_title,"CodeAndroid")
   // contentView.setTextViewText(R.id.tv_content,"Text notification")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationChannel = NotificationChannel(channelId,description,NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)

        builder = Notification.Builder(this,channelId)
            .setContentTitle("CONGRATULATIONS")
            .setContentText("Notification from TIMESHEET MASTER")
            .setSmallIcon(R.drawable.logo_high_opacity)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.logo_high_opacity))
            .setContentIntent(pendingIntent)
    }
    else
    {

        builder = Notification.Builder(this)
            .setContentTitle("CONGRATULATIONS")
            .setContentText("Notification from TIMESHEET MASTER")
            .setSmallIcon(R.drawable.logo_high_opacity)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.logo_high_opacity))
            .setContentIntent(pendingIntent)
    }
    notificationManager.notify(1234,builder.build())
}




}




//--------------------------------------ooo000EndOfFile000ooo---------------------------------------