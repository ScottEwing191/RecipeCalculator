package com.scott.recipecalculator;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class LimitFragment extends BaseFragment implements View.OnClickListener{
    ImageButton btnAddIngredient;
    Button  btnCalculate;
    Spinner ingredientsSpinner;
    String[] ingredientsArray;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        fragmentView =  inflater.inflate(R.layout.fragment2_layout, container, false);
        super.onCreateView(inflater,container,savedInstanceState);
        main.SetLimitFragment(this);

        //Set Table Row Colours
        rowColourLight = R.color.table_row_light_red;
        rowColourDark = R.color.table_row_dark_red;

        // Find Buttons
        btnAddIngredient = fragmentView.findViewById(R.id.btnAddIngredient);
        btnCalculate = fragmentView.findViewById(R.id.btnCalculate);


        // Find Ingredient Selection Spinner
        ingredientsSpinner = (Spinner) fragmentView.findViewById(R.id.spnIngredientSelection);

        // Set button OnClickListeners
        btnAddIngredient.setOnClickListener(this);
        btnCalculate.setOnClickListener(this);
        fragmentView.findViewById(R.id.ibtnDeleteIngredient).setOnClickListener(this);

        // Make first Row invisible
        ingredientRows.get(0).setVisibility(View.INVISIBLE);

        if (main.getRecipeBook().getRecipesList().size() > 0){
            SetupFragment(main.getCurrentRecipe());
        }
        return fragmentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAddIngredient:
                AddIngredientButtonClicked();
                break;
            case R.id.btnCalculate:
                CalculateButton();
                break;

            default:
                if (v instanceof ImageButton){
                    DeleteIngredientButton(v);
                    RecolourIngredientRows();
                }
                break;

        }

    }

    private void AddIngredientButtonClicked(){
        //get ingredient selected from text box
        String selectedIngredient = (String) ingredientsSpinner.getSelectedItem();
        if (selectedIngredient == null){
            Toast.makeText(getActivity(), "No Recipe Selected", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create a copy of the ingredient by first getting a reference to the IngredientData and using the information inside the Ingredient Data to generate an identical IngredientData
        // Creating a new IngredientData is required otherwise changing the quantity in the limitIngredient would also change the quantity in the original Ingredient
        IngredientData recipeIngredientData = main.getCurrentRecipe().FindIngredientByString(selectedIngredient);
        IngredientData ingredientToAdd = new IngredientData(recipeIngredientData.getName(), recipeIngredientData.getQuantity(), recipeIngredientData.getUnit());

        // Check if ingredientLimiterList contain an ingredient with the same same as ingredient to add
        if (main.getCurrentRecipe().FindLimitIngredientByString(ingredientToAdd.getName()) != null){
            Toast.makeText(getActivity(), "Ingredient Already Added", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if ingredient Data is in ingredientsLimitersList
        /*if (main.getCurrentRecipe().getIngredientsLimitersList().contains(ingredientToAdd)){
            Toast.makeText(getActivity(), "Ingredient Already Added", Toast.LENGTH_SHORT).show();
            return;
        }*/
        main.getCurrentRecipe().getIngredientsLimitersList().add(ingredientToAdd);

        ingredientToAdd.setQuantity(0);     // Set this to since this is the value the user will be entering

        if (ingredientRows.get(0).getVisibility() == View.INVISIBLE){     // if no ingredients have been added yet and only the invisible first row exists
            ingredientRows.get(0).setVisibility(View.VISIBLE);
            AssignValuesToFirstIngredientRow(main.getCurrentRecipe().getRecipeTitle(), ingredientToAdd);
        }else{
            AddIngredientRow(ingredientToAdd);
        }
    }

    private void DeleteIngredientButton(View v){
        TableRow tableRow = (TableRow) v.getParent();
        TableRow firstTableRow = ingredientRows.get(0);
        if(ingredientRows.get(0).equals(v.getParent())){
            Log.d("Scott", "Cant Delete First Row");
            ((TableRow) v.getParent()).setVisibility(View.INVISIBLE);

        }
        DeleteIngredientRow(v, ingredientRows,false);       // Delete Row From Screen and ingredientList

        // Use the name of the ingredient to fide the ingredientData Object which needs to be removed from the Recipe.ingredientsLimitersList
        EditText ingredientNameTextBox = ((EditText)tableRow.getChildAt(0));
        String ingredientName = ingredientNameTextBox.getText().toString();
        IngredientData ingredientToRemove = main.getCurrentRecipe().FindLimitIngredientByString(ingredientName);
        if (ingredientToRemove != null){
            main.getCurrentRecipe().getIngredientsLimitersList().remove(ingredientToRemove);
        }else{
            Log.d("Scott", "ERROR LimitFragment/DeleteIngredientButton - FindIngredientByString. Ingredient Not found");
        }

    }

    public void SetupFragment(Recipe currentRecipe){
        ResetUI();

        //Set Recipe Name
        ((TextView) fragmentView.findViewById(R.id.txtRecipeTitle)).setText(main.getCurrentRecipe().getRecipeTitle());      // Set Recipe Title
        ingredientsArray = main.getCurrentRecipe().GetStringArrayFromIngredientsList();                                     // Get String Containing recipe Names

        // Make first Row invisible
        ingredientRows.get(0).setVisibility(View.INVISIBLE);

        // set spinner to include ingredient names
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, ingredientsArray);
        ingredientsSpinner.setAdapter(adapter);

        // Clear Limit List
        main.getCurrentRecipe().getIngredientsLimitersList().clear();

    }

    private void CalculateButton(){
        if (SaveLimitedIngredientsList() == -1){
            return;
        }

        PassByReference<String> message = new PassByReference<String>("");
        main.getCurrentRecipe().CalculateAdjustedRecipe(message);
        main.DisplayAdjustedRecipe();
        Toast.makeText(getActivity(), "Limiting Multiplier: " + message.Obj, Toast.LENGTH_SHORT).show();

    }

    // Updated the quantity and unit values of the ingredients in the limit ingredients List with the value currently in the text boxes
    private int SaveLimitedIngredientsList() {
        // fore each ingredient row
        /*
        * for each ingredient row
        *   get limitIngredient from the list using the name ingredient name
        *   assign the limit ingredient quantity and units with values from the textbox. Validate Quantity
        * */
        // If no ingredients have been added
        if (ingredientRows.size() == 1 && ingredientRows.get(0).getVisibility() == View.INVISIBLE){
            Toast.makeText(getContext(), "Calculation Failed. Add a limiting ingredient.", Toast.LENGTH_SHORT).show();
            return -1;
        }

        for(TableRow t: ingredientRows){
            if (t.getVisibility() == View.INVISIBLE){       // skipp the first row if it is invisible
                continue;
            }
            String name = ((EditText)t.getChildAt(0)).getText().toString();                     // get name text
            String quantityAsString = ((EditText) t.getChildAt(1)).getText().toString();        // get quantity text
            String units = ((Spinner)t.getChildAt(2)).getSelectedItem().toString();             // get units text
            float quantityAsFloat;

            // Validate Quantity - String cant be empty/ white space or == 0
            if (IsBlank(quantityAsString) || quantityAsString.charAt(0) == '.'){
                Toast.makeText(getContext(), "Calculation Failed. Enter a quantity.", Toast.LENGTH_SHORT).show();
                return -1;
            }
            quantityAsFloat = Float.parseFloat(quantityAsString);
            if (quantityAsFloat == 0){
                Toast.makeText(getContext(), "Calculation Failed. Quantity cannot be 0.", Toast.LENGTH_SHORT).show();
                return -1;
            }
            //  get reference to the ingredientData in the list thal this Table Row represents. Changing this reference should change the original
            IngredientData limitIngredientToUpdate = main.getCurrentRecipe().FindLimitIngredientByString(name);

            if (limitIngredientToUpdate != null){
                limitIngredientToUpdate.setQuantity(quantityAsFloat);
                limitIngredientToUpdate.setUnit(units);
            }else{
                Log.d("Scott", "ERROR LimitFragment/SaveLimitedIngredientList - FindIngredientByString. Ingredient Not found");

            }
        }
        return 1;
    }
}
