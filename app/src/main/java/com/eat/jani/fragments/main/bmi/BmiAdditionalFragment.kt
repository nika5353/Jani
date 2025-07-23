package com.eat.jani.fragments.main.bmi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.eat.jani.R
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.databinding.FragmentBmiAdditionalBinding
import com.eat.jani.repositories.health.HealthRepository
import com.eat.jani.viewmodels.HealthViewModelFactory
import com.eat.jani.viewmodels.main.HealthViewModel
import kotlin.getValue

class BmiAdditionalFragment : Fragment() {
    private lateinit var binding: FragmentBmiAdditionalBinding
    private val viewModel: HealthViewModel by navGraphViewModels(R.id.nav_bmi_graph) {
        HealthViewModelFactory(HealthRepository(RetrofitInstance.healthApi))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bmi_additional, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.waist.value = 50
        viewModel.neck.value = 25
        viewModel.hips.value = 75
        viewModel.successfulResponse.value = false

        setUpListeners()

        return binding.root
    }

    private fun setUpListeners() {
        binding.waistSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.updateWaist(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        binding.neckSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.updateNeck(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        binding.hipsSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.updateHips(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.cancelBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.saveBtn.setOnClickListener {
            Log.d("VALIDATE_STEPS", "${viewModel.averageStepsPerDay.value!!.toIntOrNull()!!}")
            if (viewModel.averageStepsPerDay.value!!.startsWith("0") && viewModel.averageStepsPerDay.value!!.length > 1) {
                viewModel.stepsError.value = "არასწორი ფორმატი"
            } else if (viewModel.averageStepsPerDay.value!!.toInt() < 0 || viewModel.averageStepsPerDay.value!!.toInt() > 50000) {
                viewModel.stepsError.value = "შეიყვანე 0-დან 50000-ის ჩათვლით"
            } else {
                Log.d("BMI_ARGUMENTS", "Waist: ${viewModel.waist.value}cm, Neck: ${viewModel.neck.value}cm, Hips: ${viewModel.hips.value}cm")
                /*val waist = viewModel.waist.value
                val neck = viewModel.neck.value
                val hips = viewModel.hips.value
                val averageSteps = viewModel.averageStepsPerDay.value
                val success = viewModel.successfulResponse.value
                val bundle = Bundle().apply {
                    putString("waist", waist.toString())
                    putString("neck", neck.toString())
                    putString("hips", hips.toString())
                    putString("avarageSteps", averageSteps)
                    putBoolean("success", success!!)
                }*/
                findNavController().popBackStack()
            }
        }
    }
}