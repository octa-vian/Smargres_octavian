package com.octa.vian.ImageSlider;

public class ImageSliderItem {
    private String image;

    public ImageSliderItem(){
    }

    public ImageSliderItem(String image){
        this.image = image;
    }

    public String getItem() {
        return image;
    }

    public void setItem(String image) {
        this.image = image;
    }
}
