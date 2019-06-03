package prusbogdan.carent.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
public class UserInfo{
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("first_name")
    @Expose
    private String first_name;
    @SerializedName("middle_name")
    @Expose
    private String middle_name;
    @SerializedName("second_name")
    @Expose
    private String second_name;
    @SerializedName("license_id")
    @Expose
    private String license_id;
    @SerializedName("license_date")
    @Expose
    private String license_date;
    @SerializedName("photo_license")
    @Expose
    private String photo_license;
    @SerializedName("photo_user")
    @Expose
    private String photo_user;
    @SerializedName("photo_passport")
    @Expose
    private String photo_passport;
    @SerializedName("phone_number")
    @Expose
    private String phone_number;
    @SerializedName("user")
    @Expose
    private int user;

    public void setId(int id) {
        this.id = id;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public void setSecond_name(String second_name) {
        this.second_name = second_name;
    }

    public void setLicense_id(String license_id) {
        this.license_id = license_id;
    }

    public void setLicense_date(String license_date) {
        this.license_date = license_date;
    }

    public void setPhoto_license(String photo_license) {
        this.photo_license = photo_license;
    }

    public void setPhoto_user(String photo_user) {
        this.photo_user = photo_user;
    }

    public void setPhoto_passport(String photo_passport) {
        this.photo_passport = photo_passport;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public String getSecond_name() {
        return second_name;
    }

    public String getLicense_id() {
        return license_id;
    }

    public String getLicense_date() {
        return license_date;
    }

    public String getPhoto_license() {
        return photo_license;
    }

    public String getPhoto_user() {
        return photo_user;
    }

    public UserInfo(int id, String first_name, String middle_name, String second_name, String license_id, String license_date, String photo_license, String photo_user, String photo_passport, String phone_number, int user) {
        this.id = id;
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.second_name = second_name;
        this.license_id = license_id;
        this.license_date = license_date;
        this.photo_license = photo_license;
        this.photo_user = photo_user;
        this.photo_passport = photo_passport;
        this.phone_number = phone_number;
        this.user = user;
    }

    public String getPhoto_passport() {
        return photo_passport;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public int getUser() {
        return user;
    }
}
