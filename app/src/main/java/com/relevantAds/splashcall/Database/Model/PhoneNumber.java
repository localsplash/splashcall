package com.relevantAds.splashcall.Database.Model;

public class PhoneNumber {
    private int id;
    private String addedPhoneNumber;
    private String createdDate;


    public static final String TABLE_NAME = "phone_numbers_list";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_TIMESTAMP = "created_date";

    /**
     * Creating Database Table named collections
     */
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_PHONE_NUMBER + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    /**
     * required empty constructor
     */
    public PhoneNumber(){

    }

    public int getId() {
        return id;
    }

    public String getAddedPhoneNumber() {
        return addedPhoneNumber;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAddedPhoneNumber(String addedPhoneNumber) {
        this.addedPhoneNumber = addedPhoneNumber;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public PhoneNumber(int id, String addedPhoneNumber,String createdDate){
        this.id = id;
        this.addedPhoneNumber = addedPhoneNumber;
        this.createdDate = createdDate;
    }
}
