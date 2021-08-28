package com.scott.recipecalculator;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RecipeBook {
    private List<Recipe> recipesList = new ArrayList<Recipe>();

    public List<Recipe> getRecipesList() {
        return recipesList;
    }

    // returns the id that the next recipe to be created should use
    public int getNextId(RecipeBook book){
        List<Recipe> recipes = book.getRecipesList();
        if (recipes.size() > 0) {
            return recipes.get(recipes.size() - 1).getId() + 1;
        }else{      // if no recipes have been added then the first should have an index of 0
            return 0;
        }
    }

    public void AddOrEditRecipe(Recipe recipe){
        // EDIT
        for (int i = 0; i < recipesList.size(); i++){
            if(recipesList.get(i).getId() == recipe.getId()){
                recipesList.set(i, recipe);
                Log.d("Scott", "Save Success. Recipe Edited.");
                return;
            }
        }
        // ADD since No Match
        recipesList.add(recipe);
        Log.d("Scott", "Save Success. Recipe Added.");

    }
    // === START GET PREVIOUS OR NEXT RECIPE'S IN THE LIST ===
    // Get Previous Recipe
    public Recipe GetPreviousRecipe(Recipe currentRecipe, PassByReference<String> outputMessage){
        int index = recipesList.indexOf(currentRecipe);
        if (index == -1){
            if (recipesList.size() > 0){                            // if the recipe list contains any recipes, return the last recipe
                outputMessage.Obj = " Showing Last Recipe";
                return recipesList.get(recipesList.size() - 1);
            }
            outputMessage.Obj = "Recipe Not Found";
            return currentRecipe;                   // List does not contain Recipe. Would happen if th eplayer creates a new recipe and then hits Next before recipe is saved
        }else if(index == 0){
            outputMessage.Obj = "Showing First Recipe";
            return currentRecipe;                   // The current Recipe is the Last Recipe
        }else{
            outputMessage.Obj = "Showing Previous Recipe";
            return recipesList.get(index - 1);
        }
    }
    // Get Previous Recipe
    public Recipe GetPreviousRecipe(Recipe currentRecipe){
        int index = recipesList.indexOf(currentRecipe);
        // Recipe Not Found
        if (index == -1){
            if (recipesList.size() > 0){                            // if the recipe list contains any recipes, return the last recipe
                return recipesList.get(recipesList.size() - 1);
            }
            return currentRecipe;                   // List does not contain Recipe. Would happen if th eplayer creates a new recipe and then hits Next before recipe is saved
        }
        // Showing First Recipe
        else if(index == 0){
            return currentRecipe;                   // The current Recipe is the Last Recipe
        }
        // Showing Previous Recipe
        else{
            return recipesList.get(index - 1);
        }
    }

    // Get Next Recipe
    public Recipe GetNextRecipe(Recipe currentRecipe, PassByReference<String> outputMesaage){
        int index = recipesList.indexOf(currentRecipe);
        if (index == -1){
            if (recipesList.size() > 0){                            // if the recipe list contains any recipes, return the last recipe
                outputMesaage.Obj = " Showing Last Recipe";
                return recipesList.get(recipesList.size() - 1);
            }
            outputMesaage.Obj = "Recipe Not Found";
            return currentRecipe;                   // List does not contain Recipe. Would happen if th eplayer creates a new recipe and then hits Next before recipe is saved
        }else if(index == recipesList.size() - 1){
            outputMesaage.Obj = "Showing Last Recipe";
            return currentRecipe;                   // The current Recipe is the Last Recipe
        }else{
            outputMesaage.Obj = "Showing Next Recipe";
            return recipesList.get(index + 1);
        }
    }

    public Recipe GetNextRecipe(Recipe currentRecipe){
        int index = recipesList.indexOf(currentRecipe);
        // Recipe Not Found
        if (index == -1){
            if (recipesList.size() > 0){                            // if the recipe list contains any recipes, return the last recipe
                return recipesList.get(recipesList.size() - 1);
            }
            return currentRecipe;                   // List does not contain Recipe. Would happen if th eplayer creates a new recipe and then hits Next before recipe is saved
        }
        // Showing Last Recipe
        else if(index == recipesList.size() - 1){
            return currentRecipe;                   // The current Recipe is the Last Recipe
        }
        // Showing Next Recipe
        else{
            return recipesList.get(index + 1);
        }
    }
    // === END GET PREVIOUS OR NEXT RECIPE'S IN THE LIST ===


    //
    public boolean DoesPreviousRecipeExist(Recipe currentRecipe){
        int index = recipesList.indexOf(currentRecipe);
        if (index == -1){
            return false;                   // List does not contain Recipe. Would happen if the player creates a new recipe and then hits Next before recipe is saved
        }else if(index == 0){
            return false;                   // The current Recipe is the First Recipe
        }else{
            return true;                    // Previous Recipe Exists
        }
    }
    public boolean DoesNextRecipeExist(Recipe currentRecipe){
        int index = recipesList.indexOf(currentRecipe);
        if (index == -1){
            return false;                   // List does not contain Recipe. Would happen if the player creates a new recipe and then hits Next before recipe is saved
        }else if(index == recipesList.size() - 1){
            return false;                   // The current Recipe is the Last Recipe
        }else{
            return true;                    // Next Recipe Exists
        }
    }


    // Deletes the current recipe. Return the next recipe, If there is no next recipe it return the previous recipe. If no next or previous recipe it returns a black recipe
    public Recipe DeleteAndGetNext(Recipe currentRecipe) {
        Recipe recipeToReturn = new Recipe(getNextId(this));       // A blank recipe which will be returned if no next or previous recipe exists
        // Get the recipe to replace the
        if (DoesNextRecipeExist(currentRecipe)){                        // Try Get next recipe
            recipeToReturn = GetNextRecipe(currentRecipe);
        }
        else if (DoesPreviousRecipeExist(currentRecipe)) {              // If that fails Get Previous Recipe
            recipeToReturn = GetPreviousRecipe(currentRecipe);
        }
        // if that fail use empty recipe and Delete Current
        recipesList.remove(currentRecipe);
        return recipeToReturn;
    }
}
