package com.appsbit.easyfood.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.appsbit.easyfood.ui.activities.MainActivity
import com.appsbit.easyfood.ui.adapters.CategoriesAdapter
import com.appsbit.easyfood.databinding.FragmentCategoriesBinding
import com.appsbit.easyfood.ui.activities.CategoryMealsActivity
import com.appsbit.easyfood.ui.viewModels.HomeViewModel

class CategoriesFragment : Fragment() {

    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRecyclerView()

        observeCategory()

        prepareCategoriesRecyclerView()
        viewModel.getCategories()
        observeCategoriesLivedata()
        onCategoryClick()
    }

    private fun observeCategory() {
        viewModel.observeCategoriesLivedata().observe(viewLifecycleOwner, Observer { categories ->
            categoriesAdapter.setCategoriesList(categories)
        })
    }

    private fun prepareRecyclerView() {
        categoriesAdapter = CategoriesAdapter()
        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            adapter = categoriesAdapter
        }
    }

    private fun onCategoryClick() {
        categoriesAdapter.onItemClick = { category ->
            val intent = Intent(activity, CategoryMealsActivity::class.java)
            intent.putExtra(HomeFragment.CATEGORY_NAME, category.strCategory)
            startActivity(intent)
        }
    }

    private fun prepareCategoriesRecyclerView() {
        categoriesAdapter = CategoriesAdapter()
        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            adapter = categoriesAdapter
        }
    }

    private fun observeCategoriesLivedata() {
        viewModel.observeCategoriesLivedata().observe(viewLifecycleOwner, Observer { categories ->
            categoriesAdapter.setCategoriesList(categories)
        })
    }
}