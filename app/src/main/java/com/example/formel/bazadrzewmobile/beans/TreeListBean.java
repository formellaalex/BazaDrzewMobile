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
    public Integer idtreeObjects;
    public Integer inGreenhouse;
    public String street;

    public TreeListBean(Integer idtreeObjects, String nameLatin, String namePolish, String city, String districtName) {

        this.nameLatin = nameLatin;
        this.namePolish = namePolish;
        this.city = city;
        this.districtName = districtName;
        this.idtreeObjects = idtreeObjects;
    }

}
