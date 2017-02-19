package com.application.material.bookmarkswallet.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.NO_FAVICON_MODE;
import static com.application.material.bookmarkswallet.app.models.Bookmark.Utils.getBookmarkNameWrapper;

public class BookmarkFirebaseRvAdapter extends MultipleSelectorHelperAdapter {
    private String TAG = "BookmarkFirebaseRva";
    private static boolean isFaviconNotEnabled;
    private static Bitmap defaultIcon;
    private WeakReference<Context> ctx;

    public BookmarkFirebaseRvAdapter(Class<Bookmark> modelClass, int modelLayout,
                                     Class<BookmarkViewHolder> viewHolderClass, Query ref, WeakReference<Context> context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        init(context);
    }

    public BookmarkFirebaseRvAdapter(Class<Bookmark> modelClass, int modelLayout,
                                     Class<BookmarkViewHolder> viewHolderClass, DatabaseReference ref, WeakReference<Context> context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        init(context);
    }

    private void init(WeakReference<Context> context) {
        ctx = context;
        isFaviconNotEnabled = (boolean) SharedPrefHelper.getInstance(ctx).getValue(NO_FAVICON_MODE, false);
        defaultIcon = BitmapFactory.decodeResource(ctx.get().getResources(),
                R.drawable.ic_bookmark_black_48dp);
    }

    @Override
    protected void populateViewHolder(BookmarkViewHolder viewHolder, Bookmark model, int position) {
        Log.e(TAG, model.getName());
        viewHolder.bindToEvent(model, isSelectedPos(position));
    }


    public static class BookmarkViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, View.OnClickListener {
        private final WeakReference<BookmarkRvAdapter.OnActionListenerInterface> listener = null;
        private ImageView iconView;
        public TextView labelView;
        public TextView timestampView;
        public TextView urlView;
        private int darkGrey;
        private int lightGrey;
        private int darkGreyNight;
        private int lightGreyNight;


        public BookmarkViewHolder(View v, WeakReference<BookmarkRvAdapter.OnActionListenerInterface> lst) {
            super(v);
        }

        public BookmarkViewHolder(View v) { //, WeakReference<BookmarkRvAdapter.OnActionListenerInterface> lst) {
            super(v);
//        listener = lst;
            iconView = (ImageView) v.findViewById(R.id.linkIconId);
            labelView = (TextView) v.findViewById(R.id.linkTitleId);
            urlView = (TextView) v.findViewById(R.id.linkUrlId);
            timestampView = (TextView) v.findViewById(R.id.linkTimestampId);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
            initColors(itemView.getContext());
        }

        /**
         *
         * @param bookmark
         * @param isSelectedPos
         */
        void bindToEvent(Bookmark bookmark, boolean isSelectedPos) {
            labelView.setText(getBookmarkNameWrapper(bookmark.getName()));
            urlView.setText(bookmark.getUrl());
            timestampView.setText(Bookmark.Utils.getParsedTimestamp(bookmark
                    .getTimestamp()));
            selectItem(isSelectedPos);
            if (bookmark.getIconPath() != null) {
                setIcon(bookmark.getIconPath(), defaultIcon, isSelectedPos,
                        isFaviconNotEnabled);
                return;
            }
            setIcon(Utils.getIconBitmap(bookmark.getBlobIcon(), (int) itemView.getResources().getDimension(R.dimen.medium_icon_size)), defaultIcon,
                    isSelectedPos, isFaviconNotEnabled);
        }

        @Override
        public boolean onLongClick(View view) {
            return listener.get().onLongItemClick(view, getAdapterPosition());
        }

        @Override
        public void onClick(View view) {
            listener.get().onItemClick(view, getAdapterPosition());
        }

        /**
         *
         * @param selected
         */
        public void selectItem(boolean selected) {
            int darkColor = NightModeHelper.getInstance().isNightMode() ? darkGreyNight : darkGrey;
            int lightColor = NightModeHelper.getInstance().isNightMode() ? lightGreyNight : lightGrey;
            itemView.setBackgroundColor(selected ? darkColor : lightColor);
        }

        /**
         *
         * @param blobIcon
         * @param defaultIcon
         * @param isSelected
         * @param isFaviconNotEnabled
         */
        public void setIcon(Bitmap blobIcon, Bitmap defaultIcon, boolean isSelected, boolean isFaviconNotEnabled) {
            Utils.setIconOnImageView(iconView, isFaviconNotEnabled ? defaultIcon :
                    (isSelected ? defaultIcon : blobIcon), defaultIcon);
        }

        /**
         *
         * @param iconUrl
         * @param defaultIcon
         * @param isSelected
         * @param isFaviconNotEnabled
         */
        public void setIcon(String iconUrl, Bitmap defaultIcon, boolean isSelected, boolean isFaviconNotEnabled) {
            if (isFaviconNotEnabled ||
                    isSelected) {
                iconView.setImageBitmap(defaultIcon);
                return;
            }
            Picasso
                    .with(itemView.getContext())
                    .load(iconUrl)
                    .placeholder(new BitmapDrawable(itemView.getContext().getResources(), defaultIcon))
                    .into(iconView);
        }

        private void initColors(Context context) {
            lightGrey = Color.WHITE;
            darkGrey = ContextCompat.getColor(context, R.color.yellow_50);
            lightGreyNight = ContextCompat.getColor(context, R.color.grey_800);
            darkGreyNight = ContextCompat.getColor(context, R.color.grey_700);

        }

    }

}
