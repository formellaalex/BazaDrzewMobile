package com.example.formel.bazadrzewmobile.beans;

/**
 * Created by formel on 16.12.15.
 */
public class TreeListBean {

    public String nameLatin;
    public String namePolish;
    public String districtName;
    public String city;
    public String description;
    public String adddate;
    public String location;
    public String locationLongitude;
    public String locationLatitude;
    public String login;
    public Integer isPomnik;

    public TreeListBean(String nameLatin, String namePolish, String city, String districtName) {

        this.nameLatin = nameLatin;
        this.namePolish = namePolish;
        this.city = city;
        this.districtName = districtName;
    }

    public String getName_polish() {
        return namePolish;
    }

    public void setName_polish(String namePolish) {
        this.namePolish = namePolish;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName_latin() {
        return nameLatin;
    }

    public void setName_latin(String nameLatin) {
        this.nameLatin = nameLatin;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getNameLatin() {
        return nameLatin;
    }

    public void setNameLatin(String nameLatin) {
        this.nameLatin = nameLatin;
    }

    public String getNamePolish() {
        return namePolish;
    }

    public void setNamePolish(String namePolish) {
        this.namePolish = namePolish;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdddate() {
        return adddate;
    }

    public void setAdddate(String adddate) {
        this.adddate = adddate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(String locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public String getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(String locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer isPomnik() {
        return isPomnik;
    }

    public void setIsPomnik(Integer isPomnik) {
        this.isPomnik = isPomnik;
    }


}
