package com.ron.taskmanagement.ui.dialoges

import android.app.Activity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ron.taskmanagement.R
import com.ron.taskmanagement.databinding.PriorityBottomSheetBinding
import com.ron.taskmanagement.utils.RonConstants
import com.ron.taskmanagement.utils.setSelectedBg
import com.ron.taskmanagement.utils.setUnSelectedBg

class PriorityBottomSheet(
    private val activity: Activity,
    val onPrioritySelected: (String) -> Unit
) {
    private var selectedPriority: String? = null

     fun openPriorityBottomSheet(etPriority: String?) {
        val sheetBinding: PriorityBottomSheetBinding =
            PriorityBottomSheetBinding.inflate(activity.layoutInflater)
        val dialogue = BottomSheetDialog(activity, R.style.CustomBottomSheetDialog)
        dialogue.setContentView(sheetBinding.root)
        dialogue.show()
        etPriority?.let {
            selectedPriority(it, sheetBinding)
        }
        sheetBinding.priorityLow.setOnClickListener {
            selectedPriority = RonConstants.TaskPriorities.Low
            selectedPriority?.let {
                selectedPriority(it, sheetBinding)
            }
        }
        sheetBinding.priorityMedium.setOnClickListener {
            selectedPriority = RonConstants.TaskPriorities.Medium
            selectedPriority?.let {
                selectedPriority(it, sheetBinding)
            }
        }
        sheetBinding.priorityHigh.setOnClickListener {
            selectedPriority = RonConstants.TaskPriorities.High
            selectedPriority?.let {
                selectedPriority(it, sheetBinding)
            }
        }

    }

    private fun selectedPriority(etPriority: String, sheetBinding: PriorityBottomSheetBinding) {
        when (etPriority) {
            RonConstants.TaskPriorities.Low -> {
                sheetBinding.priorityLow.setSelectedBg()
                sheetBinding.priorityHigh.setUnSelectedBg()
                sheetBinding.priorityMedium.setUnSelectedBg()

            }

            RonConstants.TaskPriorities.Medium -> {
                sheetBinding.priorityLow.setUnSelectedBg()
                sheetBinding.priorityHigh.setUnSelectedBg()
                sheetBinding.priorityMedium.setSelectedBg()

            }

            RonConstants.TaskPriorities.High -> {
                sheetBinding.priorityLow.setUnSelectedBg()
                sheetBinding.priorityHigh.setSelectedBg()
                sheetBinding.priorityMedium.setUnSelectedBg()
            }
        }
        onPrioritySelected(etPriority)
//        binding.etPriority.text = "Priority $etPriority"

    }

}