package prusbogdan.carent;

import android.app.Activity;
import android.content.Context;
import android.service.autofill.SaveInfo;
import android.support.v7.app.AlertDialog;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import prusbogdan.carent.Classes.User;
import prusbogdan.carent.Classes.UserInfo;
import retrofit2.Callback;

public class Data {
    User user;
    String url = "http://192.168.1.210";
    UserInfo userInfo;
    public void SaveUser(User user_, Context context) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        try {
            FileOutputStream fos =  context.openFileOutput("user.out", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(user_);
            os.close();
            fos.close();
            alert.show();
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
            alert.show();
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

}
