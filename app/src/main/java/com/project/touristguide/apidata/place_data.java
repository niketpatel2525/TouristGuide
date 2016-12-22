package com.project.touristguide.apidata;

/**
 * Created by Harshit on 11 Nov 2016.
 */

public class place_data {

    String phone,name_str,address_str,place_image_url,rating_image_url,rating;
    double latitude,longitude;
    public place_data() {
        this.phone = "-----------";
        this.name_str = "Nothing in Favorite";
        this.address_str = "";
        this.place_image_url = "";
        this.rating_image_url = "";
        this.latitude = 0.0;
        this.longitude = 0.0;
    }
    public place_data(String ph, String nam, String add, String pl_url, String rat_url, double lat, double lon,String rat) {
        this.phone = ph;
        this.name_str =nam;
        this.address_str = add;
        this.place_image_url = pl_url;
        this.rating_image_url = rat_url;
        this.latitude = lat;
        this.longitude = lon;
        this.rating = rat;
    }
    public String getPhone(){
        return phone;
    }
    public String getAddress_str(){
        return address_str;
    }
    public String getName_str(){
        return name_str;
    }
    public String getPlace_image_url(){
        return place_image_url;
    }
    public String getRating_image_url(){
        return rating_image_url;
    }
    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public String getRating(){ return rating; }
}
