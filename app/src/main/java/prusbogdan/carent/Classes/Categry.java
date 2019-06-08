package prusbogdan.carent.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Categry implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("experience")
    @Expose
    private int experience;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getExperience() {
        return experience;
    }

    public Categry(int id, String name, int experience) {
        this.id = id;
        this.name = name;
        this.experience = experience;
    }
}
