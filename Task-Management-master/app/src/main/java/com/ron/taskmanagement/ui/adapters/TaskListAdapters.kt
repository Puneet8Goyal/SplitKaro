package com.ron.taskmanagement.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.RecyclerView
import com.ron.taskmanagement.R
import com.ron.taskmanagement.databinding.TaskItemBinding
import com.ron.taskmanagement.models.Task
import com.ron.taskmanagement.utils.TaskMenuOption
import com.ron.taskmanagement.utils.visible

class TaskListAdapters(
     private val onEditClick: TaskMenuOption
) : RecyclerView.Adapter<TaskListAdapters.Holder>() {
    private val list: MutableList<Task> = ArrayList()
    inner class Holder(val binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = list.size

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val model = list[position]
        with(holder.binding) {
            tvTitle.text = model.title
            tvDescreption.text = model.description
            startingDate.text = "Starting date: ${model.date}"
            tvDays.text =
                if (model.completed) {
                    "this task was scheduled to complete in ${model.duration} days"
                } else {
                    "this task will take ${model.duration} days"
                }
            tvPriority.text = "Priority: ${model.priority}"
            tvStatus.text = "Status: ${model.completedOnTime}"
            btnMenu.visible(!model.completed)
            val pop = PopupMenu(this.root.context, this.btnMenu)
            val inflater: MenuInflater = pop.menuInflater
            inflater.inflate(R.menu.mark_menu, pop.menu)
            pop.gravity = GravityCompat.END
            btnMenu.setOnClickListener {
                pop.show()
                pop.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.changeStatus -> {
                            onEditClick.changeStatus(model)
                        }

                        R.id.editTask -> {
                            onEditClick.onEdit(model)
                        }

                        R.id.deleteTask -> {
                            onEditClick.deleteTask(model)
                            list.remove(model)
                            notifyDataSetChanged()
                        }
                    }
                    true

                }

            }

        }
    }

    fun submitList(taskList: List<Task>) {
        val initialSize = list.size
        list.addAll(taskList)
        val finalSize = list.size
        notifyItemRangeInserted(initialSize, finalSize)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }



}