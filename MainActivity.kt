package com.example.quoraapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Room
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.example.quoraapplication.Screen.CreateQuoteList
import com.example.quoraapplication.Screen.QuoteDetail

import com.example.quoraapplication.models.Quote
import com.example.quoraapplication.ui.theme.writeJsonToFileWorker
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable


class MainActivity : ComponentActivity() {
    lateinit var database: ContactDatabase
    fun getData(){
        database.contactDao().getContact().observe(this,{
            Log.d("Data",it.toString())
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Datamanager.main(this)

//        for Database creation not effiecientway
//        database =
//            Room.databaseBuilder(applicationContext, ContactDatabase::class.java, "contactDB")
//                .build()

        database = ContactDatabase.getDatabse(this)

        GlobalScope.launch {
            database.contactDao().insertContact(Contact(0,"shazra","1234"))
        }








        setContent {
            Button(onClick = { getData() }) {
                
            }
            
////            quoteListShow(Datamanager.data){
//
//            }
//            MainScreen(this)
        }
    }

    fun newScreen(sampleCompanies: List<Company>) {

        val intent = Intent(this, MainActivity2::class.java)
        intent.putExtra("Data", sampleCompanies as Serializable)
        intent.putExtra("test", "hello")
        startActivity(intent)
    }
}


@Composable
fun MainScreen(context: Context) {
    var count by remember {
        mutableStateOf<Int>(0)
    }
    var sampleCompanies by remember {
        mutableStateOf(
            listOf(
                Company(
                    id = 1,
                    name = "Tech Innovations Inc.",
                    deadline = "2024-02-29T13:00:00",
                    studentList = listOf(
                        Student(
                            name = "Alice Johnson",
                            data = "Computer Science",
                            isEligible = true
                        ),
                        Student(
                            name = "Bob Smith",
                            data = "Information Technology",
                            isEligible = false
                        ),
                        Student(
                            name = "Charlie Davis",
                            data = "Software Engineering",
                            isEligible = true
                        )
                    )
                ),
                Company(
                    id = 2,
                    name = "Health Solutions Ltd.",
                    deadline = "2024-03-29T17:22:30",
                    studentList = listOf(
                        Student(
                            name = "Diana Adams",
                            data = "Biomedical Engineering",
                            isEligible = true
                        ),
                        Student(
                            name = "Eric Brown",
                            data = "Health Informatics",
                            isEligible = false
                        ),
                        Student(name = "Fiona Clarke", data = "Nursing", isEligible = true)
                    )
                ),
                Company(
                    id = 3,
                    name = "Eco Builders Co.",
                    deadline = "2024-02-29T17:52:30",
                    studentList = listOf(
                        Student(
                            name = "George Davis",
                            data = "Civil Engineering",
                            isEligible = true
                        ),
                        Student(name = "Hannah Evans", data = "Architecture", isEligible = false),
                        Student(
                            name = "Ian Frank",
                            data = "Environmental Science",
                            isEligible = true
                        )
                    )
                )
            )


        )
    }



    AdminView(
        sampleCompanies,
        addCompany = {
            count = count + 1
            var newCompany = Company(
                id = 4 + count, name = "Shazra${count}", deadline = "12feb", studentList = listOf(
                    Student(name = "shazra${count}", data = "doc", isEligible = true)
                )
            )
            sampleCompanies = sampleCompanies + newCompany


        },
        context = context
    )
}


data class Company(
    val id: Int,
    val name: String,
    val deadline: String,
    val studentList: List<Student>
) : Serializable

data class Student(val name: String, val data: String, val isEligible: Boolean) : Serializable


@Composable
fun AdminView(companyList: List<Company>, addCompany: () -> Unit, context: Context) {
    var selectedCompany by remember {
        mutableStateOf<Int?>(null)
    }
    Column {
        Button(onClick = addCompany) {
            Text("Add Company")
        }
        Button(onClick = { (context as MainActivity).newScreen(companyList) }) {
            Text("Move to new activity")
        }
        LazyColumn {
            items(companyList) { company ->
                companyItem(company, expanded = (selectedCompany == company.id)) {
                    selectedCompany = if (company.id == selectedCompany) null else company.id

                }
            }
        }
    }


}


@Composable
fun companyItem(company: Company, expanded: Boolean, OnClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            elevation = 8.dp, modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = OnClick)
        ) {
            Text(
                text = company.name,
                modifier = Modifier
                    .padding(8.dp)
            )
        }

        if (expanded) {

            company.studentList.forEach { student ->
                if (student.isEligible) {
                    Text(text = student.name)
                }

            }
        }

    }
}


@Composable
fun quoteListShow(data: Array<Quote>, OnClick: (quote1: Quote) -> Unit) {
    if (Datamanager.isLoaded.value) {
        if (Datamanager.currentPage.value == Pages.LISTING) {
            LazyColumn {
                items(data) { item ->
                    CreateQuoteList(item) {
                        Datamanager.switchPage(it)
                    }
                }
            }
        } else {
            Datamanager.curentQuote?.let { QuoteDetail(it) }

        }

    } else {
        Text("Loading")
    }

}

enum class Pages {
    LISTING,
    DETAIL
}
