package com.example.rdproject.Listeners;

import com.example.rdproject.Models.IngredientSearchResponse;
import com.example.rdproject.Models.SimilarRecipeResponse;

import java.util.List;

public interface SearchByIngredientsListener {

    void didFetch(List<IngredientSearchResponse> response, String message);
    void didError(String message);

}
