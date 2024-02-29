package com.example.quoraapplication

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.quoraapplication.ui.theme.writeJsonToFileWorker
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

private val CHANNEL_ID = "My_Notofication_101"
private val NOTIFICATION_ID = 100

class MainActivity2 : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
//    private val CHANNEL_ID = "My_Notofication_101"
//    private val NOTIFICATION_ID = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sampleCompanies: List<Company>? = intent.getSerializableExtra("Data") as? List<Company>
        val text = intent.getStringExtra("test")
        val jsonString = Gson().toJson(sampleCompanies)

        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<writeJsonToFileWorker>().setInputData(
                workDataOf("jsonString" to jsonString)
            ).build()
        WorkManager.getInstance(this).enqueue(uploadWorkRequest)



        setContent {
            Column {
                sampleCompanies?.forEach { company ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(company.name)
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(company.deadline)
                    }

                }

            }
            createNotificationChannel()
//            sendNotification()
//            sendNotification1()
            scheduleNotification(sampleCompanies)
//


        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleNotification(companies: List<Company>?) {
        Log.d("Notif","Hello")
        companies?.forEach { company ->
            val deadlineMillis = LocalDateTime.parse(company.deadline).toEpochSecond(
                ZoneOffset.UTC
            ) * 1000
            val currentTimeMillis = System.currentTimeMillis()
            val differenceMillis = deadlineMillis - currentTimeMillis
            if (differenceMillis > 0) {
                val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(
                        differenceMillis - TimeUnit.HOURS.toMillis(1),
                        TimeUnit.MILLISECONDS
                    )
                    .build()
                WorkManager.getInstance(this).enqueue(notificationWorkRequest)
                Log.d("Worker","Worker running ${TimeUnit.MILLISECONDS.toMinutes(differenceMillis - TimeUnit.HOURS.toMillis(1))}")


            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = "Shazra"
            val descriptionText = "Describing the notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNotification1() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Example notification")
            .setContentText("Hello describing")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            1001
        )
        Log.d(
            "Permission", ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ).toString()
        )

        with(NotificationManagerCompat.from(this)) {

            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                Log.d("sendNotification1", "Hello")
                return
            }
            Log.d("sendNotification", "Hello")
            notify(NOTIFICATION_ID, builder.build())
        }
    }


}


class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        Log.d("test","I am in do ")
        sendNotification()



        return Result.success()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNotification() {
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Example notification")
            .setContentText("Hello describing")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//            1001
//        )
//        Log.d(
//            "Permission", ActivityCompat.checkSelfPermission(
//                applicationContext,
//                Manifest.permission.POST_NOTIFICATIONS
//            ).toString()
//        )

        with(NotificationManagerCompat.from(applicationContext)) {

//            if (ActivityCompat.checkSelfPermission(
//                    applicationContext,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//
//                Log.d("sendNotification1", "Hello")
//                return
//            }
//            Log.d("sendNotification", "Hello")
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("Notifier1","This is for test")
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            Log.d("Notifier","This is for test")
            notify(NOTIFICATION_ID, builder.build())
        }
    }
}


