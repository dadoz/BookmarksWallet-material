package com.application.material.bookmarkswallet.app.adapter.realm;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by davide on 21/04/15.
 */
public class BookmarkRecyclerViewAdapter<T extends RealmObject> extends RealmRecyclerViewAdapter<Bookmark> {
    private final Activity mActivityRef;

    public BookmarkRecyclerViewAdapter(Activity activity) {
        mActivityRef = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mEditUrlLabelView;
        private final EditText mEditLabelView;
        private final View mEditLinkView;
        private ImageView mIconView;
        private TextView mLabelView;
        private TextView mTimestampView;
        private TextView mUrlView;
        private View mMainView;
        private String editNameTemp;
        private String editUrlTemp;
        private String TAG = "Holder";

        public ViewHolder(View v) {
            super(v);
            mMainView = v.findViewById(R.id.linkLayoutId);
            mEditLinkView = v.findViewById(R.id.editLinkLayoutId);
            mIconView = (ImageView) v.findViewById(R.id.linkIconId);
            mLabelView = (TextView) v.findViewById(R.id.linkTitleId);
            mUrlView = (TextView) v.findViewById(R.id.linkUrlId);
            mTimestampView = (TextView) v.findViewById(R.id.linkTimestampId);
//            mEditUrlView = (TextView) v.findViewById(R.id.editLinkUrlId);
            mEditUrlLabelView = (TextView) v.findViewById(R.id.editUrlLabelId);
            mEditLabelView = (EditText) v.findViewById(R.id.editLinkTitleId);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rvh, int position) {
        ViewHolder holder = (ViewHolder) rvh;
        Bookmark bookmark = (Bookmark) getItem(position);

        String linkName = bookmark.getName().trim().equals("") ?
                "(no title)" : bookmark.getName().trim();
        holder.mLabelView.setText(linkName);
        holder.mUrlView.setText(bookmark.getUrl());
        holder.mTimestampView.setText(Bookmark.Utils.getParsedTimestamp(bookmark.getTimestamp()));

        setIcon(holder.mIconView, bookmark);
        boolean isSelectedItem = false;
        holder.itemView.setBackgroundColor(isSelectedItem ?
                mActivityRef.getResources().getColor(R.color.material_grey_200) :
                mActivityRef.getResources().getColor(R.color.white));
    }

    private void setIcon(ImageView iconView, Bookmark bookmark) {
        iconView.setImageDrawable(mActivityRef
                .getResources()
                .getDrawable(R.drawable.ic_bookmark_outline_black_48dp));
        Bitmap bitmapIcon = Bookmark.Utils.getIconBitmap(bookmark.getBlobIcon());
        if(bitmapIcon != null) {
            iconView.setImageBitmap(bitmapIcon);
        }
    }

    @Override
    public int getItemCount() {
        return getRealmBaseAdapter() == null ? 0 : getRealmBaseAdapter().getCount();
    }

}
