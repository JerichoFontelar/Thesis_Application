package com.example.myapplication.recycler_view_horizontal;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> {

    List<ChildModelClass> childModelClassList;
    Context context;

    private ChildItemListener itemListener;
    ParentItemListener parentItemClickListener; // Interface variable

    public ChildAdapter(List<ChildModelClass> childModelClassList, Context context, ChildItemListener itemListener, ParentItemListener parentItemClickListener) {
        this.childModelClassList = childModelClassList;
        this.context = context;
        this.itemListener = itemListener;
        this.parentItemClickListener = parentItemClickListener;
    }

    @NonNull
    @Override
    public ChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.child_recyclerview_layout, null, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ChildAdapter.ViewHolder holder, int position) {
        holder.iv_child_image.setImageResource(childModelClassList.get(position).image);
        holder.cv_child_model_name.setText(childModelClassList.get(position).model_name);
        holder.cv_child_silhouette_score.setText(childModelClassList.get(position).silhouette_score);
        holder.cv_child_model_inertia.setText(childModelClassList.get(position).model_inertia);
        holder.cv_child_model_davies_bouldin.setText(childModelClassList.get(position).model_davies_bouldin);
        holder.cv_child_no_of_clusters.setText(childModelClassList.get(position).no_of_clusters);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the adapter position
                int position = holder.getAdapterPosition();
                // Get the parent position
                int parentPosition = getParentAdapterPosition(holder.itemView);
                if (parentPosition != RecyclerView.NO_POSITION && position != RecyclerView.NO_POSITION) {
                    itemListener.onChildItemClicked(parentPosition, position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return childModelClassList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_child_image;
        TextView cv_child_model_name;
        TextView cv_child_silhouette_score;
        TextView cv_child_model_inertia;
        TextView cv_child_model_davies_bouldin;
        TextView cv_child_no_of_clusters;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_child_image = itemView.findViewById(R.id.iv_child_item);
            cv_child_model_name = itemView.findViewById(R.id.model_name);
            cv_child_silhouette_score = itemView.findViewById(R.id.ss);
            cv_child_model_inertia = itemView.findViewById(R.id.mi);
            cv_child_model_davies_bouldin = itemView.findViewById(R.id.dbi);
            cv_child_no_of_clusters = itemView.findViewById(R.id.nc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the adapter position
                    int position = getAdapterPosition();
                    // Get the parent position
                    int parentPosition = getParentAdapterPosition(itemView);
                    if (parentPosition != RecyclerView.NO_POSITION && position != RecyclerView.NO_POSITION) {
                        itemListener.onChildItemClicked(parentPosition, position);
                    }
                }
            });
        }

    }
    // Helper method to get the parent position
    private int getParentAdapterPosition(View itemView) {
        ViewParent parent = itemView.getParent();
        while (parent != null) {
            if (parent instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) parent;
                RecyclerView.ViewHolder parentViewHolder = recyclerView.findContainingViewHolder(itemView);
                if (parentViewHolder != null) {
                    return parentViewHolder.getAdapterPosition();
                } else {
                    // Log a warning (optional)
                    // Log.w("ChildAdapter", "Parent ViewHolder not found!");
                }
            }
            parent = parent.getParent();
        }
        return RecyclerView.NO_POSITION;
    }



}
