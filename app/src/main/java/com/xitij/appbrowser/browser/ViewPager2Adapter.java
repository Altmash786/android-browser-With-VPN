package com.xitij.appbrowser.browser;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPager2Adapter extends FragmentStateAdapter {
    private static final String TAG = "web:viewpager";
    static List<BrowserFragment> list = new ArrayList<>();
    ViewPagerListnear viewPagerListnear;
    private boolean isprivate;
    private String url;

    public ViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public List<BrowserFragment> getList() {
        return list;
    }

    public void setList(List<BrowserFragment> list) {
        this.list = list;
    }

    public ViewPagerListnear getViewPagerListnear() {
        return viewPagerListnear;
    }

    public void setViewPagerListnear(ViewPagerListnear viewPagerListnear) {
        this.viewPagerListnear = viewPagerListnear;
    }

    public BrowserFragment getItemAt(int position) {
        return (list.get(position));
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d(TAG, "createFragment: " + isprivate + url);


        BrowserFragment browserFragment = new BrowserFragment();

        browserFragment.setArgs(isprivate, url);
        browserFragment.setBrowserFragmentListner(new BrowserFragment.BrowserFragmentListner() {
            @Override
            public void setUrl(String url) {
                viewPagerListnear.setUrl(url);
            }

            @Override
            public void setWebObj(WebView webObj) {
                viewPagerListnear.setWebObj(webObj);
            }

            @Override
            public void addTab() {
                viewPagerListnear.addTab();
            }

            @Override
            public void openTabList(Bitmap bitmap, String title) {
                Log.d(TAG, "openTabList: " + title);
                browserFragment.setTitle(title);
                browserFragment.setBitmap(bitmap);
                viewPagerListnear.openTabList(bitmap, title);
            }

            @Override
            public void openBookmarks() {
                viewPagerListnear.openBookmarks();
            }

            @Override
            public void openIngontigo() {
                viewPagerListnear.openIntigo();
            }

            @Override
            public void openHistory() {
                viewPagerListnear.openHistory();
            }
        });

        return browserFragment;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItem(BrowserFragment webFragment, boolean isprivate, String url) {
        this.isprivate = isprivate;
        this.url = url;
        Log.d(TAG, "addItem: ");
        list.add(webFragment);
        notifyItemInserted(list.size() - 1);

    }

    public void removePage(BrowserFragment newFragment, int pos) {
        list.remove(newFragment);
        notifyItemRemoved(pos);
    }

    public interface ViewPagerListnear {
        void setUrl(String url);

        void setWebObj(WebView webObj);

        void addTab();

        void openTabList(Bitmap bitmap, String title);

        void openIntigo();

        void openBookmarks();

        void openHistory();
    }
}