package com.example.android.favdish.view.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
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
import com.example.android.favdish.databinding.FragmentFavoriteDishesBinding
import com.example.android.favdish.model.entities.FavDish
import com.example.android.favdish.view.activity.AddUpdateDishActivity
import com.example.android.favdish.view.activity.MainActivity
import com.example.android.favdish.view.adapters.FavDishAdapter
import com.example.android.favdish.viewmodel.DashboardViewModel
import com.example.android.favdish.viewmodel.FavDishViewModel
import com.example.android.favdish.viewmodel.FavDishViewModelFactory

class FavoriteDishesFragment : Fragment() {

    private var mBinding: FragmentFavoriteDishesBinding? = null

    private lateinit var dashboardViewModel: DashboardViewModel

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application as FavDishApplication).repository))
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentFavoriteDishesBinding.inflate(inflater, container, false);
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding!!.rvFavoriteDishesList.layoutManager = GridLayoutManager(requireContext(),2)
        val favDishAdapter = FavDishAdapter(this@FavoriteDishesFragment)

        mBinding!!.rvFavoriteDishesList.adapter = favDishAdapter

        mFavDishViewModel.favoriteDishesList.observe(viewLifecycleOwner, {
            dishes ->
            dishes.let {
                if (it.isNotEmpty()) {
                    mBinding!!.rvFavoriteDishesList.visibility = View.VISIBLE
                    mBinding!!.tvNoFavoriteDishesAddedYet.visibility =View.GONE
                    favDishAdapter.dishesList(it)
                } else {
                    mBinding!!.rvFavoriteDishesList.visibility = View.GONE
                    mBinding!!.tvNoFavoriteDishesAddedYet.visibility =View.VISIBLE

                }
            }
        })
    }

    fun dishDetails(favDish: FavDish){
        findNavController().navigate(FavoriteDishesFragmentDirections.actionFavoriteDishesToDishDetails(
            favDish
        ))
        if(requireActivity() is MainActivity) {
            (activity as MainActivity)?.hideBottomNavigationView()
        }
    }

    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity) {
            (activity as MainActivity)?.showBottomNavigationView()
        }
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}