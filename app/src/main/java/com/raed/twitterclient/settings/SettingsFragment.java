package com.raed.twitterclient.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raed.twitterclient.R;

public class SettingsFragment extends Fragment {

    public static final int PAGES = 0;

    private SettingsFragmentCallback mCallback;

    interface SettingsFragmentCallback{
        void onSettingsItemSelected(int item);
    }

    public static Fragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (SettingsFragmentCallback) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container,false);

        view.findViewById(R.id.pages)
                .setOnClickListener(ignore -> mCallback.onSettingsItemSelected(PAGES));

        return view;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
