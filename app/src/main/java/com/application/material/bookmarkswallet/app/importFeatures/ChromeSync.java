package com.application.material.bookmarkswallet.app.importFeatures;

import android.accounts.*;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.application.material.bookmarkswallet.app.R;

/**
 * Created by davide on 27/01/15.
 */
public class ChromeSync implements DialogInterface.OnClickListener {

    private final Activity mActivityRef;
    private String AUTH_TOKEN_TYPE = "Manage your tasks";
    private String mToken;
    private String TAG = "ChromeSync";
    private Account[] accounts;
    private AccountManager mAccountManager;

    public ChromeSync(Activity activity) {
        mActivityRef = activity;
        mAccountManager = AccountManager.get(mActivityRef);
    }

    public void selectAccount() {
        try {
            accounts = mAccountManager.getAccountsByType("com.google");
            String[] items = new String[accounts.length];
            for(int i = 0; i < accounts.length; i ++) {
                items[i] = accounts[i].name;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivityRef, R.style.CustomLollipopDialogStyle);
            builder.setTitle("Choice account");
            builder.setItems(items, this);
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAuthToken(Account account) {
        try {
            mAccountManager.getAuthToken(account, AUTH_TOKEN_TYPE,
                    null, mActivityRef, callback, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {
        @Override
        public void run(AccountManagerFuture<Bundle> future) {
            try {
                Intent launch = (Intent) future.getResult().get(AccountManager.KEY_INTENT);
                if (launch != null) {
                    mActivityRef.startActivityForResult(launch, 0);
                    return;
                } else {
                    mToken = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                    Log.d(TAG, mToken);
                    Toast.makeText(mActivityRef, "hey " + mToken, Toast.LENGTH_LONG).show();
//                HTTP request
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Account account = accounts[which];
        setAuthToken(account);
    }
}
