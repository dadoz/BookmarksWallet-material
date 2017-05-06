package com.application.material.bookmarkswallet.app.helpers;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;


/**
     * Night Mode Helper
     *
     * <p>Helps use utilise the night and notnight resource qualifiers without
     * being in car or dock mode.
     *
     * <p>Implementation is simple. Add the follow line at the top of your
     * activity's onCreate just after the super.onCreate(); The idea here
     * is to do it before we create any views. So the new views will use
     * the correct Configuration.
     *
     * <pre>
     * mNightModeHelper = new NightModeHelper(this, R.style.AppTheme);
     * </pre>
     *
     * You can now use your instance of NightModeHelper to control which mode
     * you are in. You can choose to persist the current setting and hand
     * it back to this class as the defaultUiMode, otherwise this is done
     * for you automatically.
     *
     * <p>I'd suggest you setup your Theme as follows:
     *
     * <ul>
     * <li>
     * <b>res\values\styles.xml</b>
     * <pre>&lt;style name=&quot;AppTheme&quot; parent=&quot;AppBaseTheme&quot;&gt;&lt;/style&gt;</pre>
     * </li>
     * <li>
     * <b>res\values-night\styles.xml</b>
     * <pre>&lt;style name=&quot;AppBaseTheme&quot; parent=&quot;@android:style/Theme.Holo&quot;&gt;&lt;/style&gt;</pre>
     * </li>
     * <li>
     * <b>res\values-notnight\styles.xml</b>
     * <pre>&lt;style name=&quot;AppBaseTheme&quot; parent=&quot;@android:style/Theme.Holo.Light&quot;&gt;&lt;/style&gt;</pre>
     * </li>
     * </ul>
     *
     *
     * @author Simon Lightfoot <simon@demondevelopers.com>
     *
     */
    public class NightModeHelper
    {

        /**init on day mode**/
        private static int sUiNightMode = AppCompatDelegate.MODE_NIGHT_NO;

        private static WeakReference<Activity> mActivity;
        private SharedPrefHelper mPrefs;
        private static NightModeHelper instance;


        /**
         * Default behaviour is to automatically save the setting and restore it.
         */
        public NightModeHelper(Activity activity, int defaultUiMode)
        {
            setConfigMode();
            mPrefs = SharedPrefHelper.getInstance(new WeakReference<Context>(activity));
        }

        /**
         *
         * @param activity
         */
        public NightModeHelper(Activity activity)
        {
            mPrefs = SharedPrefHelper.getInstance(new WeakReference<Context>(activity));
        }

        /**
         *
         * @return
         */
        public static NightModeHelper getInstance() {
            return instance == null ? instance = new NightModeHelper(null) : instance;
        }

        /**
         *
         * @param activity
         * @return
         */
        public static NightModeHelper getInstance(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
            return instance == null ? instance = new NightModeHelper(activity) : instance;
        }

        /**
         *
         * @param activity
         * @param defaultUiMode
         * @return
         */
        public static NightModeHelper getInstance(Activity activity, int defaultUiMode) {
            mActivity = new WeakReference<Activity>(activity);
            return instance == null ? instance = new NightModeHelper(activity, defaultUiMode) : instance;
        }

        /**
         *
         * @param uiNightMode
         */
        private void updateConfig(int uiNightMode)
        {
            if (mActivity == null)
                return;

            Activity activity = mActivity.get();
            if(activity == null){
                throw new IllegalStateException("Activity went away?");
            }
            Configuration newConfig = new Configuration(activity.getResources().getConfiguration());
            newConfig.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
            newConfig.uiMode |= uiNightMode;
            if (Build.VERSION.SDK_INT > 16)
                activity.createConfigurationContext(newConfig);
            else
                activity.getResources().updateConfiguration(newConfig, null);
            sUiNightMode = uiNightMode;
            if (mPrefs != null) {
                mPrefs.setValue(SharedPrefHelper.SharedPrefKeysEnum.NIGHT_MODE, sUiNightMode);
            }
        }

        /**
         * get ui night mode
         */
        public static int getUiNightMode()
        {
            return sUiNightMode;
        }

        /**
         * toggle night mode
         */
        public void toggle()
        {
            if (sUiNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                notNight();
                return;
            }
            night();
        }

        /**
         * light mode
         */
        public void notNight()
        {
            if (mActivity == null)
                return;

            updateConfig(AppCompatDelegate.MODE_NIGHT_NO);
            mActivity.get().recreate();
        }

        /**
         * night mode
         */
        public void night()
        {
            if (mActivity == null)
                return;

            updateConfig(AppCompatDelegate.MODE_NIGHT_YES);
            mActivity.get().recreate();
        }

        /**
         *
         */
        public void setNightModeLocal() {
            if (mActivity == null ||
                    mActivity.get() == null)
                return;

            ((AppCompatActivity) mActivity.get()).getDelegate().setLocalNightMode(sUiNightMode);
        }

        /**
         *
         */
        public void setConfigurationMode() {
            setConfigMode();
            AppCompatDelegate.setDefaultNightMode(sUiNightMode);
        }

        public int getConfigMode() {
            return sUiNightMode;
        }
        /**
         *
         */
        public void setConfigMode() {
            if (mPrefs == null)
                return;

            int defaultUiMode = (int) mPrefs.getValue(SharedPrefHelper.SharedPrefKeysEnum.NIGHT_MODE,
                    Configuration.UI_MODE_NIGHT_UNDEFINED);
            if(sUiNightMode == AppCompatDelegate.MODE_NIGHT_AUTO) {
                sUiNightMode = defaultUiMode;
            }
        }

        /**
         *
         * @return
         */
        public boolean isNightMode() {
            return sUiNightMode == AppCompatDelegate.MODE_NIGHT_YES;
        }
    }

