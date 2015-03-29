package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.animation.OvershootInterpolator;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeActionbarLayoutAction;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.singleton.ActionBarHandlerSingleton;
import com.application.material.bookmarkswallet.app.singleton.ClipboardSingleton;

/**
 * Created by davide on 30/06/14.
 */
public class AddBookmarkFragment extends Fragment implements
        View.OnClickListener {
    public static String FRAG_TAG = "AddBookmarkFragment";
    private String TAG = "AddBookmarkFragment";

    private View mAddBookmarkView;
    private AddBookmarkActivity mAddActivityRef;
    @InjectView(R.id.pasteFromClipboardButtonId)
    View pasteFromClipboardButton;
    @InjectView(R.id.urlEditText)
    EditText mUrlEditText;
//    private View pasteFromClipboardButton;
//    private ActionBarHandlerSingleton mActionBarHandlerRef;
    private ClipboardSingleton mClipboardSingleton;

    public static int PICK_IMAGE_REQ_CODE;
    private long ANIM_DURATION_FAB = 400;
    private ActionBarHandlerSingleton mActionBarHandlerSingleton;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (! (activity instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        mAddActivityRef = (AddBookmarkActivity) activity;
        mClipboardSingleton = ClipboardSingleton.getInstance(mAddActivityRef);
        mActionBarHandlerSingleton = ActionBarHandlerSingleton.getInstance(mAddActivityRef);

    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAddBookmarkView = inflater.inflate(R.layout.add_bookmark_fragment, null);
        ButterKnife.inject(this, mAddBookmarkView);

        setHasOptionsMenu(true);

        onInitView();
        return mAddBookmarkView;
    }

    private void onInitView() {
        mActionBarHandlerSingleton.setViewOnActionMenu(mAddBookmarkView.
                findViewById(R.id.addBookmarkLayoutId), R.id.addBookmarkLayoutId);
        mActionBarHandlerSingleton.setTitle("New Bookmark");
        pasteFromClipboardButton.setOnClickListener(this);
    }

    public void onViewCreated(View v, Bundle savedInstance) { super.onViewCreated(v, savedInstance); }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.pasteFromClipboardButtonId:
//                Toast.makeText(mAddActivityRef, "hey saving", Toast.LENGTH_SHORT).show();
//                //activity result
////                String linkUrl = mUrlEditText.getText().toString();
//                String linkUrl = "";
//                if(linkUrl.equals("")) {
//                    Toast.makeText(mAddActivityRef, "bookmark url not valid", Toast.LENGTH_SHORT).show();
//                    break;
//                }
//
//                Intent intent = new Intent();
//                intent.putExtra(AddBookmarkActivity.LINK_URL_EXTRA, linkUrl);
//                mAddActivityRef.setResult(Activity.RESULT_OK, intent);
//                mAddActivityRef.finish();
//                break;
            case R.id.pasteFromClipboardButtonId:
                if(! mClipboardSingleton.hasClipboardText()) {
                    Toast.makeText(mAddActivityRef, "no text in clipboard", Toast.LENGTH_SHORT).show();
                    break;
                }

                String bookmarkUrl = mClipboardSingleton.getTextFromClipboard();
                if(bookmarkUrl == null) {
                    break;
                }
                mUrlEditText.setText(bookmarkUrl);
                break;
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.add_new_bookmark_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case  R.id.action_settings:
                mAddActivityRef.changeFragment(
                        new SettingsFragment(), null, SettingsFragment.FRAG_TAG);
                return true;
            case  R.id.action_save_new_bookmark:
                Toast.makeText(mAddActivityRef, "hey saving", Toast.LENGTH_SHORT).show();
                //activity result
                String linkUrl = mUrlEditText.getText().toString();
                if(linkUrl.equals("")) {
                    Toast.makeText(mAddActivityRef, "bookmark url not valid", Toast.LENGTH_SHORT).show();
                    return true;
                }

                Intent intent = new Intent();
                intent.putExtra(AddBookmarkActivity.LINK_URL_EXTRA, linkUrl);
                mAddActivityRef.setResult(Activity.RESULT_OK, intent);
                mAddActivityRef.finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void setACustomAnimation() {
        pasteFromClipboardButton.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();

    }

    //    private void onInitView() {
//        cardviewList = new ArrayList<BookmarkCardview>();
////        setACustomAnimation();
//        //TODO please replace
////        pasteFromClipboardButton.setOnClickListener(this);
//
//        RecyclerView.LayoutManager lm = new LinearLayoutManager(mAddActivityRef);
//        mRecyclerView.setLayoutManager(lm);
//
//        cardviewList.add(new Info(CardviewTypeEnum.INFO_CARDVIEW,
//                "Info", "01.01.15 - 02.01.15", 17));
//        cardviewList.add(new BookmarkCardview(CardviewTypeEnum.IMPORT_CARDVIEW, "Import"));
//
//        mAdapter = new AddBookmarkRecyclerViewAdapter(this, cardviewList);
//        mRecyclerView.setAdapter(mAdapter);
//
//        View addBookmarkLayoutTemp = mAddActivityRef.getSupportActionBar().
//                getCustomView().findViewById(R.id.addBookmarkLayoutId);
//        final ViewGroup parent = ((ViewGroup) addBookmarkLayoutTemp.getParent());
//        pasteFromClipboardButton.setTag(true);
//
//        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
////            @Override
////            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
////                super.onScrollStateChanged(recyclerView, newState);
////            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                Log.e(TAG, "hey : " + dy);
//
//                if(dy > 0) {
//
//                    boolean isVisible = (Boolean) pasteFromClipboardButton.getTag();
//
//                    if(isVisible) {
//                        View addBookmarkLayout = mAddActivityRef.getSupportActionBar().
//                                getCustomView().findViewById(R.id.addBookmarkLayoutId);
//                        addBookmarkLayout.setVisibility(View.GONE);
//                        parent.removeView(addBookmarkLayout);
//                    } else {
//                        mAddActivityRef.getLayoutInflater().
//                                inflate(R.layout.actionbar_add_bookmark_inner_layout, parent);
//                    }
//
//
//                    //hide fab button
//                    pasteFromClipboardButton.animate().
//                            translationY(isVisible ? -300 : 0).
//                            setInterpolator(new DecelerateInterpolator(3.f)).
//                            start();
//                    pasteFromClipboardButton.setTag(! isVisible);
//                }
//            }
//        });
//        //ANIMATION
//        setACustomAnimation();
//        //fill data to be animated
//
//    }


}
