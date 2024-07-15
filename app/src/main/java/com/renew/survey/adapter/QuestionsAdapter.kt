package com.renew.survey.adapter

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.icu.lang.UProperty.INT_START
import android.text.Editable
import android.text.InputType
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.color
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.RangeSlider
import com.renew.survey.R
import com.renew.survey.databinding.ItemQuestionLayoutBinding
import com.renew.survey.request.MultiSelectItem
import com.renew.survey.room.entities.FormQuestionLanguage
import com.renew.survey.room.entities.Options
import com.renew.survey.utilities.PreferenceManager
import com.renew.survey.utilities.UtilMethods
import com.renew.survey.views.MapManagerActivity
import java.util.Calendar


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
    private var lastInput: String = ""
    var mainPosition = 0
    var subPosition = 0
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
            Log.d("dependencyCheck",list.toString())
            //setIsRecyclable(false)
            with(list[position]){
                when(question_type){
                    "DATETIME","TEXT","NUMBER","GEO_LOCATION","DATE","TIME","EMAIL","MAP","LOOP","SUB"->{
                        val form = this
                        val pos = position
                        binding.llEdittext.visibility = View.VISIBLE
                        binding.llMultiselect.visibility = View.GONE
                        binding.llSpinner.visibility = View.GONE
                        binding.llRadio.visibility = View.GONE
                        binding.llRange.visibility = View.GONE
                        binding.llCheckbox.visibility = View.GONE
                        binding.llFile.visibility = View.GONE
                        binding.llRating.visibility = View.GONE
                        binding.txtEditTextLable.text =
                            getHintText(this.title, position + 1, this.is_mandatory)
                        /*if (this.parent_question_id == null || this.parent_question_id == 0 ) {
                            mainPosition = position+1
                            subPosition = 0
                            binding.txtEditTextLable.text =
                                getHintText(this.title, position + 1, this.is_mandatory)
                        } else {
                            subPosition = subPosition + 1
                            binding.txtEditTextLable.text =
                                getHintText2(this.title, mainPosition.toString()+"."+subPosition, this.is_mandatory)
                        }*/
                       Log.d("checktitle",getHintText(this.title,position+1,this.is_mandatory).toString())

                        binding.edittext.addTextChangedListener(object :TextWatcher{
                            override fun beforeTextChanged(
                                p0: CharSequence?,
                                p1: Int,
                                p2: Int,
                                p3: Int
                            ) {}

                            override fun onTextChanged(
                                p0: CharSequence?,
                                p1: Int,
                                p2: Int,
                                p3: Int
                            ) {
                                if (binding.edittext.isFocused){
                                    answer=p0.toString()
                                    var currentinput = p0.toString()
                                    if (question_type == "LOOP") {
                                        if (currentinput != lastInput) {
                                            lastInput = p0.toString()
                                            Log.d("checkcallintimes","times")
                                            clickListener.onLoopSelect(
                                                form,
                                                p0.toString()
                                            )
                                        }
                                    }
                                }
                            }

                            override fun afterTextChanged(p0: Editable?) {
                            }
                        })
                        binding.edittext.setText(this.answer)
                        if (question_type=="DATETIME" || question_type=="DATE" || question_type=="TIME" || question_type=="GEO_LOCATION" || question_type=="MAP"){
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
                                            this.answer=UtilMethods.getFormattedDate(calendar.time,this.format)
                                        }, year, month, day)
                                        dpd.show()
                                    }
                                    "TIME" -> {
                                        val tpd = TimePickerDialog(context,TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                                            val calendar=Calendar.getInstance()
                                            calendar[Calendar.HOUR]=i
                                            calendar[Calendar.MINUTE]=i2
                                            binding.edittext.setText(UtilMethods.getFormattedDate(calendar.time,this.format!!))
                                            this.answer=UtilMethods.getFormattedDate(calendar.time,this.format)
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
                                                this.answer=UtilMethods.getFormattedDate(calendar.time,this.format)
                                            },hour,minute,false)
                                            tpd.show()
                                            binding.edittext.setText(UtilMethods.getFormattedDate(calendar.time,""))
                                        }, year, month, day)
                                        dpd.show()

                                    }
                                    "GEO_LOCATION" -> {
                                        binding.edittext.setText(preferenceManager.getLocation())
                                        if (preferenceManager.getLocation()==""){
                                            UtilMethods.showToast(context,"Location not available. Please make sure that you enabled the location and internet in your device")
                                        } else {
                                            this.answer=preferenceManager.getLocation()
                                        }
                                    }
                                    "MAP" -> {
                                        binding.edittext.setOnClickListener{
                                            clickListener.onFileSelect(this,position, this.allowed_file_type!!,this.question_type)
                                        }
                                        if (this.answer!=""){
                                            binding.edittext.setText("Done")
                                        }
                                    }
                                }
                            }
                        }else{
                            binding.edittext.isFocusable=true
                            binding.edittext.isFocusableInTouchMode=true
                            binding.edittext.isEnabled=true
                        }
                        when(question_type){
                            "LOOP"->{
                                binding.edittext.inputType=InputType.TYPE_CLASS_NUMBER
                            }
                            "NUMBER"->{
                                binding.edittext.inputType=InputType.TYPE_CLASS_NUMBER
                            }
                            "TEXT"->{
                                binding.edittext.inputType=InputType.TYPE_CLASS_TEXT
                            }
                            "EMAIL"->{
                                binding.edittext.inputType=InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
                            }
                            "MAP"->{
                                binding.edittext.inputType=InputType.TYPE_NULL
                            }
                            else->{
                                binding.edittext.inputType=InputType.TYPE_CLASS_TEXT
                            }
                        }
                    }
                    "SINGLE_SELECT"->{
                        var formQuestionData = this
                        var pos = position
                        var check = true
                        binding.llEdittext.visibility = View.GONE
                        binding.llMultiselect.visibility = View.GONE
                        binding.llSpinner.visibility = View.VISIBLE
                        binding.llRadio.visibility = View.GONE
                        binding.llCheckbox.visibility = View.GONE
                        binding.llRange.visibility = View.GONE
                        binding.llFile.visibility = View.GONE
                        binding.llRating.visibility = View.GONE
                        binding.txtSpLable.text = getHintText(this.title,position+1,this.is_mandatory)
                        binding.spinner.adapter=CustomSpinnerAdapter(context,getStringList(this.options))
                        if (this.answer!=null && this.answer != ""){
                            check = false
                            binding.spinner.setSelection(getIndex(binding.spinner,this.answer!!))
                        }

                        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                if (position>0){
                                    answer=binding.spinner.selectedItem.toString()
                                    if (formQuestionData.has_dependancy_question.equals("YES") && check) {
                                        clickListener.onDependentSelect(
                                            formQuestionData,
                                            pos,
                                            binding.spinner.selectedItem.toString()
                                        )
                                    } else {
                                        check = true
                                    }
                                }
                            }
                        }
                    }
                    "MULTI_SELECT"->{
                        binding.llEdittext.visibility = View.GONE
                        binding.llMultiselect.visibility = View.VISIBLE
                        binding.llSpinner.visibility = View.GONE
                        binding.llRadio.visibility = View.GONE
                        binding.llRange.visibility = View.GONE
                        binding.llFile.visibility = View.GONE
                        binding.llCheckbox.visibility = View.GONE
                        binding.llRating.visibility = View.GONE
                        binding.txtMultiselect.text = getHintText(this.title,position+1,this.is_mandatory)
                        binding.multiselect.setText(this.answer)
                        binding.multiselect.setOnClickListener {
                            val dialog=Dialog(context)
                            dialog.setContentView(R.layout.multi_select_layout_dialog)
                            dialog.window!!
                                .setLayout(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            val recyclerView=dialog.findViewById<RecyclerView>(R.id.recyclerView)
                            val text=dialog.findViewById<TextView>(R.id.text)
                            val button=dialog.findViewById<Button>(R.id.button)
                            recyclerView.layoutManager=LinearLayoutManager(context)
                            recyclerView.adapter=MultiselectAdapterAdapter(context,this.options)
                            button.setOnClickListener {
                                dialog.cancel()
                                val selectedList= arrayListOf<String>()
                                for (x in options){
                                    if (x.selected!!){
                                        selectedList.add(x.title)
                                    }
                                }
                                answer=getComaSeparatedValues(selectedList)
                                binding.multiselect.setText(getComaSeparatedValues(selectedList))
                                Log.e("SelectedItems","items ${selectedList.toString()}")
                            }
                            dialog.show()
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
                                    answer=compoundButton.text.toString()
                                    if (this.has_dependancy_question.equals("YES")) {
                                        clickListener.onDependentSelect(
                                            this,
                                            position,
                                            compoundButton.text.toString()
                                        )
                                    }
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
                                answer="${slider.values[0]},${slider.values[1]}"
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
                            Log.d("captureImageCheck",this.question_type)
                            clickListener.onFileSelect(this,position, this.allowed_file_type!!,this.question_type)
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
                            checkBox.setOnCheckedChangeListener(object :OnCheckedChangeListener{
                                override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                                    answer = if (p1){
                                        modifyCommaSeparatedString(answer!!,"add",p0!!.text.toString())
                                    }else{
                                        modifyCommaSeparatedString(answer!!,"remove",p0!!.text.toString())
                                    }
                                }
                            })
                            binding.llCheckboxAdd.addView(checkBox)
                        }
                    }
                    "RATING"->{
                        binding.llEdittext.visibility = View.GONE
                        binding.llMultiselect.visibility = View.GONE
                        binding.llSpinner.visibility = View.GONE
                        binding.llRadio.visibility = View.GONE
                        binding.llRange.visibility = View.GONE
                        binding.llFile.visibility = View.GONE
                        binding.llCheckbox.visibility = View.GONE
                        binding.llRating.visibility = View.VISIBLE
                        binding.ratingBar.numStars=this.max_length.toInt()
                        binding.ratingBar.stepSize=this.min_length.toFloat()
                        binding.txtRatingLable.text = getHintText(this.title,position+1,this.is_mandatory)
                        if(this.answer!=""){
                            binding.ratingBar.rating=this.answer!!.toFloat()
                        }
                        binding.ratingBar.setOnRatingBarChangeListener { ratingBar, fl, b -> answer="$fl" }
                    }
                }
            }
        }
    }
    interface ClickListener{
        fun onFileSelect(question: FormQuestionLanguage,pos:Int,type:String,capture: String)
        fun onDependentSelect(question: FormQuestionLanguage,pos:Int,answer: String)
        fun onLoopSelect(question: FormQuestionLanguage,answer: String)
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
    fun getHintText2(text:String, num:String,mandatory:String):SpannableStringBuilder{
        return if (mandatory=="YES"){
            Log.e("HintText","mandatory   $mandatory")
            SpannableStringBuilder()
                .bold { append("$num ") }
                .append(text)
                .color(Color.RED) { append("*") }
        }else{
            Log.e("HintText","mandatory   $mandatory")
            SpannableStringBuilder()
                .bold { append("$num ") }
                .append(text)
        }
    }
}