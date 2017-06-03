package com.application.material.bookmarkswallet.app.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
/**
 * Created by davide on 03/06/2017.
 */

public class SparseArrayParcelable<T> extends SparseArray<T> implements Parcelable {

    public SparseArrayParcelable() {
        super();
    }

    protected SparseArrayParcelable(Parcel in) {
        int size = in.readInt();
        SparseArray sparseArray = in.readSparseArray(this.getClass().getClassLoader());
        for (int i = 0; i < size; i ++) {
            int keyLocal = sparseArray.keyAt(i);
            put(keyLocal, (T) sparseArray.valueAt(i));
        }
    }

    public static final Creator<SparseArrayParcelable> CREATOR = new Creator<SparseArrayParcelable>() {
        @Override
        public SparseArrayParcelable createFromParcel(Parcel in) {
            return new SparseArrayParcelable(in);
        }

        @Override
        public SparseArrayParcelable[] newArray(int size) {
            return new SparseArrayParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(size());
        SparseArray<Object> sparseArray = new SparseArray<>(size());
        for (int i = 0; i < size(); i ++)
            sparseArray.put(keyAt(i), valueAt(i));
        dest.writeSparseArray(sparseArray);
    }
}
