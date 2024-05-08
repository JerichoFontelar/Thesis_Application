package com.example.myapplication.recycler_views_forecast;

import com.example.myapplication.recycler_views_articles.ArticleChildModelClass;

import java.util.List;

public class ForecastParentModelClass {

    List<ForecastChildModelClass> childModelClassList;


    public ForecastParentModelClass( List<ForecastChildModelClass> childModelClassList) {
        this.childModelClassList = childModelClassList;
    }

    // Getter method for child items
    public List<ForecastChildModelClass> getChildList() {
        return childModelClassList;
    }


}
