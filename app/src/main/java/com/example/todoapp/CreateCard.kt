package com.example.todoapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.todoapp.databinding.ActivityCreateCardBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar
import android.app.TimePickerDialog
import java.util.Locale

class CreateCard : AppCompatActivity() {
    private lateinit var binding: ActivityCreateCardBinding
    private lateinit var database: myDatabase
    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0
    private var selectedHour = 0
    private var selectedMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Room.databaseBuilder(
            applicationContext, myDatabase::class.java, "To_Do"
        ).build()

        binding.createDate.setOnClickListener {
            showDatePicker()
        }
        binding.createTime.setOnClickListener {
            showTimePicker()
        }

        binding.saveButton.setOnClickListener {
            val title = binding.createTitle.text.toString().trim()
            val priority = binding.createPriority.text.toString().trim()
            val time = binding.createTime.text.toString().trim()

            val date = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"

            if (title.isNotEmpty() && priority.isNotEmpty()) {
                DataObject.setData(title, priority, date, time)
                GlobalScope.launch {
                    database.dao().insertTask(Entity(0, title, priority, date, time))
                    Log.i("database", database.dao().getTasks().toString())
                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedYear = year
                selectedMonth = month
                selectedDay = dayOfMonth

                // Update the EditText with the selected date
                val selectedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                binding.createDate.setText(selectedDate)
            },
            currentYear,
            currentMonth,
            currentDay
        )

        // Show the DatePickerDialog
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                selectedHour = hourOfDay
                selectedMinute = minute

                // Update the EditText with the selected time
                val selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                binding.createTime.setText(selectedTime)
            },
            currentHour,
            currentMinute,
            true // 24-hour format
        )

        timePickerDialog.show()
    }

}
