package com.example.myapplication.recycler_view_forecast_articles;

import com.example.myapplication.recycler_views_articles.ArticleChildModelClass;

import java.util.List;

public class ForecastArticleParentModelClass {

    String title;
    List<ForecastArticleChildModelClass> childModelClassList;

    public ForecastArticleParentModelClass(String title, List<ForecastArticleChildModelClass> childModelClassList) {
        this.title = title;
        this.childModelClassList = childModelClassList;
    }

    public String getTitle() {
        return title;
    }

    public List<ForecastArticleChildModelClass> getChildList() {
        return childModelClassList;
    }
}
