package prusbogdan.carent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import prusbogdan.carent.Classes.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {


    public static Data data;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = SplashScreen.data;
        //loaddata(context);
        setTitle(R.string.login);
        setContentView(R.layout.activity_login);
        Button createaccount = findViewById(R.id.login_create);
        final TextInputEditText login = findViewById(R.id.signin_login_edit);
        final TextInputEditText password = findViewById(R.id.signin_password_edit);
        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedata(context);
                Intent intent = new Intent(context, RegistrationActivity.class);
                startActivity(intent);
            }
        });
        Button signin = findViewById(R.id.login_signin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields())
                {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setPositiveButton(
                            R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    String url = data.url;
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    Api api = retrofit.create(Api.class);
                    Call<User> call = api.userLogin(login.getText(), password.getText());
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful()) {
                                if(response.body()!=null)
                                {
                                    data.user = response.body();
                                    data.SaveUser(response.body(),context);
                                    alert.setMessage(R.string.successfuly_login);
                                    alert.show();
                                    gotomain();
                                }
                                else
                                {
                                    alert.setMessage(R.string.incorrect);
                                    alert.show();
                                }
                            }
                            else {
                                alert.setMessage(R.string.errorconnection);
                                alert.show();
                            }
                        }
                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            alert.setMessage(R.string.errorconnection+"   "+t.getMessage());
                            alert.show();
                        }
                    });
                }
            }
        });
    }



    private void gotomain()
    {
        savedata(context);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean checkFields()
    {
        final TextInputEditText login = findViewById(R.id.signin_login_edit);
        final TextInputEditText password = findViewById(R.id.signin_password_edit);
        if(!(login.getText().toString().trim().length() > 0))
        {
            login.setError(getString(R.string.emptyfield));
            return false;
        }
        if(!(password.getText().toString().trim().length() > 0))
        {
            password.setError(getString(R.string.emptyfield));
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
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
