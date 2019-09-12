package com.fatmanurrr.sendobject.imageUpload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Lenovo on 10.09.2019.
 */

public interface UploadImageInterface {

    @Multipart
    @POST("likewp/upload.php")
    Call<UploadObject> uploadFile(@Part MultipartBody.Part file, @Part("name")RequestBody name);
}
