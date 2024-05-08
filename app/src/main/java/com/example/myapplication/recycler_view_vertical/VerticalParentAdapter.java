package com.example.myapplication.recycler_view_vertical;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.recycler_view_horizontal.ChildAdapter;
import com.example.myapplication.recycler_view_horizontal.ParentAdapter;
import com.example.myapplication.recycler_view_horizontal.ParentModelClass;

import java.util.List;

public class VerticalParentAdapter extends RecyclerView.Adapter<VerticalParentAdapter.ViewHolder>{
    List<VerticalParentModelClass> verticalParentModelClassList;
    Context context;


    public VerticalParentAdapter(List<VerticalParentModelClass> verticalParentModelClasses, Context context) {
        this.verticalParentModelClassList = verticalParentModelClasses;
        this.context = context;


        // Notify about data changes in all items
        notifyItemRangeChanged(0, verticalParentModelClassList.size());
    }

    @NonNull
    @Override
    public VerticalParentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vertical_parent_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalParentAdapter.ViewHolder holder, int position) {
        holder.vertical_tv_parent_title.setText(verticalParentModelClassList.get(position).title);
        holder.vertical_tv_magnitude_range.setText(verticalParentModelClassList.get(position).magnitude_range);
        holder.vertical_tv_depth_range.setText(verticalParentModelClassList.get(position).depth_range);

        int spanCount = calculateSpanCount(verticalParentModelClassList.get(position).verticalChildModelClass.size()); // Calculate span count based on child count

        VerticalChildAdapter verticalChildAdapter;
        verticalChildAdapter = new VerticalChildAdapter(verticalParentModelClassList.get(position).verticalChildModelClass, context);
        holder.vertical_rv_child.setLayoutManager(new GridLayoutManager(context, spanCount, GridLayoutManager.HORIZONTAL, false));
        holder.vertical_rv_child.setAdapter(verticalChildAdapter);
        verticalChildAdapter.notifyDataSetChanged();


    }

    private int calculateSpanCount(int childCount) {
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        int screenWidth = displayMetrics.widthPixels;
//
//        // Define a minimum and maximum number of columns
//        int minColumns = 2;
//        int maxColumns = 6;
//
//        // Calculate desired width per child item
//        int desiredWidthPerChild = screenWidth / maxColumns;
//
//        // Estimate average child width based on child count (assuming child views have similar width)
//        int averageChildWidth = screenWidth / childCount;
//
//        // Choose the number of columns based on desired width and average child width
//        int calculatedColumns = Math.max(minColumns, Math.min(maxColumns, desiredWidthPerChild / averageChildWidth));

        return 5;
    }


    @Override
    public int getItemCount() {
        return verticalParentModelClassList.size();
    }

    @Override
    public long getItemId(int position) {
        return verticalParentModelClassList.get(position).hashCode(); // Use a unique identifier for each parent
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        RecyclerView vertical_rv_child;
        TextView vertical_tv_parent_title;
        TextView vertical_tv_magnitude_range;
        TextView vertical_tv_depth_range;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vertical_rv_child = itemView.findViewById(R.id.vertical_rv_child);
            vertical_tv_parent_title = itemView.findViewById(R.id.vertical_tv_parent_title);
            vertical_tv_magnitude_range = itemView.findViewById(R.id.vertical_tv_parent_magnitude);
            vertical_tv_depth_range = itemView.findViewById(R.id.vertical_tv_parent_depth);
            // Set OnClickListener for the parent title view

        }

    }
}
