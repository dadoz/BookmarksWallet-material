package com.application.material.bookmarkswallet.app.views;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.strategies.ExportStrategy;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 18/06/2017.
 */

public class ExportCheckboxesView extends FrameLayout implements CompoundButton.OnCheckedChangeListener {
    private WeakReference<OnCheckCSVorHTMLCallbacks> lst;
    private CheckBox exportCSVCheckbox;
    private CheckBox exportHTMLCheckbox;
    private ExportStrategy.ExportTypeEnum status;

    public ExportStrategy.ExportTypeEnum getStatus() {
        return status;
    }

    public ExportCheckboxesView(@NonNull Context context) {
        super(context);
        initView();
    }

    public ExportCheckboxesView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ExportCheckboxesView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.export_bookmarks_checkboxes_layout, this);
        exportCSVCheckbox = (CheckBox) findViewById(R.id.exportCSVCheckboxId);
        exportHTMLCheckbox = (CheckBox) findViewById(R.id.exportHTMLCheckboxId);
        exportCSVCheckbox.setOnCheckedChangeListener(this);
        exportHTMLCheckbox.setOnCheckedChangeListener(this);
    }

    public void setListener(OnCheckCSVorHTMLCallbacks lst) {
        this.lst = new WeakReference<>(lst);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.exportCSVCheckboxId:
                resetCheckbox(exportHTMLCheckbox);
                status = ExportStrategy.ExportTypeEnum.CSV;
                if (lst != null &&
                        lst.get() != null)
                    lst.get().onCheckCSVCallback();
                break;
            case R.id.exportHTMLCheckboxId:
                resetCheckbox(exportCSVCheckbox);
                status = ExportStrategy.ExportTypeEnum.HTML;
                if (lst != null &&
                        lst.get() != null)
                    lst.get().onCheckHTMLCallback();
                break;
        }
    }

    /**
     *
     * @param checkbox
     */
    private void resetCheckbox(CheckBox checkbox) {
        checkbox.setOnCheckedChangeListener(null);
        checkbox.setChecked(false);
        checkbox.setOnCheckedChangeListener(this);
    }

    public interface OnCheckCSVorHTMLCallbacks {
        void onCheckCSVCallback();
        void onCheckHTMLCallback();
    }

}
