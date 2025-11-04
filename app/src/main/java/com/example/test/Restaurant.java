package com.example.test;

public class Restaurant {
    private String name;
    private String slogan;
    private String signatureDish;
    private String backgroundColor;
    private String theme;
    private int iconResId;

    public Restaurant(String name, String slogan, String signatureDish, String backgroundColor, String theme) {
        this.name = name;
        this.slogan = slogan;
        this.signatureDish = signatureDish;
        this.backgroundColor = backgroundColor;
        this.theme = theme;
    }

    public String getName() {
        return name;
    }

    public String getSlogan() {
        return slogan;
    }

    public String getSignatureDish() {
        return signatureDish;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getTheme() {
        return theme;
    }
}
