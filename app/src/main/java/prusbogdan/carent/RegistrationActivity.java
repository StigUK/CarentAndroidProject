package prusbogdan.carent;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import prusbogdan.carent.Classes.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationActivity extends AppCompatActivity {

    public Data data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = LoginActivity.data;
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_registration);
        Button signup = findViewById(R.id.registration_signup);
        Button signin = findViewById(R.id.registration_signin);
        final TextView login = findViewById(R.id.signin_login_edit);
        final TextView email = findViewById(R.id.signin_email_edit);
        final TextView password = findViewById(R.id.signin_password_edit);
        final Context context = this;
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFields()) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(SplashScreen.data.url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    Api api = retrofit.create(Api.class);
                    Call<User> call = api.userRegistration(login.getText(), email.getText(), password.getText());
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    SplashScreen.data.user = response.body();
                                    SplashScreen.data.SaveUser(response.body(), context);
                                    alert.setMessage(R.string.successfuly_signup);
                                    alert.show();
                                    SplashScreen.data.itsnew=true;
                                    gotomain();
                                } else {
                                    alert.setMessage(R.string.accountcreateerror);
                                    alert.show();
                                }
                            } else {
                                alert.setMessage(R.string.errorconnection);
                                alert.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            alert.setMessage(R.string.errorconnection + "   " + t.getMessage());
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.wannaexit)
                .setTitle(R.string.exit);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        //this is only needed if you have specific things
        //that you want to do when the user presses the back button.
        /* your specific things...*/
        //super.onBackPressed();
    }

    private boolean checkFields()
    {
        TextView login = findViewById(R.id.signin_login_edit);
        TextView email = findViewById(R.id.signin_email_edit);
        TextView password = findViewById(R.id.signin_password_edit);
        if(!(login.getText().toString().trim().length() > 0))
        {
            login.setError(getString(R.string.emptyfield));
            return false;
        }
        if(!(email.getText().toString().trim().length() > 0))
        {
            email.setError(getString(R.string.emptyfield));
            return false;
        }
        if(!(password.getText().toString().trim().length() > 0))
        {
            password.setError(getString(R.string.emptyfield));
            return false;
        }
        return true;
    }
}
