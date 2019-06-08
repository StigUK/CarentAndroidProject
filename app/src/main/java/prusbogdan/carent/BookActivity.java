package prusbogdan.carent;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

import prusbogdan.carent.Classes.Banlist;
import prusbogdan.carent.Classes.Order;
import prusbogdan.carent.Classes.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    Context context;
    TextInputEditText startdate;
    SeekBar termseek;
    Data data;
    int term;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.book);
        data = CarModelViewActivit.data;
        context = this;
        setContentView(R.layout.activity_book);
        startdate = findViewById(R.id.book_startdate_edit);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog datePickerDialog = new DatePickerDialog(context, this, year, month, day);
        long now = System.currentTimeMillis() - 1000;
        datePickerDialog.getDatePicker().setMinDate(now);
        datePickerDialog.getDatePicker().setMaxDate(now+(1000*60*60*24*14));
        startdate.setFocusable(false);
        final TextView price = findViewById(R.id.book_price);
        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        termseek = findViewById(R.id.book_seekterm);
        final TextView termtext = findViewById(R.id.book_term_text);
        termseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                term = progress+1;
                if(progress==0)
                    termtext.setText(getString(R.string.therm)+" "+term+" "+getString(R.string.day));
                else
                    termtext.setText(getString(R.string.therm)+" "+term+" "+getString(R.string.days));
                price.setText(data.CurrentcarModel.getPrice()*(progress+1)+getString(R.string.money));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        TextView carname = findViewById(R.id.book_carname);
        carname.setText(data.CurrentcarModel.getName());
        ImageView carimage = findViewById(R.id.book_carimage);
        new DownloadImageTask(carimage)
                .execute(data.url+"/uploads/CarModels/"+ data.CurrentcarModel.getPicture());
        Button book = findViewById(R.id.book_book);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((startdate.getText().toString().trim().length() > 0))
                {
                    check_user();
                    book();
                }
                else
                    startdate.setError(getString(R.string.emptyfield));
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if(month<9)
            startdate.setText(year+"-0"+(month+1)+"-"+dayOfMonth);
        else
            startdate.setText(year+"-"+(month+1)+"-"+dayOfMonth);
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

    private void book()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(data.url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api = retrofit.create(Api.class);
        Call<Order> call = api.createOrder(data.user.getId(), data.CurrentcarModel.getId(), startdate.getText(), term);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call1, Response<Order> response) {
                if (response.isSuccessful()) {
                    if(response.body()!=null)
                    {
                        data.currentorder = response.body();
                    }
                    else
                    {
                       error(getString(R.string.ordererror));
                    }
                }
            }
            @Override
            public void onFailure(Call<Order> call1, Throwable t) {
            }
        });
        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.cat_happy);
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setCancelable(false);
        alert.setPositiveButton(
                R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(context, ViewOrderActivity.class);
                        startActivity(intent);
                        dialog.cancel();
                    }
                });
        alert.setView(image);
        alert.setMessage(R.string.successorder);
        alert.show();
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
        alert.setMessage(getString(R.string.errorconnection) +" "+text);
        alert.show();
    }

    private void check_user()
    {
        String url = data.url;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api = retrofit.create(Api.class);

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

}
