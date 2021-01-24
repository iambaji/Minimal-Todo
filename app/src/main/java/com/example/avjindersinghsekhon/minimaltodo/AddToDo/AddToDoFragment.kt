package com.example.avjindersinghsekhon.minimaltodo.AddToDo

import android.animation.Animator
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.fragment.app.Fragment
import com.example.avjindersinghsekhon.minimaltodo.Analytics.AnalyticsApplication
import com.example.avjindersinghsekhon.minimaltodo.Main.MainFragment
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.utility.*
import com.example.avjindersinghsekhon.minimaltodo.utility.ToDoItem
import com.example.avjindersinghsekhon.minimaltodo.databinding.FragmentAddToDoBinding
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*

class AddToDoFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private val mLastEdited: Date? = null
   
    lateinit var mUserToDoItem : ToDoItem

 
    lateinit var mUserEnteredText: String
    private var mUserEnteredDescription: String? = null
    private var mUserHasReminder = false


    var mUserReminderDate : Date? = null
    private var mUserColor = 0


    private var theme: String? = null
    lateinit var app : AnalyticsApplication

    lateinit var binding : FragmentAddToDoBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        app = requireActivity().application as AnalyticsApplication
        binding = FragmentAddToDoBinding.inflate(inflater,container,false)
        //        setContentView(R.layout.new_to_do_layout);
        //Need references to these to change them during light/dark mode

        theme = requireActivity().getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE).getString(THEME_SAVED, LIGHTTHEME)
        if (theme == MainFragment.LIGHTTHEME) {
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
        mUserToDoItem = requireActivity().intent.getSerializableExtra(MainFragment.TODOITEM) as ToDoItem
        mUserEnteredText = mUserToDoItem!!.toDoText
        mUserEnteredDescription = mUserToDoItem!!.getmToDoDescription()
        mUserHasReminder = mUserToDoItem.hasReminder()
        mUserReminderDate = mUserToDoItem.toDoDate ?: Date()
        mUserColor = mUserToDoItem.todoColor


//        if(mUserToDoItem.getLastEdited()==null) {
//            mLastEdited = new Date();
//        }
//        else{
//            mLastEdited = mUserToDoItem.getLastEdited();
//        }

        binding.apply {

            if (theme == MainFragment.DARKTHEME) {

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


            if (mUserHasReminder && mUserReminderDate != null) {
//            toDoEnterDateLinearLayout.setVisibility(View.VISIBLE);
                setReminderTextView()
                setEnterDateLayoutVisibleWithAnimations(true)
            }
            if (mUserReminderDate == null) {
                toDoHasDateSwitchCompat.isChecked = false
                userToDoRemindMeTextView .visibility = View.INVISIBLE
            }

            //        TextInputLayout til = (TextInputLayout)findViewById(R.id.toDoCustomTextInput);
//        til.requestFocus();

            userToDoEditText.requestFocus()
            userToDoEditText.setText(mUserEnteredText)  
          userToDoDescription.setText(mUserEnteredDescription)
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //        imm.showSoftInput(userToDoEditText, InputMethodManager.SHOW_IMPLICIT);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        userToDoEditText.setSelection(userToDoEditText.length())
        userToDoEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mUserEnteredText = s.toString()
            }

            override fun afterTextChanged(s: Editable) {}
        })
            
        userToDoDescription!!.setText(mUserEnteredDescription)
        userToDoDescription.setSelection(userToDoDescription.length())
        userToDoDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mUserEnteredDescription = s.toString()
            }

            override fun afterTextChanged(s: Editable) {}
        })




//        String lastSeen = formatDate(DATE_FORMAT, mLastEdited);
//        mLastSeenTextView.setText(String.format(getResources().getString(R.string.last_edited), lastSeen));
            setEnterDateLayoutVisible(toDoHasDateSwitchCompat.isChecked)
            toDoHasDateSwitchCompat.isChecked = mUserHasReminder && mUserReminderDate != null
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


            makeToDoFloatingActionButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    if (userToDoEditText.length() <= 0) {
                        userToDoEditText.error = getString(R.string.todo_error)
                    } else if (mUserReminderDate != null && mUserReminderDate?.before(Date()) == true) {
                        app.send(this, "Action", "Date in the Past")
                        makeResult(Activity.RESULT_CANCELED)
                    } else {
                        app.send(this, "Action", "Make Todo")
                        makeResult(Activity.RESULT_OK)
                        requireActivity().finish()
                    }
                    hideKeyboard(userToDoEditText)
                    hideKeyboard(userToDoDescription)
                }
            })


            newTodoDateEditText.setOnClickListener {
                val date: Date?
                hideKeyboard(userToDoEditText)
                date = if (mUserToDoItem!!.toDoDate != null) {
//                    date = mUserToDoItem.getToDoDate();
                    mUserReminderDate
                } else {
                    Date()
                }
                val calendar = Calendar.getInstance()
                calendar.time = date
                val year = calendar[Calendar.YEAR]
                val month = calendar[Calendar.MONTH]
                val day = calendar[Calendar.DAY_OF_MONTH]
                val datePickerDialog = DatePickerDialog.newInstance(this@AddToDoFragment, year, month, day)
                if (theme == MainFragment.DARKTHEME) {
                    datePickerDialog.isThemeDark = true
                }
                datePickerDialog.show(requireActivity().fragmentManager, "DateFragment")
            }

            newTodoTimeEditText.setOnClickListener {
                val date: Date?
                hideKeyboard(userToDoEditText)
                date = if (mUserToDoItem!!.toDoDate != null) {
//                    date = mUserToDoItem.getToDoDate();
                    mUserReminderDate
                } else {
                    Date()
                }
                val calendar = Calendar.getInstance()
                calendar.time = date
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val minute = calendar[Calendar.MINUTE]
                val timePickerDialog = TimePickerDialog.newInstance(this@AddToDoFragment, hour, minute, DateFormat.is24HourFormat(context))
                if (theme == MainFragment.DARKTHEME) {
                    timePickerDialog.isThemeDark = true
                }
                timePickerDialog.show(requireActivity().fragmentManager, "TimeFragment")
            }




            setDateAndTimeEditText()
        }






 
  

//        mDefaultTimeOptions12H = new String[]{"9:00 AM", "12:00 PM", "3:00 PM", "6:00 PM", "9:00 PM", "12:00 AM"};
//        mDefaultTimeOptions24H = new String[]{"9:00", "12:00", "15:00", "18:00", "21:00", "24:00"};
       

//

//        mChooseDateButton = (Button)findViewById(R.id.newToDoChooseDateButton);
//        mChooseTimeButton = (Button)findViewById(R.id.newToDoChooseTimeButton);
//
//        mChooseDateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Date date;
//                hideKeyboard(userToDoEditText);
//                if(mUserToDoItem.getToDoDate()!=null){
//                    date = mUserToDoItem.getToDoDate();
//                }
//                else{
//                    date = new Date();
//                }
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(date);
//                int year = calendar.get(Calendar.YEAR);
//                int month = calendar.get(Calendar.MONTH);
//                int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//
//                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(AddToDoActivity.this, year, month, day);
//                if(theme.equals(MainActivity.DARKTHEME)){
//                    datePickerDialog.setThemeDark(true);
//                }
//                datePickerDialog.show(getFragmentManager(), "DateFragment");
//            }
//        });
//
//        mChooseTimeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Date date;
//                hideKeyboard(userToDoEditText);
//                if(mUserToDoItem.getToDoDate()!=null){
//                    date = mUserToDoItem.getToDoDate();
//                }
//                else{
//                    date = new Date();
//                }
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(date);
//                int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                int minute = calendar.get(Calendar.MINUTE);
//
//                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(AddToDoActivity.this, hour, minute, DateFormat.is24HourFormat(AddToDoActivity.this));
//                if(theme.equals(MainActivity.DARKTHEME)){
//                    timePickerDialog.setThemeDark(true);
//                }
//                timePickerDialog.show(getFragmentManager(), "TimeFragment");
//            }
//        });
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun setDateAndTimeEditText() {
        if (mUserToDoItem!!.hasReminder() && mUserReminderDate != null) {
            val userDate = formatDate("d MMM, yyyy", mUserReminderDate)
            val formatToUse: String
            formatToUse = if (DateFormat.is24HourFormat(context)) {
                "k:mm"
            } else {
                "h:mm a"
            }
            val userTime = formatDate(formatToUse, mUserReminderDate)
            binding.newTodoTimeEditText.setText(userTime) 
            binding.newTodoDateEditText.setText(userDate)
        } else {
            binding.newTodoDateEditText.setText(getString(R.string.date_reminder_default))
            //            mUserReminderDate = new Date();
            val time24 = DateFormat.is24HourFormat(context)
            val cal = Calendar.getInstance()
            if (time24) {
                cal[Calendar.HOUR_OF_DAY] = cal[Calendar.HOUR_OF_DAY] + 1
            } else {
                cal[Calendar.HOUR] = cal[Calendar.HOUR] + 1
            }
            cal[Calendar.MINUTE] = 0
            mUserReminderDate = cal.time
            Log.d("OskarSchindler", "Imagined Date: $mUserReminderDate")
            val timeString: String
            timeString = if (time24) {
                formatDate("k:mm", mUserReminderDate)
            } else {
                formatDate("h:mm a", mUserReminderDate)
            }
            binding.newTodoTimeEditText!!.setText(timeString)
            //            int hour = calendar.get(Calendar.HOUR_OF_DAY);
//            if(hour<9){
//                timeOption = time24?mDefaultTimeOptions24H[0]:mDefaultTimeOptions12H[0];
//            }
//            else if(hour < 12){
//                timeOption = time24?mDefaultTimeOptions24H[1]:mDefaultTimeOptions12H[1];
//            }
//            else if(hour < 15){
//                timeOption = time24?mDefaultTimeOptions24H[2]:mDefaultTimeOptions12H[2];
//            }
//            else if(hour < 18){
//                timeOption = time24?mDefaultTimeOptions24H[3]:mDefaultTimeOptions12H[3];
//            }
//            else if(hour < 21){
//                timeOption = time24?mDefaultTimeOptions24H[4]:mDefaultTimeOptions12H[4];
//            }
//            else{
//                timeOption = time24?mDefaultTimeOptions24H[5]:mDefaultTimeOptions12H[5];
//            }
//            newTodoTimeEditText.setText(timeOption);
        }
    }

    private val themeSet: String
        private get() = requireActivity().getSharedPreferences(MainFragment.THEME_PREFERENCES, Context.MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME)

    fun hideKeyboard(et: EditText?) {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et!!.windowToken, 0)
    }

    fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        val hour: Int
        val minute: Int
        //        int currentYear = calendar.get(Calendar.YEAR);
//        int currentMonth = calendar.get(Calendar.MONTH);
//        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        val reminderCalendar = Calendar.getInstance()
        reminderCalendar[year, month] = day
        if (reminderCalendar.before(calendar)) {
            //    Toast.makeText(this, "My time-machine is a bit rusty", Toast.LENGTH_SHORT).show();
            return
        }
        if (mUserReminderDate != null) {
            calendar.time = mUserReminderDate
        }
        hour = if (DateFormat.is24HourFormat(context)) {
            calendar[Calendar.HOUR_OF_DAY]
        } else {
            calendar[Calendar.HOUR]
        }
        minute = calendar[Calendar.MINUTE]
        calendar[year, month, day, hour] = minute
        mUserReminderDate = calendar.time
        setReminderTextView()
        //        setDateAndTimeEditText();
        setDateEditText()
    }

    fun setTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        if (mUserReminderDate != null) {
            calendar.time = mUserReminderDate
        }

//        if(DateFormat.is24HourFormat(this) && hour == 0){
//            //done for 24h time
//                hour = 24;
//        }
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        Log.d("OskarSchindler", "Time set: $hour")
        calendar[year, month, day, hour, minute] = 0
        mUserReminderDate = calendar.time
        setReminderTextView()
        //        setDateAndTimeEditText();
        setTimeEditText()
    }

    fun setDateEditText() {
        val dateFormat = "d MMM, yyyy"
        binding.newTodoDateEditText!!.setText(formatDate(dateFormat, mUserReminderDate))
    }

    fun setTimeEditText() {
        val dateFormat: String
        dateFormat = if (DateFormat.is24HourFormat(context)) {
            "k:mm"
        } else {
            "h:mm a"
        }
        binding.newTodoTimeEditText.setText(formatDate(dateFormat, mUserReminderDate))
    }

    fun setReminderTextView() {
        if (mUserReminderDate != null) {
            binding.newToDoDateTimeReminderTextView .visibility = View.VISIBLE
            if (mUserReminderDate!!.before(Date())) {
                Log.d("OskarSchindler", "DATE is $mUserReminderDate")
                binding.newToDoDateTimeReminderTextView.text = getString(R.string.date_error_check_again)
                binding.newToDoDateTimeReminderTextView.setTextColor(Color.RED)
                return
            }
            val date: Date = mUserReminderDate as Date
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
        } else {
            binding.newToDoDateTimeReminderTextView.visibility = View.INVISIBLE
        }
    }

    fun makeResult(result: Int) {
        Log.d(TAG, "makeResult - ok : in")
        val i = Intent()
        if (mUserEnteredText.length > 0) {
            val capitalizedString = Character.toUpperCase(mUserEnteredText[0]).toString() + mUserEnteredText!!.substring(1)
            mUserToDoItem!!.toDoText = capitalizedString
            Log.d(TAG, "Description: $mUserEnteredDescription")
            mUserToDoItem.setmToDoDescription(mUserEnteredDescription)
        } else {
            mUserToDoItem.toDoText = mUserEnteredText
            Log.d(TAG, "Description: $mUserEnteredDescription")
            mUserToDoItem.setmToDoDescription(mUserEnteredDescription)
        }
        //        mUserToDoItem.setLastEdited(mLastEdited);
        if (mUserReminderDate != null) {
            val calendar = Calendar.getInstance()
            calendar.time = mUserReminderDate
            calendar[Calendar.SECOND] = 0
            mUserReminderDate = calendar.time
        }
        mUserToDoItem.setHasReminder(mUserHasReminder)
        mUserToDoItem.toDoDate = mUserReminderDate
        mUserToDoItem.todoColor = mUserColor
        i.putExtra(TODOITEM, mUserToDoItem)
        requireActivity().setResult(result, i)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (NavUtils.getParentActivityName(requireActivity()) != null) {
                    app!!.send(this, "Action", "Discard Todo")
                    makeResult(Activity.RESULT_CANCELED)
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
        fun formatDate(formatString: String?, dateToFormat: Date?): String {
            val simpleDateFormat = SimpleDateFormat(formatString)
            return simpleDateFormat.format(dateToFormat)
        }

        @JvmStatic
        fun newInstance(): AddToDoFragment {
            return AddToDoFragment()
        }
    }
}