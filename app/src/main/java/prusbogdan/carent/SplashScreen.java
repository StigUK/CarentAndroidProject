package prusbogdan.carent;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import prusbogdan.carent.Adapters.ImageAdapter;
import prusbogdan.carent.Classes.User;

public class SplashScreen extends AppCompatActivity {

    public static Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new Data();
        if ( !isOnline() ){
            Toast.makeText(getApplicationContext(),
                    R.string.no_connection,Toast.LENGTH_LONG).show();
            return;
        }
        //Context context = this;
        //data.LoadUser(this);

        Context context = this;
        //savedata(context);
        if(data.LoadUser(this)){
            if(data.user!=null)
            {
                gotomain();
            }
            else{
                gotologin();
            }
        }
        else {
            gotologin();
        }
        finish();
    }
    protected boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else
            return true;
    }

    private void gotomain()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void gotologin()
    {
        Intent intent = new Intent(this, SliderActivity.class);
        startActivity(intent);
    }

    private void savedata(Context context)
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        try {
            FileOutputStream fos =  context.openFileOutput("data.out", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(data);
            os.close();
            fos.close();
        } catch (IOException e) {
            alert.setMessage(e.getMessage());
            alert.show();
            e.printStackTrace();
        }
    }

    private void loaddata(Context context)
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        try {
            FileInputStream fis = context.openFileInput("data.out");
            ObjectInputStream is = new ObjectInputStream(fis);
            data = (Data) is.readObject();
            is.close(); fis.close();
        } catch (IOException e) {
            alert.setMessage(e.getMessage());
            alert.show();
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            alert.setMessage(e.getMessage());
            alert.show();
        }
    }
}
