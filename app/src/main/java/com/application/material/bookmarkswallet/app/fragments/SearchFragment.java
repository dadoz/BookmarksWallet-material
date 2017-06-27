package com.application.material.bookmarkswallet.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.manager.SearchManager;
import com.lib.davidelm.filetreevisitorlibrary.manager.NodeListManager;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davide on 24/06/2017.
 */

class SearchFragment extends BaseFragment implements SearchManager.SearchManagerCallbackInterface {
    private static final String TAG = "SearchFrag";
    private SearchManager searchManager;
    private RecyclerView searchResultRecyclerView;
    private View searchBookmarksIcon;
    private TextView searchBookmarksNotFoundText;
    private String base;

    {
        layoutId = R.layout.fragment_search_layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        searchManager = SearchManager.getInstance();
        searchManager.setListener(this);
        searchManager.setList(NodeListManager.getInstance(getActivity()).getNodeList());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitView(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     *
     * @param view
     */
    private void onInitView(View view) {
        searchBookmarksIcon = view.findViewById(R.id.searchBookmarksIconId);
        searchBookmarksNotFoundText = (TextView) view.findViewById(R.id.searchBookmarksNotFoundTextId);
        searchResultRecyclerView = (RecyclerView) view.findViewById(R.id.searchResultRecyclerViewId);
        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultRecyclerView.setAdapter(new SearchResultAdapter(new ArrayList<>()));
        base = searchBookmarksNotFoundText.getText().toString();
    }

    @Override
    public void onOpenSearchView() {
    }

    @Override
    public void onCloseSearchView() {
        if (getActivity() != null)
            getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void publishResultCb(CharSequence query, List<TreeNodeInterface> filteredList) {
        //empty string
        searchBookmarksNotFoundText.setVisibility(filteredList.size() > 0 ? View.GONE : View.VISIBLE);
        searchBookmarksNotFoundText.setText(String.format(base, query));
        searchBookmarksIcon.setVisibility(filteredList.size() > 0 ? View.GONE : View.VISIBLE);

        //set result
        ((SearchFragment.SearchResultAdapter) searchResultRecyclerView.getAdapter()).setItems(filteredList);
    }


    /**
     * Adapter
     * TODO mv smwhere
     */
    private class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

        private List<TreeNodeInterface> items;

        private SearchResultAdapter(List<TreeNodeInterface> items) {
            this.items = items;
        }

        @Override
        public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(getContext(), R.layout.linear_node_item, null);
            return new SearchResultViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SearchResultViewHolder holder, int position) {
            holder.nodeIconImage.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.mipmap.ic_bookmark_border_dark));
            holder.nodeLabelText.setText(items.get(position).getNodeContent().getName());
            holder.nodeDescriptionText.setVisibility(View.GONE);
            holder.nodeMoreSelectButton.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void setItems(List<TreeNodeInterface> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        public class SearchResultViewHolder extends RecyclerView.ViewHolder {
            public final ImageView nodeIconImage;
            public final TextView nodeLabelText;
            public final TextView nodeDescriptionText;
            private final View nodeMoreSelectButton;

            public SearchResultViewHolder(View itemView) {
                super(itemView);
                nodeIconImage = (ImageView) itemView.findViewById(R.id.nodeIconImageId);
                nodeLabelText = (TextView) itemView.findViewById(R.id.nodeLabelTextId);
                nodeDescriptionText = (TextView) itemView.findViewById(R.id.nodeDescriptionTextId);
                nodeMoreSelectButton = itemView.findViewById(R.id.nodeMoreSelectButtonId);
            }
        }
    }
}
