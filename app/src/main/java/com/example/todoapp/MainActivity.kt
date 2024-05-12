package com.example.todoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.appcompat.widget.SearchView
import com.example.todoapp.databinding.ActivityMainBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


//entity-table
//dao-queries
//database

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: myDatabase
    private lateinit var adapter: Adapter
    private var taskList: List<CardInfo> = emptyList()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database= Room.databaseBuilder(
            applicationContext,myDatabase::class.java,"To_Do"
        ).build()

        adapter = Adapter(taskList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.add.setOnClickListener{
            val intent= Intent(this, CreateCard::class.java)
            startActivity(intent)
        }

        binding.deleteAll.setOnClickListener{
            DataObject.deleteAll()
            GlobalScope.launch{
                database.dao().deleteAll()
                loadTaskList()
            }
            setRecycler()
        }
        loadTaskList()
        setRecycler()
        setupSearchView()


    }
    private fun setRecycler(){
        binding.recyclerView.adapter = Adapter(DataObject.getAllData())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

    }
    @OptIn(DelicateCoroutinesApi::class)
    private fun loadTaskList() {
        GlobalScope.launch {
            taskList = database.dao().getTasks()
            withContext(Dispatchers.Main) {
                adapter.setData(taskList)
            }
        }
    }

    // In MainActivity.kt
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filterTaskList(it)
                }
                return true
            }
        })
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun filterTaskList(query: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val filteredList = database.dao().searchTasks(query)
            setRecyclerWithFilter(filteredList)
        }
    }

    private fun setRecyclerWithFilter(filteredList: List<CardInfo>) {
        binding.recyclerView.adapter = Adapter(filteredList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
}








