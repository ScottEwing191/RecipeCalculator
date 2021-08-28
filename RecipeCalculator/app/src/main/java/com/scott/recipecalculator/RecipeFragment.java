package com.scott.recipecalculator;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends BaseFragment implements View.OnClickListener{
    Button btnNewRecipe, btnDeleteRecipe, btnSaveRecipe, btnPreviousRecipe, btnNextRecipe;
    ImageButton btnAddIngredient;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        fragmentView =  inflater.inflate(R.layout.fragment1_layout, container, false);
        super.onCreateView(inflater, container, savedInstanceState);
        main.SetRecipeFragment(this);

        // Find Buttons
        btnNewRecipe = fragmentView.findViewById(R.id.btnNewRecipe);
        btnDeleteRecipe = fragmentView.findViewById(R.id.btnDeleteRecipe);
        btnAddIngredient = fragmentView.findViewById(R.id.btnAddIngredient);
        btnSaveRecipe = fragmentView.findViewById(R.id.btnSaveRecipe);
        btnPreviousRecipe = fragmentView.findViewById(R.id.btnPreviousRecipe);
        btnNextRecipe = fragmentView.findViewById(R.id.btnNextRecipe);


        // Set button OnClickListeners
        btnNewRecipe.setOnClickListener(this);
        btnDeleteRecipe.setOnClickListener(this);
        btnAddIngredient.setOnClickListener(this);
        btnSaveRecipe.setOnClickListener(this);
        btnPreviousRecipe.setOnClickListener(this);
        btnNextRecipe.setOnClickListener(this);
        fragmentView.findViewById(R.id.ibtnDeleteIngredient).setOnClickListener(this);

        if (main.getRecipeBook().getRecipesList().size() > 0){
            CreateUIFromRecipe(main.getCurrentRecipe(), main.getCurrentRecipe().getIngredientsList());

        }
        PositionAddIngredientButton();
        SetNavigationButtonVisibility();
        return fragmentView;
    }

    @Override
    public void onClick(View v) {           // different case for each button in RecipeFragment
        switch (v.getId()){
            case R.id.btnNewRecipe:
                CreateNewRecipe();
                SetNavigationButtonVisibility();
                break;
            case R.id.btnAddIngredient:
                AddIngredientRow(new IngredientData());
                //tableLayout.removeView((TableRow)v.getParent());
                //tableLayout.addView((TableRow)v.getParent());
                PositionAddIngredientButton();
                break;
            case R.id.btnSaveRecipe:
                SaveCurrentRecipeToRecipeBook();
                break;
            case R.id.btnPreviousRecipe:
                DisplayPreviousRecipe();
                SetNavigationButtonVisibility();
                break;
            case R.id.btnNextRecipe:
                DisplayNextRecipe();
                SetNavigationButtonVisibility();
                break;
            case R.id.btnDeleteRecipe:
                DeleteRecipe();
                SetNavigationButtonVisibility();
                break;
            default:
                break;

        }
        if (v instanceof ImageButton){
            //DeleteIngredientRow(v, true);
            DeleteIngredientRow(v, ingredientRows, true);
            RecolourIngredientRows();
        }
    }



    private void DisplayPreviousRecipe() {
        PassByReference<String> message = new PassByReference<>("");
        Recipe newCurrentRecipe = main.getRecipeBook().GetPreviousRecipe(main.getCurrentRecipe(), message);
        main.setCurrentRecipe(newCurrentRecipe);
        CreateUIFromRecipe(main.getCurrentRecipe(), main.getCurrentRecipe().getIngredientsList());
        PositionAddIngredientButton();
        main.RefreshLimitAndAdjustedFragment();          // Will change the limit Fragment to the previous Recipe. (Will fragment when doesn't need to ie first recipe already showing)
        Toast.makeText(getActivity(), message.Obj, Toast.LENGTH_SHORT).show();
    }
    private void DisplayNextRecipe() {
        PassByReference<String> message = new PassByReference<>("");
        Recipe newCurrentRecipe = main.getRecipeBook().GetNextRecipe(main.getCurrentRecipe(), message);
        main.setCurrentRecipe(newCurrentRecipe);
        CreateUIFromRecipe(main.getCurrentRecipe(), main.getCurrentRecipe().getIngredientsList());
        PositionAddIngredientButton();

        main.RefreshLimitAndAdjustedFragment();          // Will change the limit Fragment to the next Recipe. (Will fragment when doesn't need to ie last recipe already showing)
        Toast.makeText(getActivity(), message.Obj, Toast.LENGTH_SHORT).show();
    }


    private void CreateNewRecipe() {
        Toast.makeText(getContext(), "Create New Recipe", Toast.LENGTH_SHORT).show();
        ResetUI();
        main.setCurrentRecipe(new Recipe(main.getRecipeBook().getNextId(main.getRecipeBook()))) ;
        main.RefreshLimitAndAdjustedFragment();          // Will change the limit Fragment to the previous Recipe. (Will fragment when doesn't need to ie first recipe already showing)

    }
    private void DeleteRecipe() {
        // Ask if user is sure

        // If the Recipe Book does not contain the current recipe. I.e the recipe has been created but not saved
        if (!main.getRecipeBook().getRecipesList().contains(main.getCurrentRecipe())){
            Toast.makeText(getContext(), "Delete Failed. Recipe Not Saved", Toast.LENGTH_SHORT).show();
        }
        Recipe newCurrentRecipe =  main.getRecipeBook().DeleteAndGetNext(main.getCurrentRecipe());      // Delete from recipe book
        main.saveData();
        main.setCurrentRecipe(newCurrentRecipe);

        CreateUIFromRecipe(main.getCurrentRecipe(), main.getCurrentRecipe().getIngredientsList());
        PositionAddIngredientButton();

        main.RefreshLimitAndAdjustedFragment();          // Will change the limit Fragment to the previous Recipe. (Will fragment when doesn't need to ie first recipe already showing)

    }

    public int SaveCurrentRecipeToRecipeBook(){
        //Recipe backUpRecipe = main.getCurrentRecipe();                                                // revert the currentRecipe to back up if if any invalid data is found
        Recipe tempRecipe = new Recipe("", main.getCurrentRecipe().getId());

        // Set Recipe Name
        EditText text = ((EditText) fragmentView.findViewById(R.id.txtRecipeTitle));              // get text from EditText View
        String recipeName =text.getText().toString();                                           // Convert into String
        if (IsBlank(recipeName)){
            Toast.makeText(getContext(), "Save Failed. Enter a Recipe Title.", Toast.LENGTH_SHORT).show();
            return -1;
        }
        tempRecipe.setRecipeTitle(recipeName);

        List<String> ingredientNamesList = new ArrayList<>();         // Cant have multiple ingredients with the same name

        //CREATE INGREDIENT DATA's FROM TABLE ROWS AND ADD TO CURRENT RECIPE
        for (TableRow t: ingredientRows){
            String ingredientName, quantityAsString, unit;
            float quantityAsFloat;

            // === START Get data From Table Row ===
            // Validate Ingredient
            ingredientName = ((EditText) t.getChildAt(0)).getText().toString();

            if (IsBlank(ingredientName)){
                Toast.makeText(getContext(), "Save Failed. Enter an ingredient.", Toast.LENGTH_SHORT).show();
                return -1;
            }
            if (ingredientNamesList.contains(ingredientName)){
                Toast.makeText(getContext(), "Save Failed. Ingredients can't have the same name.", Toast.LENGTH_SHORT).show();
                Log.d("Scott", "Can't have duplicate ingredient names");
                return -1;
            }
            ingredientNamesList.add(ingredientName);        // Check if it already contains this name before adding name

            // Validate Quantity
            quantityAsString = ((EditText) t.getChildAt(1)).getText().toString();
            if (IsBlank(quantityAsString) || quantityAsString.charAt(0) == '.'){
                Toast.makeText(getContext(), "Save Failed. Enter a quantity.", Toast.LENGTH_SHORT).show();
                return -1;
            }
            quantityAsFloat = Float.parseFloat(quantityAsString);
            if (quantityAsFloat == 0){
                Toast.makeText(getContext(), "Save Failed. Quantity cannot be 0.", Toast.LENGTH_SHORT).show();
                return -1;
            }

            // Unit
            unit = ((Spinner) t.getChildAt(2)).getSelectedItem().toString();
            // === END Get Data From Table Row ===

            // Create Ingredient Data and Add Ingredient to Recipe
            IngredientData currentIngredientData = new IngredientData(ingredientName, quantityAsFloat, unit);
            tempRecipe.ingredientsList.add(currentIngredientData);


        }

        // Set Current recipe equal to temp now that the data is known to be valid
        main.setCurrentRecipe(tempRecipe);
        main.getRecipeBook().AddOrEditRecipe(main.getCurrentRecipe());
        main.RefreshLimitAndAdjustedFragment();          // Modify the limitFragment with the changes Made
        main.saveData();

        return 1;     // Just for Testing
    }

    // Move the add ingredient button so that it is always underneeth all the ingredients
    private void PositionAddIngredientButton(){
        ImageButton addButton = fragmentView.findViewById(R.id.btnAddIngredient);
        tableLayout.removeView((TableRow)addButton.getParent());
        tableLayout.addView((TableRow)addButton.getParent());
    }

    private void SetNavigationButtonVisibility(){
        SetPreviousRecipeButtonVisibility();
        SetNextRecipeButtonVisibility();
    }

    private void SetPreviousRecipeButtonVisibility() {
        if(main.getRecipeBook().DoesPreviousRecipeExist(main.getCurrentRecipe())){
            // Recipe Does Exist
            btnPreviousRecipe.setAlpha(1f);             // Button Visible
            btnPreviousRecipe.setClickable(true);       // Button Clickable
        }else{
            // Recipe Does not exist
            btnPreviousRecipe.setAlpha(0.5f);           // Button Not Visible
            btnPreviousRecipe.setClickable(false);      // Button Not Clickable
        }
    }

    private void SetNextRecipeButtonVisibility() {
        if(main.getRecipeBook().DoesNextRecipeExist(main.getCurrentRecipe())){
            // Recipe Does Exist
            btnNextRecipe.setAlpha(1f);
            btnNextRecipe.setClickable(true);
        }else{
            // Recipe Does Not Exist
            btnNextRecipe.setAlpha(0.5f);
            btnNextRecipe.setClickable(false);
        }
    }

}
