package com.nexis.recipe.Category;

import com.nexis.recipe.CardView.CardRecipe;

import java.util.List;

public class Category {
    private String nameCategory;
    private List<CardRecipe> recipes;

    public Category(String nameCategory, List<CardRecipe> recipes) {
        this.nameCategory = nameCategory;
        this.recipes = recipes;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public List<CardRecipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<CardRecipe> recipes) {
        this.recipes = recipes;
    }
}
