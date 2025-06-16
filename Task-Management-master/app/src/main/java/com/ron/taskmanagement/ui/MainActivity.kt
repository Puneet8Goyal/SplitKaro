package com.ron.taskmanagement.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.ron.taskmanagement.databinding.ActivityMainBinding
import com.ron.taskmanagement.ui.fragments.AddTaskFragment
import com.ron.taskmanagement.ui.fragments.SplashFragment
import com.ron.taskmanagement.ui.fragments.TaskTabsFragment
import com.ron.taskmanagement.utils.RonConstants
import com.ron.taskmanagement.utils.visible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.btnBack.setOnClickListener {
            finishAfterTransition()
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (intent.hasExtra(RonConstants.IntentStrings.type)) {
            val type = intent.getStringExtra(RonConstants.IntentStrings.type)
            if (type == RonConstants.FragmentsTypes.addTaskFragment) {
                binding.title.text = type
                loadFragment(AddTaskFragment())
            } else if (type == RonConstants.FragmentsTypes.taskTabFragment) {
                binding.title.text = type
                binding.toolbar.visible(false)
                loadFragment(TaskTabsFragment())
            }
        } else {
            binding.toolbar.visible(false)
            loadFragment(SplashFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(binding.container.id, fragment).commit()
    }

    fun setTitle(title: String) {
        binding.title.text = title
    }
}