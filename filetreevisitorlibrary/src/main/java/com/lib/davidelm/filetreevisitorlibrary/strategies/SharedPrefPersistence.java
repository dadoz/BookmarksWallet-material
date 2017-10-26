package com.lib.davidelm.filetreevisitorlibrary.strategies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNode;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.lang.ref.WeakReference;

import static com.lib.davidelm.filetreevisitorlibrary.strategies.SharedPrefPersistence.SharedPrefKeysEnum.TREE_NODE;

public class SharedPrefPersistence implements PersistenceStrategyInterface {
    private static final String TAG = "SharedPrefStrategy";
    private static SharedPreferences sharedPref;

    /**
     * reading on local storage
     */
    @Nullable
    @Override
    public TreeNodeInterface getPersistentNode() {
        Log.e(TAG, "hey get node");
        try {
            TreeNode temp = (new Gson().fromJson(getValue(TREE_NODE, null).toString(), TreeNode.class));
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * saving on local storage
     */

    @Override
    public void setPersistentNode(TreeNodeInterface node) {
        Log.e(TAG, new Gson().toJson(node));
        setValue(TREE_NODE,
                new Gson().toJson(node));

    }

    public enum SharedPrefKeysEnum { TREE_NODE }

    public SharedPrefPersistence(@NonNull WeakReference<Context> ctx) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx.get());
    }

    /**
     * @param key
     * @param defValue
     * @return
     */
    @Nullable
    private Object getValue(@NonNull SharedPrefKeysEnum key, Object defValue) {
        String temp = sharedPref.getString(key.name(), (String) defValue);
        Log.e(TAG, temp);
        return temp;
    }

    /**
     * @param key
     * @param value
     */
    private void setValue(@NonNull SharedPrefKeysEnum key, Object value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key.name(), (String) value);
        editor.apply();
        //TODO check type
//        if (value instanceof Boolean) {
//            editor.putBoolean(key.name(), (boolean) value);
//        } else if (value instanceof Integer) {
//            editor.putInt(key.name(), (int) value);
//        } else if (value instanceof String) {
//            editor.putString(key.name(), "bla");
//        }
    }

}