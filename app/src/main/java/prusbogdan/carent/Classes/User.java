package prusbogdan.carent.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
public class User {
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("login")
    @Expose
    String login;
    @SerializedName("email")
    @Expose
    String email;
    @SerializedName("password")
    @Expose
    String password;
    @SerializedName("role")
    @Expose
    int role;
    @SerializedName("active")
    @Expose
    int active;

    public void setLogin(String login) {
        this.login = login;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getRole() {
        return role;
    }

    public int getActive() {
        return active;
    }
}
