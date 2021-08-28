package com.scott.recipecalculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BaseFragment extends Fragment implements View.OnClickListener{
    protected MainActivity main;
    protected View fragmentView;

    protected List<TableRow> ingredientRows = new ArrayList<TableRow>();                          // a list of the ingredient Rows in the fragment
    protected TableLayout tableLayout;

    protected int rowColourLight = R.color.table_row_light_blue;
    protected int rowColourDark = R.color.table_row_dark_blue;

   @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
       main = MainActivity.mainActivity;
       ingredientRows.add((TableRow) fragmentView.findViewById(R.id.tableRowIngredient));
       tableLayout =  (TableLayout) fragmentView.findViewById(R.id.tableLayout);
       return fragmentView;
       //return inflater.inflate(R.layout.fragment3_layout, container, false);
    }

    @Override
    public void onClick(View v) {

    }
    protected void AddIngredientRow(String ingredientName, float ingredientQuantity, String ingredientUnit){
        //Toast.makeText(getActivity(), "Add Table Row", Toast.LENGTH_SHORT).show();

        // Declare views for new Table Row
        TableRow newTableRow = new TableRow(fragmentView.getContext());
        EditText newIngredient = new EditText(getContext());
        EditText newQuantity = new EditText(getContext());
        Spinner newUnit = new Spinner(getContext());
        ImageButton newDelete = new ImageButton(getContext());

        // Setup Ingredients EditText
        newIngredient.setHint("Ingredient");
        newIngredient.setText(ingredientName);
        newIngredient.setSingleLine(true);

        // Setup Quantity EditText
        newQuantity.setHint("Quantity");
        if (ingredientQuantity > 0){                                              // Set Quantity
            newQuantity.setText(String.valueOf(ingredientQuantity));
        }else{
            newQuantity.setText("");
        }
        EditText originalQuantity = (EditText) fragmentView.findViewById(R.id.txtDefaultQuantity);
        newQuantity.setInputType(originalQuantity.getInputType());                          // Set Input type of new EditText to the input type of the original EditText

        //Setup newUnit Spinner
        // Add entries to the spinner


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.Units, android.R.layout.simple_spinner_dropdown_item);
        newUnit.setAdapter(adapter);

        if (IsBlank(ingredientUnit)){                                             //Set Unit
            newUnit.setSelection(0);
        }else{
            newUnit.setSelection(adapter.getPosition(ingredientUnit)) ;
        }

        newUnit.setPadding(newUnit.getPaddingLeft(),newUnit.getPaddingTop(),0,newUnit.getPaddingBottom());

        // Setup newDelete Image Button
        newDelete.setImageResource(android.R.drawable.ic_delete);                               // Set Button Image
        newDelete.setOnClickListener(this);

        // Add View to Row
        newTableRow.addView(newIngredient);
        newTableRow.addView(newQuantity);
        newTableRow.addView(newUnit);
        newTableRow.addView(newDelete);

        newDelete.getLayoutParams().height = 123;       // must have this line after the button is added to the Table (It must not have a layout beforehand??)
        // Could figure how to get it to match the original button exactly so just eyeballed it

        ingredientRows.add(newTableRow);                // add row to list of rows
        //Add Row to Table
        tableLayout.addView(newTableRow);

    }
    protected void AddIngredientRow(IngredientData data){
        //Toast.makeText(getActivity(), "Add Table Row", Toast.LENGTH_SHORT).show();

        // Declare views for new Table Row
        TableRow newTableRow = new TableRow(fragmentView.getContext());
        EditText newIngredient = new EditText(getContext());
        EditText newQuantity = new EditText(getContext());
        Spinner newUnit = new Spinner(getContext());


        // Setup Ingredients EditText
        EditText originalIngredients = (EditText) fragmentView.findViewById(R.id.txtDefaultIngredient);
        newIngredient.setHint("Ingredient");
        newIngredient.setText(data.getName());
        newIngredient.setSingleLine(true);
        newIngredient.setTextColor(originalIngredients.getTextColors());
        newIngredient.setHintTextColor(originalIngredients.getHintTextColors());
        if (!originalIngredients.isFocusable()){
            newIngredient.setFocusable(false);
        }


        // Setup Quantity EditText
        newQuantity.setHint("Quantity");
        if (data.getQuantity() > 0){                                              // Set Quantity
            newQuantity.setText(String.valueOf(data.getQuantity()));
        }else{
            newQuantity.setText("");
        }
        EditText originalQuantity = (EditText) fragmentView.findViewById(R.id.txtDefaultQuantity);
        newQuantity.setInputType(originalQuantity.getInputType());                          // Set Input type of new EditText to the input type of the original EditText
        newQuantity.setTextColor(originalQuantity.getTextColors());
        newQuantity.setHintTextColor(originalQuantity.getHintTextColors());
        if (!originalQuantity.isFocusable()){
            newQuantity.setFocusable(false);
        }

        //Setup newUnit Spinner
        // Add entries to the spinner


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.Units, android.R.layout.simple_spinner_dropdown_item);
        newUnit.setAdapter(adapter);

        if (IsBlank(data.getUnit())){                                             //Set Unit
            newUnit.setSelection(0);
        }else{
            newUnit.setSelection(adapter.getPosition(data.getUnit())) ;
        }

        newUnit.setPadding(newUnit.getPaddingLeft(),newUnit.getPaddingTop(),0,newUnit.getPaddingBottom());




        // Add View to Row
        newTableRow.addView(newIngredient);
        newTableRow.addView(newQuantity);
        newTableRow.addView(newUnit);

        // Alternate Row Background Colours
        int sizeModTwo = ingredientRows.size() % 2;
        //Set new Table Row colour Table
        if (sizeModTwo == 1){
            newTableRow.setBackgroundColor(ContextCompat.getColor(getContext(), rowColourLight));
        }else{
            newTableRow.setBackgroundColor(ContextCompat.getColor(getContext(), rowColourDark));
        }


// Setup Delete Image Button - everything to do with the button is done in one place and not spread out like the EditText's and spinner
        ImageButton originalDeleteButton = (ImageButton) fragmentView.findViewById(R.id.ibtnDeleteIngredient);
        if (originalDeleteButton != null){
            ImageButton newDelete = new ImageButton(getContext());
            newDelete.setImageResource(android.R.drawable.ic_delete);                               // Set Button Image
            newDelete.setOnClickListener(this);
            newTableRow.addView(newDelete);
            newDelete.getLayoutParams().height = 123;       // must have this line after the button is added to the Table (It must not have a layout beforehand??)
            // Could figure how to get it to match the original button exactly so just eyeballed it
            newDelete.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));        // Set delete button background
        }


        ingredientRows.add(newTableRow);                // add row to list of rows
        //Add Row to Table
        tableLayout.addView(newTableRow);

    }
    protected void DeleteIngredientRow(View view, List<TableRow> tableRowsList, boolean shouldSaveToRecipeBook) {
        Toast.makeText(getActivity(), "ImageButton Selected", Toast.LENGTH_SHORT).show();
        TableRow rowToDelete;
        rowToDelete = (TableRow) view.getParent();
        if (tableRowsList.contains(rowToDelete)){       // Catch case where the Add ingredient button calls this method when pressed
            int index = tableRowsList.indexOf(rowToDelete);
            if (index > 0 ) {                           // Do not delete the first Row in the Table
                tableRowsList.remove(index);
                ((TableLayout) rowToDelete.getParent()).removeView(rowToDelete);        // Remove the Table from the View

                // MUST SAVE ONCE ROW IS DELETED ELSE OTHER TABS WILL USE OLD RECIPE
                if (shouldSaveToRecipeBook) {
                    main.getRecipeFragment().SaveCurrentRecipeToRecipeBook();
                    main.RefreshLimitAndAdjustedFragment();          // Will change the limit Fragment to the Appropriate Recipe. but will also retet the limit fragment even if save failed
                }

            }else{
                //Toast.makeText(getContext(), "Cannot Delete First Ingredient", Toast.LENGTH_SHORT).show();
                //Log.d("Scott", "Cant Delete First Row");
            }
        }
    }

    // Returns true if string is null, length 0 or containing only whit spaces
    protected boolean IsBlank(String s){
        if (s==null){
            return true;
        }
        if (s.isEmpty()){
            return true;
        }
        CharSequence charSequence = (CharSequence) s;
        for (int i = 0; i < s.length(); i++){
            if (s.charAt(i) != ' '){        // if whitespace
                return false;
            }
        }
        return true;
    }


    public void CreateUIFromRecipe(Recipe recipe, List<IngredientData> ingredientDataList){
        ResetUI();

        // If the recipe is not in the book the first row in table should Be Default Values
        if (!main.getRecipeBook().getRecipesList().contains(recipe)){
            //AssignValuesToFirstIngredientRow("", "", 0, "");
            AssignValuesToFirstIngredientRow("", new IngredientData());

        }
        else {       // Recreate a saved Recipe from the Recipe parameter
            // Set Recipe Title
            EditText recipeTitle = fragmentView.findViewById(R.id.txtRecipeTitle);
            recipeTitle.setText(recipe.getRecipeTitle());
            // Populate First Rowand make sure it is visible
            TableRow firstRow = ingredientRows.get(0);
            firstRow.setVisibility(View.VISIBLE);
            AssignValuesToFirstIngredientRow(recipe.getRecipeTitle(), ingredientDataList.get(0));


            //Create Subsequent Rows
            for (int i = 1; i < ingredientDataList.size(); i++) {
                IngredientData ingredient = ingredientDataList.get(i);
                AddIngredientRow(ingredient);
            }
        }
    }

    protected void AssignValuesToFirstIngredientRow(String title, IngredientData data){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.Units, android.R.layout.simple_spinner_dropdown_item);
        //EditText recipeTitle = fragmentView.findViewById(R.id.txtRecipeTitle);
        EditText ingredientName =  fragmentView.findViewById(R.id.txtDefaultIngredient);
        EditText ingredientQuantity = fragmentView.findViewById(R.id.txtDefaultQuantity);
        Spinner ingredientUnits =  fragmentView.findViewById(R.id.spnDefaultUnits);
        ingredientUnits.setAdapter(adapter);

        //recipeTitle.setText(title);                                     // Set Recipe Title
        ingredientName.setText(data.getName());                                   // Set Ingredient Name

        if (data.getQuantity() > 0){                                              // Set Quantity
            ingredientQuantity.setText(String.valueOf(data.getQuantity()));
        }else{
            ingredientQuantity.setText("");
        }
        if (IsBlank(data.getUnit())){                                             //Set Unit
            ingredientUnits.setSelection(0);
        }else{
            ingredientUnits.setSelection(adapter.getPosition(data.getUnit())) ;
        }
    }
    protected void AssignValuesToFirstIngredientRow(String title, String name, float quantity, String unit){
        IngredientData i = new IngredientData(name, quantity, unit);
        AssignValuesToFirstIngredientRow(title, i);
    }

    // Deletes all Rows Except the first one
    protected void ResetUI(){
        ((EditText) fragmentView.findViewById(R.id.txtRecipeTitle)).setText("");          // Clear Recipe Name
        AssignValuesToFirstIngredientRow("",new IngredientData());            // Clear First Ingredient Row

        int numberOfRows =ingredientRows.size();
        for (int i = 1; i < numberOfRows; i++){            // Dont want to delete the first row
            tableLayout.removeView(ingredientRows.get(1));
            ingredientRows.remove(1);
        }
    }

    // this method will colour the rows in the ingredient Rows so that they alternate colour
    protected void RecolourIngredientRows() {
       for (int i = 0 ;i < ingredientRows.size(); i++){
           int sizeModTwo = i % 2;
           //Set new Table Row colour Table
           if (i % 2 == 1){
               ingredientRows.get(i).setBackgroundColor(ContextCompat.getColor(getContext(), rowColourLight));
           }else{
               ingredientRows.get(i).setBackgroundColor(ContextCompat.getColor(getContext(), rowColourDark));
           }
       }
    }
}
