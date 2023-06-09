package com.example.todoapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.adapter.NoteAdapter
import com.example.todoapp.databinding.ActivityMainBinding
import com.example.todoapp.db.TaskEntity
import com.example.todoapp.utils.Constants.ALL
import com.example.todoapp.utils.Constants.BUNDLE_ID
import com.example.todoapp.utils.Constants.DELETE
import com.example.todoapp.utils.Constants.EDIT
import com.example.todoapp.utils.Constants.HIGH
import com.example.todoapp.utils.Constants.LOW
import com.example.todoapp.utils.Constants.NORMAL
import com.example.todoapp.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    @Inject
    lateinit var noteAdapter: NoteAdapter

    @Inject
    lateinit var entity: TaskEntity

    private val taskViewModel: TaskViewModel by viewModels()

    private var selectedItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.apply {

            setSupportActionBar(tasksToolbar)

            btnAddTask.setOnClickListener {
                AddTaskFragment().show(supportFragmentManager, AddTaskFragment().tag)
            }

            taskViewModel.getAllNotes()
            taskViewModel.tasksData.observe(this@MainActivity) {
                showEmpty(it.isEmpty)
                noteAdapter.setData(it.data!!)
                taskList.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                    adapter = noteAdapter
                }

            }

            tasksToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.actionFilter -> {
                        filterByPriority()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.actionDeleteAll -> {
                        DeleteAllFragment().show(supportFragmentManager, DeleteAllFragment.TAG)
                        return@setOnMenuItemClickListener true
                    }
                    else -> {
                        return@setOnMenuItemClickListener false
                    }
                }
            }

            noteAdapter.setOnItemClickListener { noteEntity, type ->
                when (type) {
                    EDIT -> {
                        val noteFragment=AddTaskFragment()
                        val bundle = Bundle()
                        bundle.putInt(BUNDLE_ID,noteEntity.id)
                        noteFragment.arguments=bundle
                        noteFragment.show(supportFragmentManager,AddTaskFragment().tag)
                    }
                    DELETE -> {
                        entity.id = noteEntity.id
                        entity.title = noteEntity.title
                        entity.desc = noteEntity.desc
                        entity.cat = noteEntity.cat
                        entity.pr = noteEntity.pr
                        taskViewModel.deleteNote(entity)
                    }
                }
            }

        }
    }

    private fun showEmpty(isShown: Boolean) {
        binding?.apply {
            if (isShown) {
                emptyBody.visibility = View.VISIBLE
                listBody.visibility = View.GONE
            } else {
                emptyBody.visibility = View.GONE
                listBody.visibility = View.VISIBLE
            }
        }
    }

    private fun filterByPriority() {
        val builder = AlertDialog.Builder(this)
        val priories = arrayOf(ALL, HIGH, NORMAL, LOW)
        builder.setSingleChoiceItems(priories, selectedItem) { dialog, item ->
            when (item) {
                0 -> {
                    taskViewModel.getAllNotes()
                }
                in 1..3 -> {
                    taskViewModel.getFilterNotes(priories[item])
                }
            }
            selectedItem = item
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        val search = menu.findItem(R.id.actionSearch)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Search..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                taskViewModel.getSearchNotes(newText!!)
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
