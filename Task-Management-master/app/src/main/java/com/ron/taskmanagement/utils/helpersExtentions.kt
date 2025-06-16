package com.ron.taskmanagement.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.google.android.material.snackbar.Snackbar
import com.ron.taskmanagement.R
import com.ron.taskmanagement.models.Task
import com.ron.taskmanagement.ui.MainActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import androidx.core.graphics.drawable.toDrawable

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

enum class StatusResult {
    Added,
    Updated,
    Deleted
}

fun View.hideKeyBoard() {
    try {
        val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.longToastShow(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun View.visible(value: Boolean = true) {
    if (value) {
        if (this.visibility != View.VISIBLE)
            this.visibility = View.VISIBLE
    } else {
            this.visibility = View.GONE
    }
}

@SuppressLint("SimpleDateFormat")
fun currentTime(): String {
    val sdf = SimpleDateFormat("hh:mm aa")
    return sdf.format(Date())
}

@SuppressLint("SimpleDateFormat")
fun currentDate(): String {
    val sdf = SimpleDateFormat("dd-MM-yyyy")
    return sdf.format(Date())

}

@SuppressLint("SimpleDateFormat")
fun getMinDate(stringDate:String): Date? {
    val sdf = SimpleDateFormat("dd-MM-yyyy")
    try {
        return sdf.parse(stringDate)
    } catch (e: ParseException) {
        e.printStackTrace()
        return Date()
    }
}

@SuppressLint("SetTextI18n")
fun Context.openCalender(minDate: Date?, maxDate: Date?, selectedDate: (Int, Int, Int) -> Unit) {
    val cal = Calendar.getInstance()
    val year = cal[Calendar.YEAR]
    val month = cal[Calendar.MONTH]
    val day = cal[Calendar.DAY_OF_MONTH]

    val dialog = DatePickerDialog(
        this,
        { _, _, _, dayOfMonth ->
            selectedDate(dayOfMonth, month + 1, year)
        },
        year, month, day
    )

    dialog.window!!.setBackgroundDrawable(Color.WHITE.toDrawable())
    minDate?.time?.let {
        dialog.datePicker.minDate = it
    }
    maxDate?.time?.let {
        dialog.datePicker.maxDate = it
    }
    dialog.show()

}


@SuppressLint("SimpleDateFormat")
fun formatDateForUi(providedDate: String): String {
    val inputFormat = SimpleDateFormat("dd-MM-yyyy")
    val outputFormat = SimpleDateFormat("dd MMM yyyy")

    val outputDateStr = try {
        val date = inputFormat.parse(providedDate)
        date?.let { outputFormat.format(it) } ?: providedDate
    } catch (e: Exception) {
        e.printStackTrace()
        providedDate
    }
    return outputDateStr
}

@SuppressLint("SimpleDateFormat")
fun formatDateForFilters(providedDate: String): String {
    val inputFormat = SimpleDateFormat("dd-MM-yyyy")
    val sdf = SimpleDateFormat("dd-MM-yyyy")

    val outputDateStr = try {
        val date = inputFormat.parse(providedDate)
        date?.let { sdf.format(it) } ?: providedDate
    } catch (e: Exception) {
        e.printStackTrace()
        providedDate
    }
    return outputDateStr
}

fun TextView.setSelectedBg() {
    this.setBackgroundResource(
        R.drawable.bg_selected_priority
    )
}

fun TextView.setUnSelectedBg() {
    this.setBackgroundResource(
        R.drawable.bg_un_selected_priority
    )
}

fun Activity.showSnackBar(message: String?) {
    val tempMessage = message ?: "Exception"

    val snackBar = Snackbar
        .make(
            this.findViewById(android.R.id.content),
            tempMessage,
            Snackbar.LENGTH_LONG
        )
    val view = snackBar.view
    val textView: TextView = view.findViewById(com.google.android.material.R.id.snackbar_text)
//    textView.typeface = ResourcesCompat.getFont(this, R.font.bold)
    textView.maxLines = 5
    textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
    textView.ellipsize = TextUtils.TruncateAt.END
    val text = textView.text.toString()
    textView.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT)
    snackBar.show()
}


inline fun Context.launchNextPage(
    fragmentType: String,
    init: Intent.() -> Unit = {},
    finishAll: Boolean = false,
    finish: Boolean = false
) {
    val options = ActivityOptionsCompat.makeCustomAnimation(
        this,
        R.anim.enter,
        R.anim.exit
    )
    val intent = Intent(this, MainActivity::class.java)
    intent.putExtra(RonConstants.IntentStrings.type, fragmentType)
    intent.init()
    startActivity(intent, options.toBundle());
    if (finishAll) {
        (this as Activity).finishAffinity()
    } else if (finish) {
        (this as Activity).finishAfterTransition()
    }
}


inline fun Context.launchWithResult(
    fragmentType: String,
    launcher: ActivityResultLauncher<Intent>,
    init: Intent.() -> Unit = {},

    ) {
    val options = ActivityOptionsCompat.makeCustomAnimation(
        this,
        R.anim.enter,
        R.anim.exit
    )
    val intent = Intent(this, MainActivity::class.java)
    intent.putExtra(RonConstants.IntentStrings.type, fragmentType)
    intent.init()
    launcher.launch(intent, options);

}


interface TaskMenuOption {
    fun onEdit(task: Task)
    fun changeStatus(task: Task)
    fun deleteTask(task: Task)
}
