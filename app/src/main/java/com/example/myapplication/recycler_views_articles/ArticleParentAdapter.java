package com.example.myapplication.recycler_views_articles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.recycler_view_horizontal.ChildAdapter;
import com.example.myapplication.recycler_view_horizontal.ChildItemListener;
import com.example.myapplication.recycler_view_horizontal.ParentAdapter;
import com.example.myapplication.recycler_view_horizontal.ParentItemListener;
import com.example.myapplication.recycler_view_horizontal.ParentModelClass;

import java.util.List;


public class ArticleParentAdapter extends RecyclerView.Adapter<ArticleParentAdapter.ViewHolder>{

    List<ArticleParentModelClass> parentModelClassList;
    Context context;

    public ArticleParentAdapter(List<ArticleParentModelClass> parentModelClassList, Context context){
        this.parentModelClassList = parentModelClassList;
        this.context = context;
    }
    @NonNull
    @Override
    public ArticleParentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.article_parent_recycler_view, null, false);
        return new ArticleParentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleParentAdapter.ViewHolder holder, int position) {
        ArticleChildAdapter childAdapter;
        childAdapter = new ArticleChildAdapter(parentModelClassList.get(position).getChildList(), context); // Call getChildList()
        holder.article_rv_child.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.article_rv_child.setAdapter(childAdapter);
        childAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return parentModelClassList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView article_rv_child;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            article_rv_child = itemView.findViewById(R.id.article_rv_child);
        }
    }
}
