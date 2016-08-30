package com.application.material.bookmarkswallet.app.utlis;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by davide on 17/07/15.
 */
public class Utils {
    public static final String IMPORT_TRIGGER = "IMPORT_TRIGGER";
    public static final int ADD_BOOKMARK_ACTIVITY_REQ_CODE = 99;
    private static final String TAG = "Utils";
    private static int CONST_VALUE = 100;
    private static String HTTP_PROTOCOL = "http://";
    private static String HTTPS_PROTOCOL = "https://";

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
     * @param blob
     * @param defaultIcon
     * @param isSelectedItem
     */
    public static void setIconOnImageView(ImageView iconView,
                                          byte [] blob, Drawable defaultIcon,
                                          boolean isSelectedItem, int size) {
        try {
            if ((blob == null || blob.length == 0) ||
                    isSelectedItem) {
                iconView.setImageDrawable(defaultIcon);
                return;
            }

            //TODO fix it
            iconView.setImageDrawable(defaultIcon);
//            iconView.setImageBitmap(getIconBitmap(blob, size));
        } catch (Exception e) {
            iconView.setImageDrawable(defaultIcon);
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

    /**
     *
     * @param v
     */
    public static void animateFabOut(View v) {
        v.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator(2))
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
}
