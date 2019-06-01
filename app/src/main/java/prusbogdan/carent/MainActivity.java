package prusbogdan.carent;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import prusbogdan.carent.Classes.CarModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private Window w = getWindow();
    private Retrofit retrofit;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setTitle("Home");
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_car_list:
                    setTitle("Car List");
                    mTextMessage.setText(R.string.title_car_list);
                    return true;
                case R.id.navigation_booking:
                    setTitle("Booking");
                    mTextMessage.setText(R.string.title_booking);
                    return true;
                case R.id.navigation_account:
                    setTitle("Account");
                    mTextMessage.setText(R.string.title_account);
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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.210")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api = retrofit.create(Api.class);
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
    }

}
