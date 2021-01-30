package com.example.avjindersinghsekhon.minimaltodo.Reminder

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu

import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.avjindersinghsekhon.minimaltodo.utility.*
import androidx.appcompat.widget.Toolbar
import com.example.avjindersinghsekhon.minimaltodo.Analytics.AnalyticsApplication
import com.example.avjindersinghsekhon.minimaltodo.AppDefault.AppDefaultFragment
import com.example.avjindersinghsekhon.minimaltodo.Main.MainActivity
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.utility.StoreRetrieveData
import com.example.avjindersinghsekhon.minimaltodo.utility.ToDoItem
import com.example.avjindersinghsekhon.minimaltodo.utility.TodoNotificationService
import fr.ganfra.materialspinner.MaterialSpinner
import org.json.JSONException
import java.io.IOException
import java.util.*

class ReminderFragment : AppDefaultFragment() {
    private var mtoDoTextTextView: TextView? = null
    private var mRemoveToDoButton: Button? = null
    private var mSnoozeSpinner: MaterialSpinner? = null
    private var snoozeOptionsArray: Array<String>? = null
    private var storeRetrieveData: StoreRetrieveData? = null
    private var mToDoItems: ArrayList<ToDoItem?>? = null
    private var mItem: ToDoItem? = null
    private var mSnoozeTextView: TextView? = null
    var theme: String? = null
    var app: AnalyticsApplication? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app = requireActivity().application as AnalyticsApplication
        app!!.send(this)
        theme = requireActivity().getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE).getString(THEME_SAVED, LIGHTTHEME)
        if (theme == LIGHTTHEME) {
            requireActivity().setTheme(R.style.CustomStyle_LightTheme)
        } else {
            requireActivity().setTheme(R.style.CustomStyle_DarkTheme)
        }
        storeRetrieveData = StoreRetrieveData(context, FILENAME)
        //mToDoItems = getLocallyStoredData(storeRetrieveData)
        (activity as AppCompatActivity?)!!.setSupportActionBar(view.findViewById<View>(R.id.toolbar) as Toolbar)
        val i = requireActivity().intent
        val id = i.getSerializableExtra(TodoNotificationService.TODOUUID) as UUID
        mItem = null
        for (toDoItem in mToDoItems!!) {
            if (toDoItem!!.identifier == id) {
                mItem = toDoItem
                break
            }
        }
        snoozeOptionsArray = resources.getStringArray(R.array.snooze_options)
        mRemoveToDoButton = view.findViewById<View>(R.id.toDoReminderRemoveButton) as Button
        mtoDoTextTextView = view.findViewById<View>(R.id.toDoReminderTextViewBody) as TextView
        mSnoozeTextView = view.findViewById<View>(R.id.reminderViewSnoozeTextView) as TextView
        mSnoozeSpinner = view.findViewById<View>(R.id.todoReminderSnoozeSpinner) as MaterialSpinner

//        mtoDoTextTextView.setBackgroundColor(item.getTodoColor());
        mtoDoTextTextView!!.text = mItem!!.toDoText
        if (theme == LIGHTTHEME) {
            mSnoozeTextView!!.setTextColor(resources.getColor(R.color.secondary_text))
        } else {
            mSnoozeTextView!!.setTextColor(Color.WHITE)
            mSnoozeTextView!!.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_snooze_white_24dp, 0, 0, 0
            )
        }
        mRemoveToDoButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                app!!.send(this, "Action", "Todo Removed from Reminder Activity")
                mToDoItems!!.remove(mItem)
                changeOccurred()
                saveData()
                closeApp()
                //                finish();
            }
        })


//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, snoozeOptionsArray);
        val adapter = ArrayAdapter(context, R.layout.spinner_text_view, snoozeOptionsArray)
        //        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        mSnoozeSpinner!!.adapter = adapter
        //        mSnoozeSpinner.setSelection(0);
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_reminder
    }

    private fun closeApp() {
        val i = Intent(context, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        //        i.putExtra(EXIT, true);
        val sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(EXIT, true)
        editor.apply()
        startActivity(i)
    }

    fun onCreateOptionsMenu(menu: Menu?): Boolean {
        requireActivity().menuInflater.inflate(R.menu.menu_reminder, menu)
        return true
    }

    private fun changeOccurred() {
        val sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(CHANGE_OCCURED, true)
        //        editor.commit();
        editor.apply()
    }

    private fun addTimeToDate(mins: Int): Date {
        app!!.send(this, "Action", "Snoozed", "For $mins minutes")
        val date = Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MINUTE, mins)
        return calendar.time
    }

    private fun valueFromSpinner(): Int {
        return when (mSnoozeSpinner!!.selectedItemPosition) {
            0 -> 10
            1 -> 30
            2 -> 60
            else -> 0
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toDoReminderDoneMenuItem -> {
                val date = addTimeToDate(valueFromSpinner())
                mItem!!.toDoDate = date
                mItem!!.setHasReminder(true)
                Log.d("OskarSchindler", "Date Changed to: $date")
                changeOccurred()
                saveData()
                closeApp()
                //foo
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveData() {
        try {
            storeRetrieveData!!.saveToFile(mToDoItems)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val EXIT = "com.avjindersekhon.exit"
        @JvmStatic
        fun newInstance(): ReminderFragment {
            return ReminderFragment()
        }
    }
}