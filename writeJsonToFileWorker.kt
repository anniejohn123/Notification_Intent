package com.example.quoraapplication.ui.theme

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.quoraapplication.Datamanager
import com.example.quoraapplication.models.Quote
import com.google.gson.Gson
import java.io.File


class writeJsonToFileWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val jsonString = inputData.getString("jsonString")?:return Result.failure()
        return try{
            writeJsonToFile(applicationContext,jsonString,"data1.json")
            Log.d("WorkerClass", jsonString)
            Result.success()


        }catch (e:Exception){
            Result.failure()

        }




    }

    private fun writeJsonToFile(applicationContext: Context, jsonString: String, fileName: String) {
        try {

            val fileOutputStream = applicationContext.openFileOutput(fileName,Context.MODE_PRIVATE)
            fileOutputStream.write(jsonString.toByteArray())
            fileOutputStream.close()
            Log.d("writeJsonToFile", fileOutputStream.toString())

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}