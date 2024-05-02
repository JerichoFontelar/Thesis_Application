package com.example.myapplication.recycler_view_horizontal;

import com.example.myapplication.recycler_view_horizontal.ChildModelClass;

import java.util.List;

public class ParentModelClass {
    String title;
    List<ChildModelClass> childModelClassList;


    public ParentModelClass( List<ChildModelClass> childModelClassList) {
        this.childModelClassList = childModelClassList;
    }

    // Getter method for child items
    public List<ChildModelClass> getChildList() {
        return childModelClassList;
    }
}
