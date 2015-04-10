package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeActionbarLayoutAction;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.singleton.ActionBarHandlerSingleton;
import com.application.material.bookmarkswallet.app.singleton.ClipboardSingleton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by davide on 30/06/14.
 */
public class AddBookmarkFragment extends Fragment implements
        View.OnClickListener, TextWatcher {
    public static String FRAG_TAG = "AddBookmarkFragment";
    private String TAG = "AddBookmarkFragment";

    private View mAddBookmarkView;
    private AddBookmarkActivity mAddActivityRef;
    @InjectView(R.id.pasteFromClipboardButtonId)
    View pasteFromClipboardButton;
    @InjectView(R.id.urlEditText)
    EditText mUrlEditText;
    private ClipboardSingleton mClipboardSingleton;

    public static int PICK_IMAGE_REQ_CODE;
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
//        mActionBarHandlerSingleton.setViewOnActionMenu(mAddBookmarkView.
//                findViewById(R.id.addBookmarkLayoutId), R.id.addBookmarkLayoutId);
        ((EditText) mAddBookmarkView.findViewById(R.id.urlEditText)).addTextChangedListener(this);
        mActionBarHandlerSingleton.setTitle("New Bookmark");
        pasteFromClipboardButton.setOnClickListener(this);

    }

    public void onViewCreated(View v, Bundle savedInstance) { super.onViewCreated(v, savedInstance); }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                String linkUrl = mUrlEditText.getText().toString();
                if(! isValidUrl(linkUrl)) {
                    invalidateLinkView(true);
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

    private boolean isValidUrl(String linkUrl) {
        Pattern p = Pattern.
                compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?");

        Matcher m = p.matcher(linkUrl);
        return ! linkUrl.equals("") &&
                m.matches();
    }

    private void invalidateLinkView(boolean invalidate) {
        View view = mAddBookmarkView.findViewById(R.id.addBookmarkMainContainerLayoutId);
        mAddBookmarkView.findViewById(R.id.addBookmarkDescriptionTextId).setVisibility(invalidate ? View.GONE : View.VISIBLE);
        mAddBookmarkView.findViewById(R.id.errorLayoutId).setVisibility(invalidate ? View.VISIBLE : View.GONE);
        view.setBackgroundColor(mAddActivityRef.
                getResources().getColor(invalidate ? R.color.material_red : R.color.lightGrey));

        if(invalidate) {
            if (Build.VERSION.SDK_INT >= 21) {
                ViewAnimationUtils.createCircularReveal(view,
                        view.getWidth() / 2,
                        view.getHeight() / 2,
                        0,
                        view.getHeight()).start();
            }
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        invalidateLinkView(false);
    }

//    public void setACustomAnimation() {
//        pasteFromClipboardButton.animate()
//                .translationY(0)
//                .setInterpolator(new OvershootInterpolator(1.f))
//                .setStartDelay(300)
//                .setDuration(ANIM_DURATION_FAB)
//                .start();
//    }

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
