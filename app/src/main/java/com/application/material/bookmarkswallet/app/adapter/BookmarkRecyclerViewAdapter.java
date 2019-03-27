package com.application.material.bookmarkswallet.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.realm.adapter.RealmModelAdapter;
import com.application.material.bookmarkswallet.app.utlis.RealmUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.NO_FAVICON_MODE;
import static com.application.material.bookmarkswallet.app.models.Bookmark.Utils.getBookmarkNameWrapper;

public class BookmarkRecyclerViewAdapter extends MultipleSelectorHelperAdapter implements ItemTouchHelperAdapter {
    private final WeakReference<Context> context;
    private final WeakReference<OnActionListenerInterface> listener;
    private static int darkGrey;
    private static int lightGrey;
    private final Bitmap defaultIcon;
    private boolean isFaviconNotEnabled;
    private static int darkGreyNight;
    private static int lightGreyNight;

    /**
     *
     * @param ctx
     * @param lst
     */
    public BookmarkRecyclerViewAdapter(WeakReference<Context> ctx, WeakReference<OnActionListenerInterface> lst) {
        super(ctx);
        context = ctx;
        listener = lst;
        initColors();
        setIsFaviconIsEnabled(ctx);
        defaultIcon = BitmapFactory.decodeResource(context.get().getResources(),
                R.drawable.ic_bookmark);
    }

    /**
     *
     * @param ctx
     */
    public void setIsFaviconIsEnabled(WeakReference<Context> ctx) {
        isFaviconNotEnabled = (boolean) SharedPrefHelper.getInstance(ctx).getValue(NO_FAVICON_MODE, false);
    }

    private void initColors() {
        lightGrey = Color.WHITE;
        darkGrey = ContextCompat.getColor(context.get(), R.color.yellow_400);
        lightGreyNight = ContextCompat.getColor(context.get(), R.color.grey_800);
        darkGreyNight = ContextCompat.getColor(context.get(), R.color.grey_700);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rvh, int position) {
        final ViewHolder holder = (ViewHolder) rvh;
        final Bookmark bookmark = getItem(position);

        holder.labelView.setText(getBookmarkNameWrapper(bookmark.getName()));
        holder.urlView.setText(bookmark.getUrl());
        holder.timestampView.setText(Bookmark.Utils.getParsedTimestamp(bookmark
                .getTimestamp()));
        holder.selectItem(isSelectedPos(position)); //TODO handle night mode
        if (bookmark.getIconPath() != null) {
            holder.setIcon(bookmark.getIconPath(), defaultIcon, isSelectedPos(position),
                    isFaviconNotEnabled);
            return;
        }
        holder.setIcon(Utils.getIconBitmap(bookmark.getBlobIcon(), (int) context.get().getResources().getDimension(R.dimen.medium_icon_size)), defaultIcon,
                isSelectedPos(position), isFaviconNotEnabled);
    }

    /**
     *
     * @param position
     * @return
     */
    public boolean isSelectedPos(int position) {
        return super.isSelectedPos(position);
    }


    @Override
    public void onItemMove(int fromPosition, int toPosition) {
    }

    @Override
    public void onItemDismiss(int position) {
    }


    /**
     * ViewHolder def
     */
    private static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, View.OnClickListener {
        private final WeakReference<OnActionListenerInterface> listener;
        private ImageView iconView;
        private TextView labelView;
        private TextView timestampView;
        private TextView urlView;

        private ViewHolder(View v, WeakReference<OnActionListenerInterface> lst) {
            super(v);
            listener = lst;
            iconView = v.findViewById(R.id.linkIconId);
            labelView = v.findViewById(R.id.linkTitleId);
            urlView = v.findViewById(R.id.linkUrlId);
            timestampView = v.findViewById(R.id.linkTimestampId);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
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
        void selectItem(boolean selected) {
            int darkColor = NightModeHelper.getInstance().isNightMode() ? darkGreyNight : darkGrey;
            int lightColor = NightModeHelper.getInstance().isNightMode() ? lightGreyNight : lightGrey;
//            itemView.setBackgroundColor(selected ? darkColor : lightColor);
        }

        /**
         *
         * @param blobIcon
         * @param defaultIcon
         * @param isSelected
         * @param isFaviconNotEnabled
         */
        void setIcon(Bitmap blobIcon, Bitmap defaultIcon, boolean isSelected, boolean isFaviconNotEnabled) {
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
    }

    /**
     *
     */
    public interface OnActionListenerInterface {
        boolean onLongItemClick(View view, int position);
        void onItemClick(View view, int position);
    }
}
