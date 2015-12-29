package com.example.formel.bazadrzewmobile.entities;

/**
 * Created by formel on 17.12.15.
 */
public class TreeEntity {


    private String name_polish;
    private String name_latin;
    private String location;
    private String street;
    private String city;
    private double location_longitude;
    private double location_latitude;
    private String description;
    private String adddate;
    private boolean is_pomnik;
    private String aktualizacja;
    private int in_greenhouse;
    private int idtree_objects;
    private DistrictEntity iddistricts;
    private int idusers;
    private int idspecial_place;
    private int blocked;
    private int icon;
    private int unknown_tree;
    private int nonexistent;
    private int view_count;
    private int post_on_fb;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName_latin() {
        return name_latin;
    }

    public void setName_latin(String name_latin) {
        this.name_latin = name_latin;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLocation_longitude() {
        return location_longitude;
    }

    public void setLocation_longitude(double location_longitude) {
        this.location_longitude = location_longitude;
    }

    public double getLocation_latitude() {
        return location_latitude;
    }

    public void setLocation_latitude(double location_latitude) {
        this.location_latitude = location_latitude;
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

    public boolean is_pomnik() {
        return is_pomnik;
    }

    public void setIs_pomnik(boolean is_pomnik) {
        this.is_pomnik = is_pomnik;
    }

    public String getAktualizacja() {
        return aktualizacja;
    }

    public void setAktualizacja(String aktualizacja) {
        this.aktualizacja = aktualizacja;
    }

    public int getIn_greenhouse() {
        return in_greenhouse;
    }

    public void setIn_greenhouse(int in_greenhouse) {
        this.in_greenhouse = in_greenhouse;
    }

    public String getName_polish() {
        return name_polish;
    }

    public void setName_polish(String name_polish) {
        this.name_polish = name_polish;
    }


    public int getIdtree_objects() {
        return idtree_objects;
    }

    public void setIdtree_objects(int idtree_objects) {
        this.idtree_objects = idtree_objects;
    }

    public DistrictEntity getIddistricts() {
        return iddistricts;
    }

    public void setIddistricts(DistrictEntity iddistricts) {
        this.iddistricts = iddistricts;
    }

    public int getIdusers() {
        return idusers;
    }

    public void setIdusers(int idusers) {
        this.idusers = idusers;
    }

    public int getIdspecial_place() {
        return idspecial_place;
    }

    public void setIdspecial_place(int idspecial_place) {
        this.idspecial_place = idspecial_place;
    }

    public int getBlocked() {
        return blocked;
    }

    public void setBlocked(int blocked) {
        this.blocked = blocked;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getUnknown_tree() {
        return unknown_tree;
    }

    public void setUnknown_tree(int unknown_tree) {
        this.unknown_tree = unknown_tree;
    }

    public int getNonexistent() {
        return nonexistent;
    }

    public void setNonexistent(int nonexistent) {
        this.nonexistent = nonexistent;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public int getPost_on_fb() {
        return post_on_fb;
    }

    public void setPost_on_fb(int post_on_fb) {
        this.post_on_fb = post_on_fb;
    }



}
