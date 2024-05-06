package com.example.myapplication.recycler_views_articles;

import com.example.myapplication.recycler_view_horizontal.ChildModelClass;

import java.util.List;

public class ArticleParentModelClass {
    String title;
    List<ArticleChildModelClass> childModelClassList;


    public ArticleParentModelClass( List<ArticleChildModelClass> childModelClassList) {
        this.childModelClassList = childModelClassList;
    }

    // Getter method for child items
    public List<ArticleChildModelClass> getChildList() {
        return childModelClassList;
    }
}
