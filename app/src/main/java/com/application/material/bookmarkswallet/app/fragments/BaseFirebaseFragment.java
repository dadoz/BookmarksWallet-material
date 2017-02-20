package com.application.material.bookmarkswallet.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.BookmarkFirebaseRvAdapter;
import com.application.material.bookmarkswallet.app.adapter.BookmarkRvAdapter;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseFirebaseFragment extends Fragment implements BookmarkRvAdapter.OnPopulateViewHolderCb, BookmarkRvAdapter.OnActionListenerInterface {

    private static final String TAG = "EventsBaseFragment";
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Bookmark, BookmarkFirebaseRvAdapter.BookmarkViewHolder> mAdapter;
    private LinearLayoutManager mManager;

    @BindView(R.id.bookmarkRecyclerViewId)
    RecyclerView recyclerView;
//    @BindView(R.id.eventListProgressbarId)
//    ProgressBar eventListProgressbar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Unbinder unbinder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_bookmark_list_layout, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        initFirebaseRef();
        signInAnonymously();
        initFirebaseView();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
        FirebaseDatabase.getInstance().goOffline();
    }

    /**
     *
     */
    protected void initFirebaseRef() {
        //get database ref and set consistency
        setDatabase();

        //loggin in user
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                updateUI(user);
                return;
            }

            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
//                updateUI(user);
        };
    }

    /**
     *
     */
    public void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(getActivity(), task -> {
                    Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInAnonymously", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     *
     */
    protected void initFirebaseView() {
        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mManager);

        mAdapter = initFirebaseAdapter();
        recyclerView.setAdapter(mAdapter);
        initFilterLabel();
    }


    /**
     * init firebase adapter
     */
    private FirebaseRecyclerAdapter<Bookmark, BookmarkFirebaseRvAdapter.BookmarkViewHolder> initFirebaseAdapter() {
        // Set up FirebaseRecyclerAdapter with the Query
        Query eventsQuery = getQuery(mDatabase);
        mAdapter = new BookmarkFirebaseRvAdapter(Bookmark.class, R.layout.bookmark_item,
                BookmarkFirebaseRvAdapter.BookmarkViewHolder.class, eventsQuery, new WeakReference<Context>(getContext()),
                new WeakReference<BookmarkRvAdapter.OnActionListenerInterface>(this), new WeakReference<BookmarkRvAdapter.OnPopulateViewHolderCb>(this));
        return mAdapter;
    }

    /**
     *
     * @param bookmark
     */
    protected void pushToDatabase(Bookmark bookmark) {
        if (mDatabase != null) {
            DatabaseReference objRef = mDatabase.child("bookmarks").push();
            objRef.setValue(bookmark);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null)
            mAdapter.cleanup();

        if (unbinder != null)
            unbinder.unbind();
    }

//    public String getUid() {
//        return FirebaseAuth.getInstance().getCurrentUser().getUid();
//    }

    /**
     *
     */
    protected abstract void initFilterLabel();

    /**
     *
     * @param databaseReference
     * @return
     */
    public abstract Query getQuery(DatabaseReference databaseReference);

    /**
     * set db and set persistence of db
     */
    public void setDatabase() {
        try {
            FirebaseDatabase firebaseInstance = FirebaseDatabase.getInstance();
            if (mDatabase == null) {
                firebaseInstance.setPersistenceEnabled(true);
                mDatabase = firebaseInstance.getReference();
                mDatabase.keepSynced(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPopulateViewHolderCb() {

    }

    @Override
    public boolean onLongItemClick(View view, int position) {
        return false;
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
