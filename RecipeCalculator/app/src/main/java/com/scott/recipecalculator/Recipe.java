package com.scott.recipecalculator;

import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Recipe {
   int id;              // used to tell if the recipe being added to the list is a new recipe or an edited version of an existing recipe
   String recipeTitle;
   List<IngredientData> ingredientsList = new ArrayList<>();
   List<IngredientData> ingredientsLimitersList = new ArrayList<IngredientData>();
   List<IngredientData> ingredientsAdjustedList = new ArrayList<IngredientData>();

   // === CONSTRUCTOR START ===
    public  Recipe(String recipeTitle, int id){
        this.recipeTitle = recipeTitle;
        this.id = id;
    }

    public  Recipe(int id){
        this.id = id;
    }

    // === CONSTRUCTOR END ===

    // === PROPERTIES START ===

    public int getId() {
        return id;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    public List<IngredientData> getIngredientsList() {
        return ingredientsList;
    }

    public List<IngredientData> getIngredientsLimitersList() {
        return ingredientsLimitersList;
    }

    public List<IngredientData> getIngredientsAdjustedList() {
        return ingredientsAdjustedList;
    }
    // === PROPERTIES END ===

    public IngredientData FindIngredientByString(String ingredientName){
        for (IngredientData i: ingredientsList) {
            if (i.getName().equals(ingredientName)) {
                return i;
            }
        }
        Log.d("Scott", "Something went Wrong in Recipe: FindIngredientByString. Ingredient Not found");
        return new IngredientData();        // must keep this as is. dont change to null as in FindLimitIngredientByString
    }

    public IngredientData FindLimitIngredientByString(String ingredientName){
        for (IngredientData i: ingredientsLimitersList) {
            if (i.getName().equals(ingredientName)) {
                return i;
            }
        }
        Log.d("Scott", "Something went Wrong in Recipe: FindLimitIngredientByString. Ingredient Not found");
        return null;
    }

    // Returns a string array made from the name of the ingredients in the list
    public String[] GetStringArrayFromIngredientsList(){
        String[] array = new String[ingredientsList.size()];
        for (int i = 0 ; i < ingredientsList.size(); i++){
            array[i] = ingredientsList.get(i).getName();
        }
        return  array;
    }


    public void CalculateAdjustedRecipe(PassByReference<String> outputMessage){
        ingredientsAdjustedList.clear();
        float limitMultiplier =  FindLimitingMultiplier(outputMessage);
        GenerateAdjustedIngredients(limitMultiplier);
    }



    public float FindLimitingMultiplier(PassByReference<String> outputMessage) {
        /*
        * for each quantity in the limited list
        * find the corresponding ingredient in the ingredients list
        * create and assign a new variable for the recipe ingredient Quantity and the limit ingredient quantity. since these may change with the conversions
        * check if the units of the limited ingredient are the same as the recipe ingredient
        * if false
        *   Check if the unit conversion is valid
        *       if false Return Message "Calculate cannon convert ... to ..."
        *       if true
        *           convert the ingredient Quantity and the limit Quantity to (g) for mass measurement and (ml) for volume measurements
        *
        * find the percentage of the original recipe value
        * keep track of the lowest percentage
        */

        // === START === Find the Limiting Multiplier ===

        float limitingMultiplier = Float.MAX_VALUE;

        for (IngredientData iLimit: ingredientsLimitersList){
            IngredientData ingredient = FindIngredientByString(iLimit.getName());       //find the corresponding ingredient in the ingredients list
            float iQuantity = ingredient.getQuantity();
            float iLimitQuantity = iLimit.getQuantity();
            float currentMultiplier;
            UnitTypes iUnits, iLimitUnits;
            iUnits = UnitTypes.ConvertStringToEnum(ingredient.getUnit());
            iLimitUnits = UnitTypes.ConvertStringToEnum(iLimit.getUnit());

            //check if the units of the limited ingredient Different from the ingredient units
            if (iUnits != iLimitUnits){
                /*if (IF THESE UNITS CAN BE CONVERTED FROM ONE TO ANOTHER){
                    outputMessage.Obj = "Cant convert " + ingredient.getUnit() + " to " + iLimit.getUnit();
                    return;
                }*/
                // convert the ingredient Quantity and the limit Quantity to (g) for mass measurement and (ml) for volume measurements
                // iQuantity = ...
                // iLimitQuantity = ...

            }
            // Calculate the proportion of iLimitQuantity to ingredientQuantity and keep track of the limiting multiplier
            currentMultiplier = iLimitQuantity / iQuantity;
            if (currentMultiplier < limitingMultiplier) {
                limitingMultiplier = currentMultiplier;
            }
        }
        outputMessage.Obj = String.valueOf(limitingMultiplier);
        // === END === Find the Limiting Multiplier ===

        return limitingMultiplier;

    }

    private void GenerateAdjustedIngredients(float limitingMultiplier) {
        /*
        * For Each Ingredient in ingredientList
        *   float adjustedQuantity = ingredient Quantity * limiting Multiplier
        *   string adjustedUnits = ingredient.units
        *       Check if this ingredient is in the limit list
        *           if true check if the units in the ingredients List are the same in the limiting Ingredients List
        *               if true Convert adjusted quantity to the appropriate units, and set adjusted units to the limitIngredients Units
        *   create IngredientData from the ingredient.name, the adjusted quantity, and the adjusted units
        *
        */

        for (IngredientData i : ingredientsList) {
            float adjustedQuantity = i.getQuantity() * limitingMultiplier;
            adjustedQuantity = round(adjustedQuantity, 1);                            // round float to one decimal place
            String adjustedUnits = i.getUnit();                                             // units in adjusted recipe will be the same by default
            IngredientData limitIngredient = FindLimitIngredientByString(i.getName());      // get the limit ingredient with the same name if it exists
            if (limitIngredient != null){                                                   //Check if this ingredient is in the limit list
                UnitTypes ingredientUnit = UnitTypes.ConvertStringToEnum(i.getUnit());
                UnitTypes limitUnit = UnitTypes.ConvertStringToEnum(limitIngredient.getUnit());
                if(ingredientUnit != limitUnit){                                             // if the user has entered different units than they originally used in the recipe
                    //adjustedQuantity = Method(adjustedQuantity, currentUnits .ie ingredientUnits, targetUnits i.e. limitUnits )
                }
            }
            IngredientData adjustedIngredient = new IngredientData(i.getName(), adjustedQuantity, adjustedUnits);
            ingredientsAdjustedList.add(adjustedIngredient);
        }
    }
    private static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

}

