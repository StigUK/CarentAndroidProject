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

import prusbogdan.carent.Adapters.ImageAdapter;

public class SplashScreen extends AppCompatActivity {

    public static Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1500);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();*/

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
}
