package com.example.workshopapp;

public class Baranje {
    private String UserId;
    private String AktivnostId;
    private String Aktivnost;
    private String OpisAktivnost;
    private String VremeOd;
    private String VremeDo;
    private String Datum;
    private String Den;
    private String Adresa;
    private String Status;
    private double Longitude;
    private double Latitude;
    private String EmailVolonter;
    private String TelefonVolonter;
    private int Rastojanie;

    public Baranje() {

    }

    public Baranje(String userId, String aktivnost, String opisAktivnost, String vremeOd, String vremeDo, String datum,
                   String den, String adresa, String status, double longitude, double latitude, String emailVolonter, String telefonVolonter) {
        this.UserId = userId;
        this.Aktivnost = aktivnost;
        this.OpisAktivnost = opisAktivnost;
        this.VremeOd = vremeOd;
        this.VremeDo = vremeDo;
        this.Datum = datum;
        this.Den = den;
        this.Adresa = adresa;
        this.Status = status;
        this.Longitude = longitude;
        this.Latitude = latitude;
        this.EmailVolonter = emailVolonter;
        this.TelefonVolonter = telefonVolonter;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getAktivnostId() {
        return AktivnostId;
    }

    public void setAktivnostId(String aktivnostId) {
        AktivnostId = aktivnostId;
    }

    public String getAktivnost() {
        return Aktivnost;
    }

    public void setAktivnost(String aktivnost) {
        Aktivnost = aktivnost;
    }

    public String getOpisAktivnost() {
        return OpisAktivnost;
    }

    public void setOpisAktivnost(String opisAktivnost) {
        OpisAktivnost = opisAktivnost;
    }

    public String getVremeOd() {
        return VremeOd;
    }

    public void setVremeOd(String vremeOd) {
        VremeOd = vremeOd;
    }

    public String getVremeDo() {
        return VremeDo;
    }

    public void setVremeDo(String vremeDo) {
        VremeDo = vremeDo;
    }

    public String getDatum() {
        return Datum;
    }

    public void setDatum(String datum) {
        Datum = datum;
    }

    public String getDen() {
        return Den;
    }

    public void setDen(String den) {
        Den = den;
    }

    public String getAdresa() {
        return Adresa;
    }

    public void setAdresa(String adresa) {
        Adresa = adresa;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public String getEmailVolonter() {
        return EmailVolonter;
    }

    public void setEmailVolonter(String emailVolonter) {
        EmailVolonter = emailVolonter;
    }

    public String getTelefonVolonter() {
        return TelefonVolonter;
    }

    public void setTelefonVolonter(String telefonVolonter) {
        TelefonVolonter = telefonVolonter;
    }

    public int getRastojanie() {
        return Rastojanie;
    }

    public void setRastojanie(int rastojanie) {
        Rastojanie = rastojanie;
    }
}
