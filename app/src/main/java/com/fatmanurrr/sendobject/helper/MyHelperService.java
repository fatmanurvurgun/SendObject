package com.fatmanurrr.sendobject.helper;

import android.util.Log;

import com.fatmanurrr.sendobject.imageUpload.UploadImageInterface;
import com.fatmanurrr.sendobject.imageUpload.UploadObject;
import com.fatmanurrr.sendobject.item.Message;
import com.fatmanurrr.sendobject.item.MessageRO;
import com.fatmanurrr.sendobject.item.PostResponse;
import com.fatmanurrr.sendobject.service.RetrofitFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
 * Created by Lenovo on 4.09.2019.
 */

public class MyHelperService {

    private static MyHelperService myHelperService;
    private static final String SERVER_PATH = "http://192.168.1.35:8082/";
    Realm realm = Realm.getDefaultInstance();


    public static MyHelperService  getMyHelperService() {

        if (myHelperService == null) {
            return new MyHelperService();
        }
        else
            return myHelperService;
    }


    public void uploadImage(final Message message) {
        final File file = new File(message.getImage_path());
        RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UploadImageInterface uploadImage = retrofit.create(UploadImageInterface.class);
        Call<UploadObject> fileUpload = uploadImage.uploadFile(fileToUpload, filename);
        fileUpload.enqueue(new Callback<UploadObject>() {
            @Override
            public void onResponse(Call<UploadObject> call, Response<UploadObject> response) {

//              imageName.replaceAll(" ", "");
                Message mItem = new Message();
                mItem.setImage_path(SERVER_PATH+"likewp/"+file.getName());
                mItem.setMessage_body("");
                mItem.setId(message.getId());
                sendMessagesService(mItem);

            }

            @Override
            public void onFailure(Call<UploadObject> call, Throwable t) {
                Log.d("Error ", t.getMessage());
            }
        });
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
                                 updateStatus(message);
                             }
                         }

                         @Override
                         public void onFailure(Call<PostResponse> call, Throwable t) {
                             Log.e("hata", t.getMessage());

                         }
                     }
        );
    }

    public void updateStatus(Message requiredUpdateData) {

        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        realm.beginTransaction();
        MessageRO stamps = realm.where(MessageRO.class).equalTo("id", requiredUpdateData.getId()).findFirst();
        stamps.setStatus(true);
        realm.commitTransaction();
    }


}
