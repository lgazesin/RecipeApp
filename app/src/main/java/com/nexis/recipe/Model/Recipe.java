package com.nexis.recipe.Model;

public class Recipe {
    String name;
    String description;
    String minute;
    String imageUrl;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    String key;

    public Recipe(){}

    public Recipe(String name, String description, String minute, String imageUrl) {
        this.name = name;
        this.description = description;
        this.minute = minute;
        this.imageUrl = imageUrl;
    }

    public Recipe(int pizza, String s){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
