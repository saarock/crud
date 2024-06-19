package com.example.com.example.todo

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.com.example.todo.databinding.FragmentAssignWorkBinding
import com.example.com.example.todo.models.Works
import com.example.com.example.todo.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [AssignWorkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AssignWorkFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAssignWorkBinding
    private var priority = "1";
    val employeeData by navArgs<AssignWorkFragmentArgs>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAssignWorkBinding.inflate(layoutInflater)
        binding.tbAssignWork.apply {
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        setPriority()
        setDate()
        binding.btnDone.setOnClickListener {
            Utils.showDialog(requireContext())
            val workTitle = binding.workTitle.text.toString()
            val workDesc = binding.taskDesc.text.toString()
            val workLastDate = binding.tvDate.text.toString()


            if (workTitle.isEmpty()) {
                Utils.apply {
                    hideDialog()
                    showToast(requireContext(), "Title required")
                }
            }
            else if (workLastDate == "Last Date") {
                Utils.apply {
                    hideDialog()
                    showToast(requireContext(), "Date requried")
                }
            } else {
                //save work
                val empId = employeeData.employeeDetail.empId
                val bossId = FirebaseAuth.getInstance().currentUser?.uid
                val workRoom = bossId  + empId

                val work = Works (
                    workTitle = workTitle,
                    workDesc = workDesc,
                    workPriority =  priority,
                    workLastDate = workLastDate,
                    workStatus = "1"
                )

                FirebaseDatabase.getInstance().getReference("Works").child(workRoom).push().setValue(work)
                    .addOnSuccessListener {
                        Utils.hideDialog()
                        Utils.showToast(requireContext(), "Work has been Assign to ${employeeData.employeeDetail.empName}")
                        val action = AssignWorkFragmentDirections.actionAssignWorkFragmentToWorksFragment(employeeData.employeeDetail)
                        findNavController().navigate(action)
                    }


            }


        }
        return binding.root
//        return inflater.inflate(R.layout.fragment_assign_work, container, false)
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment assignWorkFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AssignWorkFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun setPriority() {
        binding.apply {
            greenOval.setOnClickListener {
                Utils.showToast(requireContext(), "Priority: Low")
                priority =  "1"
                binding.greenOval.setImageResource(0)
            }

            yellowOval.setOnClickListener {
                Utils.showToast(requireContext(), "Priority: Medium")
                priority =  "2"
                binding.greenOval.setImageResource(0)
                binding.yellowOval.setImageResource(0)
                binding.redOval.setImageResource(0)
            }

            greenOval.setOnClickListener {
                Utils.showToast(requireContext(), "Priority: Low")
                priority =  "1"
                binding.greenOval.setImageResource(0)
                binding.yellowOval.setImageResource(0)
                binding.redOval.setImageResource(0)
            }

            redOval.setOnClickListener {
                Utils.showToast(requireContext(), "Priority: High")
                priority =  "3"
                binding.greenOval.setImageResource(0)
                binding.yellowOval.setImageResource(0)
                binding.redOval.setImageResource(0)
            }
        }
    }

    private fun setDate() {
        val myCalender = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalender.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLable(myCalender)
            }
        }

        binding.greenOval.setImageResource(R.drawable.tick_icon)
        binding.datePicker.setOnClickListener {
            DatePickerDialog(requireContext(), datePicker, myCalender.get(Calendar.YEAR), myCalender.get(Calendar.MONTH), myCalender.get(
                Calendar.DAY_OF_MONTH
            )).show()
        }
    }
    private  fun updateLable(myCalender: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        binding.tvDate.text = sdf.format(myCalender.time)
    }

}