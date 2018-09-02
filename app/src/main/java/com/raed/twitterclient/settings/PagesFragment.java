package com.raed.twitterclient.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raed.twitterclient.R;
import com.raed.twitterclient.settings.pages.Page;

import java.util.List;

public class PagesFragment extends Fragment{

    private PageAdapter mPageAdpater;
    private PagesFragmentViewModel mViewModel;

    public static PagesFragment newInstance() {
        return new PagesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pages, container, false);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.updatePages(mPageAdpater.mPages);
    }

    private class PageHolder extends RecyclerView.ViewHolder{

        public PageHolder(View itemView) {
            super(itemView);
        }
    }

    private class PageAdapter extends RecyclerView.Adapter<PageHolder>{

        private List<Page> mPages;

        public PageAdapter() {
            this.mPages = mViewModel.getPages();
        }

        @NonNull
        @Override
        public PageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_page, parent, false);
            return new PageHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull PageHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return mPages.size();
        }
    }
}
