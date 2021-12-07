package com.xitij.appbrowser.products;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.xitij.appbrowser.R;
import com.xitij.appbrowser.apps.MainActivity;
import com.xitij.appbrowser.databinding.FragmentProductsBinding;
import com.xitij.appbrowser.models.CategoryRoot;

import java.util.ArrayList;
import java.util.List;


public class ProductsFragment extends Fragment {
    private static final String TAG = "productfrag";
    FirebaseFirestore db;
    FragmentProductsBinding binding;

    private ProductCategoryAdapter productCategoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_products, container, false);


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MobileAds.initialize(getActivity(), initializationStatus -> {

        });
        AudienceNetworkAds.initialize(getActivity());

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);


        binding.pd.setVisibility(View.VISIBLE);
        getData();

        binding.imgback.setOnClickListener(v -> getActivity().onBackPressed());
    }

    private void getData() {
        List<CategoryRoot> categories = new ArrayList<>();
        List<CategoryRoot> categories1 = new ArrayList<>();
        db.collection("productCategories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, "getData: " + document.getData().toString());
                            CategoryRoot categoryRoot = document.toObject(CategoryRoot.class);

                            if (document.get("icon") != null) {
                                categoryRoot.setImage(document.get("icon").toString());
                                Log.d(TAG, "getData: " + categoryRoot.getImage());
                            }

                            categoryRoot.setId(document.getId());
                            categories.add(categoryRoot);
                            categories1.add(categoryRoot);
                            Log.d(TAG, "onComplete: cat " + categoryRoot.getName());
                            Log.d(TAG, "onComplete: doc " + categoryRoot.getImage());
                            Log.d(TAG, document.getId() + " => " + document.getData().toString());

                        }


                        setTopCategories(categories1);


                        for (int i = 0; i < categories.size(); i++) {
                            if (i != 0 && i % 3 == 0) {
                                categories.add(i, null);
                            }

                        }

                        productCategoryAdapter = new ProductCategoryAdapter(categories, categoryRoot -> {
                            categories.remove(categoryRoot);
                            productCategoryAdapter.notifyDataSetChanged();
                        });
                        productCategoryAdapter.setAds(MainActivity.advertisements);
                        binding.rvCategories.setAdapter(productCategoryAdapter);
                        binding.pd
                                .setVisibility(View.GONE);
                    } else {
                        Log.d(TAG, "Error getting documents.", task.getException());
                    }


                }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e.toString()));

    }

    private void setTopCategories(List<CategoryRoot> categories) {

        ProductTopCategoryAdapter productTopCategoryAdapter = new ProductTopCategoryAdapter(categories);
        binding.rvTop.setAdapter(productTopCategoryAdapter);
    }
}