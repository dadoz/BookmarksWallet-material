package com.application.material.bookmarkswallet.app.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 25/04/2017.
 */

public class BookmarkViewHolder extends RecyclerView.ViewHolder {
    public ImageView iconView;
    public TextView labelView;
    public TextView timestampView;
    public TextView urlView;

    private static int darkGrey;
    private static int lightGrey;
    private static int darkGreyNight;
    private static int lightGreyNight;

    public BookmarkViewHolder(View v) {
        super(v);
        iconView = (ImageView) v.findViewById(R.id.linkIconId);
        labelView = (TextView) v.findViewById(R.id.linkTitleId);
        urlView = (TextView) v.findViewById(R.id.linkUrlId);
        timestampView = (TextView) v.findViewById(R.id.linkTimestampId);
        initColors(itemView.getContext());
    }

//    @Override
//    public boolean onLongClick(View view) {
//        return listener.get().onLongItemClick(view, getAdapterPosition());
//    }
//
//    @Override
//    public void onClick(View view) {
//        listener.get().onItemClick(view, getAdapterPosition());
//    }
//
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
        Picasso.with(itemView.getContext())
                .load(iconUrl)
                .placeholder(new BitmapDrawable(itemView.getContext().getResources(), defaultIcon))
                .into(iconView);
    }

    /**
     *
     */
    private void initColors(Context context) {
        lightGrey = Color.WHITE;
        darkGrey = ContextCompat.getColor(context, R.color.yellow_50);
        lightGreyNight = ContextCompat.getColor(context, R.color.grey_800);
        darkGreyNight = ContextCompat.getColor(context, R.color.grey_700);

    }
}