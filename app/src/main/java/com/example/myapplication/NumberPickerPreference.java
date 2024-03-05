package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceViewHolder;

import com.example.myapplication.R;

public class NumberPickerPreference extends DialogPreference {

    private NumberPicker picker;
    private int minValue;
    private int maxValue;
    private int defaultValue;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.numberpicker_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);

        // Extract attributes from the provided AttributeSet
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerPreference);
//        minValue = a.getInt(R.styleable.NumberPickerPreference_minValue, 1);
//        maxValue = a.getInt(R.styleable.NumberPickerPreference_maxValue, 76);
//        defaultValue = a.getInt(R.styleable.NumberPickerPreference_defaultValue, 4);
        //a.recycle();
    }

    @Override
    public void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        super.onSetInitialValue(restoreValue, defaultValue);
        if (restoreValue) {
            picker.setValue(getPersistedInt(this.defaultValue));
        } else {
            picker.setValue((int) defaultValue);
        }
    }


    public View onCreateDialogView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.numberpicker_dialog, null);
        picker = view.findViewById(R.id.number_picker);
        picker.setMinValue(minValue);
        picker.setMaxValue(maxValue);
        return view;
    }

    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int value = picker.getValue();
            persistInt(value); // Manually persist the value
            notifyChanged(); // Notify preference listeners about the change
        }
    }


    @Override
    protected boolean shouldPersist() {
        return true; // Persist the value
    }


    // Custom attributes for NumberPickerPreference in styles.xml
    public static final int[] NumberPickerPreference = {
            android.R.attr.key,
            android.R.attr.title,
            android.R.attr.defaultValue,
            //R.attr.minValue,
            //R.attr.maxValue
    };

    public static final int R_attr_minValue = 0;
    public static final int R_attr_maxValue = 1;
}

