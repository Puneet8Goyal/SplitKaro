package com.ron.taskmanagement.di.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ron.taskmanagement.models.Task
import com.ron.taskmanagement.utils.RonConstants
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

//    @Query("""SELECT COUNT(*) FROM(SELECT * FROM Task)""")
//    suspend fun getTaskCount(): Int

    @Query(
        """SELECT COUNT(*) FROM (
        SELECT * FROM Task WHERE completed=:isCompletedFragment AND
        (CASE 
            WHEN :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn
            WHEN :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date
        END) BETWEEN :fromDate AND :toDate
    )"""
    )
    suspend fun getTaskCount(
        isCompletedFragment: Boolean,
        dateTypeSort: String,
        fromDate: String,
        toDate: String,
    ): Int

    @Query(
        """SELECT COUNT(*) FROM (
        SELECT * FROM Task WHERE completed=:isCompletedFragment AND priority= :priority AND
        (CASE 
            WHEN :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn
            WHEN :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date
        END) BETWEEN :fromDate AND :toDate
    )"""
    )
    suspend fun getTaskCount(
        isCompletedFragment: Boolean,
        dateTypeSort: String,
        fromDate: String,
        toDate: String,
        priority: String,
    ): Int


    @Query(
        """SELECT COUNT(*) FROM (
        SELECT * FROM Task WHERE completed=:isCompletedFragment ORDER BY
        CASE WHEN  :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn 
             WHEN  :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date END 
    )"""
    )
    suspend fun getTaskCount(
        isCompletedFragment: Boolean,
        dateTypeSort: String,
    ): Int

    @Query(
        """SELECT COUNT(*) FROM (
        SELECT * FROM Task WHERE completed=:isCompletedFragment AND priority= :priority ORDER BY
        CASE WHEN :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn 
             WHEN :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date END
    )"""
    )
    suspend fun getTaskCount(
        isCompletedFragment: Boolean,

        dateTypeSort: String,
        priority: String
    ): Int


    // filters with dates and no priority
    @Query(
        """SELECT * FROM Task WHERE completed=:isCompletedFragment AND
    (CASE 
        WHEN :isAsc = 1 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn
        WHEN :isAsc = 0 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn
        WHEN :isAsc = 1 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date
        WHEN :isAsc = 0 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date
    END) BETWEEN :fromDate AND :toDate
    ORDER BY
    CASE WHEN :isAsc = 1 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn END ASC, 
    CASE WHEN :isAsc = 0 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn END DESC, 
    CASE WHEN :isAsc = 1 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN createdOn END ASC, 
    CASE WHEN :isAsc = 0 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date END DESC
    LIMIT :pageSize OFFSET (:pageNumber - 1) * :pageSize"""
    )
    fun getTaskListWithFilters(
        isCompletedFragment: Boolean,
        isAsc: Boolean,
        pageNumber: Int,
        pageSize: Int,
        dateTypeSort: String,
        fromDate: String,
        toDate: String
    ): Flow<List<Task>>


    // filters with dates and  priority

    @Query(
        """SELECT * FROM Task WHERE priority= :priority AND completed=:isCompletedFragment AND
    (CASE 
        WHEN :isAsc = 1 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn
        WHEN :isAsc = 0 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn
        WHEN :isAsc = 1 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date
        WHEN :isAsc = 0 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date
    END) BETWEEN :fromDate AND :toDate
    ORDER BY
    CASE WHEN :isAsc = 1 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn END ASC, 
    CASE WHEN :isAsc = 1 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date END ASC,
    CASE WHEN :isAsc = 0 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn END DESC, 
    CASE WHEN :isAsc = 0 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date END DESC
    LIMIT :pageSize OFFSET (:pageNumber - 1) * :pageSize"""
    )
    fun getTaskListWithFilters(
        isCompletedFragment: Boolean,
        isAsc: Boolean,
        pageNumber: Int,
        pageSize: Int,
        dateTypeSort: String,
        fromDate: String,
        toDate: String,
        priority: String,
    ): Flow<List<Task>>


    // filters without dates and no priority

    @Query(
        """SELECT * FROM Task WHERE completed=:isCompletedFragment ORDER BY
    CASE WHEN :isAsc = 1 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn END ASC, 
    CASE WHEN :isAsc = 0 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn END DESC,
    CASE WHEN :isAsc = 1 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date END ASC,
    CASE WHEN :isAsc = 0 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date END DESC
    LIMIT :pageSize OFFSET (:pageNumber - 1) * :pageSize"""
    )
    fun getTaskListWithFilters(
        isCompletedFragment: Boolean,
        isAsc: Boolean,
        pageNumber: Int,
        pageSize: Int,
        dateTypeSort: String,
    ): Flow<List<Task>>


    // filters without dates but priority

    @Query(
        """SELECT * FROM Task WHERE priority= :priority AND completed=:isCompletedFragment ORDER BY
    CASE WHEN :isAsc = 1 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn END ASC, 
    CASE WHEN :isAsc = 0 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn END DESC,
    CASE WHEN :isAsc = 1 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date END ASC,
    CASE WHEN :isAsc = 0 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date END DESC
    LIMIT :pageSize OFFSET (:pageNumber - 1) * :pageSize"""
    )
    fun getTaskListWithFilters(
        isCompletedFragment: Boolean,
        isAsc: Boolean,
        pageNumber: Int,
        pageSize: Int,
        dateTypeSort: String,
        priority: String,
    ): Flow<List<Task>>


    @Query(
        """SELECT * FROM Task ORDER BY
        CASE WHEN :isAsc = 1 THEN taskTitle END ASC, 
        CASE WHEN :isAsc = 0 THEN taskTitle END DESC"""
    )
    fun getTaskListSortByTaskTitle(isAsc: Boolean): Flow<List<Task>>

    @Query(
        """SELECT * FROM Task ORDER BY
        CASE WHEN :isAsc = 1 THEN date END ASC, 
        CASE WHEN :isAsc = 0 THEN date END DESC"""
    )
    fun getTaskListSortByTaskDate(isAsc: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long


    // First way
    @Delete
    suspend fun deleteTask(task: Task): Int


    // Second Way
    @Query("DELETE FROM Task WHERE taskId == :taskId")
    suspend fun deleteTaskUsingId(taskId: String): Int


    @Update
    suspend fun updateTask(task: Task): Int


    @Query("SELECT * FROM Task WHERE taskTitle LIKE :query ORDER BY date DESC")
    fun searchTaskList(query: String): Flow<List<Task>>


    @Query(
        """SELECT * FROM Task WHERE completed=:isCompletedFragment ORDER BY
    CASE WHEN :isAsc = 1 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn END ASC, 
    CASE WHEN :isAsc = 0 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn END DESC,
    CASE WHEN :isAsc = 1 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date END ASC,
    CASE WHEN :isAsc = 0 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date END DESC
  """
    )
    suspend fun getTaskListWForExcel(
        isCompletedFragment: Boolean,
        isAsc: Boolean,
        dateTypeSort: String,
    ): List<Task>


    @Query(
        """SELECT * FROM Task ORDER BY
    CASE WHEN :isAsc = 1 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn END ASC, 
    CASE WHEN :isAsc = 0 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.CreatedDate}' THEN createdOn END DESC,
    CASE WHEN :isAsc = 1 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date END ASC,
    CASE WHEN :isAsc = 0 AND :dateTypeSort = '${RonConstants.TaskDateFilterType.ScheduledDate}' THEN date END DESC
  """
    )
    suspend fun getTaskListWForExcel(
        isAsc: Boolean,
        dateTypeSort: String,
    ): List<Task>


}