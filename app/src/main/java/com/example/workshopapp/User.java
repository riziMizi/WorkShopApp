package com.example.workshopapp;

public class User {
   private String Email;
   private String Phone;
   private String Tip;
   private String Name;

   public User() {

   }

    public User(String email, String phone, String tip, String name) {
        this.Email = email;
        this.Phone = phone;
        this.Tip = tip;
        this.Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getTip() {
        return Tip;
    }

    public void setTip(String tip) {
        Tip = tip;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
