package com.application.material.bookmarkswallet.app.dbAdapter.sample;
//import com.example.chromebookmarkssync.weibo.Parameters;
//import com.example.chromebookmarkssync.weibo.Token;
//import com.example.chromebookmarkssync.weibo.Utility;
//import com.example.chromebookmarkssync.weibo.WeiboException;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import com.application.material.bookmarkswallet.app.R;
import android.media.session.MediaSession.Token;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import static java.security.Policy.*;

/**
 * Created by davide on 27/01/15.
 */

public class ChromeSyncSample extends Activity {
    private static final String TAG = "MainActivity";
    private static final int DIALOG_ACCOUNTS = 1;
    private TextView mTextView;
    private StringBuffer sb = new StringBuffer();
    private AccountManager accountManager;
// private String AUTH_TOKEN_TYPE
// ="oauth2:https://www.googleapis.com/auth/tasks";
    private String AUTH_TOKEN_TYPE = "Manage your tasks";
    private String mToken;
    private String mSyncServerUrl = "https://clients4.google.com/chrome-sync/command/?client_id=";
    private SharedPreferences mSharedPreferences;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = getSharedPreferences("pref_token", 0);
        mTextView = (TextView) findViewById(R.id.withText);
        showDialog(DIALOG_ACCOUNTS);
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ACCOUNTS:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select a Google account");
                accountManager = AccountManager.get(this);
                final Account[] accounts = accountManager.getAccountsByType("com.google");
                final int size = accounts.length;
                String[] names = new String[size];
                for (int i = 0; i < size; i++) {
                    names[i] = accounts[i].name;
                }
                builder.setItems(names, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getAccount(accounts[which]);
                    }
                });

                return builder.create();
        }

        return null;
    }

    private void getAccount(Account account) {
        sb.append(account.name).append("\n");
        accountManager.getAuthToken(account, AUTH_TOKEN_TYPE, null, this, new AccountManagerCallback<Bundle>() {
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Intent launch = (Intent) future.getResult().get(AccountManager.KEY_INTENT);
                    if (launch != null) {
                        startActivityForResult(launch, 0);
                        return;
                    } else {
                        mToken = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                        Log.d(TAG, mToken);
                        new AsyncLoadTasks().execute(mToken);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accountManager.invalidateAuthToken(AUTH_TOKEN_TYPE, mToken);
    }

    class AsyncLoadTasks extends AsyncTask<String, Void, String> {
        private final ProgressDialog dialog;
        public AsyncLoadTasks() {
            dialog = new ProgressDialog(ChromeSyncSample.this);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading tasks...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
/*
            Token token = new Token();
            token.setToken(arg0[0]);
            Camera.Parameters params = new Parameters();
            String res = null;
            try {
                res = Utility.postRequest(MainActivity.this, mSyncServerUrl + URLEncoder.encode("+FPGSqUErPxKNi7STF3q/Q==", "utf-8"),
                        Utility.HTTPMETHOD_POST, params, token);
            } catch (WeiboException e) {
            } catch (UnsupportedEncodingException e) {
            }
            return res;
*/
            return null;
        }

        @Override

        protected void onPostExecute(String inputStream) {
            dialog.dismiss();
            Log.d(TAG, inputStream);
            mTextView.setText(inputStream);
        }

    }

    public static class Utils {

        public static String postRequest(Context context,
                                         String url,String method,Parameters params,
                                         Token token) {
            String result="";
            String err=null;
            int errCode=0;
            try{
//                HttpClient client = getNewHttpClient(context);
                HttpUriRequest request=null;
                HttpPost post=new HttpPost(url);
                request=post;
                request.setHeader("Authorization",(new StringBuilder()).append("GoogleLogin auth=").append("token.getToken()").toString());
                request.setHeader("Content-Type","application/octet-stream");
                request.setHeader("User-Agent","Chrome WIN 21.0.1180.89 (154005)");
//                HttpResponse response=client.execute(request);
                HttpResponse response = null;
                StatusLine status=response.getStatusLine();
                int statusCode=status.getStatusCode();
                if(statusCode!=200){
                    return status.toString();
                }else{
//                    result=read(response);
                    return result;
                }
                // parse content stream from response
            }catch(Exception e){
                //throw new Exception(e);
                return null;
            }
        }
    }
}


