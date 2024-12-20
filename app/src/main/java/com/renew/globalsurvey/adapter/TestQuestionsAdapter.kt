package com.renew.globalsurvey.adapter

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.RadioButton
import android.widget.Spinner
import androidx.core.text.bold
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.RangeSlider
import com.renew.globalsurvey.R
import com.renew.globalsurvey.databinding.ItemQuestionLayoutBinding
import com.renew.globalsurvey.room.entities.FormQuestionLanguage
import com.renew.globalsurvey.room.entities.Options
import com.renew.globalsurvey.room.entities.TestOptionLanguage
import com.renew.globalsurvey.room.entities.TestQuestionLanguage
import com.renew.globalsurvey.utilities.PreferenceManager
import java.util.Calendar


class TestQuestionsAdapter(
    val context:Context, private var list: List<TestQuestionLanguage>
) :
    RecyclerView.Adapter<TestQuestionsAdapter.ViewHolder>() {
    var previousSelected=0;
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    val hour = c.get(Calendar.HOUR)
    val minute = c.get(Calendar.MINUTE)
    val preferenceManager=PreferenceManager(context)

    fun setData(list: List<TestQuestionLanguage>){
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
                    "DATETIME","TEXT","NUMBER","GEO_LOCATION","DATE","TIME","EMAIL"->{
                        binding.llEdittext.visibility = View.VISIBLE
                        binding.llMultiselect.visibility = View.GONE
                        binding.llSpinner.visibility = View.GONE
                        binding.llRadio.visibility = View.GONE
                        binding.llRange.visibility = View.GONE
                        binding.llCheckbox.visibility = View.GONE
                        binding.llFile.visibility = View.GONE
                        binding.llRating.visibility = View.GONE
                        binding.txtEditTextLable.text = getHintText(this.title,position+1,this.is_mandatory)
                        binding.edittext.addTextChangedListener(object :TextWatcher{
                            override fun beforeTextChanged(
                                p0: CharSequence?,
                                p1: Int,
                                p2: Int,
                                p3: Int
                            ) {
                            }

                            override fun onTextChanged(
                                p0: CharSequence?,
                                p1: Int,
                                p2: Int,
                                p3: Int
                            ) {
                                if (binding.edittext.isFocused){
                                    answer2=p0.toString()
                                }
                            }

                            override fun afterTextChanged(p0: Editable?) {

                            }

                        })
                       binding.edittext.setText(this.answer2)
                        /*if (question_type=="DATETIME" || question_type=="DATE" || question_type=="TIME" || question_type=="GEO_LOCATION"){
                            binding.edittext.isFocusable=false
                            binding.edittext.setOnClickListener {
                                when (question_type) {
                                    "DATE" -> {
                                        val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                            val calendar=Calendar.getInstance()
                                            calendar[Calendar.DAY_OF_MONTH]=dayOfMonth
                                            calendar[Calendar.MONTH]=month
                                            calendar[Calendar.YEAR]=year
                                            binding.edittext.setText(UtilMethods.getFormattedDate(calendar.time,this.format!!))
                                            this.answer2=UtilMethods.getFormattedDate(calendar.time,this.format!!)
                                        }, year, month, day)
                                        dpd.show()
                                    }
                                    "TIME" -> {
                                        val tpd = TimePickerDialog(context,TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                                            val calendar=Calendar.getInstance()
                                            calendar[Calendar.HOUR]=i
                                            calendar[Calendar.MINUTE]=i2
                                            binding.edittext.setText(UtilMethods.getFormattedDate(calendar.time,this.format!!))
                                            this.answer2=UtilMethods.getFormattedDate(calendar.time,this.format!!)
                                        },hour,minute,false)
                                        tpd.show()
                                    }
                                    "DATETIME" -> {
                                        val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                                            val calendar=Calendar.getInstance()
                                            calendar[Calendar.DAY_OF_MONTH]=dayOfMonth
                                            calendar[Calendar.MONTH]=month
                                            calendar[Calendar.YEAR]=year
                                            val tpd = TimePickerDialog(context,TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                                                calendar[Calendar.HOUR]=i
                                                calendar[Calendar.MINUTE]=i2
                                                binding.edittext.setText(UtilMethods.getFormattedDate(calendar.time,this.format!!))
                                                this.answer2=UtilMethods.getFormattedDate(calendar.time,this.format)
                                            },hour,minute,false)
                                            tpd.show()
                                            binding.edittext.setText(UtilMethods.getFormattedDate(calendar.time,""))
                                        }, year, month, day)
                                        dpd.show()

                                    }
                                    "GEO_LOCATION" -> {
                                        binding.edittext.setText(preferenceManager.getLocation())
                                        this.answer2=preferenceManager.getLocation()
                                    }
                                }
                            }
                        }else{
                            binding.edittext.isFocusable=true
                            binding.edittext.isFocusableInTouchMode=true
                            binding.edittext.isEnabled=true
                        }*/
                        when(question_type){
                            "NUMBER"->{
                                binding.edittext.inputType=InputType.TYPE_CLASS_NUMBER
                            }
                            "TEXT"->{
                                binding.edittext.inputType=InputType.TYPE_CLASS_TEXT
                            }
                            "EMAIL"->{
                                binding.edittext.inputType=InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
                            }
                            else->{
                                binding.edittext.inputType=InputType.TYPE_CLASS_TEXT
                            }
                        }
                    }
                    "SINGLE_SELECT"->{
                        binding.llEdittext.visibility = View.GONE
                        binding.llMultiselect.visibility = View.GONE
                        binding.llSpinner.visibility = View.VISIBLE
                        binding.llRadio.visibility = View.GONE
                        binding.llCheckbox.visibility = View.GONE
                        binding.llRange.visibility = View.GONE
                        binding.llFile.visibility = View.GONE
                        binding.llRating.visibility = View.GONE
                        binding.txtSpLable.text = getHintText(this.title,position+1,this.is_mandatory)
                        binding.spinner.adapter=TestOptionsSpinnerAdapter(context,this.options)
                        if (this.answers.isNotEmpty()){
                            this.options.forEachIndexed { index, options ->
                                if (options.title==answers[0].title){
                                    binding.spinner.setSelection(index)
                                }
                            }
                          //  binding.spinner.setSelection(getIndex(binding.spinner,this.answer!!))
                        }
                        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                if (position>0){
                                    answers= arrayListOf()
                                    answers.add(binding.spinner.selectedItem as TestOptionLanguage)
                                }
                            }

                        }
                    }
                    "RADIO"->{
                        binding.llEdittext.visibility = View.GONE
                        binding.llMultiselect.visibility = View.GONE
                        binding.llSpinner.visibility = View.GONE
                        binding.llRadio.visibility = View.VISIBLE
                        binding.llRange.visibility = View.GONE
                        binding.llCheckbox.visibility = View.GONE
                        binding.llFile.visibility = View.GONE
                        binding.llRating.visibility = View.GONE
                        binding.txtRadio.text = getHintText(this.title,position+1,this.is_mandatory)
                        binding.rgRadio.removeAllViews()
                        for(option in this.options){
                            val radioButton=RadioButton(context)
                            radioButton.setText(option.title)
                            if (option.title==this.answer){
                                radioButton.isChecked=true
                            }
                            radioButton.setOnCheckedChangeListener { compoundButton, b ->
                                if (b){
                                    options.forEachIndexed { index, testOptionLanguage ->
                                        if (compoundButton.text==testOptionLanguage.title){
                                            answers= arrayListOf()
                                            answers.add(testOptionLanguage)
                                        }
                                    }
                                    answer=compoundButton.text.toString()
                                }
                            }
                            binding.rgRadio.addView(radioButton)
                        }
                    }
                    "RANGE"->{
                        binding.llEdittext.visibility = View.GONE
                        binding.llMultiselect.visibility = View.GONE
                        binding.llSpinner.visibility = View.GONE
                        binding.llRadio.visibility = View.GONE
                        binding.llCheckbox.visibility = View.GONE
                        binding.llRange.visibility = View.VISIBLE
                        binding.llFile.visibility = View.GONE
                        binding.llRating.visibility = View.GONE
                        binding.txtRange.text = getHintText(this.title,position+1,this.is_mandatory)
                        if (answer!=""){
                            val vals=this.answer!!.split(",")
                            val values= arrayListOf<Float>()
                            values.add(vals[0].toFloat())
                            values.add(vals[1].toFloat())
                            binding.rangeSliderPrice.values= values
                        }else{
                            val values= arrayListOf<Float>()
                            values.add(this.min_length.toFloat())
                            values.add(this.max_length.toFloat())
                            binding.rangeSliderPrice.values= values
                        }



                        binding.rangeSliderPrice.valueFrom=this.min_length.toFloat()
                        binding.rangeSliderPrice.valueTo=this.max_length.toFloat()
                        binding.rangeSliderPrice.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
                            override fun onStartTrackingTouch(slider: RangeSlider) {
                                // Responds to when slider's touch event is being started
                            }

                            override fun onStopTrackingTouch(slider: RangeSlider) {
                                answer2="${slider.values[0]},${slider.values[1]}"
                            }
                        })
                    }
                    "FILE","CAPTURE"->{
                        binding.llEdittext.visibility = View.GONE
                        binding.llMultiselect.visibility = View.GONE
                        binding.llSpinner.visibility = View.GONE
                        binding.llRadio.visibility = View.GONE
                        binding.llRange.visibility = View.GONE
                        binding.llCheckbox.visibility = View.GONE
                        binding.llFile.visibility = View.VISIBLE
                        binding.txtFileLable.text = getHintText(this.title,position+1,this.is_mandatory)
                        binding.llRating.visibility = View.GONE
                        if (this.question_type=="FILE"){
                            binding.tvFile.setText(context.getString(R.string.attach_file))
                            binding.fileImage.setImageResource(R.drawable.ic_file_attach)
                        }else if(this.question_type=="CAPTURE"){
                            binding.tvFile.setText(context.getString(R.string.capture_image))
                            binding.fileImage.setImageResource(R.drawable.ic_camera)
                        }
                        if (this.answer!=""){
                            binding.tvFile.setText(this.answer!!.substring(this.answer!!.lastIndexOf("/")+1))
                        }
                        binding.tvFile.setOnClickListener {
                           // clickListener.onFileSelect(this,position, this.allowed_file_type!!,this.question_type)
                        }
                    }
                    "CHECKBOX"->{
                        binding.llEdittext.visibility = View.GONE
                        binding.llMultiselect.visibility = View.GONE
                        binding.llSpinner.visibility = View.GONE
                        binding.llRadio.visibility = View.GONE
                        binding.llRange.visibility = View.GONE
                        binding.llFile.visibility = View.GONE
                        binding.llRating.visibility = View.GONE
                        binding.llCheckbox.visibility = View.VISIBLE
                        binding.txtCheckbox.setText(getHintText(this.title,position+1,this.is_mandatory))
                        binding.llCheckboxAdd.removeAllViews()
                        for (ops in this.options){
                            val checkBox=CheckBox(context)
                            checkBox.text=ops.title
                            for (op in answers){
                                if (op.title==ops.title){
                                    checkBox.isChecked=true
                                }
                            }
                            checkBox.setOnCheckedChangeListener(object :OnCheckedChangeListener{
                                override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                                    if (p1){
                                        options.forEachIndexed { index, testOptionLanguage ->
                                            if (testOptionLanguage.title==p0!!.text){
                                                answers.add(testOptionLanguage)
                                            }
                                        }
                                    }else{
                                        options.forEachIndexed { index, testOptionLanguage ->
                                            if (testOptionLanguage.title==p0!!.text){
                                                answers.remove(testOptionLanguage)
                                            }
                                        }
                                    }
                                }
                            })
                            binding.llCheckboxAdd.addView(checkBox)
                        }
                    }
                }
            }
        }
    }
    interface ClickListener{
        fun onFileSelect(question: FormQuestionLanguage,pos:Int,type:String,capture: String)
    }
    fun getStringList(optionList:List<Options>):List<String>{
        val list= arrayListOf<String>()
        for (d in optionList){
            list.add(d.title)
        }
        return list;
    }
    fun getComaSeparatedValues(list:List<String>):String{
        var string=""
        for (l in list){
            string=string+l+", "
        }
        return string

    }
    private fun getIndex(spinner: Spinner, myString: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }
    var questionId=0

    fun openFilePick(){

    }
    fun modifyCommaSeparatedString(inputString: String, action: String, vararg items: String): String {
        val existingItems = inputString.split(",").toMutableList()

        when (action.toLowerCase()) {
            "add" -> existingItems.addAll(items)
            "remove" -> existingItems.removeAll(items)
            else -> throw IllegalArgumentException("Invalid action. Use 'add' or 'remove'.")
        }

        return existingItems.joinToString(",")
    }
    fun getHintText(text:String, num:Int,mandatory:String):SpannableStringBuilder{
        return if (mandatory=="YES"){
            Log.e("HintText","mandatory   $mandatory")
            SpannableStringBuilder()
                .bold { append("$num. ") }
                .append(text)
                .color(Color.RED) { append("*") }
        }else{
            Log.e("HintText","mandatory   $mandatory")
            SpannableStringBuilder()
                .bold { append("$num. ") }
                .append(text)
        }
    }
}