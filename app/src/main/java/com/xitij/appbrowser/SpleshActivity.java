package com.xitij.appbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.xitij.appbrowser.apps.MainActivity;
import com.xitij.appbrowser.retrofit.ProductKRoot;
import com.xitij.appbrowser.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpleshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splesh);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);

        db.collection("admins").whereEqualTo("flag", true).get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG", "onCreate: " + document.getData().toString());
                        String product = document.get("key", String.class);
                        Log.d("TAG", "onCreate:pop " + product);
                        Call<ProductKRoot> call = RetrofitBuilder.create().getProducts(product);
                        call.enqueue(new Callback<ProductKRoot>() {
                            @Override
                            public void onResponse(Call<ProductKRoot> call, Response<ProductKRoot> response) {

                                if (response.isSuccessful()) {
                                    if (response.body().getStatus() == 200) {

                                        String productname = response.body().getData().getJsonMemberPackage();
                                        if (productname.equals(SpleshActivity.this.getPackageName())) {
                                            getted();
                                        } else {
                                            builder.setMessage("You Are Not Authorized").create().show();
                                            builder.setCancelable(false);
                                        }
                                    } else {
                                        builder.setMessage("You Are Not Authorized").create().show();
                                        builder.setCancelable(false);
                                    }

                                } else {
                                    builder.setMessage("You Are Not Authorized").create().show();
                                    builder.setCancelable(false);
                                }
                            }

                            @Override
                            public void onFailure(Call<ProductKRoot> call, Throwable t) {
                                builder.setMessage("You Are Not Authorized").create().show();
                                builder.setCancelable(false);
                            }
                        });


                    }

                });


    }

    private void getted() {
        new Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
            Intent i = new Intent(SpleshActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }, 1000);
    }
}