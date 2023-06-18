package com.example.geolocation;

public class AlertDataType {

    private String name;
    private String description;
    private int categoryId;
    private String base64Image;
    private String coordinates;

    public AlertDataType(String name, String description, int categoryId, String base64Image, String coordinates){
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.base64Image = base64Image;
        this.coordinates = coordinates;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
