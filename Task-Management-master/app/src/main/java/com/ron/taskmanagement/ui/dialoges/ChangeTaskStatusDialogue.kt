package com.ron.taskmanagement.ui.dialoges

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.ron.taskmanagement.databinding.StatusChangeDialogueBinding
import com.ron.taskmanagement.models.Task
import com.ron.taskmanagement.utils.longToastShow

class ChangeTaskStatusDialogue(val context: Context,val onSubmit:(Task)->Unit) {
     fun changeTasksStatus(task: Task) {
        val dialogueBinding =
            StatusChangeDialogueBinding.inflate(LayoutInflater.from(context), null, false)
        val dialogue =
            AlertDialog.Builder(context).setView(dialogueBinding.root).setCancelable(false)
                .show().apply {
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
        dialogueBinding.btnCancel.setOnClickListener {
            dialogue.dismiss()
        }
        dialogueBinding.title.text = task.title
        dialogueBinding.status.setText(task.completedOnTime)
        dialogueBinding.sugg1.setOnClickListener {
            dialogueBinding.status.setText(dialogueBinding.sugg1.text.toString())
        }
        dialogueBinding.sugg2.setOnClickListener {
            dialogueBinding.status.setText(dialogueBinding.sugg2.text.toString())
        }
        dialogueBinding.sugg3.setOnClickListener {
            dialogueBinding.status.setText(dialogueBinding.sugg3.text.toString())
        }
        dialogueBinding.rdPending.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                task.completed = false
            }
        }
        dialogueBinding.rdCompleted.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                task.completed = true
            }
        }
        dialogueBinding.submit.setOnClickListener {
            task.completedOnTime = dialogueBinding.status.text.toString()
            if (task.completedOnTime.isEmpty()) {
                context.longToastShow("Please Enter the Status!!")
                return@setOnClickListener
            }
            dialogue.dismiss()
            onSubmit(task)

        }
    }

}