package com.example.com.example.todo

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.com.example.todo.auth.SignInActivity
import com.example.com.example.todo.databinding.FragmentEmployeesBinding
import com.example.com.example.todo.models.User
import com.example.com.example.todo.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EmployeesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("UNREACHABLE_CODE")
class EmployeesFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding : FragmentEmployeesBinding
    private lateinit var employeesAdapter: EmployeesAdapter

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
//        return inflater.inflate(R.layout.fragment_employees, container, false)
        binding = FragmentEmployeesBinding.inflate(layoutInflater)
     apply {
         binding.goTOWorksFragmentBtn.setOnClickListener {
         findNavController().navigate(R.id.action_employeesFragment_to_worksFragment)
     }
         binding.tbEmployees.setOnMenuItemClickListener {
             when (it.itemId) {
                 R.id.logOut -> {
                     showLogOutDialog()
                     true
                 }
                 else -> false
             }
         }



     }

        prepearRvForEmployeeAdapter()
        showAllEmployees()
        return binding.root
    }

private fun prepearRvForEmployeeAdapter() {
    employeesAdapter = EmployeesAdapter()
    binding.rvEmployee.apply {
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = employeesAdapter
    }
}

    private fun showAllEmployees() {
        Utils.showDialog(requireContext())
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val empList = arrayListOf<User>()
                for (employees in snapshot.children) {
                    val currentUser = employees.getValue(User::class.java)
                    if (currentUser?.userType == "Employee") {
                        empList.add(currentUser)
                    }
                }
                employeesAdapter.differ.submitList(empList)
                Utils.hideDialog()
            }

            override fun onCancelled(error: DatabaseError) {
                Utils.apply {
                    hideDialog()
                    showToast(requireContext(), error.message)
                }
            }
        })
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment employeesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EmployeesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



private fun showLogOutDialog() {
    val builder = AlertDialog.Builder(requireContext())
    val alertDialog = builder.create()
    builder.setTitle("Log out")
        .setMessage("Are you sure you want to logout")
        .setPositiveButton("Yes") {_, _ ->
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), SignInActivity::class.java))
            requireActivity().finish()
        }
        .setNegativeButton("No") {_, _ ->
            alertDialog.dismiss()
        }
        .show()
        .setCancelable(false)
}





}