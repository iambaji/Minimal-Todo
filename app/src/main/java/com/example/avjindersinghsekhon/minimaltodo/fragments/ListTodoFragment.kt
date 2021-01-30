package com.example.avjindersinghsekhon.minimaltodo.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.example.avjindersinghsekhon.minimaltodo.About.AboutActivity
import com.example.avjindersinghsekhon.minimaltodo.AddToDo.AddToDoActivity
import com.example.avjindersinghsekhon.minimaltodo.Analytics.AnalyticsApplication
import com.example.avjindersinghsekhon.minimaltodo.Main.CustomRecyclerScrollViewListener
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.Reminder.ReminderFragment
import com.example.avjindersinghsekhon.minimaltodo.Settings.SettingsActivity
import com.example.avjindersinghsekhon.minimaltodo.utility.*
import com.example.avjindersinghsekhon.minimaltodo.adapters.TodoRecyclerViewAdapter
import com.example.avjindersinghsekhon.minimaltodo.databinding.ListTodoFragmentBinding
import org.json.JSONException
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ListTodoFragment : Fragment() {



    lateinit var adapter : TodoRecyclerViewAdapter

    lateinit var storeRetrieveData: StoreRetrieveData
    var itemTouchHelper: ItemTouchHelper? = null
    private var customRecyclerScrollViewListener: CustomRecyclerScrollViewListener? = null

    private var mTheme = -1
     var theme = "name_of_the_theme"

    lateinit var app: AnalyticsApplication
    private val testStrings = arrayOf("Clean my room",
            "Water the plants",
            "Get car washed",
            "Get my dry cleaning"
    )

    var mToDoItemsArrayList : ArrayList<ToDoItem> = ArrayList()
    companion object {
        fun newInstance() = ListTodoFragment()
    }

    private val viewModel: ListTodoViewModel by viewModels{
        ListTodoViewModelFactory((requireActivity().application as AnalyticsApplication).repository)
    }
    lateinit var binding : ListTodoFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = ListTodoFragmentBinding.inflate(inflater,container,false)

        app = requireActivity().application as AnalyticsApplication

        //We recover the theme we've set and setTheme accordingly
        theme = requireActivity().getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE).getString(THEME_SAVED, LIGHTTHEME)

        mTheme = if (theme == LIGHTTHEME) {
            R.style.CustomStyle_LightTheme
        } else {
            R.style.CustomStyle_DarkTheme
        }
        this.requireActivity().setTheme(mTheme)

        super.onCreate(savedInstanceState)


        val sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(CHANGE_OCCURED, false)
        editor.apply()

        storeRetrieveData = StoreRetrieveData(context, FILENAME)
        mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData)


        adapter = TodoRecyclerViewAdapter(app ){
            val i = Intent(context, AddToDoActivity::class.java)
            i.putExtra(TODOITEM, it.itemid)
            startActivityForResult(i, REQUEST_ID_TODO_ITEM)

        }


     binding.apply {
            addToDoItemFAB.setOnClickListener {
                app?.send(this, "Action", "FAB pressed")
                val newTodo = Intent(context, AddToDoActivity::class.java)

                val color = ColorGenerator.MATERIAL.randomColor

                startActivityForResult(newTodo, REQUEST_ID_TODO_ITEM)
            }
            if (theme == LIGHTTHEME) {
                toDoRecyclerView?.setBackgroundColor(resources.getColor(R.color.primary_lightest))
            }
            toDoRecyclerView.apply {
               // setEmptyView(toDoEmptyView)
                setHasFixedSize(true)
                setItemAnimator(DefaultItemAnimator())
                setLayoutManager(LinearLayoutManager(context))

                customRecyclerScrollViewListener = object : CustomRecyclerScrollViewListener() {
                    override fun show() {
                        addToDoItemFAB?.animate()?.translationY(0f)?.setInterpolator(DecelerateInterpolator(2F))?.start()
                        //                mAddToDoItemFAB.animate().translationY(0).setInterpolator(new AccelerateInterpolator(2.0f)).start();
                    }

                    override fun hide() {
                        val lp = addToDoItemFAB?.getLayoutParams() as CoordinatorLayout.LayoutParams
                        val fabMargin = lp.bottomMargin
                        addToDoItemFAB!!.animate().translationY((addToDoItemFAB!!.getHeight() + fabMargin).toFloat()).setInterpolator(AccelerateInterpolator(2.0f)).start()
                    }
                }

                addOnScrollListener(customRecyclerScrollViewListener as CustomRecyclerScrollViewListener)


                val callback: ItemTouchHelper.Callback = ItemTouchHelperClass(this@ListTodoFragment.adapter)
                itemTouchHelper = ItemTouchHelper(callback)
                itemTouchHelper!!.attachToRecyclerView(this)
                adapter = this@ListTodoFragment.adapter
            }

        }

        viewModel.todoItems.observe(viewLifecycleOwner){
                binding.hasToDos = it.isNotEmpty()
                adapter.submitList(ArrayList(it))
        }

        return binding.root
    }


    fun getLocallyStoredData(storeRetrieveData: StoreRetrieveData): ArrayList<ToDoItem> {
        var items: ArrayList<ToDoItem> = ArrayList()
        try {
            items = storeRetrieveData.loadFromFile()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (items == null) {
            items = ArrayList()
        }
        return items
    }

    override fun onResume() {
        super.onResume()
        app!!.send(this)
        val sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean(ReminderFragment.EXIT, false)) {
            val editor = sharedPreferences.edit()
            editor.putBoolean(ReminderFragment.EXIT, false)
            editor.apply()
            requireActivity().finish()
        }
        /*
        We need to do this, as this activity's onCreate won't be called when coming back from SettingsActivity,
        thus our changes to dark/light mode won't take place, as the setContentView() is not called again.
        So, inside our SettingsFragment, whenever the checkbox's value is changed, in our shared preferences,
        we mark our recreate_activity key as true.

        Note: the recreate_key's value is changed to false before calling recreate(), or we woudl have ended up in an infinite loop,
        as onResume() will be called on recreation, which will again call recreate() and so on....
        and get an ANR

         */if (requireActivity().getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE).getBoolean(RECREATE_ACTIVITY, false)) {
            val editor = requireActivity().getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE).edit()
            editor.putBoolean(RECREATE_ACTIVITY, false)
            editor.apply()
            requireActivity().recreate()
        }
    }

    override fun onStart() {
        app = requireActivity().application as AnalyticsApplication
        super.onStart()
//        val sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
//        if (sharedPreferences.getBoolean(CHANGE_OCCURED, false)) {
//            mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData)
//            adapter = TodoRecyclerViewAdapter(mToDoItemsArrayList,app) {
//                val i = Intent(context, AddToDoActivity::class.java)
//                i.putExtra(TODOITEM, it)
//                startActivityForResult(i, REQUEST_ID_TODO_ITEM)
//
//            }
//                binding.toDoRecyclerView.adapter = adapter
//            val editor = sharedPreferences.edit()
//            editor.putBoolean(CHANGE_OCCURED, false)
//            //            editor.commit();
//            editor.apply()
//        }
    }




    fun addThemeToSharedPreferences(theme: String?) {
        val sharedPreferences = requireActivity().getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(THEME_SAVED, theme)
        editor.apply()
    }


    fun onCreateOptionsMenu(menu: Menu?): Boolean {
        requireActivity().menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.aboutFragment -> {
                val i = Intent(context, AboutActivity::class.java)
                startActivity(i)
                true
            }
            R.id.preferences -> {
                val intent = Intent(context, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED && requestCode == REQUEST_ID_TODO_ITEM) {

        }
    }

  


    private fun addToDataStore(item: ToDoItem) {

        mToDoItemsArrayList.add(item)
        adapter.notifyItemInserted(mToDoItemsArrayList.size - 1)
    }


    fun makeUpItems(items: ArrayList<ToDoItem?>, len: Int) {
        for (testString in testStrings) {
            val item = ToDoItem(testString, testString, false, Date())

//            item.setTodoColor(getResources().getString(R.color.red_secondary));
            items.add(item)
        }
    }


    //Used when using custom fonts
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

    //Used when using custom fonts
    //    @Override
    //    protected void attachBaseContext(Context newBase) {
    //        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    //    }
    private fun saveDate() {
        try {
            storeRetrieveData!!.saveToFile(mToDoItemsArrayList)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            storeRetrieveData!!.saveToFile(mToDoItemsArrayList)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        customRecyclerScrollViewListener?.let { binding.toDoRecyclerView.removeOnScrollListener(it) }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}