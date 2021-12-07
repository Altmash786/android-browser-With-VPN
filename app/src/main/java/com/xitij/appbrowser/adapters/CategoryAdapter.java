package com.xitij.appbrowser.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.xitij.appbrowser.Const;
import com.xitij.appbrowser.R;
import com.xitij.appbrowser.apps.AppsWebsiteActivity;
import com.xitij.appbrowser.apps.ViewAllActivity;
import com.xitij.appbrowser.databinding.ItemAdsBinding;
import com.xitij.appbrowser.databinding.ItemCategoryBinding;
import com.xitij.appbrowser.models.AdsRoot;
import com.xitij.appbrowser.models.AppsRoot;
import com.xitij.appbrowser.models.CategoryRoot;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int AD_TYPE = 1;
    private static final int CONTENT_TYPE = 2;
    private static final String TAG = "adcat";
    FirebaseFirestore db;
    private List<CategoryRoot> categories;
    private OnCategoryListnear onCategoryListnear;
    private Context context;
    private com.facebook.ads.NativeAd nativeAd;
    private int p = 0;
    private List<AdsRoot> advertisements;
    private int adPosition = 0;

    public CategoryAdapter(OnCategoryListnear onCategoryListnear) {

        this.categories = categories;
        this.onCategoryListnear = onCategoryListnear;
    }

    @Override
    public int getItemViewType(int position) {
        if (categories.get(position) == null)
            return AD_TYPE;
        return CONTENT_TYPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        db = FirebaseFirestore.getInstance();

        switch (viewType) {
            case AD_TYPE:
                // fall through
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ads, parent, false);
                return new NativeAdsViewHolder(view);
            case CONTENT_TYPE:
            default:
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
                return new CategoryViewHoder(view1);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == AD_TYPE) {

            NativeAdsViewHolder nativeAdsViewHolder = (NativeAdsViewHolder) holder;
            setGoogleNative(nativeAdsViewHolder);

        } else {
            CategoryViewHoder myHolder = (CategoryViewHoder) holder;
            List<AppsRoot> apps = new ArrayList<>();
            myHolder.binding.tvCatName.setText(categories.get(position).getName());

            db.collection("apps").whereEqualTo("category_id", categories.get(position).getId())
                    .get().addOnCompleteListener(task -> {
                for (QueryDocumentSnapshot document : task.getResult()) {

                    AppsRoot app = document.toObject(AppsRoot.class);
                    app.setId(document.getId());

                    apps.add(app);
                }
                Log.d(TAG, "onBindViewHolder: categories" + position + "==   size" + apps.size());
                if (apps.size() == 0) {
                    p = 0;
                    //onCategoryListnear.oncatOperation(categories.get(position));

                    try {
                        categories.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, categories.size());
                    } catch (Exception e) {
                        Log.d(TAG, "onBindViewHolder: " + e.toString());
                        e.printStackTrace();
                    }
                } else {
                    AppsAdapter appsAdapter = new AppsAdapter(apps);
                    myHolder.binding.rvApps.setAdapter(appsAdapter);
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onBindViewHolder: categories" + position + "==   faill" + e.toString());
                }
            });

            myHolder.binding.tvSeeAll.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewAllActivity.class);
                intent.putExtra("type", "CATEGORY");
                intent.putExtra("Category", new Gson().toJson(categories.get(position)));
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void addAdvertisement(List<AdsRoot> advertisements) {

        this.advertisements = advertisements;
    }

    public void addData(List<CategoryRoot> categories) {

        this.categories = categories;
    }

    public interface OnCategoryListnear {
        void oncatOperation(CategoryRoot categoryRoot);
    }

    private void setGoogleNative(NativeAdsViewHolder nativeAdsViewHolder) {
        AdLoader adLoader = new AdLoader.Builder(context, Const.NATIVE_GOOGLE)
                .forUnifiedNativeAd(unifiedNativeAd -> {
                    // Show the ad.
                    nativeAdsViewHolder.binding1.nativeAdContainer.setVisibility(View.GONE);
                    nativeAdsViewHolder.binding1.myTemplate.setVisibility(View.VISIBLE);
                    NativeTemplateStyle styles = new
                            NativeTemplateStyle.Builder().build();

                    TemplateView template = nativeAdsViewHolder.binding1.myTemplate;
                    template.setStyles(styles);
                    template.setNativeAd(unifiedNativeAd);


                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        nativeAdsViewHolder.binding1.myTemplate.setVisibility(View.GONE);
                        setFbNAtive(nativeAdsViewHolder);
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void setFbNAtive(NativeAdsViewHolder nativeAdsViewHolder) {
        nativeAd = new com.facebook.ads.NativeAd(context, Const.FB_NATIVE);

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                nativeAdsViewHolder.binding1.nativeAdContainer.setVisibility(View.GONE);
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
                setOwnNative(nativeAdsViewHolder);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                nativeAdsViewHolder.binding1.nativeAdContainer.setVisibility(View.VISIBLE);
                nativeAdsViewHolder.binding1.myTemplate.setVisibility(View.GONE);
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                inflateAd(nativeAd, nativeAdsViewHolder);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        };

        // Request an ad
        nativeAd.loadAd(
                nativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }

    private void setOwnNative(NativeAdsViewHolder adHolder) {
        AdsRoot adsRoot;
        if (advertisements.size() == 0) {
            return;
        }
        if (advertisements.size() > adPosition) {
            adsRoot = advertisements.get(adPosition);
            adPosition++;
        } else {
            adsRoot = advertisements.get(0);
        }
        if (adsRoot != null) {
            adHolder.binding1.myTemplate.setVisibility(View.GONE);
            adHolder.binding1.nativeAdContainer.setVisibility(View.GONE);
            adHolder.binding1.cardOwn.setVisibility(View.VISIBLE);
            adHolder.binding1.tvTItle.setText(adsRoot.getTitle());
            adHolder.binding1.tvAdDes.setText(adsRoot.getDescription());
            adHolder.binding1.tvadwebsite.setText(adsRoot.getWebsite());
            Glide.with(context).load(adsRoot.getMnative()).centerCrop().into(adHolder.binding1.imgOwnAd);
            adHolder.binding1.btnAdClick.setText("OPEN NOW");

            adHolder.binding1.tvadwebsite.setOnClickListener(v -> openAds(adsRoot.getWebsite()));
            adHolder.binding1.imgOwnAd.setOnClickListener(v -> openAds(adsRoot.getWebsite()));
            adHolder.binding1.tvAdDes.setOnClickListener(v -> openAds(adsRoot.getWebsite()));
            adHolder.binding1.tvTItle.setOnClickListener(v -> openAds(adsRoot.getWebsite()));
            adHolder.binding1.btnAdClick.setOnClickListener(v -> openAds(adsRoot.getWebsite()));
        }
    }

    private void openAds(String website) {
        Intent intent = new Intent(context, AppsWebsiteActivity.class);
        intent.putExtra("URL", website);
        context.startActivity(intent);
    }

    private void inflateAd(NativeAd nativeAd, NativeAdsViewHolder nativeAdsViewHolder) {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        NativeAdLayout nativeAdLayout = nativeAdsViewHolder.binding1.nativeAdContainer;
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.facebook_native, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = nativeAdLayout.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.

        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);

        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.

    }

    public class CategoryViewHoder extends RecyclerView.ViewHolder {
        ItemCategoryBinding binding;

        public CategoryViewHoder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCategoryBinding.bind(itemView);
        }
    }

    private class NativeAdsViewHolder extends RecyclerView.ViewHolder {
        ItemAdsBinding binding1;

        public NativeAdsViewHolder(View view) {
            super(view);
            binding1 = ItemAdsBinding.bind(view);
        }
    }
}
