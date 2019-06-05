package prusbogdan.carent;

import android.text.Editable;

import prusbogdan.carent.Classes.Banlist;
import prusbogdan.carent.Classes.CarModel;

import java.util.ArrayList;

import prusbogdan.carent.Classes.User;
import prusbogdan.carent.Classes.UserInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
public interface Api {


    @GET("api/user/signin?")
    Call<User> userLogin(@Query("login") Editable login, @Query("password") Editable pass);

    @GET("api/user/check?")
    Call<User> userCheck(@Query("login") String login, @Query("password") String pass);

    @GET("api/user/info?")
    Call<UserInfo> userInfo(@Query("login") String login);

    @GET("api/user/signup?")
    Call<User> userRegistration(@Query("login") CharSequence login, @Query("email") CharSequence email, @Query("password") CharSequence pass);

    @GET("api/user/setinfo?")
    Call<Boolean> userUpdateinfo(@Query("userid") Integer userid, @Query("email") CharSequence email,  @Query("firstname") CharSequence firstname, @Query("secondname") CharSequence secondname, @Query("middlename") CharSequence middlename, @Query("phonenumber") CharSequence phonenumber, @Query("idlicense") CharSequence idlicense, @Query("datelicense") CharSequence datelicense);


    //BanList

    @GET("api/banlist/getban?")
    Call<Banlist> getBan(@Query("user_id") int user_id);

    @Headers("Accept: application/json")
    @GET("api/carmodel/view")
    Call<ArrayList<CarModel>> getCarModels();
}
