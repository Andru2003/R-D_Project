package com.example.rdproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rdproject.Adapter.RecipeAdapter;
import com.example.rdproject.Listeners.RecipeClickListener;
import com.example.rdproject.Listeners.SearchByIngredientsListener;
import com.example.rdproject.Models.IngredientSearchResponse;

import java.util.List;

public class HomeFragment extends Fragment {

    private EditText searchEditText;
    private Button searchButton;
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private RequestManager requestManager;
    boolean isExpanded = false;

    CardView welcomeCard;
    TextView welcomeText;
    TextView culinaryInspirationText;
    TextView goodbyeIngredientsText;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        CardView howToUseContainer = view.findViewById(R.id.howToUseContainer);
        TextView howToUseText = view.findViewById(R.id.howToUseText);

        howToUseContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpanded = !isExpanded;
                if (isExpanded) {
                    howToUseText.setVisibility(View.VISIBLE);
                    howToUseContainer.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                } else {
                    howToUseText.setVisibility(View.GONE);
                    howToUseContainer.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.cardview_default_height);
                }
                howToUseContainer.requestLayout();
            }
        });



        welcomeCard = view.findViewById(R.id.welcomeCard);
        welcomeText = view.findViewById(R.id.welcomeText);
        culinaryInspirationText = view.findViewById(R.id.culinaryInspirationText);
        goodbyeIngredientsText = view.findViewById(R.id.goodbyeIngredientsText);

        welcomeCard.setOnClickListener(new View.OnClickListener() {
            boolean isExpanded = false;

            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    // Collapse the CardView
                    culinaryInspirationText.setVisibility(View.GONE);
                    goodbyeIngredientsText.setVisibility(View.GONE);
                    isExpanded = false;
                } else {
                    // Expand the CardView
                    culinaryInspirationText.setVisibility(View.VISIBLE);
                    goodbyeIngredientsText.setVisibility(View.VISIBLE);
                    isExpanded = true;
                }
            }
        });

        RelativeLayout cardContainer = view.findViewById(R.id.cardContainer);
        LinearLayout cardContent = view.findViewById(R.id.cardContent);
        ImageView expandCollapseIcon = view.findViewById(R.id.expandCollapseIcon);

        cardContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the visibility of the card content
                if (cardContent.getVisibility() == View.VISIBLE) {
                    cardContent.setVisibility(View.GONE);
                    expandCollapseIcon.setImageResource(R.drawable.baseline_expand_more_24);
                } else {
                    cardContent.setVisibility(View.VISIBLE);
                    expandCollapseIcon.setImageResource(R.drawable.ic_expandless);
                }
            }
        });


//        Button button = (Button) view.findViewById(R.id.buttonlogin);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getContext(), LogIn.class));
//            }
//        });

        searchEditText = view.findViewById(R.id.editTextSearch);
        searchButton = view.findViewById(R.id.buttonSearch);
        recyclerView = view.findViewById(R.id.recipe_recycler_view);

        requestManager = new RequestManager(requireContext());

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredients = searchEditText.getText().toString().trim();
                performRecipeSearch(ingredients);
            }
        });

        return view;
    }

    private void performRecipeSearch(String ingredients) {
        requestManager.SearchByIngredientsResponse(ingredients, new SearchByIngredientsListener() {
            @Override
            public void didFetch(List<IngredientSearchResponse> response, String message) {
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recipeAdapter = new RecipeAdapter(response, getContext(), recipeClickListener);
                recyclerView.setAdapter(recipeAdapter);
            }

            @Override
            public void didError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            //Toast.makeText(getActivity(),id,Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(),RecipePageActivity.class).putExtra("id",id));
        }
    };

}
