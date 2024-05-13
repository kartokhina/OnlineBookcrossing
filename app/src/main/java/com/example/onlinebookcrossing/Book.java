package com.example.onlinebookcrossing;

public class Book {
    String key, title, author, user, year, place, contact, extra, imageURL;

    public Book() {

    }

    // Геттеры
    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getUser() {
        return user;
    }

    public String getYear() {
        return year;
    }

    public String getPlace() {
        return place;
    }

    public String getContact() {
        return contact;
    }

    public String getExtra() {
        return extra;
    }
    public String getUserName(){return user;}

    public String getImageURL(){return imageURL;}
    // Сеттеры


    public void setKey(String key) {
        this.key = key;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public void setYear(String year) {
        this.year = year;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
    public void setUserName(String user) {
        this.user = user;
    }
    public void setImageURL(String imageURL){this.imageURL = imageURL;}
}
