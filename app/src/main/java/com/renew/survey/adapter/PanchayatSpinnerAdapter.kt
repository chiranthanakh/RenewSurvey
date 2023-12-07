package com.renew.survey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import com.renew.survey.R
import com.renew.survey.databinding.SpinnerItemBinding
import com.renew.survey.response.PanchayathModel
import com.renew.survey.response.StateModel

class PanchayatSpinnerAdapter(val context: Context, val list: List<PanchayathModel>) : BaseAdapter(),SpinnerAdapter{
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(p0: Int): Any {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val binding = SpinnerItemBinding.inflate(LayoutInflater.from(p2?.context), p2, false)
        binding.textView1.text = list[p0].panchayat_name
        if (p0 == 0) {
            binding.textView1.setTextColor(context.resources.getColor(R.color.hint_color))
        } else {
            binding.textView1.setTextColor(context.resources.getColor(R.color.black))
        }
        return binding.root
    }
}