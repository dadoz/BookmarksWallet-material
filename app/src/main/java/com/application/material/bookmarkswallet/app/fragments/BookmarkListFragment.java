package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.tjeannin.apprate.AppRate;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class BookmarkListFragment extends Fragment
		implements View.OnClickListener,
        Filterable, SwipeRefreshLayout.OnRefreshListener {
	private static final String TAG = "LinksListFragment_TAG";
	public static final String FRAG_TAG = "LinksListFragment";
	private MainActivity mMainActivityRef;
	@InjectView(R.id.linksListId)
	RecyclerViewCustom mRecyclerView;
	@InjectView(R.id.addLinkButtonId)
    FloatingActionButton addLinkButton;
	@InjectView(R.id.importFloatingButtonId)
	FloatingActionButton importFloatingButton;
	@InjectView(R.id.clipboardFloatingButtonId)
	FloatingActionButton clipboardFloatingButton;
	@InjectView(R.id.floatingMenuButtonId)
    FloatingActionsMenu floatingMenuButton;
	@InjectView(R.id.undoLinkDeletedLayoutId)
	View undoLinkDeletedLayout;
	@InjectView(R.id.undoButtonId)
	View undoButton;
	@InjectView(R.id.dismissButtonId)
	View dismissButton;
    @InjectView(R.id.emptyLinkListViewId)
	View emptyLinkListView;
    @InjectView(R.id.emptySearchResultLayoutId)
    View mEmptySearchResultView;
    @InjectView(R.id.mainContainerViewId)
    SwipeRefreshLayout mSwipeRefreshLayout;

	private LinearLayoutManager linearLayoutManager;
	private RealmResults<Bookmark> mItems;
//	private SwipeDismissRecyclerViewTouchListener touchListener;
	private GestureDetectorCompat detector;
	private View mLinkListView;
	private ActionBarHandlerSingleton mActionBarHandlerSingleton;
	private RecyclerViewActionsSingleton rvActionsSingleton;
	private ExportBookmarkSingleton exportBookmarksSingleton;
    private BookmarkRecyclerViewAdapter mLinkRecyclerViewAdapter;
//    private android.support.v7.widget.ShareActionProvider mShareActionProvider;
    private static Realm mRealm;
    private ClipboardSingleton mClipboardSingleton;
//    private FeedbackDialog mFeedBackDialog;


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
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_blue_bright,
                android.R.color.holo_green_light);
//		mActionBarHandlerSingleton.setViewOnActionMenu(mSwipeRefreshLayout, actionbarInfoView, R.id.actionbarInfoLayoutId, this);
		mActionBarHandlerSingleton.setToolbarScrollManager(mRecyclerView, (View) addLinkButton.getParent());
        mActionBarHandlerSingleton.setTitle(null);
        mActionBarHandlerSingleton.setDisplayHomeEnabled(false);

        View.OnClickListener longClickListener = (View.OnClickListener) mRecyclerView.getAdapter();
//        touchListener = new SwipeDismissRecyclerViewTouchListener(mRecyclerView, longClickListener, this); //LISTENER TO SWIPE
        rvActionsSingleton = RecyclerViewActionsSingleton.
                getInstance(mSwipeRefreshLayout, mRecyclerView, mMainActivityRef, this);

        mItems = rvActionsSingleton.getBookmarksList();

		addLinkButton.setOnClickListener(this);
        importFloatingButton.setOnClickListener(this);
        clipboardFloatingButton.setOnClickListener(this);
		undoButton.setOnClickListener(this);
		dismissButton.setOnClickListener(this);

		initRecyclerView();
        rvActionsSingleton.setAdapter();
        if(mActionBarHandlerSingleton.isEditMode()) {
            rvActionsSingleton.selectBookmarkEditMenu(mActionBarHandlerSingleton.getEditItemPos());
        }
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

        if(! isItemSelected) {
            Drawable icon = menu.findItem(R.id.action_search).getIcon();
            icon.setColorFilter(mMainActivityRef.getResources().getColor(R.color.material_violet_500),
                    PorterDuff.Mode.SRC_IN);
            menu.findItem(R.id.action_search).setIcon(icon);
        } else {
            Drawable icon = menu.findItem(R.id.action_edit).getIcon();
            icon.setColorFilter(mMainActivityRef.getResources().getColor(R.color.material_violet_500),
                    PorterDuff.Mode.SRC_IN);
            menu.findItem(R.id.action_edit).setIcon(icon);

        }
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
                                    .setSearchResult(true);
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {
                            ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter())
                                    .setSearchResult(false);
                            rvActionsSingleton.setAdapter();
                            return true;
                        }
                    });
        }

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(mMainActivityRef.getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
            {
                public boolean onQueryTextChange(String newText)
                {
                    if(newText.trim().toLowerCase().equals("")) {
                        rvActionsSingleton.setAdapter();
                        return true;
                    }

                    getFilter().filter(newText);
                    return true;
                }

                public boolean onQueryTextSubmit(String query)
                {
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
				exportBookmarksSingleton.exportAction();
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
			case R.id.dismissExportButtonDialogId:
				exportBookmarksSingleton.dismissExportDialog();
				break;
			case R.id.saveEditUrlDialogId:
                rvActionsSingleton.saveEditLinkDialog();
                break;
			case R.id.exportConfirmButtonDialogId:
				exportBookmarksSingleton.exportBookmarks(mItems);
				break;
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
                break;
            case R.id.importFloatingButtonId:
                mActionBarHandlerSingleton.toggleActionBar(true, false, false);
                mMainActivityRef.changeFragment(new ImportBookmarkFragment(),
                        null, ImportBookmarkFragment.FRAG_TAG);
                break;
			case R.id.actionbarInfoActionIconId:
				Toast.makeText(mMainActivityRef, "dismiss", Toast.LENGTH_SHORT).show();
//				mActionBarHandlerSingleton.initToggleSettings(false, false);
//				mActionBarHandlerSingleton.showLayoutByMenuAction(R.id.actionbarInfoActionIconId);
				break;
			case R.id.actionbarImportActionIconId:
				Toast.makeText(mMainActivityRef, "dismiss", Toast.LENGTH_SHORT).show();
				break;
			case R.id.addLinkButtonId:
				mMainActivityRef.startActivityForResultWrapper(AddBookmarkActivity.class,
						AddBookmarkActivity.ADD_REQUEST, null);
				break;
//			case R.id.editUrlLabelId:
//				String url = (String) v.getTag();
//				rvActionsSingleton.editLinkDialog(url);
//				break;

//			case R.id.undoButtonId:
//				Toast.makeText(mMainActivityRef, "undo", Toast.LENGTH_SHORT).show();
////				Bookmark deletedItem = adapter.getDeletedItem();
//				int deletedItemPosition = adapter.getDeletedItemPosition();
//                adapter.notifyItemInserted(deletedItemPosition);
//                rvActionsSingleton.setDeletedItemPosition(-1);
////				adapter.addOnPosition(deletedItem, deletedItemPosition);
//				setUndoDeletedLinkLayout(false);
//				break;
//			case R.id.dismissButtonId:
//				Toast.makeText(mMainActivityRef, "dismiss", Toast.LENGTH_SHORT).show();
////				deletedItem = adapter.getDeletedItem();
////				dbConnector.deleteLinkById((int) deletedItem.getId());
//                rvActionsSingleton.deleteBookmark(adapter.getDeletedItemPosition());
//				setUndoDeletedLinkLayout(false);
//				break;

//			case R.id.actionbarInfoLayoutId:
//				mActionBarHandlerSingleton.toggleInnerLayoutByActionMenu(v.getId());
//				break;
		}
	}


	//onclick listener
/*	@Override
	public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
		detector.onTouchEvent(motionEvent);
		return false;
	}

	@Override
	public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
	}
*/
	public void addLinkOnRecyclerViewWrapper(String url) {
        try {
            rvActionsSingleton.addBookmarkWithInfo(url);
        } catch (Exception e) {
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(mMainActivityRef, "error on add bookmark - url not valid!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
	}

	//TODO rename it
//	public void setUndoDeletedLinkLayout(boolean isDeleting) {
//		undoLinkDeletedLayout.setVisibility(isDeleting ? View.VISIBLE : View.GONE);
//		addLinkButton.setVisibility(isDeleting ? View.GONE : View.VISIBLE);
//	}

	public void undoEditBookmarkRecyclerViewWrapper() {
		rvActionsSingleton.undoEditBookmark();
//        setInitialAdapter();
    }

//	public void saveEditLinkRecyclerViewWrapper() {
//        rvActionsSingleton.saveEditLink();
//    }

	public void toggleAddLinkButton(boolean isVisible) {
		//hide fab button
        floatingMenuButton.collapse();
        floatingMenuButton.animate().
				translationY(isVisible ? 300 : 0).
				setInterpolator(new DecelerateInterpolator(3.f)).
				setStartDelay(200).
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

    private class LinkFilter extends Filter {
        public LinkFilter() {
        }

        @Override
        protected FilterResults performFiltering(final CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            filterResults.values = null;
            filterResults.count = 0;
//            mRealmRef.beginTransaction();
//            RealmQuery<Bookmark> query = mRealmRef.where(Bookmark.class);
//            RealmResults<Bookmark> filteredList = query
//                    .contains("name", (String) constraint).or()
//                    .contains("url", (String) constraint)
//                    .findAll();
//            mRealm.commitTransaction();
/*
            if(constraint != null &&
                    constraint.length() != 0 &&
                    mDataset != null &&
                    mDataset.size() != 0) {

//                for(Bookmark bookmark : mDataset) {
//                    if(bookmark.getName().toLowerCase().trim().contains(constraint.toString().toLowerCase())) {
//                        filteredList.add(bookmark);
//                    }
//                }
                if(filteredList.size() != 0) {
                    filterResults.values = filteredList;
                    filterResults.count = filteredList.size();
                }

//                mDataset.where(Bookmark.class).findAll()
            }
*/
            return filterResults;
        }

        @Override
        protected void publishResults(final CharSequence constraint, FilterResults results) {
            mMainActivityRef.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RealmQuery<Bookmark> query = mRealm.where(Bookmark.class);
                    RealmResults<Bookmark> filteredList;
                    filteredList = query
                            .contains("name", (String) constraint).or()
                            .contains("url", (String) constraint)
                            .findAll();

                    Log.e(TAG, "hey" + filteredList.size());
                    if (filteredList.size() == 0) {
                        mRecyclerView.setEmptySearchResultQuery(constraint);
                    }
                    rvActionsSingleton.setAdapterByDataItems(filteredList);
                }
            });


//            ArrayList<Link> temp = new ArrayList<Link>();
//            temp.add(mDataset.get(0));


//            ((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).updateDataset();


//            LinkRecyclerViewAdapter searchResultRecyclerViewAdapter =
//                    new LinkRecyclerViewAdapter(mFragmentRef, temp, true);
//            int oldPosition = ((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).getSelectedItemPosition();
//            searchResultRecyclerViewAdapter.setSelectedItemPosition(oldPosition);
//            rvActionsSingleton.setAdapterRef(searchResultRecyclerViewAdapter);
//            mRecyclerView.setAdapter(searchResultRecyclerViewAdapter);
        }
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
