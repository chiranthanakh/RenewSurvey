package com.renew.globalsurvey.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import com.renew.globalsurvey.R
import com.renew.globalsurvey.databinding.SpinnerItemBinding
import com.renew.globalsurvey.response.TehsilModel

class TehsilSpinnerAdapter(val context: Context, val list: List<TehsilModel>) : BaseAdapter(),SpinnerAdapter{
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
        binding.textView1.text = list[p0].tehsil_name
        if (p0 == 0) {
            binding.textView1.setTextColor(context.resources.getColor(R.color.hint_color))
        } else {
            binding.textView1.setTextColor(context.resources.getColor(R.color.black))
        }
        return binding.root
    }
}