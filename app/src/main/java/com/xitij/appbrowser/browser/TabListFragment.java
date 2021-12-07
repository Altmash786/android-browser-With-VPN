package com.xitij.appbrowser.browser;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.xitij.appbrowser.R;
import com.xitij.appbrowser.databinding.FragmentTabListBinding;

import java.util.List;
import java.util.Objects;


public class TabListFragment extends Fragment {

    public static final String TAB_OPEN = "OPEN";
    public static final String TAB_ADD = "ADD";
    public static final String CLEARALL = "CLEAR";
    private static final String TAG = "web:tablist";

    ListFragmentListner listFragmentListner;
    FragmentTabListBinding binding;
    private TabAdapter tabAdapter;
    private List<BrowserFragment> list;

    public TabListFragment(List<BrowserFragment> list, ListFragmentListner listFragmentListner) {

        this.list = list;
        this.listFragmentListner = listFragmentListner;
    }

    public ListFragmentListner getListFragmentListner() {
        return listFragmentListner;
    }

    public void setListFragmentListner(ListFragmentListner listFragmentListner) {
        this.listFragmentListner = listFragmentListner;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        for (int i = 0; i < list.size(); i++) {
            Log.d(TAG, "onCreate: " + list.get(i));
        }

        tabAdapter = new TabAdapter(list, (websiteModel, pos, work) -> {
            if (work.equals("CLOSE")) {
                list.remove(pos);
                tabAdapter.notifyItemRemoved(pos);
                listFragmentListner.onClickRemove(websiteModel, pos);
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
            if (work.equals("OPEN")) {
                listFragmentListner.onListClick(websiteModel, pos);
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
        binding.rvtabs.setAdapter(tabAdapter);

        binding.imgadd.setOnClickListener(v -> {
            listFragmentListner.onClickAdd();
            Objects.requireNonNull(getActivity()).onBackPressed();
        });
        binding.imgbookmark.setOnClickListener(v ->
                listFragmentListner.onClickBookmark());
        binding.imghistry.setOnClickListener(v ->
                listFragmentListner.onClickHistory());

        binding.imgback.setOnClickListener(v -> getActivity().onBackPressed());
    }

    public interface ListFragmentListner {
        void onListClick(BrowserFragment browserFragment, int pos);

        void onClickRemove(BrowserFragment browserFragment, int pos);

        void onClickAdd();

        void closeAll();

        void onClickBookmark();

        void onClickHistory();
    }
}
