package com.example.com.example.todo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.com.example.todo.databinding.ItemViewEmployeesBinding
import com.example.com.example.todo.models.User

class EmployeesAdapter: RecyclerView.Adapter<EmployeesAdapter.EmployeeViewHolder>() {
class EmployeeViewHolder(val binding: ItemViewEmployeesBinding): ViewHolder(binding.root)


    val diffUtil = object : DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        return EmployeeViewHolder(ItemViewEmployeesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employeeDatas = differ.currentList[position]
        holder.binding.apply {
//            solve the error here
            // handel image
//            tvEmployeeName.text  = employeeDatas.empName
            tvEmployeeName.text  = employeeDatas.empName

        }
        holder.itemView.setOnClickListener {
            val action = EmployeesFragmentDirections.actionEmployeesFragmentToWorksFragment(employeeDatas)
            Navigation.findNavController(it).navigate(action)
        }
    }

}