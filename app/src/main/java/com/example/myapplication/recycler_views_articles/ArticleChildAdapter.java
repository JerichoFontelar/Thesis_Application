package com.example.myapplication.recycler_views_articles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ArticleChildAdapter extends RecyclerView.Adapter<ArticleChildAdapter.ViewHolder> {

    List<ArticleChildModelClass> childModelClassList;
    Context context;


    public ArticleChildAdapter(List<ArticleChildModelClass> childModelClassList, Context context) {
        this.childModelClassList = childModelClassList;
        this.context = context;
    }

    @NonNull
    @Override
    public ArticleChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.article_recycler_view, null, false);

        return new ArticleChildAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder.article != null) {
            holder.article.setText(childModelClassList.get(position).article);
        }
        if(holder.term != null){
            holder.term.setText(childModelClassList.get(position).term);
        }
        //holder.term.setText(childModelClassList.get(position).term);
    }


    @Override
    public int getItemCount() {
        return childModelClassList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView term;
        TextView article;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            term = itemView.findViewById(R.id.forecast_article_term);
            article = itemView.findViewById(R.id.forecast_article_paragraph);
        }

    }


}
