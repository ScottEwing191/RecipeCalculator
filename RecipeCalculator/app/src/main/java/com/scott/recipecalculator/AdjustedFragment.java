package com.scott.recipecalculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class AdjustedFragment extends BaseFragment {

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        fragmentView =  inflater.inflate(R.layout.fragment3_layout, container, false);
        super.onCreateView(inflater,container,savedInstanceState);
        main.SetAdjustedFragment(this);

        //Set Table Row Colours
        rowColourLight = R.color.table_row_light_yellow;
        rowColourDark = R.color.table_row_dark_yellow;

        if (main.getRecipeBook().getRecipesList().size() > 0){
            SetupFragment();
        }

        return fragmentView;
    }

    // Take Sets up the adjusted fragment using the values in the current recipe's adjusted ingredients list list
    public void SetupFragment(){
        ResetUI();
        ((TextView) fragmentView.findViewById(R.id.txtRecipeTitle)).setText(main.getCurrentRecipe().getRecipeTitle());      // Set Recipe Title
        // Make first Row invisible
        ingredientRows.get(0).setVisibility(View.INVISIBLE);
        //CreateUIFromRecipe(main.getCurrentRecipe(), main.getCurrentRecipe().getIngredientsAdjustedList());

    }
}
