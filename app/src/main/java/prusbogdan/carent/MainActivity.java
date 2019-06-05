package prusbogdan.carent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import prusbogdan.carent.Classes.Banlist;
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
    public static Data data;
    Context context = this;
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
        loaddata();
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        TextView verified = findViewById(R.id.text_verificated);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        String url = data.url;
        itsnew();
        check_user();
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
        verified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setPositiveButton(
                        R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                alert.setMessage(R.string.verified_status_info);
                alert.show();
            }
        });
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
        Button logout = findViewById(R.id.bt_logout);
        Button editaccount = findViewById(R.id.bt_edit_account);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.destroy(context))
                {
                    savedata(context);
                    Intent intent = new Intent(context, SliderActivity.class);
                    startActivity(intent);
                }
            }
        });
        editaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedata(context);
                Intent intent = new Intent(context, Edit_AccountActivity.class);
                startActivity(intent);
            }
        });
    }

    private void reloadfields()
    {

    }

    private void check_user()
    {
        String url = data.url;
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

        Call<User> call1 = api.userCheck(data.user.getLogin(), data.user.getPassword());
        call1.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call1, Response<User> response) {
                if (response.isSuccessful()) {
                    if(response.body()==null)
                    {
                        data.user = null;
                        data.SaveUser(null,context);
                        SplashScreen.data = null;
                        kick();
                    }
                    else
                    {
                        data.user = response.body();
                        data.SaveUser(data.user, context);
                        savedata(context);
                    }
                }
            }
            @Override
            public void onFailure(Call<User> call1, Throwable t) {
            }
        });

        Call<Banlist> call = api.getBan(data.user.getId());
        call.enqueue(new Callback<Banlist>() {
            @Override
            public void onResponse(Call<Banlist> call, Response<Banlist> response) {
                if(!response.isSuccessful()){
                    //Offline mode
                    return;
                }
                if(response.body()!=null)
                {
                    data.ban = response.body();
                    savedata(context);
                    Intent intent = new Intent(context, BanActivity.class);
                    startActivity(intent);
                }
                else return;
            }

            @Override
            public void onFailure(Call<Banlist> call, Throwable t) {
                mTextMessage.setText(t.getMessage());
            }
        });
    }


    private void setAccountFields()
    {
        //Fiels
        TextView login = findViewById(R.id.ac_field_login);
        TextView email = findViewById(R.id.ac_field_email);
        TextView verified = findViewById(R.id.text_verificated);
        final TextView firstname = findViewById(R.id.ac_field_firstname);
        final TextView secondname = findViewById(R.id.ac_field_secondname);
        final TextView middlename = findViewById(R.id.ac_field_middlename);
        final TextView phonenumber = findViewById(R.id.ac_field_phonenumber);
        final TextView licenseid = findViewById(R.id.ac_field_licenseid);
        final TextView licensedate = findViewById(R.id.ac_field_licensedate);
        ImageView image = findViewById(R.id.userimage);
        ///---///
        login.setText(data.user.getLogin());
        email.setText(data.user.getEmail());
        if(data.user.getActive()==1)
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
        Call<UserInfo> call = api.userInfo(data.user.getLogin());
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    if(response.body()!=null)
                    {
                        data.userInfo = response.body();
                        new DownloadImageTask((ImageView) findViewById(R.id.userimage))
                                .execute("http://192.168.1.210/uploads/User/"+ data.userInfo.getPhoto_user());
                        firstname.setText(data.userInfo.getFirst_name());
                        secondname.setText(data.userInfo.getSecond_name());
                        middlename.setText(data.userInfo.getMiddle_name());
                        phonenumber.setText(data.userInfo.getPhone_number());
                        licensedate.setText(data.userInfo.getLicense_date());
                        licenseid.setText(data.userInfo.getLicense_id());
                    }
                }
            }
            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
            }
        });


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

            BitmapDrawable ob = new BitmapDrawable(getResources(), result);
            bmImage.setBackground(ob);
            //bmImage.setImageBitmap(result);
        }
    }

    private void itsnew()
    {
        if(data.itsnew)
        {
            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setPositiveButton(
                    R.string.letsgo,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            data.itsnew=false;
                            BottomNavigationView bottomNavigationView;
                            bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_view);
                            bottomNavigationView.setSelectedItemId(R.id.navigation_account);
                        }
                    });
            alert.setMessage(R.string.congratulations);
            alert.show();
        }
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

    private void loaddata()
    {
        /*final AlertDialog.Builder alert = new AlertDialog.Builder(context);
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
        }*/
        data = SplashScreen.data;
    }

    private void kick()
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setCancelable(false);
        alert.setPositiveButton(
                R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(context, SliderActivity.class);
                        startActivity(intent);
                        dialog.cancel();
                    }
                });
        alert.setMessage(R.string.changepass);
        alert.show();
    }
}
