package com.application.material.bookmarkswallet.app.utlis;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.application.material.bookmarkswallet.app.models.Bookmark;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static final int ADD_BOOKMARK_ACTIVITY_REQ_CODE = 99;
    private static final String TAG = "Utils";
    private static final int CONST_VALUE = 100;
    private static final String HTTP_PROTOCOL = "http://";
    private static final String HTTPS_PROTOCOL = "https://";

    /**
     * @param url
     * @return
     */
    public static boolean isValidUrl(String url) {
        Pattern p = Pattern.
                compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?(https://)?(ftp://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/([#&\\n\\-=?\\+\\%/\\.\\w]+)?)?");

        Matcher m = p.matcher(url);
        return ! url.equals("") &&
                m.matches();

    }

    /**
     * hide Keyboard
     */
    public static void hideKeyboard(Context context) {
        try {
            View view = ((Activity) context).getWindow().getDecorView().getRootView();
            ((InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param url
     * @return
     */
    public static byte[] getBytesArrayByUrl(URL url) {
        byte[] byteArray;
        try {
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int byteRead;
            while((byteRead = bis.read()) != -1) {
                baos.write(byteRead);
            }
            byteArray = baos.toByteArray();
            baos.close();
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(TAG, "Error getting the image from server : " + e.getMessage().toString());
            return null;
        }
        return byteArray;
    }

    /**
     *
     * @param drawable
     * @param color
     */
    public static void setColorFilter(Drawable drawable, int color) {
        drawable.setColorFilter(color,
                PorterDuff.Mode.SRC_IN);
    }

    /**
     * set icon - default or imageIcon
     * @param iconView
     * @param icon
     * @param defaultIcon
     */
    public static void setIconOnImageView(ImageView iconView,
                                          Bitmap icon, Bitmap defaultIcon) {
        try {
            if ((icon == null)) {
                iconView.setImageBitmap(defaultIcon);
                return;
            }
            iconView.setImageBitmap(icon);
        } catch (Exception e) {
            iconView.setImageBitmap(defaultIcon);
        }
    }

    /**
     *
     * @param blobIcon
     * @return
     */
    public static Bitmap getIconBitmap(byte[] blobIcon, int size) {
        try {
            Bitmap bmp = BitmapFactory.decodeByteArray(blobIcon, 0, blobIcon.length);
            return Bitmap.createScaledBitmap(bmp,size, size, false);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param v
     */
    public static void animateFabIn(View v) {
        v.animate().translationY(v.getHeight() + CONST_VALUE)
                .setInterpolator(new AccelerateInterpolator(2))
                .start();
    }

    /**check if url contain already http or https protocol otw atttach it
     *
     */
    public static String buildUrl(String url, boolean isHttps) {
        return url == null ||
                url.contains("http://") ||
                url.contains("https://") ?
                url : (isHttps ? HTTPS_PROTOCOL : HTTP_PROTOCOL) + url.trim();
    }


    /**
     *
     * @param bmp
     * @return
     */
    public static byte[] convertBitmapToByteArray(Bitmap bmp) {
        if (bmp != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }
        return null;
    }

    /**
     *
     * @param url
     * @param bookmarkUrl
     * @return
     */
    public static String buildUrlToSearchIcon(String url, String bookmarkUrl) {
        return (url.split("/")[1]).compareTo("") != 0 ?
                bookmarkUrl + url :
                url.contains("http") || url.contains("https") ? url : "http:" + url;
    }

    /**
     *
     * @param context
     * @return
     */
    public static boolean getSearchOnUrlEnabledFromSharedPref(WeakReference<Context> context) {
        return (boolean) SharedPrefHelper.getInstance(context)
                .getValue(SharedPrefHelper.SharedPrefKeysEnum.SEARCH_URL_MODE, false);
    }

    /**
     *  @param view
     * @param ctx
     * @param message
     * @param isError
     */
    public static void setSnackbar(View view, final WeakReference<Context> ctx, String message, boolean isError,
                                   String actionLabel, WeakReference<View.OnClickListener> actionListener) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(ctx.get(), isError ? R.color.red_400 : R.color.teal_400));
        if (actionListener != null) {
            snackbar.setAction(actionLabel, actionListener.get());
            snackbar.setActionTextColor(ContextCompat.getColor(ctx.get(), R.color.blue_grey_900));
        }
        snackbar.show();
    }

    /**
     *
     * @param bookmark
     * @return
     */
    public static String getNameByBookmark(Bookmark bookmark) {
        if (bookmark == null) {
            return "No Title";
        }
        return (bookmark.getName().equals("") ? bookmark.getUrl() : bookmark.getName());
    }
}
