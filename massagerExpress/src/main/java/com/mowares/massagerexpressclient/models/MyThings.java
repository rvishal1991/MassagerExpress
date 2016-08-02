/**
 *
 */
package com.mowares.massagerexpressclient.models;

import java.io.Serializable;

/**
 * @author Hardik A Bhalodi
 */
public class MyThings implements Serializable {
    private String name;
    private String age;
    private String type;
    private String notes;
    private String imgUrl;
    private String thingId;

    public String getThingId() {
        return thingId;
    }

    public void setThingId(String thingId) {
        this.thingId = thingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String WALKER_FGIMAGE, WALKER_BGIMAGE, WALKER_TYPE, WALKER_SUBTYPE;
    public int WALKER_ID, SUBTYPE_ID, MAX_SIZE, IS_DEFAULT, IS_VISIBLE, BASE_PRICE, BASE_DISTANCE, TYPE_ID;
    public double WALKER_LATITUDE, WALKER_LONGITUDE, WALKER_DISTANCE;

    //FOR SUBTYPE ARRAY
    public String SUBTYPE_NAME, SUBTYPE_ICON, MAIN_TYPE;
    // FOR TYPE ARRAY
    public String TYPE_NAME;
}
