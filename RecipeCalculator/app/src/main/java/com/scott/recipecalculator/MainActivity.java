package com.scott.recipecalculator;

//import android.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import com.scott.recipecalculator.databinding.ActivityMainBinding;
import com.scott.recipecalculator.ui.main.SectionsPagerAdapter;

//import android.view.Menu;
//import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity = null;
    private ActivityMainBinding binding;
    private RecipeFragment recipeFragment;
    private LimitFragment limitFragment;
    private AdjustedFragment adjustedFragment;


    private RecipeBook recipeBook = new RecipeBook();
    private Recipe currentRecipe;


    //Properties


    public RecipeBook getRecipeBook() {
        return recipeBook;
    }

    public void setRecipeBook(RecipeBook recipeBook) {
        this.recipeBook = recipeBook;
    }

    public Recipe getCurrentRecipe() {
        return currentRecipe;
    }

    public void setCurrentRecipe(Recipe currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(sectionsPagerAdapter.getCount() - 1);       // keep all fragments loaded at once
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;
        loadData();

        if (recipeBook.getRecipesList().size() > 0){
            currentRecipe = recipeBook.getRecipesList().get(0);

        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //LOAD RECIPE BOOK FROM FILE
        //Assign Current Recipe
        AssignInitialCurrentRecipe();
    }

    private void AssignInitialCurrentRecipe(){
        // if Recipe Book is Empty then create a new recipe
        if (recipeBook.getRecipesList().size() == 0){
            currentRecipe = new Recipe("", recipeBook.getNextId(recipeBook));       // if the size() is 0 then the id will always be 0
        }
        // else if Recipe book is not empty then assign the first recipe as the current recipe
        else {
            currentRecipe = recipeBook.getRecipesList().get(0);
        }
    }
    public void SetRecipeFragment(RecipeFragment f){
        recipeFragment = f;
    }

    public RecipeFragment getRecipeFragment() {
        return recipeFragment;
    }

    public void SetLimitFragment(LimitFragment f) {
        limitFragment = f;
    }

    public void SetAdjustedFragment(AdjustedFragment f) {
        adjustedFragment = f;
    }


    public void RefreshLimitAndAdjustedFragment(){
        RefreshLimitFragment();
        RefreshAdjustedFragment();
    }
    public void RefreshLimitFragment(){
        limitFragment.SetupFragment(currentRecipe);
    }
    public void RefreshAdjustedFragment(){
        adjustedFragment.SetupFragment();
    }

    public void DisplayAdjustedRecipe(){
        adjustedFragment.CreateUIFromRecipe(currentRecipe, currentRecipe.getIngredientsAdjustedList());
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(recipeBook);
        editor.putString("recipe book", json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("recipe book", null);
        recipeBook = gson.fromJson(json, RecipeBook.class);
        if (recipeBook == null) {
            recipeBook = new RecipeBook();
        }
    }
}