package prusbogdan.carent;

import android.text.Editable;

import prusbogdan.carent.Classes.CarModel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import prusbogdan.carent.Classes.User;
import prusbogdan.carent.Classes.UserInfo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface Api {


    @GET("api/user/signin?")
    Call<User> userLogin(@Query("login") Editable login, @Query("password") Editable pass);

    @GET("api/user/info?")
    Call<UserInfo> userInfo(@Query("login") String login);

    @Headers("Accept: application/json")
    @GET("api/carmodel/view")
    Call<ArrayList<CarModel>> getCarModels();
}
