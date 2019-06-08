package prusbogdan.carent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import prusbogdan.carent.Classes.Banlist;
import prusbogdan.carent.Classes.Order;
import prusbogdan.carent.Classes.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewOrderActivity extends AppCompatActivity {

    Data data;
    Retrofit retrofit;
    Api api;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.viewbooking);
        setContentView(R.layout.activity_view_order);
        data = MainActivity.data;
        setFields();
        Button cancel = findViewById(R.id.vieworder_cancel);
        Button pay = findViewById(R.id.vieworder_pay);
        context = this;

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView image = new ImageView(context);
                image.setImageResource(R.drawable.cat_wow);
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setPositiveButton(
                        R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                switch (data.currentorder.getActive())
                                {
                                    case 0:{
                                        setstatus(3);
                                        ImageView image = new ImageView(context);
                                        image.setImageResource(R.drawable.cat_verysad);
                                        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                        alert.setCancelable(false);
                                        alert.setPositiveButton(
                                                R.string.ok,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Intent intent = new Intent(context, MainActivity.class);
                                                        startActivity(intent);
                                                        dialog.cancel();
                                                    }
                                                });
                                        alert.setView(image);
                                        alert.setMessage(R.string.successcancel);
                                        alert.show();
                                        break;
                                    }
                                    case 1:
                                    {
                                        error1(getString(R.string.cancelcomplited));
                                        break;
                                    }
                                    case 2:
                                    {
                                        error1(getString(R.string.cancelpaid));
                                        break;
                                    }
                                    case 3:
                                    {
                                        error1(getString(R.string.wascanceled));
                                        break;
                                    }
                                    case 4:{
                                        error1(getString(R.string.cancelactive));
                                        break;
                                    }
                                }
                                dialog.cancel();
                            }
                        });
                alert.setNegativeButton(
                        R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                alert.setView(image);
                alert.setMessage(R.string.questcancel);
                alert.show();
            }
        });
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_user();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setFields()
    {
        ImageView image = findViewById(R.id.orderview_carimage);
        TextView id = findViewById(R.id.orderview_id);
        TextView carname = findViewById(R.id.orderview_carname);
        TextView price = findViewById(R.id.orderview_price);
        TextView status = findViewById(R.id.orderview_status);
        TextView datestart = findViewById(R.id.orderview_datestart);
        TextView term = findViewById(R.id.orderview_term);
        id.setText(getString(R.string.systemid)+" "+data.currentorder.getId());
        carname.setText(getString(R.string.car)+": "+data.currentorder.returncar());
        datestart.setText(getString(R.string.date_start)+" "+data.currentorder.getDate());
        term.setText(getString(R.string.therm)+" "+data.currentorder.getTerm()+" "+getString(R.string.days));
        price.setText(getString(R.string.price)+": "+data.currentorder.getPrice()+getString(R.string.money));
        switch (data.currentorder.getActive()) {
            case 0:{
                status.setTextColor(this.getResources().getColor(R.color.yellow));
                status.setText(getString(R.string.status)+" "+getString(R.string.payexpected));
                break;
            }
            case 1:{
                status.setTextColor(this.getResources().getColor(R.color.white));
                status.setText(getString(R.string.status)+" "+getString(R.string.completed));
                break;
            }
            case 2: {
                status.setTextColor(this.getResources().getColor(R.color.positive));
                status.setText(getString(R.string.status)+" "+getString(R.string.paid));
                break;
            }
            case 3: {
                status.setTextColor(this.getResources().getColor(R.color.negative));
                status.setText(getString(R.string.status)+" "+getString(R.string.canceled));
                break;
            }
            case 4: {
                status.setTextColor(this.getResources().getColor(R.color.blue));
                status.setText(getString(R.string.status)+" "+getString(R.string.active));
                break;
            }
        }

        new DownloadImageTask(image)
                .execute(data.url+"/uploads/CarModels/"+ data.currentorder.returncarimage());
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
            //bmImage.setImageBitmap(result);
        }
    }

    private void setstatus(int status)
    {
        retrofit = new Retrofit.Builder()
                .baseUrl(data.url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
        Call<Order> call = api.setStatus(data.currentorder.getId(), status);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    if(response.body()!=null)
                    {
                        data.currentorder = response.body();
                    }
                    else
                    {
                        error(response.message());
                    }
                }
            }
            @Override
            public void onFailure(Call<Order> call, Throwable t) {
            }
        });
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
                error(t.getMessage());
            }
        });
    }
    private void error(String text)
    {
        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.cat_error);
        image.setMaxHeight(10);
        image.setMaxWidth(10);
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setPositiveButton(
                R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert.setView(image);
        alert.setMessage("Error: "+text);
        alert.show();
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
    private void error1(String text)
    {
        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.cat_er2);
        image.setMaxHeight(10);
        image.setMaxWidth(10);
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setPositiveButton(
                R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert.setView(image);
        alert.setMessage(text);
        alert.show();
    }
}