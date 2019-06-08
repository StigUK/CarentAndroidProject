package prusbogdan.carent;

import android.annotation.SuppressLint;
import android.content.Context;
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
import java.util.ArrayList;

import prusbogdan.carent.Classes.CarModel;
import prusbogdan.carent.Classes.Categry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarModelViewActivit extends AppCompatActivity {

    Context context;
    public static Data data;
    Api api;
    Retrofit retrofit;
    String category="";
    Button book;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_car_model_view);
        data = MainActivity.data;
        book = findViewById(R.id.carviewbuy);
        if(getCategories())loadfields();
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedata(context);
                Intent intent = new Intent(context, BookActivity.class);
                startActivity(intent);
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

    @SuppressLint("SetTextI18n")
    private void loadfields(){
        //getCarmodel(data.CurrentcarModel.getId());
        TextView name = findViewById(R.id.carviewname);
        TextView price = findViewById(R.id.carviewprice);
        TextView engine = findViewById(R.id.carviewengine_text);
        TextView transmission = findViewById(R.id.carviewgearbox_text);
        TextView condition = findViewById(R.id.carviewcondition_text);
        TextView carbody = findViewById(R.id.carviewcarbody_text);
        TextView seats = findViewById(R.id.carviewseats_text);
        TextView fuel = findViewById(R.id.carviewfuel_text);
        ImageView image = findViewById(R.id.carviewimage);
        new DownloadImageTask(image)
                .execute(data.url+"/uploads/CarModels/"+ data.CurrentcarModel.getPicture());
        price.setText(getString(R.string.price)+": "+data.CurrentcarModel.getPrice()+getString(R.string.money));
        engine.setText(data.CurrentcarModel.getEngine_type()+", "+data.CurrentcarModel.getEngine_volume());
        fuel.setText(data.CurrentcarModel.getFuel_consumption()+" "+getString(R.string.fuel_capacity));
        transmission.setText(data.CurrentcarModel.getTransmission());
        if(data.CurrentcarModel.getAir_conditioning()==1) condition.setText(getString(R.string.withcondition));
        else condition.setText("-");
        carbody.setText(data.CurrentcarModel.getBody());
        seats.setText(""+data.CurrentcarModel.getSeats());
        if(data.CurrentcarModel.getCount()==0)
        {
            book.setText(R.string.notavailable);
            book.setEnabled(false);
        }
        //String category = data.categories.get(2).getName();
        //final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        //alert.setMessage("саксесфул   "+data.categories.get(1).getName());
        name.setText(category+""+data.CurrentcarModel.getName());
        //name.setText(data.CurrentcarModel.getCategoryString()+"- "+data.CurrentcarModel.getName());
    }

    private String getCategorybyid(int id)
    {
        for(int i=0; i<data.categories.size(); i++)
        {
            if(data.categories.get(i).getId()==id)
            {
                return data.categories.get(i).getName();
            }
        }
        return null;
    }

    private boolean getCategories()
    {
        final boolean[] errors = {false};
        retrofit = new Retrofit.Builder()
            .baseUrl(data.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        api = retrofit.create(Api.class);
        Call<ArrayList<Categry>> call = api.getCategories();
        call.enqueue(new Callback<ArrayList<Categry>>() {
            @Override
            public void onResponse(Call<ArrayList<Categry>> call, Response<ArrayList<Categry>> response) {
                if(!response.isSuccessful()){
                    errors[0] =true;
                    return;
                }
                //final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                ArrayList<Categry> categries = response.body();
                data.categories = categries;
                category = data.categories.get(1).getName();
                //alert.setMessage("саксесфул   "+data.categories.get(1).getName());
                savedata(context);
                //alert.show();
            }

            @Override
            public void onFailure(Call<ArrayList<Categry>> call, Throwable t) {
                errors[0] =true;
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage(t.getMessage());
                alert.show();
            }
        });
        return !errors[0];
    }

    private void getCarmodel(int id)
    {
        retrofit = new Retrofit.Builder()
                .baseUrl(data.url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
        Call<CarModel> call = api.getCarModel(id);
        call.enqueue(new Callback<CarModel>() {
            @Override
            public void onResponse(Call<CarModel> call, Response<CarModel> response) {
                if(!response.isSuccessful()){
                    return;
                }
                CarModel carModel = response.body();
                data.CurrentcarModel = carModel;
                savedata(context);
            }

            @Override
            public void onFailure(Call<CarModel> call, Throwable t) {
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
            bmImage.setImageBitmap(result);
            //bmImage.setImageBitmap(result);
        }
    }
}
