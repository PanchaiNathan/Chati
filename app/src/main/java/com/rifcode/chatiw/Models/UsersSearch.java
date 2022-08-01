package com.rifcode.chatiw.Models;

/**
 * Created by ibra_ on 25/02/2018.
 */

public class UsersSearch {

    String username;
    String thumb_image;

    public String getthumb_image() {
        return thumb_image;
    }

    public void setthumb_image(String photoProfile) {
        this.thumb_image = photoProfile;
    }

    public UsersSearch(String username, String photoProfile) {
        this.username = username;
        this.thumb_image = photoProfile;
    }

    public UsersSearch() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
