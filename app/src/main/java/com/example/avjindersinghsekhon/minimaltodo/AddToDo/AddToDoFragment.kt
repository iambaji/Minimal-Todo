package com.example.avjindersinghsekhon.minimaltodo.AddToDo

import android.animation.Animator
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.avjindersinghsekhon.minimaltodo.AlarmManager
import com.example.avjindersinghsekhon.minimaltodo.Analytics.AnalyticsApplication
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.database.ToDo
import com.example.avjindersinghsekhon.minimaltodo.utility.*
import com.example.avjindersinghsekhon.minimaltodo.databinding.FragmentAddToDoBinding
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_reminder.*
import java.text.SimpleDateFormat
import java.util.*

class AddToDoFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

   

    var toDoItem : ToDo?  = null
 

    private var mUserHasReminder = false
    var shouldUpdate : Boolean = false

    var mUserReminderDate : Date? = null
    private var mUserColor = 0


    private var theme: String? = null
    lateinit var app : AnalyticsApplication

    lateinit var binding : FragmentAddToDoBinding
    private val viewmodel : AddToDoViewModel by viewModels {
        AddToDoViewModelFactory((requireActivity().application as AnalyticsApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        app = requireActivity().application as AnalyticsApplication
        binding = FragmentAddToDoBinding.inflate(inflater,container,false)

        theme = requireActivity().getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE).getString(THEME_SAVED, LIGHTTHEME)
        if (theme == LIGHTTHEME) {
            requireActivity().setTheme(R.style.CustomStyle_LightTheme)
            Log.d("OskarSchindler", "Light Theme")
        } else {
            requireActivity().setTheme(R.style.CustomStyle_DarkTheme)
        }


        //Show an X in place of <-
        val cross = resources.getDrawable(R.drawable.ic_clear_white_24dp)
        cross?.setColorFilter(resources.getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP)
        val mToolbar = binding.toolbar
        (activity as AppCompatActivity?)!!.setSupportActionBar(mToolbar)
        if ((activity as AppCompatActivity?)!!.supportActionBar != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.elevation = 0f
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            (activity as AppCompatActivity?)!!.supportActionBar!!.setHomeAsUpIndicator(cross)
        }
        val itemid = requireActivity().intent.getIntExtra(TODOITEM,-1) as Int

        if(itemid == -1)
        {
            toDoItem = ToDo()
            binding.toDoHasDateSwitchCompat.isChecked = false
            setEnterDateLayoutVisible(binding.toDoHasDateSwitchCompat.isChecked)
            setEnterDateLayoutVisibleWithAnimations(binding.toDoHasDateSwitchCompat.isChecked)
        }
        else{
          viewmodel.getItem(itemid).observe(viewLifecycleOwner){

              toDoItem = it
              shouldUpdate = true

              binding.userToDoEditText.setText(toDoItem?.toDoTitle)
              binding.userToDoDescription.setText(toDoItem?.toDoDescription)
              binding.toDoHasDateSwitchCompat.isChecked = toDoItem?.hasReminder == true
              setEnterDateLayoutVisible(binding.toDoHasDateSwitchCompat.isChecked)
              setEnterDateLayoutVisibleWithAnimations(binding.toDoHasDateSwitchCompat.isChecked)

          }

        }


        binding.apply {

            if (theme == DARKTHEME) {

                userToDoReminderIconImageButton.setImageDrawable(resources.getDrawable(R.drawable.ic_alarm_add_white_24dp))
                userToDoRemindMeTextView.setTextColor(Color.WHITE)
            }





            //OnClickListener for CopyClipboard Button

            copyclipboard.setOnClickListener {
                val toDoTextContainer = userToDoEditText.text
                val toDoTextBodyDescriptionContainer = userToDoDescription.text
                val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val combinationText = "Title : $toDoTextContainer\nDescription : $toDoTextBodyDescriptionContainer\n -Copied From MinimalToDo"
                val clip = ClipData.newPlainText("text", combinationText)
                clipboard.primaryClip = clip
                Toast.makeText(context, "Copied To Clipboard!", Toast.LENGTH_SHORT).show()
            }
            todoReminderAndDateContainerLayout.setOnClickListener {
                hideKeyboard(userToDoEditText)
                hideKeyboard(userToDoDescription)
            }





            userToDoEditText.requestFocus()

            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            //        imm.showSoftInput(userToDoEditText, InputMethodManager.SHOW_IMPLICIT);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            userToDoEditText.setSelection(userToDoEditText.length())
            userToDoEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    toDoItem?.toDoTitle = s.toString()
                }

                override fun afterTextChanged(s: Editable) {}
            })


            userToDoDescription.setSelection(userToDoDescription.length())
            userToDoDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    toDoItem?.toDoDescription = s.toString()
                }

                override fun afterTextChanged(s: Editable) {}
            })




            toDoHasDateSwitchCompat.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                    mUserHasReminder = isChecked

                    if (isChecked) {
                        app!!.send(this, "Action", "Reminder Set")

                        setDateAndTimeEditText()
                    } else {
                        mUserReminderDate = null
                        app!!.send(this, "Action", "Reminder Removed")
                    }


                    setEnterDateLayoutVisibleWithAnimations(isChecked)
                    hideKeyboard(userToDoEditText)
                    hideKeyboard(userToDoDescription)
                }
            })

            makeToDoFloatingActionButton.setOnClickListener {
                if (userToDoEditText.text.isEmpty()) {
                    userToDoEditText.error = getString(R.string.todo_error)
                } else if (toDoItem?.toDoRemindDate != null && toDoItem?.toDoRemindDate?.before(Date()) == true) {
                    app.send(this, "Action", "Date in the Past")
                    toDoHasDateSwitchCompat.error = getString(R.string.date_error_check_again)
                } else {
                    app.send(this, "Action", "Make Todo")
                    println(toDoItem?.toDoRemindDate.toString())
                     toDoItem?.apply {
                        toDoTitle = userToDoEditText.text.toString()
                        toDoDescription = userToDoDescription.text.toString()
                        hasReminder = mUserHasReminder
                         todoColor = 0
                    }



                    viewmodel.insert(toDoItem!!)
                    val alarmManager = context?.let { it1 -> AlarmManager(it1) }
                    alarmManager?.createAlarmForItem(toDoItem!!)



                    requireActivity().setResult(Activity.RESULT_OK)
                    hideKeyboard(userToDoEditText)
                    hideKeyboard(userToDoDescription)
                    requireActivity().finish()
                }
                hideKeyboard(userToDoEditText)
                hideKeyboard(userToDoDescription)
            }




            newTodoDateEditText.setOnClickListener {
                val date: Date?
                hideKeyboard(userToDoEditText)

                val calendar = Calendar.getInstance()
                val year = calendar[Calendar.YEAR]
                val month = calendar[Calendar.MONTH]
                val day = calendar[Calendar.DAY_OF_MONTH]
                val datePickerDialog = DatePickerDialog.newInstance(this@AddToDoFragment, year, month, day)
                if (theme == DARKTHEME) {
                    datePickerDialog.isThemeDark = true
                }
                datePickerDialog.show(requireActivity().fragmentManager, "DateFragment")
            }

            newTodoTimeEditText.setOnClickListener {
                val date: Date?
                hideKeyboard(userToDoEditText)
                date = if (toDoItem?.toDoRemindDate != null) {
                    toDoItem?.toDoRemindDate
                } else {
                    Date()
                }
                val calendar = Calendar.getInstance()
                calendar.time = date
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val minute = calendar[Calendar.MINUTE]
                val timePickerDialog = TimePickerDialog.newInstance(this@AddToDoFragment, hour, minute, DateFormat.is24HourFormat(context))
                if (theme == DARKTHEME) {
                    timePickerDialog.isThemeDark = true
                }
                timePickerDialog.show(requireActivity().fragmentManager, "TimeFragment")
            }




            setDateAndTimeEditText()
        }









        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun setDateAndTimeEditText() {

        this.toDoItem?.apply {
            if(this != null && this?.hasReminder && toDoRemindDate!=null)
            {
                val userDate = formatDate("d MMM, yyyy", toDoRemindDate)
                val formatToUse: String
                formatToUse = if (DateFormat.is24HourFormat(context)) {
                    "k:mm"
                } else {
                    "h:mm a"
                }
                val userTime = formatDate(formatToUse, toDoRemindDate)
                binding.newTodoTimeEditText.setText(userTime)
                binding.newTodoDateEditText.setText(userDate)
            }else{
                this.hasReminder = true
                binding.newTodoDateEditText.setText(getString(R.string.date_reminder_default))

                val time24 = DateFormat.is24HourFormat(context)
                val cal = Calendar.getInstance()
                if (time24) {
                    cal[Calendar.HOUR_OF_DAY] = cal[Calendar.HOUR_OF_DAY] + 1
                } else {
                    cal[Calendar.HOUR] = cal[Calendar.HOUR] + 1
                }
                cal[Calendar.MINUTE] = 0
                this?.toDoRemindDate = cal.time
                Log.d("OskarSchindler", "Imagined Date: $toDoRemindDate")
                val timeString: String
                timeString = if (time24) {
                    formatDate("k:mm", toDoRemindDate)
                } else {
                    formatDate("h:mm a", toDoRemindDate)
                }
                binding.newTodoTimeEditText!!.setText(timeString)

            }
        }



    }

    private val themeSet: String
        private get() = requireActivity().getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE).getString(THEME_SAVED, LIGHTTHEME)

    fun hideKeyboard(et: EditText?) {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et!!.windowToken, 0)
    }

    fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        val hour: Int
        val minute: Int

        val reminderCalendar = Calendar.getInstance()
        reminderCalendar[year, month] = day
        if (reminderCalendar.before(calendar)) {
            //    Toast.makeText(this, "My time-machine is a bit rusty", Toast.LENGTH_SHORT).show();
            return
        }
        if (toDoItem?.toDoRemindDate != null) {
            calendar.time = mUserReminderDate
        }
        hour = if (DateFormat.is24HourFormat(context)) {
            calendar[Calendar.HOUR_OF_DAY]
        } else {
            calendar[Calendar.HOUR]
        }
        minute = calendar[Calendar.MINUTE]
        calendar[year, month, day, hour] = minute
        toDoItem?.toDoRemindDate = calendar.time
        setReminderTextView()
        //        setDateAndTimeEditText();
        setDateEditText()
    }

    fun setTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        if (toDoItem?.toDoRemindDate != null) {
            calendar.time = toDoItem?.toDoRemindDate
        }
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        Log.d("OskarSchindler", "Time set: $hour")
        calendar[year, month, day, hour, minute] = 0
        toDoItem?.toDoRemindDate = calendar.time
        setReminderTextView()
        //        setDateAndTimeEditText();
        setTimeEditText()
    }

    fun formatDate(formatString: String?, dateToFormat: Date?): String {
        val simpleDateFormat = SimpleDateFormat(formatString)
        return simpleDateFormat.format(dateToFormat)
    }

    fun setDateEditText() {
        val dateFormat = "d MMM, yyyy"
        binding.newTodoDateEditText!!.setText(formatDate(dateFormat, toDoItem?.toDoRemindDate))
    }

    fun setTimeEditText() {
        val dateFormat: String
        dateFormat = if (DateFormat.is24HourFormat(context)) {
            "k:mm"
        } else {
            "h:mm a"
        }
        binding.newTodoTimeEditText.setText(formatDate(dateFormat, toDoItem?.toDoRemindDate))
    }

    fun setReminderTextView() {

        toDoItem.apply {
            if(this?.toDoRemindDate != null){

                if (this?.toDoRemindDate?.before(Date()) == true) {
                    Log.d("OskarSchindler", "DATE is $mUserReminderDate")
                    binding.newToDoDateTimeReminderTextView.text = getString(R.string.date_error_check_again)
                    binding.newToDoDateTimeReminderTextView.setTextColor(Color.RED)
                    return
                }
                val date: Date = toDoRemindDate as Date
                val dateString = formatDate("d MMM, yyyy", date)
                val timeString: String
                var amPmString = ""
                if (DateFormat.is24HourFormat(context)) {
                    timeString = formatDate("k:mm", date)
                } else {
                    timeString = formatDate("h:mm", date)
                    amPmString = formatDate("a", date)
                }
                val finalString = String.format(resources.getString(R.string.remind_date_and_time), dateString, timeString, amPmString)
                binding.newToDoDateTimeReminderTextView.setTextColor(resources.getColor(R.color.secondary_text))
                binding.newToDoDateTimeReminderTextView.text = finalString
            }
            else{
                binding.newToDoDateTimeReminderTextView.visibility = View.INVISIBLE
            }
        }

    }

    fun makeResult(result: Int) {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (NavUtils.getParentActivityName(requireActivity()) != null) {
                    app!!.send(this, "Action", "Discard Todo")

                    NavUtils.navigateUpFromSameTask(requireActivity())
                }
                hideKeyboard(binding.userToDoEditText)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onTimeSet(radialPickerLayout: RadialPickerLayout, hour: Int, minute: Int) {
        setTime(hour, minute)
    }

    override fun onDateSet(datePickerDialog: DatePickerDialog, year: Int, month: Int, day: Int) {
        setDate(year, month, day)
    }

    fun setEnterDateLayoutVisible(checked: Boolean) {
        if (checked) {
           binding.toDoEnterDateLinearLayout.visibility = View.VISIBLE
        } else {
            binding.toDoEnterDateLinearLayout.visibility = View.INVISIBLE
        }
    }

    fun setEnterDateLayoutVisibleWithAnimations(checked: Boolean) {
        if (checked) {
            setReminderTextView()
            binding.toDoEnterDateLinearLayout.animate().alpha(1.0f).setDuration(500).setListener(
                    object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {
                            binding.toDoEnterDateLinearLayout.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(animation: Animator) {}
                        override fun onAnimationCancel(animation: Animator) {}
                        override fun onAnimationRepeat(animation: Animator) {}
                    }
            )
        } else {
            binding.toDoEnterDateLinearLayout.animate().alpha(0.0f).setDuration(500).setListener(
                    object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {}
                        override fun onAnimationEnd(animation: Animator) {
                            binding.toDoEnterDateLinearLayout.visibility = View.INVISIBLE
                        }

                        override fun onAnimationCancel(animation: Animator) {}
                        override fun onAnimationRepeat(animation: Animator) {}
                    }
            )
        }
    }


    companion object {
        private const val TAG = "AddToDoFragment"
        const val DATE_FORMAT = "MMM d, yyyy"
        const val DATE_FORMAT_MONTH_DAY = "MMM d"
        const val DATE_FORMAT_TIME = "H:m"


        @JvmStatic
        fun newInstance(): AddToDoFragment {
            return AddToDoFragment()
        }
    }
}