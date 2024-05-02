package com.example.myapplication.recycler_view_vertical;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.recycler_view_horizontal.ChildAdapter;
import com.example.myapplication.recycler_view_horizontal.ChildModelClass;

import java.util.List;

public class VerticalChildAdapter extends RecyclerView.Adapter<VerticalChildAdapter.ViewHolder>{
    List<VerticalChildModelClass> verticalChildModelClassList;
    Context context;

    public VerticalChildAdapter(List<VerticalChildModelClass> verticalChildModelClassList, Context context) {
        this.verticalChildModelClassList = verticalChildModelClassList;
        this.context = context;
    }

    @NonNull
    @Override
    public VerticalChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vertical_child_recyclerview_layout, parent, false); // Pass 'parent' and 'false'
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerticalChildAdapter.ViewHolder holder, int position) {
        holder.province.setText(verticalChildModelClassList.get(position).province);
    }

    @Override
    public int getItemCount() {
        return verticalChildModelClassList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView province;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            province = itemView.findViewById(R.id.province);


        }
    }
}
