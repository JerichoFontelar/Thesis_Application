package com.example.myapplication.recycler_view_forecast_articles;

public class ForecastArticleChildModelClass {
    String article;
    String term;

    public ForecastArticleChildModelClass(String article, String term) {
        this.article = article;
        this.term = term;
    }

    public String getArticle() {
        return article;
    }

    public String getTerm() {
        return term;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
