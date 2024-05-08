package com.example.myapplication.recycler_view_forecast_articles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.recycler_views_articles.ArticleChildAdapter;
import com.example.myapplication.recycler_views_articles.ArticleChildModelClass;

import java.util.List;

public class ForecastArticleChildAdapter extends RecyclerView.Adapter<ForecastArticleChildAdapter.ViewHolder> {

        List<ForecastArticleChildModelClass> childModelClassList;
        Context context;


    public ForecastArticleChildAdapter(List<ForecastArticleChildModelClass> childModelClassList, Context context) {
            this.childModelClassList = childModelClassList;
            this.context = context;
        }

        @NonNull
        @Override
        public ForecastArticleChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.forecast_article_recycler_view, null, false);

            return new ForecastArticleChildAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.forecast_article.setText(childModelClassList.get(position).article);
            holder.forecast_term.setText(childModelClassList.get(position).term);
        }


        @Override
        public int getItemCount() {
            return childModelClassList.size();
        }



        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView forecast_term;
            TextView forecast_article;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                forecast_term = itemView.findViewById(R.id.forecast_article_term);
                forecast_article = itemView.findViewById(R.id.forecast_article_paragraph);
            }

        }
}
