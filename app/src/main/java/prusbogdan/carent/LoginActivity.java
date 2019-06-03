package prusbogdan.carent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import prusbogdan.carent.Classes.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this;
        setContentView(R.layout.activity_login);
        Button createaccount = findViewById(R.id.login_create);
        final TextInputEditText login = findViewById(R.id.signin_login_edit);
        final TextInputEditText password = findViewById(R.id.signin_password_edit);
        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
                    String url = SplashScreen.data.url;
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
                                    SplashScreen.data.user = response.body();
                                    //SplashScreen.data.SaveUser(response.body(),context);
                                    alert.setMessage(R.string.successfuly_login);
                                    alert.show();
                                    gotomain();
                                }
                                else
                                {
                                    //login.setError(getString(R.string.incorrect));
                                    //password.setError(getString(R.string.incorrect));
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
    /*public void logIn(""){
        String hash_pass = CryptWithMD5.cryptWithMD5(pass);
        final Call<Client> client = MainActivity.serverApi.getOneClient(carNumb, hash_pass);
        client.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(Call<Client> call, Response<Client> response) {
                if (response.isSuccessful()) {
                    if(response.body()!=null)
                    {
                        saveUserData(response.body());
                        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                        startActivity(intent);
                    }
                    else {
                        incorrectData();
                        showToast("Не правильний номер чи пароль!");
                    }
                }
                else showToast("Немає з'єднання із сервером!");
            }
            @Override
            public void onFailure(Call<Client> call, Throwable t) {
                tv.setText("failure " + t);
            }
        });
    }*/

}
