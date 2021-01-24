package com.example.avjindersinghsekhon.minimaltodo.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.example.avjindersinghsekhon.minimaltodo.AlarmManager
import com.example.avjindersinghsekhon.minimaltodo.AddToDo.AddToDoActivity
import com.example.avjindersinghsekhon.minimaltodo.AddToDo.AddToDoFragment
import com.example.avjindersinghsekhon.minimaltodo.Analytics.AnalyticsApplication
import com.example.avjindersinghsekhon.minimaltodo.Main.MainActivity
import com.example.avjindersinghsekhon.minimaltodo.Main.MainFragment
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.utility.ItemTouchHelperClass
import com.example.avjindersinghsekhon.minimaltodo.utility.ToDoItem
import com.example.avjindersinghsekhon.minimaltodo.utility.TodoNotificationService
import com.example.avjindersinghsekhon.minimaltodo.databinding.ListCircleTryBinding
import com.example.avjindersinghsekhon.minimaltodo.databinding.ListTodoFragmentBinding
import com.example.avjindersinghsekhon.minimaltodo.fragments.ListTodoFragment
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList
import com.example.avjindersinghsekhon.minimaltodo.utility.*




class TodoRecyclerViewAdapter  (private val items: ArrayList<ToDoItem>, private val app : AnalyticsApplication, val itemClick : (ToDoItem) -> Unit )
    : RecyclerView.Adapter<TodoRecyclerViewAdapter.ViewHolder>(), ItemTouchHelperClass.ItemTouchHelperAdapter {

    public var mJustDeletedToDoItem: ToDoItem? = null
    private var mIndexOfDeletedToDoItem = 0
    lateinit var context : Context
    lateinit var view : View
    lateinit var alarmManager: AlarmManager
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoRecyclerViewAdapter.ViewHolder {
        context = parent.context


        // set up alaram manager
        alarmManager = AlarmManager(items, context)
        alarmManager.setAlarms()
        view =  parent.rootView
        return ViewHolder(ListCircleTryBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: TodoRecyclerViewAdapter.ViewHolder, position: Int) {
        val item = items[position]
        //            if(item.getToDoDate()!=null && item.getToDoDate().before(new Date())){
//                item.setToDoDate(null);
//            }
        holder.bind(item)
    }

    override fun getItemCount(): Int {
       return items.size
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemRemoved(position: Int) {

        app.send(this, "Action", "Swiped Todo Away")
        mJustDeletedToDoItem = items.removeAt(position)
        mIndexOfDeletedToDoItem = position
        val i = Intent(context, TodoNotificationService::class.java)
        alarmManager.deleteAlarm(i,mJustDeletedToDoItem?.identifier.hashCode())

        notifyItemRemoved(position)

//            String toShow = (mJustDeletedToDoItem.getToDoText().length()>20)?mJustDeletedToDoItem.getToDoText().substring(0, 20)+"...":mJustDeletedToDoItem.getToDoText();
        val toShow = "Todo"
        Snackbar.make( view , "Deleted $toShow", Snackbar.LENGTH_LONG)
                .setAction("UNDO", object : View.OnClickListener {
                    override fun onClick(v: View) {

                        //Comment the line below if not using Google Analytics
                        app?.send(this, "Action", "UNDO Pressed")
                        items.add(mIndexOfDeletedToDoItem, mJustDeletedToDoItem!!)
                        if (mJustDeletedToDoItem?.getToDoDate() != null && mJustDeletedToDoItem!!.hasReminder()) {
                            val i = Intent(context, TodoNotificationService::class.java)
                            i.putExtra(TodoNotificationService.TODOTEXT,mJustDeletedToDoItem?.getToDoText())
                            i.putExtra(TodoNotificationService.TODOUUID, mJustDeletedToDoItem?.getIdentifier())
                            val date = this@TodoRecyclerViewAdapter.mJustDeletedToDoItem?.getToDoDate()?.getTime()
                            alarmManager.createAlarm(i, mJustDeletedToDoItem?.getIdentifier().hashCode(), date!!)
                        }
                        notifyItemInserted(this@TodoRecyclerViewAdapter.mIndexOfDeletedToDoItem)
                    }
                }).show()
    }




    inner class ViewHolder(val listCircleTryBinding: ListCircleTryBinding) : RecyclerView.ViewHolder(listCircleTryBinding.root) {


    ;
        init {

            listCircleTryBinding.setClickListener{
                val item = items[this@ViewHolder.adapterPosition]

                itemClick(item)
               
            }
            
        }



        fun bind(item: ToDoItem){
            
            val sharedPreferences: SharedPreferences? = context.getSharedPreferences(MainFragment.THEME_PREFERENCES, Context.MODE_PRIVATE)
            //Background color for each to-do item. Necessary for night/day mode
            val bgColor: Int
            //color of title text in our to-do item. White for night mode, dark gray for day mode
            val todoTextColor: Int
            if (sharedPreferences?.getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME) == MainFragment.LIGHTTHEME) {
                bgColor = Color.WHITE
                todoTextColor = context.getResources().getColor(R.color.secondary_text)
            } else {
                bgColor = Color.DKGRAY
                todoTextColor = Color.WHITE
            }
            listCircleTryBinding.listItemLinearLayout.setBackgroundColor(bgColor)
            if (item.hasReminder() && item.toDoDate != null) {
                listCircleTryBinding.toDoListItemTextview.maxLines = 1
                listCircleTryBinding.todoListItemTimeTextView.visibility = View.VISIBLE
                //                listCircleTryBinding.toDoListItemTextview.setVisibility(View.GONE);
            } else {
                listCircleTryBinding.todoListItemTimeTextView.visibility = View.GONE
                listCircleTryBinding.toDoListItemTextview.maxLines = 2
            }
            listCircleTryBinding.toDoListItemTextview.text = item.toDoText
            listCircleTryBinding.toDoListItemTextview.setTextColor(todoTextColor)
            //            holder.mColorTextView.setBackgroundColor(Color.parseColor(item.getTodoColor()));

//            TextDrawable myDrawable = TextDrawable.builder().buildRoundRect(item.getToDoText().substring(0,1),Color.RED, 10);
            //We check if holder.color is set or not
//            if(item.getTodoColor() == null){
//                ColorGenerator generator = ColorGenerator.MATERIAL;
//                int color = generator.getRandomColor();
//                item.setTodoColor(color+"");
//            }
//            Log.d("OskarSchindler", "Color: "+item.getTodoColor());
            val myDrawable = TextDrawable.builder().beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .toUpperCase()
                    .endConfig()
                    .buildRound(item.toDoText.substring(0, 1), item.todoColor)

//            TextDrawable myDrawable = TextDrawable.builder().buildRound(item.getToDoText().substring(0,1),holder.color);
            listCircleTryBinding.toDoListItemColorImageView.setImageDrawable(myDrawable)
            if (item.toDoDate != null) {
                val timeToShow: String
                timeToShow = if (DateFormat.is24HourFormat(context)) {
                    AddToDoFragment.formatDate(DATE_TIME_FORMAT_24_HOUR, item.toDoDate)
                } else {
                    AddToDoFragment.formatDate(DATE_TIME_FORMAT_12_HOUR, item.toDoDate)
                }
                listCircleTryBinding.todoListItemTimeTextView.text = timeToShow
            }


        }
    }


}