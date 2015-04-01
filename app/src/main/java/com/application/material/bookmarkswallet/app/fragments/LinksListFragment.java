package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.provider.Browser;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.*;
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
			RecyclerView.OnItemTouchListener {
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
//	private View mExportBookmarksRevealView;
//	private AlertDialog exportDialog;
	private ActionBarHandlerSingleton mActionBarHandlerSingleton;
	private RecyclerViewActionsSingleton rvActionsSingleton;
	private ExportBookmarkSingleton exportBookmarksSingleton;

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
		setHasOptionsMenu(true);
		onInitView();
		return mLinkListView;
	}

	private void onInitView() {
		View actionbarInfoView = mLinkListView.findViewById(R.id.infoButtonLayoutId);
		mActionBarHandlerSingleton.setViewOnActionMenu(actionbarInfoView, R.id.infoButtonLayoutId, this);
		mActionBarHandlerSingleton.initActionBar(mRecyclerView, addLinkButton);

		mItems = dbConnector.getLinkList();
		mLinkListView.findViewById(R.id.infoButtonLayoutId).setOnClickListener(this);

		addLinkButton.setOnClickListener(this);
		undoButton.setOnClickListener(this);
		dismissButton.setOnClickListener(this);

		initRecyclerView();
		rvActionsSingleton = RecyclerViewActionsSingleton.getInstance(mRecyclerView, mainActivityRef, this, dbConnector);
	}

	private void initRecyclerView() {
		LinkRecyclerViewAdapter linkRecyclerViewAdapter =
				new LinkRecyclerViewAdapter(this, mItems);

		//empty recyclerview set an observer on recyclerview
		detector = new GestureDetectorCompat(mainActivityRef, new RecyclerViewOnGestureListener());
		touchListener = new SwipeDismissRecyclerViewTouchListener(mRecyclerView, this);
		linearLayoutManager = new LinearLayoutManager(mainActivityRef);
		emptyLinkListView.findViewById(R.id.importLocalBookmarksButtonId).setOnClickListener(this);


		//set empty view
		mRecyclerView.setEmptyView(emptyLinkListView);
		mRecyclerView.setHasFixedSize(true);
		//set layout manager
		mRecyclerView.setLayoutManager(linearLayoutManager);
		mRecyclerView.setAdapter(linkRecyclerViewAdapter);
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
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_save_edit_link:
				rvActionsSingleton.saveEditLink();
				break;
			case  R.id.action_settings:
                mainActivityRef.changeFragment(new SettingsFragment(), null, SettingsFragment.FRAG_TAG);
				mActionBarHandlerSingleton.toggleActionBar(SettingsFragment.TITLE, false, false);
                return true;
			case  R.id.action_export:
				exportBookmarksSingleton.exportAction();
				return true;
			case  R.id.action_import:
//				Toast.makeText(mainActivityRef, "Import cardview", Toast.LENGTH_SHORT).show();
				mActionBarHandlerSingleton.toggleActionBar(ImportBookmarkFragment.TITLE, false, false);
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
				ArrayList<Link> items = rvActionsSingleton.getBookmarksByProvider();
				for(Link obj : items) {
					((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).add(obj);
					dbConnector.insertLink(obj);
				}
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
//			case R.id.actionbarExportActionIconId:
//				exportAction();
//				break;
			case R.id.addLinkButtonId:
				mainActivityRef.startActivityForResultWrapper(AddBookmarkActivity.class,
						AddBookmarkActivity.ADD_REQUEST, null);
				break;
			case R.id.infoButtonLayoutId:
				mActionBarHandlerSingleton.toggleInnerLayoutByActionMenu(v.getId());
//				mActionBarHandlerSingleton.initToggleSettings(false, false);
//				mActionBarHandlerSingleton.showLayoutByMenuAction(R.id.infoButtonLayoutId);

				break;
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
		rvActionsSingleton.addLink(url);
	}

	//TODO rename it
	public void setUndoDeletedLinkLayout(boolean isDeleting) {
		undoLinkDeletedLayout.setVisibility(isDeleting ? View.VISIBLE : View.GONE);
		addLinkButton.setVisibility(isDeleting ? View.GONE : View.VISIBLE);
	}

	public void undoEditLinkRecyclerViewWrapper() {
		rvActionsSingleton.undoEditLink();
	}

	public void toggleAddLinkButton(boolean isVisible) {
		//hide fab button
		addLinkButton.animate().
				translationY(isVisible ? 170 : 0).
				setInterpolator(new DecelerateInterpolator(3.f)).
				setStartDelay(200).
				start();
	}

	private class RecyclerViewOnGestureListener extends GestureDetector.SimpleOnGestureListener {
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

		public void onLongPress(MotionEvent e) {
			View view = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
			int position = mRecyclerView.getChildPosition(view);
			LinkRecyclerViewAdapter.ViewHolder holder =
					(LinkRecyclerViewAdapter.ViewHolder) mRecyclerView.
							findViewHolderForPosition(position);

			holder.itemView.setSelected(true);

			// handle long press
			Log.e(TAG, "Hey long touch ");
			rvActionsSingleton.editLink(position);
			super.onLongPress(e);
		}
	}

}
