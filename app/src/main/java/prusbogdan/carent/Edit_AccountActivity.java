package prusbogdan.carent;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Date;
import java.util.Calendar;

import prusbogdan.carent.Classes.Banlist;
import prusbogdan.carent.Classes.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Edit_AccountActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    TextInputEditText email;
    TextInputEditText phonenumber;
    TextInputEditText firstname;
    TextInputEditText secondname;
    TextInputEditText middlename;
    TextInputEditText licenseid;
    TextInputEditText licensedate;
    Retrofit retrofit;
    Api api;
    Data data;
    Context context = this;
    DatePickerDialog datePickerDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = MainActivity.data;
        setContentView(R.layout.activity_edit__account);
        setTitle(R.string.editaccount);
        email = findViewById(R.id.account_edit_email_edit);
        phonenumber = findViewById(R.id.account_edit_phonenumber_edit);
        firstname = findViewById(R.id.account_edit_firstname_edit);
        secondname = findViewById(R.id.account_edit_secondname_edit);
        middlename = findViewById(R.id.account_edit_middlename_edit);
        licenseid = findViewById(R.id.account_edit_licenseid_edit);
        licensedate = findViewById(R.id.account_edit_licensedate_edit);
        refleshfields();
        Button changepassword = findViewById(R.id.account_edit_changepassword);
        Button cancelchanges = findViewById(R.id.account_edit_cancel);
        Button savechanges = findViewById(R.id.account_edit_save);
        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        cancelchanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refleshfields();
            }
        });
        savechanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setCancelable(false);
                alert.setPositiveButton(
                        R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                check_user();
                                savefields();
                                dialog.cancel();
                            }
                        });
                alert.setNegativeButton(
                        R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                refleshfields();
                                dialog.cancel();
                            }
                        });
                alert.setMessage(R.string.savechanges);
                alert.show();
            }
        });
    }

    private void refleshfields()
    {
        email.setText(data.user.getEmail());
        firstname.setText(data.userInfo.getFirst_name());
        secondname.setText(data.userInfo.getSecond_name());
        middlename.setText(data.userInfo.getMiddle_name());
        phonenumber.setText(data.userInfo.getPhone_number());
        licenseid.setText(data.userInfo.getLicense_id());
        licensedate.setText(data.userInfo.getLicense_date());
        //String [] dateParts = data.userInfo.getLicense_date().split("-");
        //int day = Integer.parseInt(dateParts[0]);
        //int month = Integer.parseInt(dateParts[1]);
        //int year = Integer.parseInt(dateParts[2]);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(context, this, year, month, day);
        licensedate.setFocusable(false);
        licensedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if(month<9)
        licensedate.setText(year+"-0"+(month+1)+"-"+dayOfMonth);
        else
            licensedate.setText(year+"-"+(month+1)+"-"+dayOfMonth);
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

            }
        });
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

    private void savefields()
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        String url = data.url;
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

        Call<Boolean> call = api.userUpdateinfo(data.user.getId(), email.getText(), firstname.getText(), secondname.getText(), middlename.getText(), phonenumber.getText(), licenseid.getText(), licensedate.getText());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    if(response.body())
                    {
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
                        alert.setMessage(R.string.changeinfo);
                        alert.show();
                    }
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }
}
