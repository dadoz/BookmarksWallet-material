package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.LinkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnInitActionBarInterface;
import com.application.material.bookmarkswallet.app.models.Link;
import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * Created by davide on 30/06/14.
 */
public class AddBookmarkFragment extends Fragment implements View.OnClickListener {
    public static String FRAG_TAG = "AddBookmarkFragment";
    private View addBookmarkView;
    private AddBookmarkActivity addBookmarkActivityRef;
    @InjectView(R.id.addLinkButtonId)
    FloatingActionButton addLinkButton;
    @InjectView(R.id.bokmarksCounterTextId)
    TextView bokmarksCounterText;
    @InjectView(R.id.periodAddBookmarkTextId) TextView periodAddBookmarkText;
    private ClipboardManager clipboard;
    private String TAG = "AddBookmarkFragment";
    private EditText addBookmarkUrlEditText;
    private View pasteFromClipboardButton;

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
        addBookmarkActivityRef = (AddBookmarkActivity) activity;
        //mmmmm
        clipboard = (ClipboardManager) addBookmarkActivityRef.
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
        addBookmarkActivityRef.initActionBarWithCustomView(toolbar);

        //get view from actionbar
        View customView = addBookmarkActivityRef.getSupportActionBar().getCustomView();
        addBookmarkUrlEditText = (EditText) customView.
                findViewById(R.id.addBookmarkUrlEditTextId);
        pasteFromClipboardButton = customView.
                findViewById(R.id.pasteFromClipboardButtonId);

        setHasOptionsMenu(true);

        onInitView();
        return addBookmarkView;
    }

    private void onInitView() {
        addLinkButton.setOnClickListener(this);
        //TODO please replace
        periodAddBookmarkText.setText("01.01.15 - 02.01.15");
        bokmarksCounterText.setText("20");
        pasteFromClipboardButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addLinkButtonId:
                Toast.makeText(addBookmarkActivityRef, "hey saving", Toast.LENGTH_SHORT).show();
                //activity result
                String linkUrl = addBookmarkUrlEditText.getText().toString();
                if(linkUrl.equals("")) {
                    Toast.makeText(addBookmarkActivityRef, "bookmark url not valid", Toast.LENGTH_SHORT).show();
                    break;
                }

//                Link link = new Link(-1, null, "name from url", linkUrl, -1, null, false);

                Intent intent = new Intent();
                intent.putExtra(AddBookmarkActivity.LINK_URL_EXTRA, linkUrl);
                addBookmarkActivityRef.setResult(Activity.RESULT_OK, intent);
                addBookmarkActivityRef.finish();
                break;
            case R.id.pasteFromClipboardButtonId:
                if(! hasClipboardText()) {
                    Toast.makeText(addBookmarkActivityRef, "no text in clipboard", Toast.LENGTH_SHORT).show();
                    break;
                }

                String bookmarkUrl = getTextFromClipboard();
                if(bookmarkUrl == null) {
                    break;
                }

                addBookmarkUrlEditText.setText(bookmarkUrl);
//                Toast.makeText(addBookmarkActivityRef, "paste from clipboard", Toast.LENGTH_SHORT).show();
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
                addBookmarkActivityRef.changeFragment(
                        new SettingsFragment(), null, SettingsFragment.FRAG_TAG);
                return true;

        }
        return true;
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
}
