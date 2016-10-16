package com.application.material.bookmarkswallet.app.helpers.itemTouchHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import com.application.material.bookmarkswallet.app.helpers.itemTouchHelper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.models.Bookmark;


public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private static final String TAG = "SimpleItemTouchHelperCb";
    private final BookmarkRecyclerViewAdapter adapter;

    public SimpleItemTouchHelperCallback(BookmarkRecyclerViewAdapter adpt) {
        adapter = adpt;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        Log.e(TAG, "Hey swipe");

 //        adapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, final RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

//        //block swipe on certain width
        float translationX = Math.min(-dX, viewHolder.itemView.getWidth() / 2);
        viewHolder.itemView.setTranslationX(-translationX);

        //set background color and button on background canvas
        Paint p = new Paint();
        p.setColor(ContextCompat.getColor(recyclerView.getContext(), R.color.indigo_400));
        final Bitmap icon = BitmapFactory
                .decodeResource(recyclerView.getContext().getResources(), R.drawable.ic_delete_white_48dp);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            final View itemView = viewHolder.itemView;
            if (dX < 0) {
                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), ((float) itemView.getRight()),
                        (float) itemView.getBottom(), p);
                c.drawBitmap(icon,
                        (float) itemView.getRight() - pxFromDp(recyclerView.getContext(), 60f),
                        (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight())/2,
                        p);
            }

        }
    }


    /**
     * TODO mov to utils
     * @param context
     * @param dp
     * @return
     */
    private static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
