package com.labs.lc.lab1

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.view.View
import android.content.Intent
import android.net.Uri
import android.widget.EditText
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import android.support.v4.app.NotificationManagerCompat






class MainActivity : AppCompatActivity() {

    lateinit var editText : EditText

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"//getString(R.string.channel_name)
            val description = "description"//getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("5", name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel();
    }

    fun sendNotification(v: View) {
        val mBuilder = NotificationCompat.Builder(this, "5")
                .setSmallIcon(R.drawable.notification_icon_background)
                .setContentTitle("title")
                .setContentText("content")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notificationManager = NotificationManagerCompat.from(this)

        notificationManager.notify(93, mBuilder.build())
    }

    fun search(v: View) {
        editText = findViewById<View>(R.id.editText) as EditText
        val uri = Uri.parse("https://www.google.com/search?q=" + editText.text.toString())
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

}
