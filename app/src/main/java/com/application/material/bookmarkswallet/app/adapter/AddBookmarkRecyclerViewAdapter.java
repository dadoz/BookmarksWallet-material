package com.application.material.bookmarkswallet.app.adapter;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.models.BookmarkCardview;
import com.application.material.bookmarkswallet.app.models.BookmarkCardview.CardviewTypeEnum;
import com.application.material.bookmarkswallet.app.models.Info;
import com.application.material.bookmarkswallet.app.models.Link;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by davide on 17/01/15.
 */
public class AddBookmarkRecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ANIMATED_ITEMS_COUNT = 2;
    private String TAG = "AddBookmarkRecyclerViewAdapter";
    private ArrayList<BookmarkCardview> mDataset;
    private static Context mActivityRef;
    private View.OnClickListener clickListenerRef;
    private int latestPos = -1;


    public AddBookmarkRecyclerViewAdapter(Fragment fragmentRef, ArrayList<BookmarkCardview> data) {
        mDataset = data;
        clickListenerRef = (View.OnClickListener) fragmentRef;
        mActivityRef = fragmentRef.getActivity();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        switch (viewType) {
            case 0:
                //INFO
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.info_cardview_layout, parent, false);
                return new InfoViewHolder(v);
            case 1:
                //IMPORT BOOKMARK
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.import_cardview_layout, parent, false);
                return new ImportViewHolder(v);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if(position == CardviewTypeEnum.INFO_CARDVIEW.ordinal()) {
                ((InfoViewHolder) holder).mCounterTextview.
                        setText(Integer.toString(((Info) mDataset.get(0)).
                                getCounter()));
                ((InfoViewHolder) holder).mPeriodTextview.
                        setText(((Info) mDataset.get(0)).getPeriod());
                ((InfoViewHolder) holder).mTitleView.
                        setText((mDataset.get(0)).getTitle());
                setAnimation(((InfoViewHolder) holder).mMainView, position);
                return;
            }

            if(position == CardviewTypeEnum.IMPORT_CARDVIEW.ordinal()) {
                //useful - init checkbox
                ((ImportViewHolder) holder).mCsvFormatCheckbox.setChecked(true);
                ((ImportViewHolder) holder).mCsvFormatCheckbox.
                        setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) clickListenerRef);
                ((ImportViewHolder) holder).mHtmlFormatCheckbox.setChecked(false);
                ((ImportViewHolder) holder).mHtmlFormatCheckbox.
                        setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) clickListenerRef);
                ((ImportViewHolder) holder).mImportButton.setOnClickListener(clickListenerRef);
                ((ImportViewHolder) holder).mTitleView.
                        setText((mDataset.get(1)).getTitle());
                setAnimation(((ImportViewHolder) holder).mMainView, position);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAnimation(View view, int position) {
        if (position > ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > latestPos) {
            latestPos = position;
            view.setTranslationY(Utils.getScreenHeight(mActivityRef));
            long duration = 500;
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(duration)
                    .setStartDelay(position * duration);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }


    public static class InfoViewHolder extends RecyclerView.ViewHolder {

        private final View mMainView;
        private final TextView mCounterTextview;
        private final TextView mPeriodTextview;
        public final CardviewTypeEnum mViewType;
        private final TextView mTitleView;

        public InfoViewHolder(View v) {
            super(v);
            //INFO
            mViewType = CardviewTypeEnum.INFO_CARDVIEW;
            mMainView = v;
            mTitleView = (TextView) v.findViewById(R.id.infoTitleViewId);
            mCounterTextview = (TextView) v.findViewById(R.id.infoCounterTextviewId);
            mPeriodTextview = (TextView) v.findViewById(R.id.infoPeriodTextviewId);
        }
    }

    public static class ImportViewHolder extends RecyclerView.ViewHolder {
        private final View mMainView;
        private final TextView mImportButton;
        private final CheckBox mCsvFormatCheckbox;
        private final CheckBox mHtmlFormatCheckbox;
        public final CardviewTypeEnum mViewType;
        private final TextView mTitleView;

        public ImportViewHolder(View v) {
            super(v);
//            , boolean isHtmlFormatEnabled
            mViewType = CardviewTypeEnum.IMPORT_CARDVIEW;
            mMainView = v;
            mTitleView = (TextView) v.findViewById(R.id.importTitleViewId);
            mCsvFormatCheckbox = (CheckBox) v.findViewById(R.id.csvFormatCheckboxId);
            mHtmlFormatCheckbox = (CheckBox) v.findViewById(R.id.htmlFormatCheckboxId);
            mImportButton = (TextView) v.findViewById(R.id.importButtonId);

        }
    }

    private static class Utils {
        private static int screenHeight = 0;

        public static int getScreenHeight(Context c) {
            if (screenHeight == 0) {
                WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                screenHeight = size.y;
            }

            return screenHeight;
        }

    }
}