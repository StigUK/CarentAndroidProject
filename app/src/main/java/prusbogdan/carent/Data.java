package prusbogdan.carent;

import android.app.Activity;
import android.content.Context;
import android.service.autofill.SaveInfo;
import android.support.v7.app.AlertDialog;

import com.google.gson.annotations.SerializedName;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import prusbogdan.carent.Classes.Banlist;
import prusbogdan.carent.Classes.CarModel;
import prusbogdan.carent.Classes.Categry;
import prusbogdan.carent.Classes.Order;
import prusbogdan.carent.Classes.User;
import prusbogdan.carent.Classes.UserInfo;
import retrofit2.Callback;

public class Data implements Serializable {

    @SerializedName("itsnew")
    @Expose
    boolean itsnew;
    @SerializedName("user")
    @Expose
    User user;
    @SerializedName("currentcar")
    @Expose
    CarModel CurrentcarModel;
    @SerializedName("categories")
    @Expose
    public
    ArrayList<Categry> categories;
    @SerializedName("carmodels")
    @Expose
    public
    ArrayList<CarModel> carModels;
    @SerializedName("orders")
    @Expose
    ArrayList<Order> orders;
    @SerializedName("currentorder")
    @Expose
    Order currentorder;
    @SerializedName("url")
    @Expose
    String url = "http://192.168.1.210";
    @SerializedName("ban;")
    @Expose
    Banlist ban;
    @SerializedName("userInfo")
    @Expose
    UserInfo userInfo;
    public void SaveUser(User user_, Context context) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        try {
            FileOutputStream fos =  context.openFileOutput("user.out", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(user_);
            os.close();
            fos.close();
        } catch (IOException e) {
            alert.setMessage(e.getMessage());
            alert.show();
            e.printStackTrace();
        }
        user = user_;
    }
    public boolean LoadUser(Context context)
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        try {
            FileInputStream fis = context.openFileInput("user.out");
            ObjectInputStream is = new ObjectInputStream(fis);
            user = (User) is.readObject();
            is.close(); fis.close();
            return true;
        } catch (IOException e) {
            alert.setMessage(e.getMessage());
            alert.show();
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            alert.setMessage(e.getMessage());
            alert.show();
            return false;
        }
    }

    public void SaveInfo(UserInfo info)
    {
        userInfo = info;
    }

    public boolean destroy(Context context)
    {
        userInfo=null;
        ban=null;
        user=null;
        SaveUser(user, context);
        return true;
    }

}
