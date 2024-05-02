package com.example.myapplication.recycler_view_horizontal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ViewHolder> implements ChildItemListener{

    List<ParentModelClass> parentModelClassList;
    Context context;
    private final ChildItemListener listener;

    ParentItemListener parentItemListener;


    public ParentAdapter(List<ParentModelClass> parentModelClassList, Context context, ChildItemListener listener, ParentItemListener parentItemListener){
        this.parentModelClassList = parentModelClassList;
        this.context = context;
        this.listener = listener;
        this.parentItemListener = parentItemListener;
    }

    @NonNull
    @Override
    public ParentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.parent_recycler_view, null, false);
        return new ViewHolder(view, parentItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentAdapter.ViewHolder holder, int position) {


        final int parentPosition = position;

        ChildAdapter childAdapter;
        childAdapter = new ChildAdapter(parentModelClassList.get(position).getChildList(), context, listener, parentItemListener); // Call getChildList()
        holder.rv_child.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.rv_child.setAdapter(childAdapter);
        childAdapter.notifyDataSetChanged();


//
//        // Loop through child items and set click listener with child position
//        List<ChildModelClass> childItems = parentModelClassList.get(position).getChildList();
//        for (int childPosition = 0; childPosition < childItems.size(); childPosition++) {
//            View childItemView = holder.rv_child.findViewWithTag(childItems.get(childPosition).getId()); // Call getId()
//            if (childItemView != null) {
//                int finalChildPosition = childPosition;
//                childItemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        listener.onChildItemClicked(holder.getAdapterPosition(), finalChildPosition); // Pass both positions directly
//                        Log.d("ViewHolder", "Child view clicked for child: " + childItems.get(finalChildPosition));
//                    }
//                });
//            } else {
//                Log.w("ViewHolder", "Child view not found for child: " + childItems.get(childPosition));
//            }
//        }
    }






    @Override
    public int getItemCount() {
        return parentModelClassList.size();
    }

    @Override
    public void onChildItemClicked(int parentPosition, int childPosition) {
//         if (listener != null) {
//             listener.onChildItemClicked(parentPosition, childPosition);
//             Log.d("ParentAdapter", "Child item clicked for parent: " + parentModelClassList.get(parentPosition) + " at position: " + childPosition);
//         }
        //return parentPosition;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        RecyclerView rv_child;

        public ViewHolder(@NonNull View itemView, ParentItemListener listener) {
            super(itemView);

            rv_child = itemView.findViewById(R.id.rv_child);

        }

    }

    public void setParentItemListener(ParentItemListener parentItemListener) {
        this.parentItemListener = parentItemListener;
    }
}
