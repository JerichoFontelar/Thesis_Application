package com.example.myapplication.recycler_views_forecast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ForecastParentAdapter extends RecyclerView.Adapter<ForecastParentAdapter.ViewHolder>{

    List<ForecastParentModelClass> parentModelClassList;
    Context context;

    public ForecastParentAdapter(List<ForecastParentModelClass> parentModelClassList, Context context){
        this.parentModelClassList = parentModelClassList;
        this.context = context;
    }
    @NonNull
    @Override
    public ForecastParentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.time_series_parent_recycler_view, null, false);
        return new ForecastParentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForecastChildAdapter childAdapter;
        childAdapter = new ForecastChildAdapter(parentModelClassList.get(position).getChildList(), context); // Call getChildList()
        holder.rv_timeseries_child.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.rv_timeseries_child.setAdapter(childAdapter);
        childAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return parentModelClassList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rv_timeseries_child;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_timeseries_child = itemView.findViewById(R.id.rv_timeseries_child);
        }
    }
}
