package prusbogdan.carent.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CarModel implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("category")
    @Expose
    private int category;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("engine_volume")
    @Expose
    private String engine_volume;
    @SerializedName("engine_type")
    @Expose
    private String engine_type;
    @SerializedName("fuel_consumption")
    @Expose
    private double fuel_consumption;
    @SerializedName("seats")
    @Expose
    private int seats;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("doors")
    @Expose
    private int doors;
    @SerializedName("transmission")
    @Expose
    String transmission;
    @SerializedName("air_conditioning")
    @Expose
    private int air_conditioning;
    @SerializedName("count")
    @Expose
    private int count;

    public int getId() {
        return id;
    }

    public int getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getPicture() {
        return picture;
    }

    public String getEngine_volume() {
        return engine_volume;
    }

    public String getEngine_type() {
        return engine_type;
    }

    public double getFuel_consumption() {
        return fuel_consumption;
    }

    public int getSeats() {
        return seats;
    }

    public String getBody() {
        return body;
    }

    public int getDoors() {
        return doors;
    }

    public String getTransmission() {
        return transmission;
    }

    public int getAir_conditioning() {
        return air_conditioning;
    }

    public int getCount() {
        return count;
    }
}
