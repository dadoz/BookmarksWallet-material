package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.database.Cursor;
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
import com.application.material.bookmarkswallet.app.exportFeature.CSVExportParser;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.models.Link;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.recyclerView.RecyclerViewCustom;
import com.application.material.bookmarkswallet.app.singleton.ActionBarHandlerSingleton;
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
	private View mExportBookmarksRevealView;
	private AlertDialog exportDialog;
	private ActionBarHandlerSingleton mActionBarHandlerSingleton;

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

		//TODO refactor it
		inflater.inflate(R.layout.empty_link_list_layout,
				(ViewGroup) mLinkListView.findViewById(R.id.mainContainerViewId));
		emptyLinkListView = mLinkListView.findViewById(R.id.emptyLinkListViewId);
		//TODO export reveal view
		mExportBookmarksRevealView = mainActivityRef.getLayoutInflater().
				inflate(R.layout.export_bookmarks_layout, null);

		setHasOptionsMenu(true);
		onInitView();
		return mLinkListView;
	}

	private void onInitView() {
		View actionbarInfoView = mLinkListView.findViewById(R.id.infoButtonLayoutId);
		mActionBarHandlerSingleton.setViewOnActionMenu(actionbarInfoView, R.id.infoButtonLayoutId, this);
		mActionBarHandlerSingleton.initActionBar(mRecyclerView, addLinkButton);

		mItems = dbConnector.getLinkList();

		if(mItems == null) {
			mItems = new ArrayList<Link>();
		}

		mLinkListView.findViewById(R.id.infoButtonLayoutId).setOnClickListener(this);

		LinkRecyclerViewAdapter linkRecyclerViewAdapter =
				new LinkRecyclerViewAdapter(this, mItems);
		//empty recyclerview
		//set an observer on recyclerview
		emptyLinkListView.findViewById(R.id.importLocalBookmarksButtonId).setOnClickListener(this);
		mRecyclerView.setEmptyView(emptyLinkListView);
		mRecyclerView.setHasFixedSize(true);

		linearLayoutManager = new LinearLayoutManager(mainActivityRef);

		mRecyclerView.setLayoutManager(linearLayoutManager);

		mRecyclerView.setAdapter(linkRecyclerViewAdapter);
		mRecyclerView.setItemAnimator(null);

		//SWIPE
		touchListener = new SwipeDismissRecyclerViewTouchListener(mRecyclerView, this);
		mRecyclerView.setOnTouchListener(touchListener);
		mRecyclerView.setOnScrollListener(touchListener.makeScrollListener());

		//ON ITEM CLICK and LONG CLICK
		detector = new GestureDetectorCompat(mainActivityRef, new RecyclerViewOnGestureListener());
		mRecyclerView.addOnItemTouchListener(this);

		addLinkButton.setOnClickListener(this);
		undoButton.setOnClickListener(this);
		dismissButton.setOnClickListener(this);
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
				saveEditLinkRecyclerView();
				break;
			case  R.id.action_settings:
                mainActivityRef.changeFragment(new SettingsFragment(), null, SettingsFragment.FRAG_TAG);
				mActionBarHandlerSingleton.toggleActionBar(SettingsFragment.TITLE, false, false);
                return true;
			case  R.id.action_export:
				exportAction();
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
			case R.id.dismissExportButtonId:
				if(exportDialog != null) {
					exportDialog.dismiss();
				}
				break;
			case R.id.exportConfirmButtonId:
				exportBoomarks();
				break;
			case R.id.importLocalBookmarksButtonId:
				for(Link obj : getBookmarksByProvider()) {
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
			openLinkOnBrowser(url);

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
			editLinkRecyclerView(position);
			super.onLongPress(e);
		}
	}

	private void toggleAddLinkButton(boolean isVisible) {
		//hide fab button
		addLinkButton.animate().
				translationY(isVisible ? 170 : 0).
				setInterpolator(new DecelerateInterpolator(3.f)).
				setStartDelay(200).
				start();
	}

	public void saveEditLinkRecyclerView() {
		LinkRecyclerViewAdapter adapter = (LinkRecyclerViewAdapter)
				mRecyclerView.getAdapter();

		int position = adapter.getSelectedItemPosition();
		Toast.makeText(mainActivityRef, "save", Toast.LENGTH_SHORT).show();

		LinkRecyclerViewAdapter.ViewHolder holder =
				(LinkRecyclerViewAdapter.ViewHolder) mRecyclerView.
						findViewHolderForPosition(position);
		adapter.update(position,holder.getEditLinkName(),
				holder.getEditUrlName());

		adapter.deselectedItemPosition();
		adapter.notifyDataSetChanged();

		mActionBarHandlerSingleton.toggleActionBar(null, true, false, R.id.infoButtonLayoutId);

		mainActivityRef.invalidateOptionsMenu();
		mRecyclerView.addOnItemTouchListener(this);
		toggleAddLinkButton(false);
	}

	public void undoEditLinkRecyclerView() {
		Toast.makeText(mainActivityRef, "undo edit", Toast.LENGTH_SHORT).show();
		((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).deselectedItemPosition();
		(mRecyclerView.getAdapter()).notifyDataSetChanged();
		mainActivityRef.invalidateOptionsMenu();
		mRecyclerView.addOnItemTouchListener(this);
		toggleAddLinkButton(false);
	}

	public void editLinkRecyclerView(int position) {
		Toast.makeText(mainActivityRef, "edit" + position, Toast.LENGTH_SHORT).show();

		mActionBarHandlerSingleton.setEditMode(true);
		mActionBarHandlerSingleton.toggleActionBar("Edit link", true, true, R.id.infoButtonLayoutId);

		((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).setSelectedItemPosition(position);
		mRecyclerView.getAdapter().notifyDataSetChanged();
		mainActivityRef.invalidateOptionsMenu();
		mRecyclerView.removeOnItemTouchListener(this);
		toggleAddLinkButton(true);

	}

	public void addLinkOnRecyclerView(String url) {
		Link link = new Link(-1, null, "NEW FAKE", url, -1);
		((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).add(link);
		dbConnector.insertLink(link);
	}

	private void exportAction() {
		mExportBookmarksRevealView.findViewById(R.id.exportConfirmButtonId).
				setOnClickListener(this);
		mExportBookmarksRevealView.findViewById(R.id.dismissExportButtonId).
				setOnClickListener(this);

		if(exportDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mainActivityRef);
			exportDialog = builder.
					setTitle("Bookmarks export!").
					setView(mExportBookmarksRevealView).
					create();
		}
		exportDialog.show();
	}

	public void exportBoomarks() {
		View view = mExportBookmarksRevealView.findViewById(R.id.exportFrameLayoutId);
		view.setBackgroundColor(mainActivityRef.getResources().getColor(R.color.material_green));
		if (Build.VERSION.SDK_INT >= 21) {
			ViewAnimationUtils.createCircularReveal(view,
					view.getWidth() / 2, view.getHeight() / 2, 0, view.getHeight()).start();
		}

		boolean isFileCreated = CSVExportParser.writeFile(mItems);
		if(isFileCreated) {
			(view.findViewById(R.id.exportInfoTextId)).setVisibility(View.GONE);
			(view.findViewById(R.id.exportSuccessTextId)).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.exportSuccessTextId)).
					append(CSVExportParser.EXPORT_FILE_NAME);

			((TextView) view.findViewById(R.id.dismissExportButtonId)).
					setTextColor(mainActivityRef.getResources().getColor(R.color.white));

			(view.findViewById(R.id.exportConfirmButtonId)).setOnClickListener(null);
			((ImageView) view.findViewById(R.id.exportConfirmButtonId)).setImageDrawable(
					mainActivityRef.getResources().getDrawable(R.drawable.ic_check_circle_white_48dp));
		}

	}

	public void openLinkOnBrowser(String linkUrl) {
		try {
			if(! checkURL(linkUrl)) {
				Toast.makeText(mainActivityRef, "your URL is wrong "
						+ linkUrl, Toast.LENGTH_SHORT).show();
				return;
			}

			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(linkUrl));
			getActivity().startActivity(browserIntent);
		} catch(Exception e) {
			Log.e(TAG, "error - " + e);
			Toast.makeText(mainActivityRef, "I cant load your URL "
					+ e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	private boolean checkURL(String linkUrl) {
		return true;
	}

	//TODO rename it
	public void setUndoDeletedLinkLayout(boolean isDeleting) {
		undoLinkDeletedLayout.setVisibility(isDeleting ? View.VISIBLE : View.GONE);
		addLinkButton.setVisibility(isDeleting ? View.GONE : View.VISIBLE);
	}

	public ArrayList<Link> getBookmarksByProvider() {
		//TODO asyncTask
		ArrayList<Link> bookmarkList = new ArrayList<Link>();
		try {
			ContentResolver cr = mainActivityRef.getContentResolver();
			String[] projection = {
					Browser.BookmarkColumns.FAVICON,
					Browser.BookmarkColumns.TITLE,
					Browser.BookmarkColumns.URL
			};
			Cursor cursor = cr.query(Browser.BOOKMARKS_URI, projection, null, null, null);
			int urlId = cursor.getColumnIndex(Browser.BookmarkColumns.URL);
			int titleId = cursor.getColumnIndex(Browser.BookmarkColumns.TITLE);
			int faviconId = cursor.getColumnIndex(Browser.BookmarkColumns.FAVICON);

			if(cursor.moveToFirst()) {
				do {
					Log.e(TAG, "hey " + cursor.getString(urlId));
//					Bitmap favicon = BitmapFactory.decodeByteArray(cursor.getBlob(faviconId), 0, 0, null);
					bookmarkList.add(new Link(-1, null, cursor.getString(titleId), cursor.getString(urlId), -1));
				} while(cursor.moveToNext());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bookmarkList;
	}




    public ArrayList<Link> getLinkListMockup() {
    	ArrayList<Link> linksDataList = new ArrayList<Link>();
    	ArrayList<String> linksUrlArray = new ArrayList<String>();
		boolean deletedLinkFlag = false;

    	linksUrlArray.add("heavy metal1");
		linksUrlArray.add("pop1");
		linksUrlArray.add("underground");
		linksUrlArray.add("heavy metal");
		linksUrlArray.add("underground");
		linksUrlArray.add("underground");
		linksUrlArray.add("heavy metal");
		linksUrlArray.add("underground");
		linksUrlArray.add("hey_ure_fkin_my_shitty_dog_are_u_sure_u_want_to_cose_ure_crazy");
		linksUrlArray.add("bla1");
		linksUrlArray.add("link2");
		linksUrlArray.add("bla1");
		linksUrlArray.add("link2");
    	String linkUrl = "http://www.google.it";
    	int userId = 0;
    	for(int i = 0; i < linksUrlArray.size(); i ++) {
			linksDataList.add(new Link(i, "ic_launcher", linksUrlArray.get(i), linkUrl, userId));
		}
    	return linksDataList;
    }

}
