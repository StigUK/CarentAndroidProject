package prusbogdan.carent;

import android.text.Editable;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import prusbogdan.carent.Classes.Banlist;
import prusbogdan.carent.Classes.CarModel;

import java.util.ArrayList;

import prusbogdan.carent.Classes.Categry;
import prusbogdan.carent.Classes.Order;
import prusbogdan.carent.Classes.User;
import prusbogdan.carent.Classes.UserInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
public interface Api {


    @GET("api/user/signin?")
    Call<User> userLogin(@Query("login") Editable login, @Query("password") Editable pass);

    @GET("api/user/changepassword?")
    Call<Boolean> userChangepassword(@Query("login") String login, @Query("oldpassword") String oldpass,   @Query("newpassword") String newpass);

    @GET("api/user/check?")
    Call<User> userCheck(@Query("login") String login, @Query("password") String pass);

    @GET("api/user/info?")
    Call<UserInfo> userInfo(@Query("id") int id);

    @GET("api/user/signup?")
    Call<User> userRegistration(@Query("login") CharSequence login, @Query("email") CharSequence email, @Query("password") CharSequence pass);

    @GET("api/user/setinfo?")
    Call<Boolean> userUpdateinfo(@Query("userid") Integer userid, @Query("email") CharSequence email,  @Query("firstname") CharSequence firstname, @Query("secondname") CharSequence secondname, @Query("middlename") CharSequence middlename, @Query("phonenumber") CharSequence phonenumber, @Query("idlicense") CharSequence idlicense, @Query("datelicense") CharSequence datelicense);

    @Multipart
    @POST("api/user/uploadphoto")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file, @Part("upfile") RequestBody requestBody, @Query("userid") int userid);
    //BanList

    @GET("api/banlist/getban?")
    Call<Banlist> getBan(@Query("user_id") int user_id);

    //carmodels

    @Headers("Accept: application/json")
    @GET("api/carmodel/getCarmodel")
    Call<CarModel> getCarModel(@Query("id") Integer id);

    @Headers("Accept: application/json")
    @GET("api/carmodel/view")
    Call<ArrayList<CarModel>> getCarModels();

    //orders

    @Headers("Accept: application/json")
    @GET("api/order/showall")
    Call<ArrayList<Order>> getOrders(@Query("userid") Integer userid);

    @Headers("Accept: application/json")
    @GET("api/order/showone")
    Call<Order> getOrder(@Query("id") Integer id);

    @Headers("Accept: application/json")
    @GET("api/order/setstatus")
    Call<Order> setStatus(@Query("id") Integer id, @Query("status") Integer status);

    @Headers("Accept: application/json")
    @GET("api/order/create")
    Call<Order> createOrder(@Query("user_id") Integer user_id, @Query("car_id") Integer car_id, @Query("date") CharSequence date,  @Query("term") Integer term);

    //category
    @Headers("Accept: application/json")
    @GET("api/category/categorylist")
    Call<ArrayList<Categry>> getCategories();
}
