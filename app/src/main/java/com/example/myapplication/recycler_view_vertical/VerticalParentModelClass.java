package com.example.myapplication.recycler_view_vertical;

import com.example.myapplication.recycler_view_horizontal.ChildModelClass;

import java.util.List;

public class VerticalParentModelClass {
    String title;
    String magnitude_range;
    String depth_range;
    List<VerticalChildModelClass> verticalChildModelClass;


    public VerticalParentModelClass(String title, List<VerticalChildModelClass> verticalChildModelClass, String magnitude_range, String depth_range) {
        this.title = title;
        this.verticalChildModelClass = verticalChildModelClass;
        this.magnitude_range = magnitude_range;
        this.depth_range = depth_range;
    }
}

