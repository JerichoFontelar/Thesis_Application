package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.util.Pair;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.slider.RangeSlider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

public class SettingsFragment extends PreferenceFragmentCompat {

    SharedPreferences earthquake_setting;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        //Creation of Preference
        earthquake_setting = requireActivity().getSharedPreferences("shared_setting", MODE_PRIVATE);

        // Find specific Preferences Object by name
        Preference magnitudeDialog = (Preference) findPreference("magnitude_dialog");
        Float minDefaultValue = earthquake_setting.getFloat("min_magnitude", 3.0F);
        Float maxDefaultValue = earthquake_setting.getFloat("max_magnitude", 7.4F);
        magnitudeDialog.setSummary(String.format(Locale.getDefault(), "%.1f - %.1f", minDefaultValue, maxDefaultValue));


        Preference depthDialog = (Preference) findPreference("depth_dialog");
        int minDepth = earthquake_setting.getInt("min_depth", 0);
        int maxDepth = earthquake_setting.getInt("max_depth", 1063);
        depthDialog.setSummary(String.format(Locale.getDefault(), "%s - %s", minDepth, maxDepth));


        Preference dateRangeDialog = (Preference) findPreference("date_dialog");
        long defaultStartDate = earthquake_setting.getLong("start_date", 1483200000000L);
        long defaultEndDate = earthquake_setting.getLong("end_date", 1704038340000L);

        // Create a Date object from milliseconds
        Date sdate = new Date(defaultStartDate);
        Date edate = new Date(defaultEndDate);

        // Define the desired date format string
        String dateFormat = "dd-MMM-yyyy"; // Example format: "10-Mar-2024"

        // Create a SimpleDateFormat object with the format string
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

        // Format the Date object into a String
        String formattedDate1 = sdf.format(sdate);
        String formattedDate2 = sdf.format(edate);

        dateRangeDialog.setSummary(String.format(Locale.getDefault(), "%s to %s", formattedDate1, formattedDate2));


        magnitudeDialog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showMagnitudeDialog(magnitudeDialog);
                return true;
            }
        });

        depthDialog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDepthDialog(depthDialog);
                return true;
            }
        });

        dateRangeDialog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDateRangeDialog(dateRangeDialog);
                return true;
            }
        });


    }

    private void showMagnitudeDialog(Preference magnitudePreference) {
        // Inflate the custom layout
        View view = getLayoutInflater().inflate(R.layout.layout_numberpicker_dialog, null);


        RangeSlider rangeSlider = view.findViewById(R.id.rangeSlider);
        TextView minimumMagnitudeText = view.findViewById(R.id.minimum_magnitude);


        //List<Float> defaultValues = rangeSlider.getValues();

        // Extract minimum and maximum values (if applicable)
        Float minDefaultValue = earthquake_setting.getFloat("min_magnitude", 3.0F);
        Float maxDefaultValue = earthquake_setting.getFloat("max_magnitude", 7.4F);
        rangeSlider.setValues(minDefaultValue, maxDefaultValue);
        minimumMagnitudeText.setText(getString(R.string.dash_float, minDefaultValue, maxDefaultValue));
        magnitudePreference.setSummary(String.format(Locale.getDefault(), "%.1f - %.1f", minDefaultValue, maxDefaultValue));


        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view); // Set the custom layout as the dialog view

        builder.setPositiveButton(
                "Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the selected values from the RangeSlider
                        float min = rangeSlider.getValues().get(0);
                        float max = rangeSlider.getValues().get(1);

                        // Save the selected values to SharedPreferences
                        SharedPreferences.Editor editor = earthquake_setting.edit();
                        editor.putFloat("min_magnitude", min);
                        editor.putFloat("max_magnitude", max);
                        editor.apply();


                        //magnitudePreference.setText(String.format(Locale.getDefault(), "%s - %s", min, max));
                        magnitudePreference.setSummary(String.format(Locale.getDefault(), "%s - %s", min, max));
                    }
                });

        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(RangeSlider slider, float value, boolean fromUser) {
                // Directly access values using slider.getValues().get(0) and slider.getValues().get(1)
                float min = slider.getValues().get(0);
                float max = slider.getValues().get(1);

                // Update the TextViews with formatted values
                minimumMagnitudeText.setText(getString(R.string.dash_float, min, max));


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing on cancel
            }
        });

        // Show the dialog
        builder.show();
    }

    public void showDepthDialog(Preference depthPreference) {
        // Inflate the custom layout
        View view = getLayoutInflater().inflate(R.layout.layout_depthpicker_dialog, null);

        RangeSlider rangeSlider = view.findViewById(R.id.depth_slider);
        TextView minimumDepthText = view.findViewById(R.id.minimum_depth);

        List<Float> defaultValues = rangeSlider.getValues();

        // Extract minimum and maximum values (if applicable)
        int minDefaultValue = earthquake_setting.getInt("min_depth", 0);
        int maxDefaultValue = earthquake_setting.getInt("max_depth", 1063);
        rangeSlider.setValues((float) minDefaultValue, (float) maxDefaultValue);
        minimumDepthText.setText(getString(R.string.dash_int, minDefaultValue, maxDefaultValue));


        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view); // Set the custom layout as the dialog view

        builder.setPositiveButton(
                "Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the selected values from the RangeSlider
                        float min = rangeSlider.getValues().get(0);
                        float max = rangeSlider.getValues().get(1);

                        // Save the selected values to SharedPreferences
                        int minDepth = (int) min;
                        int maxDepth = (int) max;
                        SharedPreferences.Editor editor = earthquake_setting.edit();
                        editor.putInt("min_depth", minDepth);
                        editor.putInt("max_depth", maxDepth);
                        editor.apply();


                        //magnitudePreference.setText(String.format(Locale.getDefault(), "%s - %s", min, max));
                        depthPreference.setSummary(String.format(Locale.getDefault(), "%s - %s", minDepth, maxDepth));
                    }
                });

        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(RangeSlider slider, float value, boolean fromUser) {
                // Directly access values using slider.getValues().get(0) and slider.getValues().get(1)
                float min = slider.getValues().get(0);
                float max = slider.getValues().get(1);

                String minText = String.valueOf(min);
                String maxText = String.valueOf(max);


                // Update the TextViews with formatted values
                minimumDepthText.setText(getString(R.string.dash_string, minText, maxText));

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing on cancel
            }
        });

        // Show the dialog
        builder.show();
    }

    public void showDateRangeDialog(Preference dateRangePreference) {
        // Inflate the custom layout
        View view = getLayoutInflater().inflate(R.layout.layout_datepicker_dialog, null);


        long defaultStartDate = earthquake_setting.getLong("start_date", 1483200000000L);
        long defaultEndDate = earthquake_setting.getLong("end_date", 1704038340000L);

        // Create a Date object from milliseconds
        Date sdate = new Date(defaultStartDate);
        Date edate = new Date(defaultEndDate);

        // Define the desired date format string
        String dateFormat = "dd-MMM-yyyy"; // Example format: "10-Mar-2024"

        // Create a SimpleDateFormat object with the format string
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());

        // Format the Date object into a String
        String formattedDate1 = sdf.format(sdate);
        String formattedDate2 = sdf.format(edate);

        Button dateButton = view.findViewById(R.id.datePickerButton);
        TextView startDate = view.findViewById(R.id.starting_date);
        startDate.setText(getString(R.string.dash_string, formattedDate1, formattedDate2));


        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view); // Set the custom layout as the dialog view

        //Temporary container of selected dates
        AtomicLong temp_start = new AtomicLong();
        AtomicLong temp_end = new AtomicLong();
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Material Date Range Picker
                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

                Date start_Date = new Date(117, 1, 1); // January 1, 2017
                long startDateInMillis = start_Date.getTime();

                Date end_Date = new Date(123, 11, 31); // December 31, 2023
                long endDateInMillis = end_Date.getTime();


                CalendarConstraints constraints = new CalendarConstraints.Builder()
                        .setStart(startDateInMillis)  // Set minimum date in milliseconds
                        .setEnd(endDateInMillis)    // Set maximum date in milliseconds
                        .build();

                MaterialDatePicker<Pair<Long, Long>> picker = builder.setCalendarConstraints(constraints).build();
                picker.show(requireActivity().getSupportFragmentManager(), picker.toString());

                picker.addOnPositiveButtonClickListener(selection -> {
                    if (selection != null) {
                        temp_start.set(selection.first);
                        temp_end.set(selection.second);
                        String date1 = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date(selection.first));
                        String date2 = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(new Date(selection.second));


                        //startDate.setText(date1);
                        startDate.setText(getString(R.string.dash_string, date1, date2));
                        dateRangePreference.setSummary(String.format(Locale.getDefault(), "%s to %s", date1, date2));
                    }
                });
            }
        });

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences.Editor editor = earthquake_setting.edit();
                editor.putLong("start_date", temp_start.get());
                editor.putLong("end_date", temp_end.get());
                editor.apply();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing on cancel
            }
        });

        // Show the dialog
        builder.show();
    }
}


