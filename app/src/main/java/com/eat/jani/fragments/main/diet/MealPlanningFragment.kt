package com.eat.jani.fragments.main.diet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.eat.jani.R
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.databinding.FragmentMealPlanningBinding
import com.eat.jani.models.enums.PlanType
import com.eat.jani.repositories.health.HealthRepository
import com.eat.jani.viewmodels.MealPlanningViewModelFactory
import com.eat.jani.viewmodels.main.MealPlanningViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MealPlanningFragment : Fragment() {
    private lateinit var binding: FragmentMealPlanningBinding
    private val viewModel: MealPlanningViewModel by navGraphViewModels(R.id.nav_meal_plan_graph) {
        MealPlanningViewModelFactory(HealthRepository(RetrofitInstance.healthApi))
    }

    // Target Weight Counter
    private var increaseJob: Job? = null
    private var decreaseJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_meal_planning, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.getInfo()
        setupSpinner()
        setUpObservers()
        setUpListeners()

        return binding.root
    }

    private fun setupSpinner() {
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            viewModel.dietDistributionDisplay
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
        viewModel.planType.observe(viewLifecycleOwner) { planType ->
            val selectedBackground = R.drawable.gender_selected
            val unselectedBackground = R.drawable.gender_unselected
            val weight = viewModel.healthInfo.value?.weight ?: 0.0
            val target = viewModel.targetWeight.value?.toDoubleOrNull() ?: 0.0

            binding.gainLayout.setBackgroundResource(
                if (planType == PlanType.Gain) {
                    viewModel.targetWeight.value = (if (weight > target) weight else target).toString()
                    selectedBackground
                } else {
                    unselectedBackground
                }
            )
            binding.lossLayout.setBackgroundResource(
                if(planType == PlanType.Loss) {
                    viewModel.targetWeight.value = (if (weight < target) weight else target).toString()
                    selectedBackground
                } else {
                    unselectedBackground
                }
            )
            binding.maintenanceLayout.setBackgroundResource(
                if (planType == PlanType.Maintenance) {
                    viewModel.targetWeight.value = weight.toString()
                    binding.weightInput.setFocusable(false)
                    binding.weightInput.isFocusableInTouchMode = false
                    selectedBackground
                } else {
                    binding.weightInput.setFocusable(true)
                    binding.weightInput.isFocusableInTouchMode = true
                    unselectedBackground
                }
            )
        }
        viewModel.timeFrame.observe(viewLifecycleOwner) { timeFrame ->
            val selectedBackground = R.drawable.gender_selected
            val unselectedBackground = R.drawable.gender_unselected

            binding.month1.setBackgroundResource(
                if (timeFrame == 1) selectedBackground else unselectedBackground
            )
            binding.month3.setBackgroundResource(
                if(timeFrame == 3) selectedBackground else unselectedBackground
            )
            binding.month6.setBackgroundResource(
                if (timeFrame == 6) selectedBackground else unselectedBackground
            )
        }
        viewModel.mealsPerDay.observe(viewLifecycleOwner) { mealsPerDay ->
            val selectedBackground = R.drawable.gender_selected
            val unselectedBackground = R.drawable.gender_unselected

            binding.mealTimes1.setBackgroundResource(
                if (mealsPerDay == 1) selectedBackground else unselectedBackground
            )
            binding.mealTimes2.setBackgroundResource(
                if(mealsPerDay == 2) selectedBackground else unselectedBackground
            )
            binding.mealTimes3.setBackgroundResource(
                if (mealsPerDay == 3) selectedBackground else unselectedBackground
            )
            binding.mealTimes4.setBackgroundResource(
                if (mealsPerDay == 4) selectedBackground else unselectedBackground
            )
            binding.mealTimes5.setBackgroundResource(
                if (mealsPerDay == 5) selectedBackground else unselectedBackground
            )
        }
        viewModel.getInfoSuccess.observe(viewLifecycleOwner) {  success ->
            if(!success) {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.mealPlanningFragment, true) // ამით წაიშლება mealPlanningFragment BackStack-იდან
                    .build()
                AlertDialog.Builder(requireContext())
                    .setTitle("ყურადღება!!!")
                    .setMessage("თქვენ არ გაქვთ შევსებული თქვენი ჯანმრთელობის მონაცემები. \nდიეტის დასაგეგმვად საჭიროა 'BMI და კალორიების' გვერდზე შეავსოთ თქვენი მონაცემები")
                    .setNegativeButton("გასაგებია") { dialog, _ ->
                        dialog.dismiss()
                        requireActivity().onBackPressed()
                    }
                    .setCancelable(true)
                    .setOnCancelListener {
                        requireActivity().onBackPressed()
                    }
                    .show()
            }
        }
        viewModel.dietPlanSuccess.observe(viewLifecycleOwner) { success ->
            if (!success) {
                val weight = viewModel.healthInfo.value?.weight ?: 0.0
                val target = viewModel.targetWeight.value?.toDoubleOrNull() ?: 0.0
                val errorText = if (viewModel.planType.value == PlanType.Gain && weight >= target) {
                    "თქვენ სამიზნე წონა აირჩიეთ არასწორად. \nსამიზნე წონა მეტი უნდა იყოს თქვენს წონაზე"
                } else if (viewModel.planType.value == PlanType.Loss && weight <= target) {
                    "თქვენ სამიზნე წონა აირჩიეთ არასწორად. \nსამიზნე წონა ნაკლები უნდა იყოს თქვენს წონაზე"
                } else {
                    "თქვენ შეიყვანეთ არარეალური ან არაჯანსაღი მიზანი. \nთქვენი წონიდან ${viewModel.targetWeight.value}კგ-მდე ${viewModel.timeFrame.value} თვეში მიღწევა შეუძლებელია. გაზარდეთ ხანგრძლივობა ან აირჩიეთ შედარებით ნაკლები სამიზნე წონა."
                }
                AlertDialog.Builder(requireContext())
                    .setTitle("დაგეგმვა ვერ მოხდა!")
                    .setMessage(errorText)
                    .setNegativeButton("გასაგებია") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(true)
                    .show()
            } else {
                findNavController().navigate(R.id.action_mealPlanningFragment_to_mealPlanningResultFragment)
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
                val selectedText = viewModel.dietDistributionDisplay[position]
                viewModel.setDietDistribution(selectedText)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // არ არის საჭირო
            }
        }
        binding.weightPlusButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.increaseTargetWeight()
                    increaseJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(400)
                        while (isActive) {
                            viewModel.increaseTargetWeight()
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
                    viewModel.decreaseTargetWeight()
                    decreaseJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(400)
                        while (isActive) {
                            viewModel.decreaseTargetWeight()
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
        binding.hourPlusBtn.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.increaseActivityTimeHours()
                    decreaseJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(400)
                        while (isActive) {
                            viewModel.increaseActivityTimeHours()
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
        binding.hourMinusBtn.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.decreaseActivityTimeHours()
                    decreaseJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(400)
                        while (isActive) {
                            viewModel.decreaseActivityTimeHours()
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
        binding.minutePlusBtn.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.increaseActivityTimeMinutes()
                    decreaseJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(400)
                        while (isActive) {
                            viewModel.increaseActivityTimeMinutes()
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
        binding.minuteMinusBtn.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.decreaseActivityTimeMinutes()
                    decreaseJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(400)
                        while (isActive) {
                            viewModel.decreaseActivityTimeMinutes()
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
        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.doesPhysicalActivity.value = true
                binding.linearLayout21.visibility = View.VISIBLE
                viewModel.activityTime.value = "${viewModel.hours.value}:${viewModel.minutes.value}"
            } else {
                viewModel.doesPhysicalActivity.value = false
                binding.linearLayout21.visibility = View.INVISIBLE
                viewModel.activityTime.value = null
            }
        }
    }
}