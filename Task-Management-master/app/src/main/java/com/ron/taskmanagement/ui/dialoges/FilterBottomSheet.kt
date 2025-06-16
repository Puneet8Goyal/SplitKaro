package com.ron.taskmanagement.ui.dialoges

import android.app.Activity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ron.taskmanagement.R
import com.ron.taskmanagement.databinding.FilterBottomSheetBinding
import com.ron.taskmanagement.utils.RonConstants
import com.ron.taskmanagement.utils.formatDateForFilters
import com.ron.taskmanagement.utils.getMinDate
import com.ron.taskmanagement.utils.openCalender
import com.ron.taskmanagement.utils.visible
import java.util.Date

class FilterBottomSheet(val activity: Activity) {

    fun openFilterSheet(
        sortByAccentingOrder: Boolean = false,
        dateType: String = RonConstants.TaskDateFilterType.CreatedDate,
        priority: String? = null,
        fromDate: String? = null,
        toDate: String? = null,
        applyFilters: (Boolean, String, String?, String?, String?) -> Unit
    ) {

        val sheetBinding: FilterBottomSheetBinding =
            FilterBottomSheetBinding.inflate(activity.layoutInflater)
        val dialogue = BottomSheetDialog(activity, R.style.CustomBottomSheetDialog)
        dialogue.setContentView(sheetBinding.root)
        dialogue.show()
        sheetBinding.rdAccending.isChecked = sortByAccentingOrder
        sheetBinding.rdDecending.isChecked = !sortByAccentingOrder
        if (dateType == RonConstants.TaskDateFilterType.CreatedDate) {
            sheetBinding.rdCreatedDate.isChecked = true
        } else {
            sheetBinding.rdScheduledDate.isChecked = true
        }
        priority?.let {
            when (it) {
                RonConstants.TaskPriorities.Low -> {
                    sheetBinding.priorityLow.isChecked = true
                }

                RonConstants.TaskPriorities.Medium -> {
                    sheetBinding.priorityMedium.isChecked = true
                }

                RonConstants.TaskPriorities.High -> {
                    sheetBinding.priorityHigh.isChecked = true
                }

                else -> {
                    sheetBinding.priorityAny.isChecked = true
                }
            }
        }
        if (!fromDate.isNullOrEmpty()) {
            sheetBinding.endDate.visible()
            sheetBinding.tv6.visible()
            sheetBinding.startDate.setText(fromDate.toString())
        }
        toDate?.let {
            sheetBinding.endDate.setText(it)
        }

        with(sheetBinding) {
            startDate.setOnClickListener {
                activity.openCalender(minDate = null, maxDate = null) { day, month, year ->
                    val formattedDate = formatDateForFilters("$day-$month-$year")
//                        formatDateForUi("$day-$month-$year")
                    startDate.setText(formattedDate)
                    sheetBinding.endDate.visible()
                    sheetBinding.tv6.visible()
                }
            }
            endDate.setOnClickListener {
                val minDate=getMinDate(startDate.text.toString())
                activity.openCalender(minDate = minDate, maxDate = null) { day, month, year ->
                    val formattedDate = formatDateForFilters("$day-$month-$year")
                    endDate.setText(formattedDate)
                }
            }




            submit.setOnClickListener {
                val dateWise = if (sheetBinding.rdCreatedDate.isChecked)
                    RonConstants.TaskDateFilterType.CreatedDate
                else
                    RonConstants.TaskDateFilterType.ScheduledDate
                val selectedP: String? =
                    if (priorityLow.isChecked) RonConstants.TaskPriorities.Low
                    else if (priorityMedium.isChecked) RonConstants.TaskPriorities.Medium
                    else if (priorityHigh.isChecked) RonConstants.TaskPriorities.High
                    else null
                applyFilters(
                    rdAccending.isChecked,
                    dateWise,
                    selectedP,
                    startDate.text.toString(),
                    endDate.text.toString(),
                )
                dialogue.cancel()

            }
            btnCancel.setOnClickListener {
                applyFilters(
                    false,
                    RonConstants.TaskDateFilterType.CreatedDate,
                    null,
                    null,
                    null,
                )
                dialogue.cancel()
            }
        }

    }
}