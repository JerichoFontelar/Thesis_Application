package com.example.myapplication.recycler_view_forecast_articles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.recycler_views_articles.ArticleChildAdapter;
import com.example.myapplication.recycler_views_articles.ArticleParentAdapter;
import com.example.myapplication.recycler_views_articles.ArticleParentModelClass;

import java.util.List;

public class ForecastArticleParentAdapter extends RecyclerView.Adapter<ForecastArticleParentAdapter.ViewHolder>{

    List<ForecastArticleParentModelClass> parentModelClassList;
    Context context;

    public ForecastArticleParentAdapter(List<ForecastArticleParentModelClass> parentModelClassList, Context context){
        this.parentModelClassList = parentModelClassList;
        this.context = context;
    }
    @NonNull
    @Override
    public ForecastArticleParentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forecast_article_parent_recycler_view, null, false);
        return new ForecastArticleParentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForecastArticleChildAdapter childAdapter;
        childAdapter = new ForecastArticleChildAdapter(parentModelClassList.get(position).getChildList(), context); // Call getChildList()
        holder.forecast_article_rv_child.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.forecast_article_rv_child.setAdapter(childAdapter);
        childAdapter.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return parentModelClassList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView forecast_article_rv_child;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            forecast_article_rv_child = itemView.findViewById(R.id.forecast_article_rv_child);
        }
    }
}
