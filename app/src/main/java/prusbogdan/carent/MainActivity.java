package prusbogdan.carent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.provider.MediaStore;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import prusbogdan.carent.Classes.Banlist;
import prusbogdan.carent.Classes.CarModel;
import prusbogdan.carent.Classes.Categry;
import prusbogdan.carent.Classes.Order;
import prusbogdan.carent.Classes.User;
import prusbogdan.carent.Classes.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
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
            FrameLayout layout_carcatalog = findViewById(R.id.layout_carcatalog);
            FrameLayout layout_orders = findViewById(R.id.layout_orders);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setTitle("Home");
                    layout_account.setVisibility(View.INVISIBLE);
                    layout_orders.setVisibility(View.INVISIBLE);
                    layout_carcatalog.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_car_list:
                    setTitle("Car List");
                    layout_orders.setVisibility(View.INVISIBLE);
                    layout_account.setVisibility(View.INVISIBLE);
                    layout_carcatalog.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_booking:
                    setTitle("Booking");
                    layout_account.setVisibility(View.INVISIBLE);
                    layout_carcatalog.setVisibility(View.INVISIBLE);
                    layout_orders.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_account:
                    setTitle("Account");
                    layout_account.setVisibility(View.VISIBLE);
                    layout_orders.setVisibility(View.INVISIBLE);
                    layout_carcatalog.setVisibility(View.INVISIBLE);
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
        TextView verified = findViewById(R.id.text_verificated);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        String url = data.url;
        itsnew();
        check_user();
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.swiperefreshaccount);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                check_user();
                setAccountFields();
                pullToRefresh.setRefreshing(false);
            }
        });
        final SwipeRefreshLayout refleshcatalog = findViewById(R.id.refleshcatalog);
        refleshcatalog.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                check_user();
                reloadcatalog();
                refleshcatalog.setRefreshing(false);
            }
        });
        final SwipeRefreshLayout refleshorders = findViewById(R.id.refleshorders);
        refleshorders.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                check_user();
                reloadOrders();
                refleshorders.setRefreshing(false);
            }
        });
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
        reloadcatalog();
        reloadOrders();
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
        Call<UserInfo> call = api.userInfo(data.user.getId());
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    if(response.body()!=null)
                    {
                        data.userInfo = response.body();
                        new DownloadImageTask((ImageView) findViewById(R.id.userimage))
                                .execute(data.url+"/uploads/User/"+ data.userInfo.getPhoto_user());
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
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions,1);
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setPositiveButton(
                    R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Uri photoUri = data.getData();
                            String picturePath = getPath( context, photoUri);
                            uploadToServer(picturePath);
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
            alert.setMessage(R.string.wannachangeimage);
            alert.show();
        }
    }

    public static String getPath( Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
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

    private void uploadToServer(String filePath) {
        //ImageView image = findViewById(R.id.userimage);
        //image.setImageURI(Uri.parse(filePath));
        retrofit = new Retrofit.Builder()
                .baseUrl(data.url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
        //Create a file object using file path
        File file = new File(filePath);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("upfile", file.getName(), fileReqBody);
        //Create request body with text description and text media type
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        //
        Call call = api.uploadImage(part, description, data.user.getId());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful())
                {
                    setAccountFields();
                }
                else
                {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage(getString(R.string.errorconnection)+" "+response.message());
                    alert.show();
                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage(getString(R.string.errorconnection)+" "+t.getMessage());
                alert.show();
            }
        });
    }

    private void reloadcatalog()
    {
        LinearLayout catalog = findViewById(R.id.carcatalog);
        catalog.removeAllViews();
        Call<ArrayList<CarModel>> call = api.getCarModels();
        call.enqueue(new Callback<ArrayList<CarModel>>() {
            @Override
            public void onResponse(Call<ArrayList<CarModel>> call, Response<ArrayList<CarModel>> response) {
                if(!response.isSuccessful()){
                    error("Code: "+response.code());
                    return;
                }
                ArrayList<CarModel> cars = response.body();
                data.carModels = cars;
                for (CarModel car:cars){
                    buildrow(car);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CarModel>> call, Throwable t) {
                error(t.getMessage());
            }
        });
    }

    private void buildrow(final CarModel carmodel)
    {
        LinearLayout main = findViewById(R.id.carcatalog);
        int id = carmodel.getId();
        LinearLayout layout = new LinearLayout(this);
        layout.setId(10000+id);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,10,0,0);
        layout.setLayoutParams(layoutParams);
        layout.setBackgroundColor(this.getResources().getColor(R.color.backwhite));
            TextView name = new TextView(this);
            name.setText(carmodel.getName());
            name.setTextSize((float)25);
            name.setGravity(Gravity.CENTER);
            name.setTextColor(this.getResources().getColor(R.color.white));
            TextView price = new TextView(this);
            price.setText(getString(R.string.price)+": "+carmodel.getPrice()+getString(R.string.money));
            price.setTextSize((float)25);
            price.setGravity(Gravity.LEFT);
            price.setPadding(20,0,0,0);
            price.setTextColor(this.getResources().getColor(R.color.white));
                ImageView image = new ImageView(this);
                new DownloadImageTask(image)
                        .execute(data.url+"/uploads/CarModels/"+ carmodel.getPicture());
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.CurrentcarModel=carmodel;
                        savedata(context);
                        Intent intent = new Intent(context, CarModelViewActivit.class);
                        startActivity(intent);
                    }
                });
                final float scale = this.getResources().getDisplayMetrics().density;
                int pixels = (int) (240 * scale + 0.5f);
                image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, pixels));
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                image.setMaxWidth((int)(10 * scale + 0.5f));
        layout.addView(name);
        layout.addView(image);
        layout.addView(price);
        main.addView(layout);
    }

    private void getCategories()
    {
        Call<ArrayList<Categry>> call = api.getCategories();
        call.enqueue(new Callback<ArrayList<Categry>>() {
            @Override
            public void onResponse(Call<ArrayList<Categry>> call, Response<ArrayList<Categry>> response) {
                if(!response.isSuccessful()){
                    return;
                }
                ArrayList<Categry> categries = response.body();
                data.categories = categries;
                savedata(context);
            }

            @Override
            public void onFailure(Call<ArrayList<Categry>> call, Throwable t) {
            }
        });
    }

    private void reloadOrders()
    {
        LinearLayout orders = findViewById(R.id.orders);
        orders.removeAllViews();
        Call<ArrayList<Order>> call = api.getOrders(data.user.getId());
        call.enqueue(new Callback<ArrayList<Order>>() {
            @Override
            public void onResponse(Call<ArrayList<Order>> call, Response<ArrayList<Order>> response) {
                if(!response.isSuccessful()){
                    error("Code: "+response.code());
                    return;
                }
                FrameLayout main = findViewById(R.id.nullorder);
                if(response.body()!=null)
                {
                    ArrayList<Order> orders = response.body();
                    data.orders = orders;
                    for (Order order:orders){
                        buildorderrow(order);
                    }
                    main.setVisibility(View.INVISIBLE);
                }
                else
                {
                    main.setVisibility(View.VISIBLE);
                    Button button = findViewById(R.id.goorder);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BottomNavigationView bottomNavigationView;
                            bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_view);
                            bottomNavigationView.setSelectedItemId(R.id.navigation_car_list);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Order>> call, Throwable t) {
                error(t.getMessage());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void buildorderrow(final Order order)
    {
        LinearLayout main = findViewById(R.id.orders);
        int id = order.getId();
        LinearLayout layout = new LinearLayout(this);
        layout.setId(11000+id);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,10,0,0);
        layout.setLayoutParams(layoutParams);
        layout.setPadding(10,0,0,0);
        layout.setBackgroundColor(this.getResources().getColor(R.color.backwhite));

        TextView id_ = new TextView(this);
        id_.setText(getString(R.string.systemid)+" "+order.getId());
        id_.setTextSize((float)20);
        id_.setGravity(Gravity.LEFT);
        id_.setTextColor(this.getResources().getColor(R.color.white));

        TextView car = new TextView(this);
        car.setText(getString(R.string.car)+": "+order.returncar());
        car.setTextSize((float)20);
        car.setGravity(Gravity.LEFT);
        car.setTextColor(this.getResources().getColor(R.color.white));

        TextView datestart = new TextView(this);
        datestart.setText(getString(R.string.date_start)+" "+order.getDate());
        datestart.setTextSize((float)20);
        datestart.setGravity(Gravity.LEFT);
        datestart.setTextColor(this.getResources().getColor(R.color.white));

        TextView therm = new TextView(this);
        therm.setText(getString(R.string.therm)+" "+order.getTerm()+" "+getString(R.string.days));
        therm.setTextSize((float)20);
        therm.setGravity(Gravity.LEFT);
        therm.setTextColor(this.getResources().getColor(R.color.white));

        TextView price = new TextView(this);
        price.setText(getString(R.string.price)+": "+order.getPrice()+getString(R.string.money));
        price.setTextSize((float)20);
        price.setGravity(Gravity.LEFT);
        price.setTextColor(this.getResources().getColor(R.color.white));

        TextView status = new TextView(this);
        String stat="";
        status.setText(getString(R.string.status)+" "+stat);
        status.setTextSize((float)20);
        status.setGravity(Gravity.LEFT);
        switch (order.getActive()) {
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

        Button vieworder = new Button(this);
        vieworder.setText(R.string.viewbooking);
        LinearLayout.LayoutParams layoutParams11 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams11.setMargins(10, 10, 20, 10);
        vieworder.setLayoutParams(layoutParams11);
        vieworder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.currentorder = order;
                savedata(context);
                Intent intent = new Intent(context, ViewOrderActivity.class);
                startActivity(intent);
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.currentorder = order;
                savedata(context);
                Intent intent = new Intent(context, ViewOrderActivity.class);
                startActivity(intent);
            }
        });
        layout.addView(id_);
        layout.addView(car);
        layout.addView(datestart);
        layout.addView(therm);
        layout.addView(price);
        layout.addView(status);
        layout.addView(vieworder);
        main.addView(layout);
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

    private String returncar(int id)
    {
        for(int i=0; i<data.carModels.size(); i++)
        {
            if(data.carModels.get(i).getId()==id)
                return data.carModels.get(i).getName();
        }
        return "";
    }
}
