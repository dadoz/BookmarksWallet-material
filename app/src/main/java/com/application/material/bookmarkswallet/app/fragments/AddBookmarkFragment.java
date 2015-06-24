package com.application.material.bookmarkswallet.app.fragments;

import android.animation.Animator;
import android.app.Activity;
import android.content.*;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton;
import com.application.material.bookmarkswallet.app.singleton.ClipboardSingleton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by davide on 30/06/14.
 */
public class AddBookmarkFragment extends Fragment implements
        TextWatcher, CompoundButton.OnCheckedChangeListener {
    public static String FRAG_TAG = "AddBookmarkFragment";
    private String TAG = "AddBookmarkFragment";

    private View mAddBookmarkView;
    private AddBookmarkActivity mAddActivityRef;
    @InjectView(R.id.httpsFormatCheckboxId)
    CheckBox httpsFormatCheckbox;
    @InjectView(R.id.httpFormatCheckboxId)
    CheckBox httpFormatCheckbox;
    @InjectView(R.id.urlEditText)
    EditText mUrlEditText;

    public static int PICK_IMAGE_REQ_CODE;
    private ActionbarSingleton mActionbarSingleton;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (! (activity instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        mAddActivityRef = (AddBookmarkActivity) activity;
        mActionbarSingleton = ActionbarSingleton.getInstance(mAddActivityRef);

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
//        mActionbarSingleton.setViewOnActionMenu(mAddBookmarkView.
//                findViewById(R.id.addBookmarkLayoutId), R.id.addBookmarkLayoutId);
        ((EditText) mAddBookmarkView.findViewById(R.id.urlEditText)).addTextChangedListener(this);
        mActionbarSingleton.setTitle("Add");

        httpFormatCheckbox.setOnCheckedChangeListener(this);
        httpsFormatCheckbox.setOnCheckedChangeListener(this);
        String httpString = "http://";
        mUrlEditText.setText(httpString);
        mUrlEditText.setSelection(httpString.length());

    }

    public void onViewCreated(View v, Bundle savedInstance) { super.onViewCreated(v, savedInstance); }

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
                mActionbarSingleton.hideSoftKeyboard(mUrlEditText);
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

    private static boolean isValidUrl(String linkUrl) {
        Pattern p = Pattern.
                compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?(https://)?(ftp://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?");

        Matcher m = p.matcher(linkUrl);
        return ! linkUrl.equals("") &&
                m.matches();
    }

    private void invalidateLinkView(boolean invalidate) {
        View view = mAddBookmarkView.findViewById(R.id.errorLayoutId); //TODO replace
        mAddBookmarkView.findViewById(R.id.addBookmarkDescriptionTextviewId).setVisibility(!invalidate ? View.VISIBLE : View.GONE);
        ((ViewGroup) view).getChildAt(0).setVisibility(! invalidate ? View.INVISIBLE : View.VISIBLE);
        ((ViewGroup) view).getChildAt(1).setVisibility(! invalidate ? View.INVISIBLE : View.VISIBLE);

        view.setBackgroundColor(mAddActivityRef.
                getResources().getColor(invalidate ? R.color.material_violet_500 : R.color.lightGrey));

        if(invalidate) {
            if (Build.VERSION.SDK_INT >= 21) {
                Animator reveal = ViewAnimationUtils
                        .createCircularReveal(view,
                                view.getWidth() / 2,
                                view.getHeight() / 2,
                                0,
                                view.getHeight());
                reveal.start();
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //reset
        httpsFormatCheckbox.setOnCheckedChangeListener(null);
        httpFormatCheckbox.setOnCheckedChangeListener(null);

        switch (buttonView.getId()) {
            case R.id.httpFormatCheckboxId:
                httpsFormatCheckbox.setChecked(false);
                String httpString = "http://";
                mUrlEditText.setText(httpString);
                mUrlEditText.setSelection(httpString.length());
                break;
            case R.id.httpsFormatCheckboxId:
                httpFormatCheckbox.setChecked(false);
                String httpsString = "https://";
                mUrlEditText.setText(httpsString);
                mUrlEditText.setSelection(httpsString.length());
                break;
        }

        httpFormatCheckbox.setOnCheckedChangeListener(this);
        httpsFormatCheckbox.setOnCheckedChangeListener(this);
    }
}
