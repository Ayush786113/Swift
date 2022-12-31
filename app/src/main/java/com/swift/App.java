package com.swift;

import android.graphics.drawable.Drawable;

public class App  {
    String name, label;

    @Override
    public String toString() {
        return "App{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", icon=" + icon +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    Drawable icon;
}
