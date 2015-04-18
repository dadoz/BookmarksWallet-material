package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.*;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.adapter.LinkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.dbAdapter.DbAdapter;
import com.application.material.bookmarkswallet.app.dbAdapter.DbConnector;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.models.Link;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.recyclerView.RecyclerViewCustom;
import com.application.material.bookmarkswallet.app.singleton.ActionBarHandlerSingleton;
import com.application.material.bookmarkswallet.app.singleton.ExportBookmarkSingleton;
import com.application.material.bookmarkswallet.app.singleton.RecyclerViewActionsSingleton;
import com.application.material.bookmarkswallet.app.touchListener.SwipeDismissRecyclerViewTouchListener;
import com.getbase.floatingactionbutton.FloatingActionButton;

public class LinksListFragment extends Fragment
		implements View.OnClickListener,
			SwipeDismissRecyclerViewTouchListener.DismissCallbacks,
			RecyclerView.OnItemTouchListener, Filterable, SwipeRefreshLayout.OnRefreshListener {
	private static final String TAG = "LinksListFragment_TAG";
	public static final String FRAG_TAG = "LinksListFragment";
	public DbAdapter db;
	private MainActivity mainActivityRef;
	@InjectView(R.id.linksListId)
	RecyclerViewCustom mRecyclerView;
	@InjectView(R.id.addLinkButtonId)
	FloatingActionButton addLinkButton;
	@InjectView(R.id.undoLinkDeletedLayoutId)
	View undoLinkDeletedLayout;
	@InjectView(R.id.undoButtonId)
	View undoButton;
	@InjectView(R.id.dismissButtonId)
	View dismissButton;
	private View emptyLinkListView;
	private LinearLayoutManager linearLayoutManager;
	private ArrayList<Link> mItems;
	private SwipeDismissRecyclerViewTouchListener touchListener;
	private GestureDetectorCompat detector;
	private DbConnector dbConnector;
	private View mLinkListView;
	private ActionBarHandlerSingleton mActionBarHandlerSingleton;
	private RecyclerViewActionsSingleton rvActionsSingleton;
	private ExportBookmarkSingleton exportBookmarksSingleton;
    private LinkRecyclerViewAdapter mLinkRecyclerViewAdapter;
    private View mEmptySearchResultView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof OnChangeFragmentWrapperInterface)) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnLoadViewHandlerInterface");
		}
		mainActivityRef =  (MainActivity) activity;
		db = new DbAdapter(getActivity());
		dbConnector = DbConnector.getInstance(mainActivityRef);
		mActionBarHandlerSingleton = ActionBarHandlerSingleton.getInstance(mainActivityRef);
		exportBookmarksSingleton = ExportBookmarkSingleton.getInstance(this, mainActivityRef);

//		dataApplication = (DataApplication) addActivityRef.getApplication();
	}

	@Override
	public void onActivityCreated(Bundle savedInstance) {
		super.onActivityCreated(savedInstance);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstance) {
		mLinkListView = inflater.inflate(R.layout.links_list_layout,
				container, false);
		ButterKnife.inject(this, mLinkListView);

		emptyLinkListView = mLinkListView.findViewById(R.id.emptyLinkListViewId);
        mEmptySearchResultView = mLinkListView.findViewById(R.id.emptySearchResultLayoutId);
		setHasOptionsMenu(true);
		onInitView();
		return mLinkListView;
	}

	private void onInitView() {
		View actionbarInfoView = mLinkListView.findViewById(R.id.actionbarInfoLayoutId);
		mSwipeRefreshLayout = (SwipeRefreshLayout) mLinkListView.findViewById(R.id.mainContainerViewId);
        mSwipeRefreshLayout.setOnRefreshListener(this);
//        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light, android.R.color.holo_red_light,
//                android.R.color.holo_orange_light);
		mActionBarHandlerSingleton.setViewOnActionMenu(mSwipeRefreshLayout, actionbarInfoView, R.id.actionbarInfoLayoutId, this);
		mActionBarHandlerSingleton.setToolbarScrollManager(mRecyclerView, (View) addLinkButton.getParent());
        mActionBarHandlerSingleton.setTitle(null);
        mActionBarHandlerSingleton.setDisplayHomeEnabled(false);

		mItems = dbConnector.getLinkList();

		addLinkButton.setOnClickListener(this);
		undoButton.setOnClickListener(this);
		dismissButton.setOnClickListener(this);

		initRecyclerView();
		rvActionsSingleton = RecyclerViewActionsSingleton.
				getInstance(mSwipeRefreshLayout, mRecyclerView, mainActivityRef, this, dbConnector, touchListener);
        if(mActionBarHandlerSingleton.isEditMode()) {
            rvActionsSingleton.editLink(mActionBarHandlerSingleton.getEditItemPos());
        }
	}

	private void initRecyclerView() {
		mLinkRecyclerViewAdapter =
				new LinkRecyclerViewAdapter(this, mItems, false);

		detector = new GestureDetectorCompat(mainActivityRef, new RecyclerViewOnGestureListener()); //ONCLICK - ONLONGCLICK
		touchListener = new SwipeDismissRecyclerViewTouchListener(mRecyclerView, this); //LISTENER TO SWIPE

		linearLayoutManager = new LinearLayoutManager(mainActivityRef);
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
		mRecyclerView.setOnTouchListener(touchListener);
		mRecyclerView.setOnScrollListener(touchListener.makeScrollListener());
		//set on item click listener
		mRecyclerView.addOnItemTouchListener(this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		boolean isItemSelected = ((LinkRecyclerViewAdapter) mRecyclerView.
				getAdapter()).isItemSelected();
		inflater.inflate(isItemSelected ? R.menu.save_edit_link_menu :
                R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) mainActivityRef.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            MenuItemCompat.setOnActionExpandListener( menu.findItem(R.id.action_search),
                    new MenuItemCompat.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem item) {
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {
                            setInitialAdapter();
                            return true;
                        }
                    });
        }

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(mainActivityRef.getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
            {
                public boolean onQueryTextChange(String newText)
                {
                    if(newText.trim().toLowerCase().equals("")) {
                        setInitialAdapter();
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
        super.onCreateOptionsMenu(menu, inflater);
	}

    private void setInitialAdapter() {
        if(mRecyclerView.getAdapter() != mLinkRecyclerViewAdapter) {
            mRecyclerView.setAdapter(mLinkRecyclerViewAdapter);
            rvActionsSingleton.setAdapterRef(mLinkRecyclerViewAdapter);
        }
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_save_edit_link:
                saveEditLinkRecyclerViewWrapper();
				break;
			case  R.id.action_settings:
				mActionBarHandlerSingleton.toggleActionBar(true, false, false);
                mainActivityRef.changeFragment(new SettingsFragment(), null, SettingsFragment.FRAG_TAG);
                return true;
			case  R.id.action_export:
				exportBookmarksSingleton.exportAction();
				return true;
			case  R.id.action_import:
//				Toast.makeText(mainActivityRef, "Import cardview", Toast.LENGTH_SHORT).show();
				mActionBarHandlerSingleton.toggleActionBar(true, false, false);
				mainActivityRef.changeFragment(new ImportBookmarkFragment(), null, ImportBookmarkFragment.FRAG_TAG);
				return true;

		}
		return true;
	}

	@Override
	public void onClick(View v) {
		LinkRecyclerViewAdapter adapter = (LinkRecyclerViewAdapter) mRecyclerView.getAdapter();
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
			case R.id.editUrlLabelId:
				String url = (String) v.getTag();
				rvActionsSingleton.editLinkDialog(url);
				break;
			case R.id.importLocalBookmarksButtonId:
                mSwipeRefreshLayout.setRefreshing(true);
				ArrayList<Link> items = rvActionsSingleton.getBookmarksByProvider();
                dbConnector.insertLinkList(items);

				for(Link obj : items) {
					((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).add(obj);
//					dbConnector.insertLink(obj);
				}
                mSwipeRefreshLayout.setRefreshing(false);
				break;
			case R.id.undoButtonId:
				Toast.makeText(mainActivityRef, "undo", Toast.LENGTH_SHORT).show();
				Link deletedItem = adapter.getDeletedItem();
				int deletedItemPosition = adapter.getDeletedItemPosition();
				adapter.addOnPosition(deletedItem, deletedItemPosition);
				setUndoDeletedLinkLayout(false);
				break;
			case R.id.dismissButtonId:
				Toast.makeText(mainActivityRef, "dismiss", Toast.LENGTH_SHORT).show();
				deletedItem = adapter.getDeletedItem();
				dbConnector.deleteLinkById(deletedItem.getLinkId());
				setUndoDeletedLinkLayout(false);
				break;
			case R.id.actionbarInfoActionIconId:
				Toast.makeText(mainActivityRef, "dismiss", Toast.LENGTH_SHORT).show();
//				mActionBarHandlerSingleton.initToggleSettings(false, false);
//				mActionBarHandlerSingleton.showLayoutByMenuAction(R.id.actionbarInfoActionIconId);
				break;
			case R.id.actionbarImportActionIconId:
				Toast.makeText(mainActivityRef, "dismiss", Toast.LENGTH_SHORT).show();
				break;
			case R.id.addLinkButtonId:
				mainActivityRef.startActivityForResultWrapper(AddBookmarkActivity.class,
						AddBookmarkActivity.ADD_REQUEST, null);
				break;
//			case R.id.actionbarInfoLayoutId:
//				mActionBarHandlerSingleton.toggleInnerLayoutByActionMenu(v.getId());
//				break;
		}
	}

	//SWIPE ACTION
	@Override
	public boolean canDismiss(int position) {
		return true;
	}

	@Override
	public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
		Log.e(TAG, reverseSortedPositions.toString() + "removing action");
		int position = reverseSortedPositions[0];
		((LinkRecyclerViewAdapter) recyclerView.getAdapter()).remove(position);
		setUndoDeletedLinkLayout(true);
	}

	//onclick listener
	@Override
	public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
		detector.onTouchEvent(motionEvent);
		return false;
	}

	@Override
	public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
	}

	public void addLinkOnRecyclerViewWrapper(String url) {
        try {
            rvActionsSingleton.addLinkRetrivingUrlInfo(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	//TODO rename it
	public void setUndoDeletedLinkLayout(boolean isDeleting) {
		undoLinkDeletedLayout.setVisibility(isDeleting ? View.VISIBLE : View.GONE);
		addLinkButton.setVisibility(isDeleting ? View.GONE : View.VISIBLE);
	}

	public void undoEditLinkRecyclerViewWrapper() {
		rvActionsSingleton.undoEditLink();
        setInitialAdapter();
    }

	public void saveEditLinkRecyclerViewWrapper() {
        rvActionsSingleton.saveEditLink();
        setInitialAdapter();
    }

	public void toggleAddLinkButton(boolean isVisible) {
		//hide fab button
		addLinkButton.animate().
				translationY(isVisible ? 170 : 0).
				setInterpolator(new DecelerateInterpolator(3.f)).
				setStartDelay(200).
				start();
	}

    @Override
    public Filter getFilter() {
        return new LinkFilter(mItems, this);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        //NEED TO BE IMPLEMENTED
    }

    private class LinkFilter extends Filter {
        private final Fragment mFragmentRef;
        private ArrayList<Link> mDataset;

        public LinkFilter(ArrayList data, Fragment fragmentRef) {
            mDataset = data;
            mFragmentRef = fragmentRef;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<Link> filteredList = new ArrayList<Link>();
            filterResults.values = new ArrayList<Link>();
            filterResults.count = 0;

            if(constraint != null &&
                    constraint.length() != 0 &&
                    mDataset != null &&
                    mDataset.size() != 0) {

                for(Link link : mDataset) {
                    if(link.getLinkName().toLowerCase().trim().contains(constraint.toString().toLowerCase())) {
                        filteredList.add(link);
                    }
                }

                if(filteredList.size() != 0) {
                    filterResults.values = filteredList;
                    filterResults.count = filteredList.size();
                }
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Link> temp = (ArrayList<Link>) results.values;
            if(results.count == 0) {
                mRecyclerView.setEmptySearchResultQuery(constraint);
            }
//            ArrayList<Link> temp = new ArrayList<Link>();
//            temp.add(mDataset.get(0));


            LinkRecyclerViewAdapter searchResultRecyclerViewAdapter =
                    new LinkRecyclerViewAdapter(mFragmentRef, temp, true);
            int oldPosition = ((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).getSelectedItemPosition();
            searchResultRecyclerViewAdapter.setSelectedItemPosition(oldPosition);
            rvActionsSingleton.setAdapterRef(searchResultRecyclerViewAdapter);
            mRecyclerView.setAdapter(searchResultRecyclerViewAdapter);
        }
    }



    private class RecyclerViewOnGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent event) {
//			View view = mRecyclerView.findChildViewUnder(event.getX(), event.getY());
//			view.findViewById(R.id.linkLayoutId).setPressed(true);
			return false;
		}

		@Override
		public void onShowPress(MotionEvent event) {
			View view = mRecyclerView.findChildViewUnder(event.getX(), event.getY());
			view.findViewById(R.id.linkLayoutId).setPressed(true);
		}
		@Override
		public boolean onSingleTapUp(MotionEvent event) {
			View view = mRecyclerView.findChildViewUnder(event.getX(), event.getY());
			view.findViewById(R.id.linkLayoutId).setPressed(true);
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
			int position = mRecyclerView.getChildPosition(view);

			LinkRecyclerViewAdapter.ViewHolder holder =
					(LinkRecyclerViewAdapter.ViewHolder) mRecyclerView.
							findViewHolderForPosition(position);
			holder.itemView.setSelected(true);
			// handle single tap
			String url = (mItems.get(position)).getLinkUrl();
			rvActionsSingleton.openLinkOnBrowser(url);

			return super.onSingleTapConfirmed(e);
        }

        @Override
		public void onLongPress(MotionEvent e) {
			mRecyclerView.setOnTouchListener(null);
			View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
			int position = mRecyclerView.getChildPosition(view);
			LinkRecyclerViewAdapter.ViewHolder holder =
					(LinkRecyclerViewAdapter.ViewHolder) mRecyclerView.
							findViewHolderForPosition(position);
            mActionBarHandlerSingleton.setEditItemPos(position);
			holder.itemView.setSelected(true);

			// handle long press
			Log.e(TAG, "Hey long touch ");
			rvActionsSingleton.editLink(position);
			super.onLongPress(e);
		}
	}

}
