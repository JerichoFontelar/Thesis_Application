package com.example.myapplication.recycler_view_vertical;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomLayoutManager extends LinearLayoutManager {

    private int rowsPerColumn; // Number of rows per column

    public CustomLayoutManager(Context context, int rowsPerColumn, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.rowsPerColumn = rowsPerColumn;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int startPos = getChildCount() == 0 ? 0 : getPosition(getChildAt(0));
        int endPos = state.getItemCount() - 1;

        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }

        int currentColumn = 0;
        int currentRow = 0;

        for (int i = startPos; i <= endPos; i++) {
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);

            int width = getDecoratedMeasuredWidth(child);
            int height = getDecoratedMeasuredHeight(child);

            int left = currentColumn * width;
            int top = currentRow * height;
            layoutDecorated(child, left, top, left + width, top + height);

            currentRow++;

            // Move to the next column if the current row reaches the limit
            if (currentRow == rowsPerColumn) {
                currentRow = 0;
                currentColumn++;
            }
        }
    }

}
