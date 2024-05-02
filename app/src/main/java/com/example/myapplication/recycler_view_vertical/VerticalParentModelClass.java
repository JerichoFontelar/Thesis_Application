package com.example.myapplication.recycler_view_vertical;

import com.example.myapplication.recycler_view_horizontal.ChildModelClass;

import java.util.List;

public class VerticalParentModelClass {
    String title;
    List<VerticalChildModelClass> verticalChildModelClass;


    public VerticalParentModelClass(String title, List<VerticalChildModelClass> verticalChildModelClass, int defaultExpandedPosition) {
        this.title = title;
        this.verticalChildModelClass = verticalChildModelClass;
    }
}

