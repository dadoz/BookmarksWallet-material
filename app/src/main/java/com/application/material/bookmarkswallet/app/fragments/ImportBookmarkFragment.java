package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.AddBookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnInitActionBarInterface;
import com.application.material.bookmarkswallet.app.models.BookmarkCardview;
import com.application.material.bookmarkswallet.app.models.BookmarkCardview.CardviewTypeEnum;
import com.application.material.bookmarkswallet.app.models.Info;
import com.application.material.bookmarkswallet.app.singleton.ActionBarHandlerSingleton;
import com.flurry.android.FlurryAgent;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * Created by davide on 30/06/14.
 */
public class ImportBookmarkFragment extends Fragment implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static String FRAG_TAG = "ExportBookmarkFragment";
    public static String TITLE = "Import";
    private View addBookmarkView;
    private MainActivity mMainActivityRef;
    @InjectView(R.id.addBookmarkRecyclerViewId)
    RecyclerView mRecyclerView;
    private String TAG = "AddBookmarkFragment";
    public static int PICK_IMAGE_REQ_CODE;
    private AddBookmarkRecyclerViewAdapter mAdapter;
    private ArrayList<BookmarkCardview> cardviewList;
    private ActionBarHandlerSingleton mActionBarHandlerSingleton;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (! (activity instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        mMainActivityRef = (MainActivity) activity;
        mActionBarHandlerSingleton = ActionBarHandlerSingleton.getInstance(mMainActivityRef);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        addBookmarkView = inflater.inflate(R.layout.import_bookmark_fragment, null);
        ButterKnife.inject(this, addBookmarkView);
        FlurryAgent.logEvent("IMPORT_BOOKMARK_EVENT");

        setHasOptionsMenu(true);
        onInitView();
        return addBookmarkView;
    }

    private void onInitView() {
        mActionBarHandlerSingleton.initActionBar();
        mActionBarHandlerSingleton.setTitle(TITLE);
        mActionBarHandlerSingleton.setDisplayHomeEnabled(true);

        cardviewList = new ArrayList<BookmarkCardview>();
        RecyclerView.LayoutManager lm = new LinearLayoutManager(mMainActivityRef);
        mRecyclerView.setLayoutManager(lm);

        cardviewList.add(new BookmarkCardview(CardviewTypeEnum.IMPORT_CARDVIEW, "Import"));

        mAdapter = new AddBookmarkRecyclerViewAdapter(this, cardviewList);
        mRecyclerView.setAdapter(mAdapter);

    }

    public void onViewCreated(View v, Bundle savedInstance) { super.onViewCreated(v, savedInstance); }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.importButtonId:
                boolean isCsvImport = ((CheckBox) addBookmarkView.
                        findViewById(R.id.csvFormatCheckboxId)).isChecked();
                Log.e(TAG, "csv - " + isCsvImport);
                if(android.os.Build.MANUFACTURER.equals("samsung")) {
                    Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
                    intent.putExtra("CONTENT_TYPE", "*/*");
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    mMainActivityRef.startActivityForResult(intent, PICK_IMAGE_REQ_CODE);
                    break;
                }

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                mMainActivityRef.startActivityForResult(intent, PICK_IMAGE_REQ_CODE);
                break;
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case  R.id.action_settings:
                mMainActivityRef.changeFragment(
                        new SettingsFragment(), null, SettingsFragment.FRAG_TAG);
                mActionBarHandlerSingleton.toggleActionBar(true, false, false);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //reset
        CheckBox htmlFormatCheckbox = ((CheckBox) addBookmarkView.findViewById(R.id.htmlFormatCheckboxId));
        CheckBox csvFormatCheckbox = ((CheckBox) addBookmarkView.findViewById(R.id.csvFormatCheckboxId));

        csvFormatCheckbox.setOnCheckedChangeListener(null);
        htmlFormatCheckbox.setOnCheckedChangeListener(null);

        switch (buttonView.getId()) {
            case R.id.csvFormatCheckboxId:
                htmlFormatCheckbox.setChecked(false);
                break;
            case R.id.htmlFormatCheckboxId:
                csvFormatCheckbox.setChecked(false);
                break;
        }

        csvFormatCheckbox.setOnCheckedChangeListener(this);
        htmlFormatCheckbox.setOnCheckedChangeListener(this);
    }

}
