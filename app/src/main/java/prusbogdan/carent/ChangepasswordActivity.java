package prusbogdan.carent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import prusbogdan.carent.Classes.Banlist;
import prusbogdan.carent.Classes.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

public class ChangepasswordActivity extends AppCompatActivity {

    Data data;
    Context context = this;
    TextInputEditText lastpassword;
    TextInputEditText newpassword;
    TextInputEditText newpassowrdrepeat;
    Retrofit retrofit;
    Api api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        data = Edit_AccountActivity.data;
        lastpassword=findViewById(R.id.passchange_lastpass_edit);
        newpassword=findViewById(R.id.passchange_newpass_edit);
        newpassowrdrepeat=findViewById(R.id.passchange_newpassrepeat_edit);
        check_user();
        Button passchange_save = findViewById(R.id.passchange_save);
        passchange_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_user();
                if(checkfields())
                {
                    changepass();
                }

            }
        });
    }

    private void changepass()
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        String url = data.url;
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
        Call<Boolean> call = api.userChangepassword(data.user.getLogin(),  data.user.getPassword() , newpassowrdrepeat.getText().toString());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    if(response.body())
                    {
                        MainActivity.data.user.setPassword(newpassword.getText().toString());
                        data.user.setPassword(newpassowrdrepeat.getText().toString());
                        savedata(context);
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
                        alert.setMessage(R.string.changepass);
                        alert.show();
                    }
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
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
                alert.setMessage(R.string.errorrequest);
                alert.show();
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

    private boolean checkfields()
    {
        int errors=0;
        if(!(lastpassword.getText().toString().trim().length() > 0))
        {
            lastpassword.setError(getString(R.string.emptyfield));
            errors++;
        }
        else
        {
            if(!checkoldpass())
                errors++;
        }
        if(!(newpassword.getText().toString().trim().length() > 0))
        {
            newpassword.setError(getString(R.string.emptyfield));
            errors++;
        }
        if(!(newpassowrdrepeat.getText().toString().trim().length() > 0))
        {
            newpassowrdrepeat.setError(getString(R.string.emptyfield));
            errors++;
        }
        else
        {
            if(!checknewpass())
                errors++;
        }
        if(errors<1)
        {
            return true;
        }
        else
        return false;
    }

    private boolean checknewpass()
    {
        if(newpassword.getText().toString().equals(newpassowrdrepeat.getText().toString()))
        {
            return true;
        }
        else
        {
            newpassword.setError(getString(R.string.dnmatch));
            newpassowrdrepeat.setError(getString(R.string.dnmatch));
            return false;
        }
    }

    private boolean checkoldpass()
    {
        String oldpassword;
        oldpassword = lastpassword.getText().toString();
        if(oldpassword.equals(data.user.getPassword()))
        {
            return true;
        }
        else
        {
            lastpassword.setError(getString(R.string.incorrectpass));
            return false;
        }
    }
}
