package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.AddBookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnInitActionBarInterface;
import com.application.material.bookmarkswallet.app.models.BookmarkCardview;
import com.application.material.bookmarkswallet.app.models.BookmarkCardview.CardviewTypeEnum;
import com.application.material.bookmarkswallet.app.models.Info;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * Created by davide on 30/06/14.
 */
public class AddBookmarkFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static String FRAG_TAG = "AddBookmarkFragment";
    private View addBookmarkView;
    private AddBookmarkActivity mAddActivityRef;
    @InjectView(R.id.addLinkButtonId)
    FloatingActionButton addLinkButton;
    @InjectView(R.id.addBookmarkRecyclerViewId)
    RecyclerView mRecyclerView;
//    @InjectView(R.id.infoCounterTextviewId)
//    TextView infoCounterTextview;
//    @InjectView(R.id.infoPeriodTextviewId) TextView infoPeriodTextview;
//    @InjectView(R.id.importCardViewId)
//    CardView importCardView;
    private ClipboardManager clipboard;
    private String TAG = "AddBookmarkFragment";
    private EditText addBookmarkUrlEditText;
    private View pasteFromClipboardButton;
    public static int PICK_IMAGE_REQ_CODE;
    private long ANIM_DURATION_FAB = 400;
    private AddBookmarkRecyclerViewAdapter mAdapter;
    private ArrayList<BookmarkCardview> cardviewList;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (! (activity instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        if (! (activity instanceof OnInitActionBarInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnInitActionBarInterface");
        }
        mAddActivityRef = (AddBookmarkActivity) activity;
        //mmmmm
        clipboard = (ClipboardManager) mAddActivityRef.
                getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        addBookmarkView = inflater.inflate(R.layout.add_bookmark_fragment, null);
        ButterKnife.inject(this, addBookmarkView);
        Toolbar toolbar = (Toolbar) addBookmarkView.findViewById(R.id.toolbarId);
        mAddActivityRef.initActionBarWithCustomView(toolbar);

        //get view from actionbar
        View customView = mAddActivityRef.getSupportActionBar().getCustomView();
        addBookmarkUrlEditText = (EditText) customView.
                findViewById(R.id.addBookmarkUrlEditTextId);
        pasteFromClipboardButton = customView.
                findViewById(R.id.pasteFromClipboardButtonId);

        setHasOptionsMenu(true);

        onInitView();
        return addBookmarkView;
    }

    private void onInitView() {
        cardviewList = new ArrayList<BookmarkCardview>();
        addLinkButton.setOnClickListener(this);
//        setACustomAnimation();
        //TODO please replace
        pasteFromClipboardButton.setOnClickListener(this);

        RecyclerView.LayoutManager lm = new LinearLayoutManager(mAddActivityRef);
        mRecyclerView.setLayoutManager(lm);

        cardviewList.add(new Info(CardviewTypeEnum.INFO_CARDVIEW,
                "Info", "01.01.15 - 02.01.15", 17));
        cardviewList.add(new BookmarkCardview(CardviewTypeEnum.IMPORT_CARDVIEW, "Import"));

        mAdapter = new AddBookmarkRecyclerViewAdapter(this, cardviewList);
        mRecyclerView.setAdapter(mAdapter);

        View addBookmarkLayoutTemp = mAddActivityRef.getSupportActionBar().
                getCustomView().findViewById(R.id.addBookmarkLayoutId);
        final ViewGroup parent = ((ViewGroup) addBookmarkLayoutTemp.getParent());
        addLinkButton.setTag(true);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e(TAG, "hey : " + dy);

                if(dy > 0) {

                    boolean isVisible = (Boolean) addLinkButton.getTag();

                    if(isVisible) {
                        View addBookmarkLayout = mAddActivityRef.getSupportActionBar().
                                getCustomView().findViewById(R.id.addBookmarkLayoutId);
                        addBookmarkLayout.setVisibility(View.GONE);
                        parent.removeView(addBookmarkLayout);
                    } else {
                        mAddActivityRef.getLayoutInflater().
                                inflate(R.layout.actionbar_add_bookmark_inner_layout, parent);
                    }


                    //hide fab button
                    addLinkButton.animate().
                            translationY(isVisible ? -300 : 0).
                            setInterpolator(new DecelerateInterpolator(3.f)).
                            start();
                    addLinkButton.setTag(! isVisible);
                }
            }
        });
        //ANIMATION
        setACustomAnimation();
        //fill data to be animated

    }

    public void onViewCreated(View v, Bundle savedInstance) { super.onViewCreated(v, savedInstance); }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addLinkButtonId:
                Toast.makeText(mAddActivityRef, "hey saving", Toast.LENGTH_SHORT).show();
                //activity result
                String linkUrl = addBookmarkUrlEditText.getText().toString();
                if(linkUrl.equals("")) {
                    Toast.makeText(mAddActivityRef, "bookmark url not valid", Toast.LENGTH_SHORT).show();
                    break;
                }

//                Link link = new Link(-1, null, "name from url", linkUrl, -1, null, false);

                Intent intent = new Intent();
                intent.putExtra(AddBookmarkActivity.LINK_URL_EXTRA, linkUrl);
                mAddActivityRef.setResult(Activity.RESULT_OK, intent);
                mAddActivityRef.finish();
                break;
            case R.id.pasteFromClipboardButtonId:
                if(! hasClipboardText()) {
                    Toast.makeText(mAddActivityRef, "no text in clipboard", Toast.LENGTH_SHORT).show();
                    break;
                }

                String bookmarkUrl = getTextFromClipboard();
                if(bookmarkUrl == null) {
                    break;
                }

                addBookmarkUrlEditText.setText(bookmarkUrl);
//                Toast.makeText(mAddActivityRef, "paste from clipboard", Toast.LENGTH_SHORT).show();
                break;
            case R.id.importButtonId:
                boolean isCsvImport = ((CheckBox) addBookmarkView.
                        findViewById(R.id.csvFormatCheckboxId)).isChecked();
                Log.e(TAG, "csv - " + isCsvImport);
                if(android.os.Build.MANUFACTURER.equals("samsung")) {
                    intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
                    intent.putExtra("CONTENT_TYPE", "*/*");
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    mAddActivityRef.startActivityForResult(intent, PICK_IMAGE_REQ_CODE);
                    break;
                }

                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                mAddActivityRef.startActivityForResult(intent, PICK_IMAGE_REQ_CODE);
                break;
//            case R.id.importFromCSVCardId:
                //stored bookmarks from phone browser!
//                break;
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
                mAddActivityRef.changeFragment(
                        new SettingsFragment(), null, SettingsFragment.FRAG_TAG);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void setACustomAnimation() {
        addLinkButton.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();

    }
    public boolean hasClipboardText() {
        // If the clipboard doesn't contain data, disable the paste menu item.
        // If it does contain data, decide if you can handle the data.
        return (clipboard.hasPrimaryClip()) &&
                (clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN));
    }

    public String getTextFromClipboard() {
        // Examines the item on the clipboard. If getText() does not return null, the clip item contains the
        // text. Assumes that this application can only handle one item at a time.
        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

        // Gets the clipboard as text.
        CharSequence pasteData = item.getText();
        // If the string contains data, then the paste operation is done
        if (pasteData != null) {
            return pasteData.toString();
        }

        // The clipboard does not contain text. If it contains a URI, attempts to get data from it
        Uri pasteUri = item.getUri();
        // If the URI contains something, try to get text from it
        if (pasteUri != null) {

            // calls a routine to resolve the URI and get data from it. This routine is not
            // presented here.
            //TODO implement it if you need it:)
//            pasteData = resolveUri(Uri);
            return null;
        }

        // Something is wrong. The MIME type was plain text, but the clipboard does not contain either
        // text or a Uri. Report an error.
        Log.e(TAG, "Clipboard contains an invalid data type");
        return null;
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
                Toast.makeText(mAddActivityRef, "csvFormatCheckboxId", Toast.LENGTH_SHORT).show();
                break;
            case R.id.htmlFormatCheckboxId:
                csvFormatCheckbox.setChecked(false);
                Toast.makeText(mAddActivityRef, "htmlFormatCheckboxId", Toast.LENGTH_SHORT).show();
                break;
        }

        csvFormatCheckbox.setOnCheckedChangeListener(this);
        htmlFormatCheckbox.setOnCheckedChangeListener(this);
        Log.e(TAG, "is checked " + isChecked);
    }

}
