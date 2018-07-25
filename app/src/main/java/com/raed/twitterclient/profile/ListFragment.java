package com.raed.twitterclient.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ListFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setAdapter(new MyAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return recyclerView;
    }

    private class MyAdapter extends RecyclerView.Adapter<MyVH>{


        @NonNull
        @Override
        public MyVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyVH(new TextView(getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull MyVH holder, int position) {
            holder.mTextView.setText("Item " + position);
        }

        @Override
        public int getItemCount() {
            return 100;
        }
    }

    private class MyVH extends RecyclerView.ViewHolder{

        private TextView mTextView;
        public MyVH(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
            mTextView.setPadding(50, 50, 50, 50);
        }

    }
}
