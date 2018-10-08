package com.raed.twitterclient.media;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.raed.twitterclient.R;

public class ImageFragment extends Fragment {

    private static final String KEY_IMAGE_URL = "image_url";

    public static ImageFragment newInstance(String url){
        Bundle args = new Bundle();
        args.putString(KEY_IMAGE_URL, url);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ImageView imageView = (ImageView) inflater.inflate(R.layout.fragment_image, container, false);
        String url = getArguments().getString(KEY_IMAGE_URL);
        Glide.with(imageView)
                .load(url)
                .into(imageView);
        return imageView;
    }
}
