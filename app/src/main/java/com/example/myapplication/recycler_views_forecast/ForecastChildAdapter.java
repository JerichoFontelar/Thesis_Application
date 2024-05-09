package com.example.myapplication.recycler_views_forecast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ForecastChildAdapter extends RecyclerView.Adapter<ForecastChildAdapter.ViewHolder> {

    List<ForecastChildModelClass> childModelClassList;
    Context context;


    public ForecastChildAdapter(List<ForecastChildModelClass> childModelClassList, Context context) {
        this.childModelClassList = childModelClassList;
        this.context = context;
    }

    @NonNull
    @Override
    public ForecastChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.time_series_recycler_view, null, false);

        return new ForecastChildAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastChildAdapter.ViewHolder holder, int position) {
        holder.image.setImageResource(childModelClassList.get(position).image);
        holder.title.setText(childModelClassList.get(position).title);
    }


    @Override
    public int getItemCount() {
        return childModelClassList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.arima_image);
            title = itemView.findViewById(R.id.arima_title);
        }

    }


}