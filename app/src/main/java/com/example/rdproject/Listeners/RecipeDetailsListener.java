package com.example.rdproject.Listeners;

import com.example.rdproject.Models.RecipeDetailsResponse;

public interface RecipeDetailsListener {
    void didFetch(RecipeDetailsResponse response,String message);
    void didError(String message);
}
