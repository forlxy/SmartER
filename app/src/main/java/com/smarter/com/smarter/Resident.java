package com.smarter.com.smarter;

import android.os.Parcel;
import android.os.Parcelable;

import com.smarter.tools.Datetools;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by kasal on 7/04/2018.
 */

public class Resident implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Resident createFromParcel(Parcel in) {
            return new Resident(in);
        }

        public Resident[] newArray(int size) {
            return new Resident[size];
        }
    };

    // Parcelling part
    public Resident(Parcel in){
        this.resid = in.readInt();
        this.number = in.readInt();
        this.fname = in.readString();
        this.sname =  in.readString();
        try {
            this.dob = Datetools.parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.address =  in.readString();
        this.postcode =  in.readString();
        this.email =  in.readString();
        this.mobile =  in.readString();
        this.providerName =  in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.resid);
        dest.writeInt(this.number);
        dest.writeString(this.fname);
        dest.writeString(this.sname);
        dest.writeString(Datetools.toString(this.dob));
        dest.writeString(this.address);
        dest.writeString(this.postcode);
        dest.writeString(this.email);
        dest.writeString(this.mobile);
        dest.writeString(this.providerName);

    }
    private Integer resid;
    private String fname;
    private String sname;
    private Date dob;
    private String address;
    private String postcode;
    private String email;
    private String mobile;
    private int number;
    private String providerName;

    public Resident() {
    }

    public Resident(Integer resid) {
        this.resid = resid;
    }

    public Resident(Integer resid, String fname, String sname, Date dob, String address, String postcode, String email, String mobile, int number, String providerName) {
        this.resid = resid;
        this.fname = fname;
        this.sname = sname;
        this.dob = dob;
        this.address = address;
        this.postcode = postcode;
        this.email = email;
        this.mobile = mobile;
        this.number = number;
        this.providerName = providerName;
    }

    public Resident(Integer resid, String mobile, int number, String providerName) {
        this.resid = resid;
        this.mobile = mobile;
        this.number = number;
        this.providerName = providerName;
    }

    public Integer getResid() {
        return resid;
    }

    public void setResid(Integer resid) {
        this.resid = resid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

//    @Override
//    public String toString()
//    {
//        return "{\"address\":\"" + address + "\",\"dob\":\"" + Datetools.toString(dob) +
//                "\",\"email\":\"" + email +"\",\"fname\":\""+ fname+ "\",\"mobile\":\"" + mobile +
//                "\",\"number\":" + number +",\"postcode\":\"" + postcode + "\",\"providerName\":\"" + providerName +
//                "\",\"resid\":" + resid + ",\"sname\":\"" + sname + "\"}";
//    }
}
