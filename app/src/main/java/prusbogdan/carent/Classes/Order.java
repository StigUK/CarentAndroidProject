package prusbogdan.carent.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import prusbogdan.carent.MainActivity;

public class Order implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("car_id")
    @Expose
    private int car_id;
    @SerializedName("user_id")
    @Expose
    private int user_id;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("term")
    @Expose
    private int term;

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public int getCar_id() {
        return car_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getDate() {
        return date;
    }

    public int getTerm() {
        return term;
    }

    public int getActive() {
        return active;
    }

    public int getPrice() {
        return price;
    }

    @SerializedName("active")
    @Expose
    private int active;
    @SerializedName("price")
    @Expose
    private int price;

    public Order(int id, int car_id, int user_id, String date, int term, int active, int price) {
        this.id = id;
        this.car_id = car_id;
        this.user_id = user_id;
        this.date = date;
        this.term = term;
        this.active = active;
        this.price = price;
    }

    public String returncar()
    {
        for(int i=0; i< MainActivity.data.carModels.size(); i++)
        {
            if(MainActivity.data.carModels.get(i).getId()==this.car_id)
                return MainActivity.data.carModels.get(i).getName();
        }
        return "";
    }

    public String returncarimage()
    {
        for(int i=0; i< MainActivity.data.carModels.size(); i++)
        {
            if(MainActivity.data.carModels.get(i).getId()==this.car_id)
                return MainActivity.data.carModels.get(i).getPicture();
        }
        return "";
    }
}
