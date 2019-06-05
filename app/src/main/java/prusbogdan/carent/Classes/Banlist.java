package prusbogdan.carent.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
public class Banlist implements Serializable{
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("user")
    @Expose
    int user;
    @SerializedName("date_from")
    @Expose
    String date_from;
    @SerializedName("date_to")
    @Expose
    String date_to;
    @SerializedName("reason")
    @Expose
    String reason;
    @SerializedName("active")
    @Expose
    int active;

    public int getId() {
        return id;
    }

    public int getUser() {
        return user;
    }

    public String getDate_from() {
        return date_from;
    }

    public String getDate_to() {
        return date_to;
    }

    public String getReason() {
        return reason;
    }

    public int getActive() {
        return active;
    }
}
