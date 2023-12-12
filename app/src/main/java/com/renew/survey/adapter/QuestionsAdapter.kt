package com.renew.survey.adapter

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.renew.survey.R
import com.renew.survey.databinding.ItemLanguageBinding
import com.renew.survey.databinding.ItemQuestionGroupBinding
import com.renew.survey.databinding.ItemQuestionLayoutBinding
import com.renew.survey.room.entities.FormQuestionLanguage
import com.renew.survey.room.entities.FormQuestionOptionsEntity
import com.renew.survey.room.entities.LanguageEntity
import com.renew.survey.room.entities.Options
import com.renew.survey.room.entities.QuestionGroupWithLanguage
import com.renew.survey.utilities.PreferenceManager
import com.renew.survey.utilities.UtilMethods
import java.util.Calendar
import java.util.Locale

class QuestionsAdapter(
    val context:Context, private var list: List<FormQuestionLanguage>,
    var clickListener: ClickListener) :
    RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {
    var previousSelected=0;
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    val hour = c.get(Calendar.HOUR)
    val minute = c.get(Calendar.MINUTE)
    val preferenceManager=PreferenceManager(context)

    fun setData(list: List<FormQuestionLanguage>){
        this.list=list
        notifyDataSetChanged()
    }

    class ViewHolder (val binding: ItemQuestionLayoutBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuestionLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                when(question_type){
                    "DATETIME","TEXT","NUMBER","GEO_LOCATION","DATE","TIME"->{
                        binding.llEdittext.visibility = View.VISIBLE
                        binding.llMultiselect.visibility = View.GONE
                        binding.llSpinner.visibility = View.GONE
                        binding.llRadio.visibility = View.GONE
                        binding.llRange.visibility = View.GONE
                        binding.llFile.visibility = View.GONE
                        binding.txtEditTextLable.text = this.title
                        if (question_type=="DATETIME" || question_type=="DATE" || question_type=="GEO_LOCATION"){
                            binding.edittext.isFocusable=false
                            binding.edittext.setOnClickListener {
                                if (question_type=="DATE"){
                                    val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                        val calendar=Calendar.getInstance()
                                        calendar[Calendar.DAY_OF_MONTH]=dayOfMonth
                                        calendar[Calendar.MONTH]=month
                                        calendar[Calendar.YEAR]=year
                                        binding.edittext.setText(UtilMethods.getFormattedDate(calendar.time,"dd-mm-yyyy"))
                                    }, year, month, day)
                                    dpd.show()
                                }else if (question_type=="TIME"){
                                    val tpd = TimePickerDialog(context,TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                                        val calendar=Calendar.getInstance()
                                        calendar[Calendar.HOUR]=i
                                        calendar[Calendar.MINUTE]=i2
                                        binding.edittext.setText(UtilMethods.getFormattedDate(calendar.time,"hh:mm a"))
                                    },hour,minute,false)
                                    tpd.show()
                                }else if (question_type=="DATETIME"){
                                    val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                        val calendar=Calendar.getInstance()
                                        calendar[Calendar.DAY_OF_MONTH]=dayOfMonth
                                        calendar[Calendar.MONTH]=month
                                        calendar[Calendar.YEAR]=year
                                        val tpd = TimePickerDialog(context,TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                                            calendar[Calendar.HOUR]=i
                                            calendar[Calendar.MINUTE]=i2
                                            binding.edittext.setText(UtilMethods.getFormattedDate(calendar.time,"dd-mm-yyyy hh:mm a"))
                                        },hour,minute,false)
                                        tpd.show()
                                        binding.edittext.setText(UtilMethods.getFormattedDate(calendar.time,""))
                                    }, year, month, day)
                                    dpd.show()

                                }else if (question_type=="GEO_LOCATION"){
                                    binding.edittext.setText(preferenceManager.getLocation())
                                }
                            }
                        }else{
                            binding.edittext.isFocusable=true
                        }
                        if (this.question_type=="NUMBER"){
                            binding.edittext.inputType=InputType.TYPE_CLASS_NUMBER
                        }else{
                            binding.edittext.inputType=InputType.TYPE_CLASS_TEXT
                        }
                    }
                    "SINGLE_SELECT"->{
                        binding.llEdittext.visibility = View.GONE
                        binding.llMultiselect.visibility = View.GONE
                        binding.llSpinner.visibility = View.VISIBLE
                        binding.llRadio.visibility = View.GONE
                        binding.llRange.visibility = View.GONE
                        binding.llFile.visibility = View.GONE
                        binding.txtSpLable.text = this.title
                        binding.spinner.adapter=CustomSpinnerAdapter(context,getStringList(this.options))
                    }
                    "MULTI_SELECT"->{
                        binding.llEdittext.visibility = View.GONE
                        binding.llMultiselect.visibility = View.VISIBLE
                        binding.llSpinner.visibility = View.GONE
                        binding.llRadio.visibility = View.GONE
                        binding.llRange.visibility = View.GONE
                        binding.llFile.visibility = View.GONE
                        binding.txtMultiselect.text = this.title
                    }
                    "RADIO"->{
                        binding.llEdittext.visibility = View.GONE
                        binding.llMultiselect.visibility = View.GONE
                        binding.llSpinner.visibility = View.GONE
                        binding.llRadio.visibility = View.VISIBLE
                        binding.llRange.visibility = View.GONE
                        binding.llFile.visibility = View.GONE
                        binding.txtRadio.text = this.title
                        for(option in this.options){
                            val radioButton=RadioButton(context)
                            radioButton.setText(option.title)
                            binding.rgRadio.addView(radioButton)
                        }
                    }
                    "RANGE"->{
                        binding.llEdittext.visibility = View.GONE
                        binding.llMultiselect.visibility = View.GONE
                        binding.llSpinner.visibility = View.GONE
                        binding.llRadio.visibility = View.GONE
                        binding.llRange.visibility = View.VISIBLE
                        binding.llFile.visibility = View.GONE
                        binding.txtRange.text = this.title
                        val values= arrayListOf<Float>()
                        values.add(this.min_length.toFloat())
                        values.add(this.max_length.toFloat())
                        binding.rangeSliderPrice.values= values
                        binding.rangeSliderPrice.valueFrom=this.min_length.toFloat()
                        binding.rangeSliderPrice.valueTo=this.max_length.toFloat()
                    }
                    "FILE"->{
                        binding.llEdittext.visibility = View.GONE
                        binding.llMultiselect.visibility = View.GONE
                        binding.llSpinner.visibility = View.GONE
                        binding.llRadio.visibility = View.GONE
                        binding.llRange.visibility = View.GONE
                        binding.llFile.visibility = View.VISIBLE
                        binding.txtFileLable.text = this.title
                    }
                }
            }
        }
    }
    interface ClickListener{
        fun onQuestionGroupSelected(questionGroup: QuestionGroupWithLanguage,pos:Int)
    }
    fun getStringList(optionList:List<Options>):List<String>{
        val list= arrayListOf<String>()
        for (d in optionList){
            list.add(d.title)
        }
        return list;
    }
}