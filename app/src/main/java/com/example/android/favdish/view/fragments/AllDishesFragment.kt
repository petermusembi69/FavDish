package com.example.android.favdish.view.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.favdish.R
import com.example.android.favdish.application.FavDishApplication
import com.example.android.favdish.databinding.FragmentAllDishesBinding
import com.example.android.favdish.model.database.FavDishRepository
import com.example.android.favdish.model.entities.FavDish
import com.example.android.favdish.view.activity.AddUpdateDishActivity
import com.example.android.favdish.view.activity.MainActivity
import com.example.android.favdish.view.adapters.FavDishAdapter
import com.example.android.favdish.viewmodel.FavDishViewModel
import com.example.android.favdish.viewmodel.FavDishViewModelFactory
import com.example.android.favdish.viewmodel.HomeViewModel

class AllDishesFragment : Fragment() {

    private lateinit var mBinding: FragmentAllDishesBinding

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireContext(),2)
        val favDishAdapter = FavDishAdapter(this@AllDishesFragment)

        mBinding.rvDishesList.adapter = favDishAdapter

        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) {
            dishes ->
            dishes.let {
                if (it.isNotEmpty()) {
                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility =View.GONE
                    favDishAdapter.dishesList(it)
                } else {
                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility =View.VISIBLE

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentAllDishesBinding.inflate(inflater, container, false);
        return mBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_add_dish -> {
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun dishDetails(favDish: FavDish){
        findNavController().navigate(AllDishesFragmentDirections.actionAllDishesToDishDetails(
            favDish
        ))
        if(requireActivity() is MainActivity) {
            (activity as MainActivity)?.hideBottomNavigationView()
        }
    }

    fun deleteDish(favDish: FavDish){
        var builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Delete Dish")
        builder.setMessage("Are you sure you want to delete this Dish")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){ dialogInterface,_ ->
            mFavDishViewModel.delete(favDish)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("No"){ dialogInterface,_ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()

    }
    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity) {
            (activity as MainActivity)?.showBottomNavigationView()
        }
    }
}