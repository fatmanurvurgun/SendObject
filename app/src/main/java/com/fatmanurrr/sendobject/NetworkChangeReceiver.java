package com.fatmanurrr.sendobject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.fatmanurrr.sendobject.activity.MainActivity;
import com.fatmanurrr.sendobject.helper.MyHelperService;
import com.fatmanurrr.sendobject.imageUpload.UploadImageInterface;
import com.fatmanurrr.sendobject.imageUpload.UploadObject;
import com.fatmanurrr.sendobject.item.Message;
import com.fatmanurrr.sendobject.item.MessageRO;
import com.fatmanurrr.sendobject.item.PostResponse;
import com.fatmanurrr.sendobject.service.RetrofitFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Lenovo on 11.09.2019.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    MyHelperService myServiceHelper;
    static boolean isConnected = false;
    Context context;
    private Realm realm = Realm.getDefaultInstance();



    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        isNetworkAvailable(context);

        if (isConnected) {
            getMessagesByStatus();
        }

        myServiceHelper=MyHelperService.getMyHelperService();
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            isConnected = true;
            Toast.makeText(context, "internete Bağlandınız!", Toast.LENGTH_LONG).show();
            return true;
        } else {
            isConnected = false;
            Toast.makeText(context, "İnternet Yok", Toast.LENGTH_LONG).show();
            return false;
        }
    }


    public void sendMessagesService(final Message message) {
        RetrofitFactory.RetrofitServices retrofitService = RetrofitFactory.getService();
        Map<String,String> map = new HashMap<>();
        map.put("message_body",message.getMessage_body()+"");
        map.put("image_path",message.getImage_path()+"");
        Call<PostResponse> call = retrofitService.addMessages(map);
        call.enqueue(new Callback<PostResponse>() {
                         @Override
                         public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                             if (response.isSuccessful()) {
                                 myServiceHelper.updateStatus(message);
                             }
                         }

                         @Override
                         public void onFailure(Call<PostResponse> call, Throwable t) {
                             Log.e("hata", t.getMessage());

                         }
                     }
        );
    }

    private void getMessagesByStatus() {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        RealmResults<MessageRO> realmResults = realm.where(MessageRO.class).equalTo("status", false).findAll();
        for (MessageRO messageRO : realmResults) {
            Message item = new Message();
            item.setImage_path(messageRO.getImage_path());
            item.setMessage_body(messageRO.getMessage_body());
            item.setId(messageRO.getId());
            if (item.getImage_path() != null && item.getImage_path() != "") {
                myServiceHelper.uploadImage(item);
            } else
                sendMessagesService(item);
        }
    }


}
