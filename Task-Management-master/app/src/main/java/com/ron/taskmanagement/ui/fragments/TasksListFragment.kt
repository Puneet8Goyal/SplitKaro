package com.ron.taskmanagement.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ron.taskmanagement.databinding.ExcelTypeDialogueBinding
import com.ron.taskmanagement.databinding.FragmentTasksListBinding
import com.ron.taskmanagement.models.Task
import com.ron.taskmanagement.ui.adapters.TaskListAdapters
import com.ron.taskmanagement.ui.dialoges.ChangeTaskStatusDialogue
import com.ron.taskmanagement.utils.ItemDragHelper
import com.ron.taskmanagement.utils.PaginationHelper
import com.ron.taskmanagement.utils.RonConstants
import com.ron.taskmanagement.utils.Status
import com.ron.taskmanagement.utils.TaskMenuOption
import com.ron.taskmanagement.utils.launchWithResult
import com.ron.taskmanagement.utils.longToastShow
import com.ron.taskmanagement.utils.showSnackBar
import com.ron.taskmanagement.utils.visible
import com.ron.taskmanagement.viewmodels.TaskListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TasksListFragment : Fragment() {
    private lateinit var binding: FragmentTasksListBinding
    lateinit var fragmentType: String
    private lateinit var taskAdapter: TaskListAdapters
    private val viewModel: TaskListViewModel by lazy {
        ViewModelProvider(this)[TaskListViewModel::class.java]
    }
    private val pagingScroller by lazy { PaginationHelper() }
    private var totalPagesFound = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTasksListBinding.inflate(inflater, container, false)
        return binding.root
    }


    private val addEditLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val model = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getSerializableExtra(
                    RonConstants.IntentStrings.payload, Task::class.java
                )
            } else {
                result.data?.getSerializableExtra(
                    RonConstants.IntentStrings.payload
                ) as Task?
            }
            model?.let {
                resetTaskList()
            }
        }

    }

    private fun resetTaskList() {
        pagingScroller.setCurrentPages(1)
        taskAdapter.clearList()
        binding.centerProgress.visible()
        viewModel.getTaskListWithPaging(1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (fragmentType == RonConstants.FragmentsTypes.completedTaskList) {
            binding.btnAddTask.visible(false)
            viewModel.setCompletedList(true)
        }

        viewModel.filterApplied.observe(requireActivity()) {
            if (it) {
                resetTaskList()
            }
        }
        taskAdapter = TaskListAdapters(object : TaskMenuOption {
            override fun onEdit(task: Task) {
                requireContext().launchWithResult(
                    RonConstants.FragmentsTypes.addTaskFragment, addEditLauncher
                ) {
                    putExtra(RonConstants.IntentStrings.data, task)
                }
            }

            override fun changeStatus(task: Task) {
                ChangeTaskStatusDialogue(requireContext()) {
                    viewModel.updateTask(task)
                    resetTaskList()
                }.changeTasksStatus(task)
            }

            override fun deleteTask(task: Task) {
                viewModel.deleteTask(task)
            }
        })
        resetTaskList()
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.totalPages.collectLatest {
                totalPagesFound = it.data ?: 1
                pagingScroller.setTotalPages(totalPagesFound)
            }
        }
        binding.recycler.apply {
            adapter = taskAdapter
            pagingScroller.create(this.layoutManager as LinearLayoutManager) {
                binding.bottomProgressbar.visible()
                viewModel.getTaskListWithPaging(it)
            }
            addOnScrollListener(pagingScroller.listScrollListener)
            ItemDragHelper().itemTouchHelper.attachToRecyclerView(this)

        }
        binding.btnAddTask.setOnClickListener {
            viewModel.openAddTaskPage(requireContext(), addEditLauncher)
        }

        binding.swipeRefresh.setOnRefreshListener {
            resetTaskList()
        }
        callGetTaskList(taskAdapter)
    }


    private fun callGetTaskList(taskRecyclerViewAdapter: TaskListAdapters) {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.taskStateFlow.collectLatest {
                Log.d("status", it.status.toString())
                binding.swipeRefresh.isRefreshing = false
                when (it.status) {
                    Status.LOADING -> {
//                            binding.centerProgress.visible()
                    }

                    Status.SUCCESS -> {
                        binding.centerProgress.visible(false)
                        it.data?.collect { taskList ->
                            binding.errorText.visible(taskList.isEmpty() && pagingScroller.getCurrentPage() <= 1)
                            binding.bottomProgressbar.visible(false)
                            taskRecyclerViewAdapter.submitList(taskList)
                        }
                    }

                    Status.ERROR -> {
//                            loadingDialog.dismiss()
                        it.message?.let { it1 -> requireContext().longToastShow(it1) }
                    }
                }

            }
        }
    }

    fun openFilters() {
        viewModel.openFilterSheet(requireActivity())

    }

    fun excelDownload() {
        val dialogueBinding =
            ExcelTypeDialogueBinding.inflate(LayoutInflater.from(requireActivity()), null, false)
        val dialogue =
            AlertDialog.Builder(requireActivity()).setView(dialogueBinding.root).show().also {
                it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        dialogueBinding.close.setOnClickListener {
            dialogue.dismiss()
        }
        dialogueBinding.submit.setOnClickListener {
            val selectedType =
                if (dialogueBinding.rdAll.isChecked) RonConstants.DownloadExcelTypes.allTasks
                else if (dialogueBinding.rdPending.isChecked) RonConstants.DownloadExcelTypes.pendingTasks
                else RonConstants.DownloadExcelTypes.completedTasks
            viewModel.saveExcel(selectedType) {
                dialogue.dismiss()
                requireActivity().showSnackBar(it)
            }
        }
    }


}