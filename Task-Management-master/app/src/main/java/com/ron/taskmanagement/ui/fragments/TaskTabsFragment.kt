package com.ron.taskmanagement.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.ron.taskmanagement.databinding.FragmentTaskTabsBinding
import com.ron.taskmanagement.ui.adapters.ViewStateAdapter
import com.ron.taskmanagement.utils.RonConstants


class TaskTabsFragment : Fragment() {

    private lateinit var binding: FragmentTaskTabsBinding

    private var currentFragmentIndex = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskTabsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentLists = ArrayList<Fragment>()
        val pendingFragment = TasksListFragment().also {
            it.fragmentType = RonConstants.FragmentsTypes.pendingTasksList
        }
        val completedFragment = TasksListFragment().also {
            it.fragmentType = RonConstants.FragmentsTypes.completedTaskList

        }
        fragmentLists.add(pendingFragment)
        fragmentLists.add(completedFragment)

        binding.viewPager.adapter =
            ViewStateAdapter(requireActivity().supportFragmentManager, lifecycle, fragmentLists)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->

            when (position) {
                0 -> {
                    tab.text = RonConstants.FragmentsTypes.pendingTasksList
                }

                1 -> {
                    tab.text = RonConstants.FragmentsTypes.completedTaskList
                }

            }
        }.attach()


        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentFragmentIndex = position
            }
        })
        binding.btnFilters.setOnClickListener {
            if (currentFragmentIndex == 0) {
                pendingFragment.openFilters()
            } else {
                completedFragment.openFilters()
            }
        }
        binding.btnDownload.setOnClickListener {
            if (currentFragmentIndex == 0) {
                pendingFragment.excelDownload()
            } else {
                completedFragment.excelDownload()
            }
        }
    }

}