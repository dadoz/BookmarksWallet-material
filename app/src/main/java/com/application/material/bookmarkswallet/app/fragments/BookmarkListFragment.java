package com.application.material.bookmarkswallet.app.fragments;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.*;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.adapter.realm.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.recyclerView.RecyclerViewCustom;
import com.application.material.bookmarkswallet.app.singleton.ActionBarHandlerSingleton;
import com.application.material.bookmarkswallet.app.singleton.ClipboardSingleton;
import com.application.material.bookmarkswallet.app.singleton.ExportBookmarkSingleton;
import com.application.material.bookmarkswallet.app.singleton.RecyclerViewActionsSingleton;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.tjeannin.apprate.AppRate;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookmarkListFragment extends Fragment
		implements View.OnClickListener,
        Filterable, SwipeRefreshLayout.OnRefreshListener, FloatingActionMenu.OnMenuToggleListener, SlidingUpPanelLayout.PanelSlideListener, CompoundButton.OnCheckedChangeListener {
	private static final String TAG = "LinksListFragment_TAG";
	public static final String FRAG_TAG = "LinksListFragment";
	private MainActivity mMainActivityRef;
	@InjectView(R.id.urlEditText)
    EditText mUrlEditText;
    @InjectView(R.id.httpFormatCheckboxId)
    CheckBox mHttpFormatCheckbox;
	@InjectView(R.id.slidingPanelLabelTextId)
    TextView mSlidingPanelLabelText;
	@InjectView(R.id.slidingPanelDoneIconId)
    ImageView slidingPanelDoneIcon;
	@InjectView(R.id.slidingPanelLayoutId)
    LinearLayout mSlidingPanelLayout;
	@InjectView(R.id.slidingLayerLayoutId)
    SlidingUpPanelLayout mSlidingLayerLayout;
	@InjectView(R.id.linksListId)
	RecyclerViewCustom mRecyclerView;
	@InjectView(R.id.addLinkButtonId)
    FloatingActionButton addLinkButton;
	@InjectView(R.id.importFloatingButtonId)
	FloatingActionButton importFloatingButton;
	@InjectView(R.id.clipboardFloatingButtonId)
    FloatingActionButton clipboardFloatingButton;
	@InjectView(R.id.floatingMenuButtonId)
    FloatingActionMenu mFloatingMenuButton;
//	@InjectView(R.id.undoLinkDeletedLayoutId)
//	View undoLinkDeletedLayout;
//	@InjectView(R.id.undoButtonId)
//	View undoButton;
//	@InjectView(R.id.dismissButtonId)
//	View dismissButton;
    @InjectView(R.id.emptyLinkListViewId)
	View emptyLinkListView;
    @InjectView(R.id.emptySearchResultLayoutId)
    View mEmptySearchResultView;
    @InjectView(R.id.mainContainerViewId)
    SwipeRefreshLayout mSwipeRefreshLayout;

	private LinearLayoutManager linearLayoutManager;
	private RealmResults<Bookmark> mItems;
	private GestureDetectorCompat detector;
	private View mLinkListView;
	private ActionBarHandlerSingleton mActionBarHandlerSingleton;
	private RecyclerViewActionsSingleton rvActionsSingleton;
	private ExportBookmarkSingleton exportBookmarksSingleton;
    private BookmarkRecyclerViewAdapter mLinkRecyclerViewAdapter;
    private static Realm mRealm;
    private ClipboardSingleton mClipboardSingleton;


    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof OnChangeFragmentWrapperInterface)) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnLoadViewHandlerInterface");
		}
		mMainActivityRef =  (MainActivity) activity;
		mActionBarHandlerSingleton = ActionBarHandlerSingleton.getInstance(mMainActivityRef);
		exportBookmarksSingleton = ExportBookmarkSingleton.getInstance(this, mMainActivityRef);
        mClipboardSingleton = ClipboardSingleton.getInstance(mMainActivityRef);
        mRealm = Realm.getInstance(mMainActivityRef);
//		dataApplication = (DataApplication) addActivityRef.getApplication();
	}

	@Override
	public void onActivityCreated(Bundle savedInstance) {
		super.onActivityCreated(savedInstance);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstance) {
		mLinkListView = inflater.inflate(R.layout.bookmark_list_layout,
				container, false);
		ButterKnife.inject(this, mLinkListView);

		setHasOptionsMenu(true);
		onInitView();

        new AppRate(mMainActivityRef)
                .setMinDaysUntilPrompt(7)
                .setMinLaunchesUntilPrompt(20)
                .init();
		return mLinkListView;
	}

	private void onInitView() {
        ArrayList<View> viewArrayList = new ArrayList<>();
        viewArrayList.add(clipboardFloatingButton);
        viewArrayList.add(mSlidingLayerLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSlidingLayerLayout.setPanelSlideListener(this);
        slidingPanelDoneIcon.setOnClickListener(this);
//        mSlidingLayerLayout
        mSwipeRefreshLayout.setColorSchemeColors(android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_blue_bright,
                android.R.color.holo_green_light);
//		mActionBarHandlerSingleton.setViewOnActionMenu(mSwipeRefreshLayout, actionbarInfoView, R.id.actionbarInfoLayoutId, this);
		mActionBarHandlerSingleton.setToolbarScrollManager(mRecyclerView, viewArrayList);
        mActionBarHandlerSingleton.setTitle(null);
        mActionBarHandlerSingleton.setDisplayHomeEnabled(false);

//        View.OnClickListener longClickListener = (View.OnClickListener) mRecyclerView.getAdapter();
//        touchListener = new SwipeDismissRecyclerViewTouchListener(mRecyclerView, longClickListener, this); //LISTENER TO SWIPE
        rvActionsSingleton = RecyclerViewActionsSingleton
                .getInstance(mSwipeRefreshLayout, mRecyclerView, mMainActivityRef, this);

        mItems = rvActionsSingleton.getBookmarksList();

//        setIconOnFLoatingButton();

        addLinkButton.setOnClickListener(this);
        importFloatingButton.setOnClickListener(this);
        clipboardFloatingButton.setOnClickListener(this);

        mFloatingMenuButton.setOnMenuToggleListener(this);
		initRecyclerView();
        rvActionsSingleton.setAdapter();
        if (mActionBarHandlerSingleton.isEditMode()) {
            rvActionsSingleton.selectBookmarkEditMenu(mActionBarHandlerSingleton.getEditItemPos());
        }
	}

    private void setIconOnFLoatingButton() {
//        Resources res = mMainActivityRef.getResources();
/*        Drawable icon = res.getDrawable(R.drawable.ic_insert_drive_file_white_24dp);
        mActionBarHandlerSingleton.setColorFilter(icon, R.color.material_mustard_yellow);
        importFloatingButton.setImageDrawable(icon);

        icon = res.getDrawable(R.drawable.ic_content_paste_white_24dp);
        mActionBarHandlerSingleton.setColorFilter(icon, R.color.material_mustard_yellow);
        clipboardFloatingButton.setImageDrawable(icon);

        icon = res.getDrawable(R.drawable.ic_edit_white_24dp);
        mActionBarHandlerSingleton.setColorFilter(icon, R.color.material_mustard_yellow);
        addLinkButton.setImageDrawable(icon);*/

    }

    private void initRecyclerView() {
		mLinkRecyclerViewAdapter =
				new BookmarkRecyclerViewAdapter(mMainActivityRef, mRecyclerView);

//		detector = new GestureDetectorCompat(mMainActivityRef, new RecyclerViewOnGestureListener()); //ONCLICK - ONLONGCLICK

		linearLayoutManager = new LinearLayoutManager(mMainActivityRef);
		emptyLinkListView.findViewById(R.id.importLocalBookmarksButtonId).setOnClickListener(this);

		//set empty view
		mRecyclerView.setEmptyView(emptyLinkListView);
		mRecyclerView.setEmptySearchResultView(mEmptySearchResultView);

		mRecyclerView.setHasFixedSize(true);
		//set layout manager
		mRecyclerView.setLayoutManager(linearLayoutManager);
		mRecyclerView.setAdapter(mLinkRecyclerViewAdapter);
		mRecyclerView.setItemAnimator(null);
		//set SWIPE
//		mRecyclerView.setOnTouchListener(touchListener);
//		mRecyclerView.setOnScrollListener(touchListener.makeScrollListener());
		//set on item click listener
//		mRecyclerView.addOnItemTouchListener(this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        boolean isItemSelected = mActionBarHandlerSingleton.isEditMode();

		inflater.inflate(isItemSelected ? R.menu.save_edit_link_menu :
                R.menu.menu_main, menu);

        Drawable icon = menu.findItem(! isItemSelected ? R.id.action_search : R.id.action_edit).getIcon();
        icon.setColorFilter(mMainActivityRef.getResources().getColor(R.color.material_violet_500),
                PorterDuff.Mode.SRC_IN);
        menu.findItem(! isItemSelected ? R.id.action_search : R.id.action_edit).setIcon(icon);
        //LAYOUT MANAGER
//        if(! isItemSelected) {
//            menu.findItem(R.id.action_grid)
//                    .setVisible(mActionBarHandlerSingleton
//                            .isLayoutManagerList());
//            menu.findItem(R.id.action_list)
//                    .setVisible(mActionBarHandlerSingleton
//                            .isLayoutManagerGrid());
//        }

        //SEARCH VIEW HANDLER
        searchViewHandler(menu);

        super.onCreateOptionsMenu(menu, inflater);
	}

    public void searchViewHandler(Menu menu) {
        //SEARCH ITEM
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) mMainActivityRef.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            MenuItemCompat.setOnActionExpandListener(searchItem,
                    new MenuItemCompat.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem item) {
                            ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter())
                                    .setSearchMode(true);
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                            int startDelay = 600;
                            toggleAddLinkButton(true, startDelay);
                            mSlidingLayerLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {
                            ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter())
                                    .setSearchMode(false);
                            rvActionsSingleton.setAdapter();
//                            toggleAddLinkButton(false, -1);
                            return true;
                        }
                    });
        }

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(mMainActivityRef.getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {
                    if(newText.trim().toLowerCase().equals("")) {
                        rvActionsSingleton.setAdapter();
                        return true;
                    }

                    getFilter().filter(newText);
                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
//                    ((SearchView) searchItem.getActionView()).setIconified(true);
//                    (searchItem.getActionView()).clearFocus();
                    return false;
                }
            });
        }

    }


/*    private void setInitialAdapter() {
        if(mRecyclerView.getAdapter() != mLinkRecyclerViewAdapter) {
            mRecyclerView.setAdapter(mLinkRecyclerViewAdapter);
            rvActionsSingleton.setAdapterRef(mLinkRecyclerViewAdapter);
        }
    }*/

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit:
//                Toast.makeText(mMainActivityRef, "edit action",  Toast.LENGTH_SHORT).show();
                Bookmark bookmark = rvActionsSingleton.getSelectedItemFromAdapter();
                rvActionsSingleton.editLinkDialog(bookmark);
				break;
			case R.id.action_share:
//                Toast.makeText(mMainActivityRef, "share action",  Toast.LENGTH_SHORT).show();
                bookmark = rvActionsSingleton.getSelectedItemFromAdapter();
                Intent intent = rvActionsSingleton.getIntentForEditBookmark(bookmark);
                mMainActivityRef.startActivity(Intent.createChooser(intent, "share bookmark to..."));
				break;
			case R.id.action_settings:
				mActionBarHandlerSingleton.toggleActionBar(true, false, false);
                mMainActivityRef.changeFragment(new SettingsFragment(), null, SettingsFragment.FRAG_TAG);
                return true;
			case R.id.action_export:
				exportBookmarksSingleton.exportAction(mItems);
				return true;
//			case R.id.action_grid:
//                mRecyclerView.setLayoutManager(new GridLayoutManager(mMainActivityRef, 2));
//                mRecyclerView.getAdapter().notifyDataSetChanged();
//                mActionBarHandlerSingleton.setLayoutManagerType(LayoutManagerTypeEnum.GRID);
//                mMainActivityRef.invalidateOptionsMenu();
//				return true;
//			case R.id.action_list:
//                mRecyclerView.setLayoutManager(new LinearLayoutManager(mMainActivityRef));
//                mRecyclerView.getAdapter().notifyDataSetChanged();
//                mActionBarHandlerSingleton.setLayoutManagerType(LayoutManagerTypeEnum.LIST);
//                mMainActivityRef.invalidateOptionsMenu();
//				return true;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
//		BookmarkRecyclerViewAdapter adapter = (BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter();
		switch (v.getId()) {
//			case R.id.dismissExportButtonDialogId:
//				exportBookmarksSingleton.dismissExportDialog();
//				break;
//			case R.id.saveEditUrlDialogId:
//                rvActionsSingleton.saveEditLinkDialog();
//                break;
			case R.id.importLocalBookmarksButtonId:
				rvActionsSingleton.setBookmarksByProvider();
				break;
            case R.id.clipboardFloatingButtonId:
                if(! mClipboardSingleton.hasClipboardText()) {
                    Toast.makeText(mMainActivityRef, "no text in clipboard", Toast.LENGTH_SHORT).show();
                    break;
                }

                String bookmarkUrl = mClipboardSingleton.getTextFromClipboard();
                if(bookmarkUrl == null) {
                    break;
                }
                addLinkOnRecyclerViewWrapper(bookmarkUrl.trim());
                mFloatingMenuButton.close(true);
                break;
            case R.id.importFloatingButtonId:
                mActionBarHandlerSingleton.toggleActionBar(true, false, false);
                mMainActivityRef.changeFragment(new ImportBookmarkFragment(),
                        null, ImportBookmarkFragment.FRAG_TAG);
                mFloatingMenuButton.close(true);
                break;
			case R.id.actionbarInfoActionIconId:
				Toast.makeText(mMainActivityRef, "dismiss", Toast.LENGTH_SHORT).show();
				break;
			case R.id.actionbarImportActionIconId:
				Toast.makeText(mMainActivityRef, "dismiss", Toast.LENGTH_SHORT).show();
				break;
			case R.id.addLinkButtonId:
//				mMainActivityRef.startActivityForResultWrapper(AddBookmarkActivity.class,
//						AddBookmarkActivity.ADD_REQUEST, null);
                mFloatingMenuButton.close(true);
                break;
			case R.id.slidingPanelDoneIconId:
                Log.e("TAG", mUrlEditText.getText().toString());
                if (!isValidUrl(mUrlEditText.getText().toString())) {
                    Toast.makeText(mMainActivityRef, "no valid url typed in!", Toast.LENGTH_SHORT).show();
                    break;
                }

                try {
                    rvActionsSingleton.addBookmarkWithInfo(mUrlEditText.getText().toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                mSlidingLayerLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                mUrlEditText.setText("");
                break;
		}
	}


	public void addLinkOnRecyclerViewWrapper(String url) {
        try {
            rvActionsSingleton.addBookmarkWithInfo(url);
        } catch (Exception e) {
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(mMainActivityRef, "error on add bookmark - url not valid!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
	}

	public void undoEditBookmarkRecyclerViewWrapper() {
		rvActionsSingleton.undoEditBookmark();
    }

	public void collapseAddLinkButton() {
        mFloatingMenuButton.close(true);
    }

	public void toggleAddLinkButton(boolean isVisible, int startDelay) {
		//hide fab button
//        mFloatingMenuButton.close(true);
        clipboardFloatingButton.animate().
				translationY(isVisible ? 300 : 0).
				setInterpolator(new DecelerateInterpolator(3.f)).
				setStartDelay(startDelay == -1 ? 200 : 600).
				start();
	}

    @Override
    public Filter getFilter() {
        return new LinkFilter();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        rvActionsSingleton.setAdapter();
        //get data from provider again :)
//        ((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).updateDataset();

        //NEED TO BE IMPLEMENTED
    }

    @Override
    public void onMenuToggle(boolean isVisible) {
        mActionBarHandlerSingleton.setOverrideBackPressed(isVisible);
    }

    @Override
    public void onPanelSlide(View view, float v) {
        ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        int startColor = mMainActivityRef.getResources().getColor(R.color.material_violet_500);
        int endColor = mMainActivityRef.getResources().getColor(R.color.white);
        int interpolatedColor = (int) argbEvaluator.evaluate(v, startColor, endColor);
        int inverseInterpolatedColor = (int) argbEvaluator.evaluate(v, endColor, startColor);
        mSlidingPanelLayout.setBackgroundColor(interpolatedColor);
        mSlidingPanelLabelText.setTextColor(inverseInterpolatedColor);
    }

    @Override
    public void onPanelCollapsed(View view) {
        mActionBarHandlerSingleton.setOverrideBackPressed(false);
        mActionBarHandlerSingleton.hideSoftKeyboard(mUrlEditText);
//        mActionBarHandlerSingleton.setColorFilter(slidingPanelLabelIcon.getDrawable(),
//                R.color.white);
        clipboardFloatingButton.show(true);
        slidingPanelDoneIcon.setVisibility(View.GONE);
    }

    @Override
    public void onPanelExpanded(View view) {
        mActionBarHandlerSingleton.setOverrideBackPressed(true);
        clipboardFloatingButton.hide(true);
        slidingPanelDoneIcon.setVisibility(View.VISIBLE);
        mActionBarHandlerSingleton.setColorResourceFilter(slidingPanelDoneIcon.getDrawable(),
                R.color.material_violet_500);
        mUrlEditText.setFocusableInTouchMode(true);
        mUrlEditText.requestFocus();
        mHttpFormatCheckbox.setOnCheckedChangeListener(this);
        mUrlEditText.setText(mHttpFormatCheckbox.isChecked() ? "https://" : "http://");
        mUrlEditText.setSelection(mUrlEditText.getText().length());
    }

    @Override
    public void onPanelAnchored(View view) {
        Log.e(TAG, "hey");
//        mActionBarHandlerSingleton.hideSoftKeyboard(mUrlEditText);
    }

    @Override
    public void onPanelHidden(View view) {

    }

    public void collapseSlidingPanel() {
        mSlidingLayerLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.httpFormatCheckboxId:
                mUrlEditText.setText(isChecked ? "https://" : "http://");
                mUrlEditText.setSelection(mUrlEditText.getText().length());
                break;
        }
    }

    private class LinkFilter extends Filter {
        public LinkFilter() {
        }

        @Override
        protected FilterResults performFiltering(final CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            filterResults.values = null;
            filterResults.count = 0;
            return filterResults;
        }

        @Override
        protected void publishResults(final CharSequence constraint, FilterResults results) {
            mMainActivityRef.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RealmQuery<Bookmark> query = mRealm.where(Bookmark.class);
                    RealmResults<Bookmark> filteredList;
                    String searchValue = ((String) constraint).toLowerCase();
                    boolean caseSensitive = false;
                    filteredList = query
                            .contains("name", searchValue, caseSensitive)
                            .findAll();

                    Log.e(TAG, "hey" + filteredList.size());
                    if (filteredList.size() == 0) {
                        mRecyclerView.setEmptySearchResultQuery(constraint);
                    }
                    rvActionsSingleton.setAdapterByDataItems(filteredList);
                }
            });
        }
    }

    private static boolean isValidUrl(String linkUrl) {
        Pattern p = Pattern.
                compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?(https://)?(ftp://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?");

        Matcher m = p.matcher(linkUrl);
        return ! linkUrl.equals("") &&
                m.matches();
    }

/*

    private class RecyclerViewOnGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent event) {
			View view = mRecyclerView.findChildViewUnder(event.getX(), event.getY());
			view.findViewById(R.id.linkLayoutId).setPressed(true);
            mSwipeRefreshLayout.setDownView(view);
			return false;
		}

/*		@Override
		public void onShowPress(MotionEvent event) {
			View view = mRecyclerView.findChildViewUnder(event.getX(), event.getY());
			view.findViewById(R.id.linkLayoutId).setPressed(true);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent event) {
			View view = mRecyclerView.findChildViewUnder(event.getX(), event.getY());
			view.findViewById(R.id.linkLayoutId).setPressed(true);
			return false;
		}*/
/*
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
			int position = mRecyclerView.getChildPosition(view);

            Bookmark bookmark = (Bookmark) ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter()).getItem(position);
            rvActionsSingleton.openLinkOnBrowser(bookmark.getUrl());

			return super.onSingleTapConfirmed(e);
        }

        @Override
		public void onLongPress(MotionEvent e) {
			View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
			int position = mRecyclerView.getChildPosition(view);
            BookmarkRecyclerViewAdapter.ViewHolder holder =
					(BookmarkRecyclerViewAdapter.ViewHolder) mRecyclerView.
							findViewHolderForPosition(position);
			holder.itemView.setSelected(true);
            mActionBarHandlerSingleton.setEditItemPos(position);

			// handle long press
			rvActionsSingleton.selectBookmarkEditMenu(position);
			super.onLongPress(e);
		}
	}
*/
}
