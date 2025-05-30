package com.eat.jani.fragments.main.bmi

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.eat.jani.R
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.databinding.DialogBmiResultBinding
import com.eat.jani.databinding.FragmentBmiBinding
import com.eat.jani.models.enums.Gender
import com.eat.jani.repositories.health.HealthRepository
import com.eat.jani.viewmodels.HealthViewModelFactory
import com.eat.jani.viewmodels.main.HealthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt

class BmiFragment : Fragment() {
    private lateinit var binding: FragmentBmiBinding
    private val viewModel: HealthViewModel by navGraphViewModels(R.id.nav_bmi_graph) {
        HealthViewModelFactory(HealthRepository(RetrofitInstance.healthApi))
    }

    // Age Counter
    private var increaseJob: Job? = null
    private var decreaseJob: Job? = null
    // Args
    private var waist: String? = null
    private var neck: String? = null
    private var hips: String? = null
    private var averageSteps: String? = null
    private var success: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bmi, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        waist = arguments?.getString("waist")
        neck = arguments?.getString("neck")
        hips = arguments?.getString("hips")
        averageSteps = arguments?.getString("avarageSteps")
        success = arguments?.getBoolean("success")


        if (waist != null && neck != null && hips != null && success != null) {
            viewModel.waist.value = waist!!.toInt()
            viewModel.neck.value = neck!!.toInt()
            viewModel.hips.value = hips!!.toInt()
            viewModel.averageStepsPerDay.value = averageSteps
            viewModel.successfulResponse.value = success
        }

        setupSpinner()
        setUpObservers()
        setUpListeners()

        return binding.root
    }

    private fun setupSpinner() {
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            viewModel.activityLevelsDisplay
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createCustomView(position, convertView, parent)
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.spinner_dropdown_item, parent, false)
                val textView = view.findViewById<TextView>(R.id.spinnerDropDownText)
                textView.text = getItem(position)
                return view
            }

            private fun createCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.spinner_item, parent, false)
                val textView = view.findViewById<TextView>(R.id.spinnerText)
                textView.text = getItem(position)
                return view
            }
        }

        binding.spinner.adapter = adapter
    }

    private fun setUpObservers() {
        viewModel.gender.observe(viewLifecycleOwner, Observer { gender ->
            val selectedBackground = R.drawable.gender_selected
            val unselectedBackground = R.drawable.gender_unselected

            binding.maleLayout.setBackgroundResource(
                if (gender == Gender.Male) selectedBackground else unselectedBackground
            )
            binding.femaleLayout.setBackgroundResource(
                if (gender == Gender.Female) selectedBackground else unselectedBackground
            )
        })
        viewModel.successfulResponse.observe(viewLifecycleOwner) { success ->
            if(success) {
                showBmiResultDialog()
            }
        }
        viewModel.generalError.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.loadingAnimation.visibility = if (isLoading) {
                binding.loadingAnimation.playAnimation()
                View.VISIBLE
            } else {
                binding.loadingAnimation.cancelAnimation()
                View.GONE
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpListeners() {
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedText = viewModel.activityLevelsDisplay[position]
                viewModel.setActivityLevelByDisplay(selectedText)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // არ არის საჭირო
            }
        }
        binding.heightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.updateHeight(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        binding.agePlusButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.increaseAge()
                    increaseJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(500)
                        while (isActive) {
                            viewModel.increaseAge()
                            delay(50)
                        }
                    } as CoroutineScope as Job?
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    increaseJob!!.cancel()
                }
            }
            false
        }
        binding.ageMinusButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.decreaseAge()
                    decreaseJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(500)
                        while (isActive) {
                            viewModel.decreaseAge()
                            delay(50)
                        }
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    decreaseJob!!.cancel()
                }
            }
            false
        }
        binding.weightPlusButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.increaseWeight()
                    increaseJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(400)
                        while (isActive) {
                            viewModel.increaseWeight()
                            delay(20)
                        }
                    } as CoroutineScope as Job?
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    increaseJob!!.cancel()
                }
            }
            false
        }
        binding.weightMinusButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.decreaseWeight()
                    decreaseJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(400)
                        while (isActive) {
                            viewModel.decreaseWeight()
                            delay(20)
                        }
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    decreaseJob!!.cancel()
                }
            }
            false
        }
        binding.additionalInfoLayout.setOnClickListener {
            findNavController().navigate(R.id.action_bmiFragment_to_bmiAdditionalFragment)
        }
    }

    private fun showBmiResultDialog() {
        val dialogBinding = DialogBmiResultBinding.inflate(LayoutInflater.from(requireContext()))

        dialogBinding.viewModel = viewModel
        dialogBinding.lifecycleOwner = viewLifecycleOwner

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        when (viewModel.healthResponse.value?.healthStatus) {
            "Underweight" -> {
                dialogBinding.healthStatusText.setTextColor("#FFEDAD".toColorInt())
                dialogBinding.healthStatusText.text = "დაბალწონიანი"
            }
            "Normal" -> {
                dialogBinding.healthStatusText.setTextColor("#FFE670".toColorInt())
                dialogBinding.healthStatusText.text = "ნორმალური"
            }
            "Overweight" -> {
                dialogBinding.healthStatusText.setTextColor("#FFE636".toColorInt())
                dialogBinding.healthStatusText.text = "ჭარბწონიანი"
            }
            "Obesity" -> {
                dialogBinding.healthStatusText.setTextColor("#FFDF00".toColorInt())
                dialogBinding.healthStatusText.text = "ზედმეტად ჭარბწონიანი"
            }
        }

        dialogBinding.btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
