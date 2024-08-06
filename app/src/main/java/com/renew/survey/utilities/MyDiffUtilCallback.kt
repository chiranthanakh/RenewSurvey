package com.renew.survey.utilities

import androidx.recyclerview.widget.DiffUtil
import com.renew.survey.room.entities.FormQuestionLanguage
import com.renew.survey.room.entities.QuestionGroupWithLanguage

class MyDiffUtilCallback(
    private val oldList: List<FormQuestionLanguage>,
    private val newList: List<FormQuestionLanguage>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Check if items have the same unique identifier (e.g., ID field)
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.id == newItem.id// Replace 'id' with your unique identifier
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Check if item contents are the same (consider all relevant fields)
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem // Modify this based on your data comparison logic
    }

    // Optional: Consider implementing these methods for better performance
    //  - getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any?

}