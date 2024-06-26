package com.appsbit.easyfood.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.appsbit.easyfood.R
import com.appsbit.easyfood.ui.activities.CategoryMealsActivity
import com.appsbit.easyfood.ui.activities.MainActivity
import com.appsbit.easyfood.ui.activities.MealActivity
import com.appsbit.easyfood.ui.adapters.CategoriesAdapter
import com.appsbit.easyfood.ui.adapters.MostPopularAdapter
import com.appsbit.easyfood.databinding.FragmentHomeBinding
import com.appsbit.easyfood.ui.fragments.bottomsheet.MealBottomSheetFragment
import com.appsbit.easyfood.data.models.MealsByCategory
import com.appsbit.easyfood.data.models.Meal
import com.appsbit.easyfood.ui.viewModels.HomeViewModel
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var randomMeal: Meal
    private lateinit var popularItemsAdapter: MostPopularAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter

    companion object {
        const val MEAL_ID = "com.appsbit.easyfood.fragments.idMeal"
        const val MEAL_NAME = "com.appsbit.easyfood.fragments.nameMeal"
        const val MEAL_THUMB = "com.appsbit.easyfood.fragments.thumbMeal"
        const val CATEGORY_NAME = "com.appsbit.easyfood.fragments.categoryName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        popularItemsAdapter = MostPopularAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prparePopularItemsRecyclerView()

        observerRandomMeal()
        onRandomMealClick()

        viewModel.getPopularItems()
        obserPopularItemsLiveData()
        onPopularItemClick()

        prepareCategoriesRecyclerView()
        viewModel.getCategories()
        observeCategoriesLivedata()
        onCategoryClick()

        onPopularItemLongClick()

        onSearchIconClick()

        viewModel.observeSnackbarMessage().observe(viewLifecycleOwner, Observer { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun onSearchIconClick() {
        binding.imgSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun onPopularItemLongClick() {
        popularItemsAdapter.onLongItemClick = { meal ->
            val mealBottomSheetFragment = MealBottomSheetFragment.newInstance(meal.idMeal)
            mealBottomSheetFragment.show(childFragmentManager, "Meal Info")
        }
    }

    private fun onCategoryClick() {
        categoriesAdapter.onItemClick = { category ->
            val intent = Intent(activity, CategoryMealsActivity::class.java)
            intent.putExtra(CATEGORY_NAME, category.strCategory)
            startActivity(intent)
        }
    }

    private fun prepareCategoriesRecyclerView() {
        categoriesAdapter = CategoriesAdapter()
        binding.recViewCategories.apply {
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            adapter = categoriesAdapter
        }
    }

    private fun observeCategoriesLivedata() {
        viewModel.observeCategoriesLivedata().observe(viewLifecycleOwner, Observer { categories ->
            categoriesAdapter.setCategoriesList(categories)
        })
    }

    private fun onPopularItemClick() {
        popularItemsAdapter.onItemClick = { meal ->
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(MEAL_ID, meal.idMeal)
            intent.putExtra(MEAL_NAME, meal.strMeal)
            intent.putExtra(MEAL_THUMB, meal.strMealThumb)
            startActivity(intent)
        }
    }

    private fun prparePopularItemsRecyclerView() {
        binding.recViewMealsPopular.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = popularItemsAdapter
        }
    }

    private fun obserPopularItemsLiveData() {
        viewModel.observePopularItemsLivedata().observe(
            viewLifecycleOwner
        ) { mealList ->
            popularItemsAdapter.setMeals(mealsList = mealList as ArrayList<MealsByCategory>)
        }
    }

    private fun onRandomMealClick() {
        binding.randomMealCard.setOnClickListener {
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(MEAL_ID, randomMeal.idMeal)
            intent.putExtra(MEAL_NAME, randomMeal.strMeal)
            intent.putExtra(MEAL_THUMB, randomMeal.strMealThumb)
            startActivity(intent)
        }
    }

    private fun observerRandomMeal() {
        viewModel.observeRandomMealLiveData().observe(
            viewLifecycleOwner
        ) { value ->
            Glide.with(this@HomeFragment)
                .load(value!!.strMealThumb)
                .into(binding.imgRandomMeal)

            this.randomMeal = value
        }
    }
}