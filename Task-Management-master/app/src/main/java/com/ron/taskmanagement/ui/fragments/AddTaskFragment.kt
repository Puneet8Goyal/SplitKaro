package com.ron.taskmanagement.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ron.taskmanagement.R
import com.ron.taskmanagement.databinding.FragmentAddTaskBinding
import com.ron.taskmanagement.databinding.WordMeaningDialogueBinding
import com.ron.taskmanagement.models.Task
import com.ron.taskmanagement.ui.MainActivity
import com.ron.taskmanagement.ui.dialoges.PriorityBottomSheet
import com.ron.taskmanagement.utils.MyEditText
import com.ron.taskmanagement.utils.RonConstants
import com.ron.taskmanagement.utils.StatusResult
import com.ron.taskmanagement.utils.currentDate
import com.ron.taskmanagement.utils.currentTime
import com.ron.taskmanagement.utils.formatDateForUi
import com.ron.taskmanagement.utils.hideKeyBoard
import com.ron.taskmanagement.utils.openCalender
import com.ron.taskmanagement.utils.showSnackBar
import com.ron.taskmanagement.viewmodels.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date


@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class AddTaskFragment : Fragment() {
    private lateinit var binding: FragmentAddTaskBinding
    private var selectedPriority: String? = null
    private var modelForEdit: Task? = null
    private val taskViewModel: TaskViewModel by lazy {
        ViewModelProvider(this)[TaskViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (requireActivity().intent.hasExtra(RonConstants.IntentStrings.data)) {
            modelForEdit = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireActivity().intent.getSerializableExtra(
                    RonConstants.IntentStrings.data,
                    Task::class.java
                )
            } else {
                requireActivity().intent.getSerializableExtra(
                    RonConstants.IntentStrings.data
                ) as Task?
            }
            modelForEdit?.let {
                (requireActivity() as MainActivity).setTitle("Edit Task")
                setDataForEdit(it)
            }
        }

        binding.descreption.setSelectionListener(object : MyEditText.SelectionListener {
            override fun onTextSelected(selectString: String) {
                binding.root.hideKeyBoard()
                if (selectString.isNotEmpty()) {
                    openDialogueWithMeaning(selectString)
                }

            }

        })


        binding.submit.setOnClickListener {
            addDataIntoDb()
        }
        binding.selectDate.setOnClickListener {
            requireContext().openCalender(minDate = Date(), maxDate = null) { day, month, year ->
                val formattedDate = formatDateForUi("$day-$month-$year")
                binding.selectDate.setText(formattedDate)
            }
        }
        binding.etPriority.setOnClickListener {
            priorityBottomSheet.openPriorityBottomSheet(selectedPriority)
        }
        taskViewModel.statusLiveData.observe(requireActivity()) {
            if (it.data == StatusResult.Updated || it.data == StatusResult.Added) {
                requireActivity().showSnackBar(it.message)
            }

        }
    }

    private fun openDialogueWithMeaning(selectString: String) {
        val dialogueBinding =
            WordMeaningDialogueBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        val dialogue =
            AlertDialog.Builder(requireActivity()).setView(dialogueBinding.root).show().also {
                it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
//  API Call will be triggered with this function and a dialogue will be visible
//  once we get the response then the meaning of the word will be visible
//        taskViewModel.getWordMeaning(selectString)
        taskViewModel.wordsMeaning.observe(requireActivity()) {
            dialogueBinding.selectedText.text = "You have Selected the Text :$selectString"
        }
        dialogueBinding.selectedText.text =
            "the meaning of the word will come once the billing of the api is done for now you have selected the word :$selectString"
        dialogueBinding.close.setOnClickListener {
            taskViewModel.wordsMeaning.removeObservers(requireActivity())
            dialogue.dismiss()
        }
    }

    private fun setDataForEdit(taskModel: Task?) {
        selectedPriority = taskModel?.priority
        binding.etTitle.setText(taskModel?.title ?: "")
        binding.descreption.setText(taskModel?.description ?: "")
        binding.selectDate.setText(taskModel?.date ?: "")
        binding.etPriority.setText("Priority $selectedPriority")
        binding.etDuration.setText(taskModel?.duration ?: "")


    }

    private val priorityBottomSheet by lazy {
        PriorityBottomSheet(requireActivity()) {
            selectedPriority = it
            binding.etPriority.setText("Priority $selectedPriority")
        }
    }

    private fun addDataIntoDb() {
        binding.root.hideKeyBoard()
        if (checkValidations()) {
            Task(
                binding.etTitle.text.toString().trim(),
                binding.descreption.text.toString().trim(),
                binding.selectDate.text.toString().trim(),
                selectedPriority.toString(),
                binding.etDuration.text.toString().trim(),
                modelForEdit?.createdOn ?: currentDate(),
                modelForEdit?.createdAt ?: currentTime(),
                modelForEdit?.completedOnTime ?: getString(R.string.curreently_working),
            ).also { task ->
                if (modelForEdit?.taskId != null) {
                    task.taskId = modelForEdit?.taskId!!
                    taskViewModel.updateTask(task)
                } else {
                    taskViewModel.insertTask(task)
                }
                requireActivity().setResult(Activity.RESULT_OK, Intent().also {
                    it.putExtra(RonConstants.IntentStrings.payload, task)
                })
                requireActivity().finishAfterTransition()


            }
        }
    }


    private fun checkValidations(): Boolean {
        var errorMessage: String? = null
        with(binding) {
            if (etTitle.text.isEmpty()) {
                etTitle.requestFocus()
                errorMessage = "Please Enter the Title"
            } else if (descreption.text.toString().isEmpty()) {
                descreption.requestFocus()
                errorMessage = "Please Enter the Description"
            } else if (selectDate.text.isEmpty()) {
                selectDate.requestFocus()
                errorMessage = "Please Select the Date!!"
            } else if (etPriority.text.isEmpty()) {
                etPriority.requestFocus()
                errorMessage = "Please Select the Priority!!"
            } else if (etDuration.text.isEmpty() || etDuration.text.toString().toInt() <= 0) {
                selectDate.requestFocus()
                errorMessage = "Please Select a valid Required Days!!"
            }
            errorMessage?.let {
                requireActivity().showSnackBar(errorMessage)
                return false
            } ?: return true
        }
    }


}