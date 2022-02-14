package com.example.android.favdish.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.favdish.R
import com.example.android.favdish.databinding.ItemDishLayoutBinding
import com.example.android.favdish.model.entities.FavDish
import com.example.android.favdish.view.fragments.AllDishesFragment
import com.example.android.favdish.view.fragments.FavoriteDishesFragment

class FavDishAdapter(private val fragment: Fragment) : RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    private var dishes: List<FavDish> = listOf()

    class ViewHolder(view: ItemDishLayoutBinding): RecyclerView.ViewHolder(view.root) {
        val ivDishImage = view.ivDishImage
        val tvTitle = view.tvDishTitle
        val ibMore = view.ibMore
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]
        Glide.with(fragment)
            .load(dish.image)
            .into(holder.ivDishImage)
        holder.tvTitle.text = dish.title
        holder.itemView.setOnClickListener {
            if(fragment is AllDishesFragment) {
                fragment.dishDetails(dish)
            }
            if(fragment is FavoriteDishesFragment) {
                fragment.dishDetails(dish)
            }
        }
        holder.ibMore.setOnClickListener {
            val popup = PopupMenu(fragment.context,holder.ibMore)
            popup.menuInflater.inflate(R.menu.menu_adapter,popup.menu)

            popup.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_edit_dish) {

                }else if (it.itemId == R.id.action_delete_dish) {

                }
                true
            }
            popup.show()
        }
        if(fragment is AllDishesFragment) {
            holder.ibMore.visibility = View.VISIBLE
        }else if(fragment is FavoriteDishesFragment) {
            holder.ibMore.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemDishLayoutBinding = ItemDishLayoutBinding.inflate(
            LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    fun dishesList(list: List<FavDish>) {
        dishes = list
        notifyDataSetChanged()
    }
}