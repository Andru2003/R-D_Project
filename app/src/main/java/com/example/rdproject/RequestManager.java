package com.example.rdproject;

import android.content.Context;

import com.example.rdproject.Listeners.RandomRecipiResponseListener;
import com.example.rdproject.Listeners.RecipeDetailsListener;
import com.example.rdproject.Models.RandomRApiResponse;
import com.example.rdproject.Models.RecipeDetailsResponse;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class RequestManager {


    //singleton class to
    Context context; //the context of our request
    //initialisation of the Retrofit tool
    //it takes the base url of the online api, does a convert
    //on the Json and buids it.
    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.spoonacular.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    // constructor of the request manager
    public RequestManager(Context context) {
        this.context = context;
    }


    // gets all the necesary data from the api
    public void getRandomRecipes(RandomRecipiResponseListener l, List<String> tags)
    {
        CallRandomRecipes callRandomRecipes = retrofit.create(CallRandomRecipes.class);
        Call<RandomRApiResponse> call = callRandomRecipes.callRandomRecipe(context.getString(R.string.api_key),"5", tags);
        call.enqueue(new Callback<RandomRApiResponse>() {
            @Override
            public void onResponse(Call<RandomRApiResponse> call, Response<RandomRApiResponse> response) {
                if(!response.isSuccessful())
                {
                    l.didError(response.message());
                    return;
                }
                    l.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<RandomRApiResponse> call, Throwable t) {
                l.didError(t.getMessage());
            }
        });
    }
    
    //setting the summary that has to be cleaned.
    String summary;

    public void getRecipeDetails(RecipeDetailsListener listener, int id){
        CallRecipeDetails callRecipeDetails = retrofit.create(CallRecipeDetails.class);
        Call<RecipeDetailsResponse> call = callRecipeDetails.callRecipeDetails(id, context.getString(R.string.api_key));
        call.enqueue(new Callback<RecipeDetailsResponse>() {
            @Override
            public void onResponse(Call<RecipeDetailsResponse> call, Response<RecipeDetailsResponse> response) {
                if(!response.isSuccessful()) {
                    listener.didError(response.message());
                    return;
                }
                RecipeDetailsResponse detailsResponse = response.body();
                if(detailsResponse != null)
                {
                    summary = detailsResponse.summary;
                }
                //Using Jsoup to remove the HTML tags from the text
                String cleanedSummary = Jsoup.clean(summary, Whitelist.none());

                //Set the cleaned summary
                detailsResponse.summary = cleanedSummary;

                listener.didFetch(response.body(),response.message());
            }

            @Override
            public void onFailure(Call<RecipeDetailsResponse> call, Throwable t) {
                listener.didError(t.getMessage());

            }
        });

    }


    //mandatory interface used to make the calls to the API
    private interface  CallRandomRecipes{
        @GET("recipes/random")
        Call<RandomRApiResponse> callRandomRecipe(
                 //We have to insert the parameters of our random recipes Querry.
                //To do this we must consult the Documentation of the Spoonacular API
               //link to documentation: "https://spoonacular.com/food-api/docs#Get-Random-Recipes"

                 @Query("apiKey") String apiKey,
                 @Query("number") String number,

                 // Radu has been here, we will add a new query
                 @Query("tags") List<String> tags
        );
    }
    private interface CallRecipeDetails{
        @GET("recipes/{id}/information")
        Call<RecipeDetailsResponse> callRecipeDetails (
                @Path("id") int id,
                @Query("apiKey") String apiKey

        );
    }

}
