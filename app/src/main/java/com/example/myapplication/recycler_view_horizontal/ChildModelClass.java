package com.example.myapplication.recycler_view_horizontal;

public class ChildModelClass {

    int image;
    private int id;
    String model_name;
    String silhouette_score;
    String model_inertia;
    String model_davies_bouldin;
    String no_of_clusters;


    public ChildModelClass(int image, int id, String model_name, String silhouette_score, String model_inertia, String model_davies_bouldin, String no_of_clusters) {
        this.image = image;
        this.id = id; // Assign the passed ID
        this.model_name = model_name;
        this.silhouette_score = silhouette_score;
        this.model_inertia = model_inertia;
        this.model_davies_bouldin = model_davies_bouldin;
        this.no_of_clusters = no_of_clusters;
    }
    public int getId() {
        return id;
    }


}
