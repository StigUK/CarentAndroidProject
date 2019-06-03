package prusbogdan.carent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

import prusbogdan.carent.Classes.CarModel;
import prusbogdan.carent.Classes.User;
import prusbogdan.carent.Classes.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private Window w = getWindow();
    private User user;
    Retrofit retrofit;
    Api api;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FrameLayout layout_account = findViewById(R.id.layout_account);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setTitle("Home");
                    mTextMessage.setText(R.string.title_home);
                    layout_account.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_car_list:
                    setTitle("Car List");
                    mTextMessage.setText(R.string.title_car_list);
                    layout_account.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_booking:
                    setTitle("Booking");
                    mTextMessage.setText(R.string.title_booking);
                    layout_account.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_account:
                    setTitle("Account");
                    mTextMessage.setText(R.string.title_account);
                    layout_account.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        String url = SplashScreen.data.url;
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
        Call<ArrayList<CarModel>> call = api.getCarModels();
        call.enqueue(new Callback<ArrayList<CarModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CarModel>> call, Response<ArrayList<CarModel>> response) {
                if(!response.isSuccessful()){
                    mTextMessage.setText("Code: "+ response.code());
                    return;
                }
                ArrayList<CarModel> cars = response.body();

                mTextMessage.setText("");
                for (CarModel car:cars){
                    String content ="";
                    content+="ID: " + car.getId()+"\n";

                    mTextMessage.append(content);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CarModel>> call, Throwable t) {
                mTextMessage.setText(t.getMessage());
            }
        });
        setAccountFields();
    }

    private void setAccountFields()
    {
        TextView login = findViewById(R.id.ac_field_login);
        TextView email = findViewById(R.id.ac_field_email);
        TextView verified = findViewById(R.id.text_verificated);
        ImageView image = findViewById(R.id.userimage);
        login.setText(SplashScreen.data.user.getLogin());
        email.setText(SplashScreen.data.user.getEmail());
        final UserInfo[] userInfo = new UserInfo[1];
        if(SplashScreen.data.user.getActive()==1)
        {
            verified.setText(R.string.verificated);
            verified.setTextColor(Color.GREEN);
        }
        else
        {
            verified.setText(R.string.noverificated);
            verified.setTextColor(Color.RED);
        }
        final Context context = this;
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        Call<UserInfo> call = api.userInfo(SplashScreen.data.user.getLogin());
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    if(response.body()!=null)
                    {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        userInfo[0] = response.body();
                        alert.setMessage(userInfo[0].getMiddle_name());
                        new DownloadImageTask((ImageView) findViewById(R.id.userimage))
                                .execute("http://192.168.1.210/uploads/User/"+ userInfo[0].getPhoto_user());
                        alert.show();
                    }
                }
            }
            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
            }
        });
        alert.show();


    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
