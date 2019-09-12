package com.fatmanurrr.sendobject.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.fatmanurrr.sendobject.NetworkChangeReceiver;
import com.fatmanurrr.sendobject.R;
import com.fatmanurrr.sendobject.adapter.DataListAdapter;
import com.fatmanurrr.sendobject.helper.MyHelperService;
import com.fatmanurrr.sendobject.imageUpload.UploadImageInterface;
import com.fatmanurrr.sendobject.imageUpload.UploadObject;
import com.fatmanurrr.sendobject.item.Message;
import com.fatmanurrr.sendobject.item.MessageRO;
import com.fatmanurrr.sendobject.item.PostResponse;
import com.fatmanurrr.sendobject.service.RetrofitFactory;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

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

public class MainActivity extends AppCompatActivity {

    private ArrayList<Message> messageList = new ArrayList<>();

    private DataListAdapter mAdapter;

    private RecyclerView dataRecyclerView;
    EditText editTextMessage;
    ImageView pre_image_view;
    FloatingActionButton fabBtnAddMessage;
    ImageButton btnAddImage;

    public static final int PICK_IMAGE = 1;
    private String selectImage;
    private String imageName;
    MessageRO message;
    MyHelperService myServiceHelper;

    //broadcast
    private NetworkChangeReceiver receiver;
    Realm realm = Realm.getDefaultInstance();




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_datalist);

        //broadcast ten sürekli dinliyor.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);

        //myHelperService
        myServiceHelper=MyHelperService.getMyHelperService();

        dataRecyclerView = (RecyclerView) findViewById(R.id.datalist);
        btnAddImage = (ImageButton) findViewById(R.id.send_image_btn);
        pre_image_view = (ImageView) findViewById(R.id.pre_image_view);
        fabBtnAddMessage = (FloatingActionButton) findViewById(R.id.send_message_btn);
        editTextMessage = (EditText) findViewById(R.id.input_message);

        messageList = getDatasRealm();
        if (messageList != null) {
            mAdapter = new DataListAdapter(messageList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            dataRecyclerView.setLayoutManager(mLayoutManager);
            dataRecyclerView.setAdapter(mAdapter);
        }


        fabBtnAddMessage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(

                        new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {

                                message = new MessageRO();
                                Number id = bgRealm.where(MessageRO.class).max("id");
                                int nextId = (id == null) ? 0 : id.intValue() + 1;

                                if (!editTextMessage.getText().toString().equals("")) {
                                    message.setMessage_body(editTextMessage.getText().toString());
                                    message.setImage_path(null);
                                } else {
                                    message.setMessage_body(null);
                                    message.setImage_path(selectImage);
                                }
                                message.setId(nextId);
                                message.setStatus(false);
                                bgRealm.insert(message);
                            }

                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                Log.v("database", "local data inserted");

                                mAdapter = new DataListAdapter(getDatasRealm());
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                dataRecyclerView.setLayoutManager(mLayoutManager);
                                dataRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                dataRecyclerView.setAdapter(mAdapter);

                                if (selectImage != null) {
                                    Message mItem = new Message();
                                    mItem.setImage_path(message.getImage_path());
                                    mItem.setId(message.getId());
                                    mItem.setMessage_body("");
                                    myServiceHelper.uploadImage(mItem);
                                    selectImage = null;
                                    editTextMessage.setVisibility(View.VISIBLE);
                                    pre_image_view.setVisibility(View.GONE);

                                }
                                else {
                                    Message mItem = new Message();
                                    mItem.setImage_path("");
                                    mItem.setId(message.getId());
                                    mItem.setMessage_body(editTextMessage.getText().toString());
                                    myServiceHelper.sendMessagesService(mItem);
                                }
                                editTextMessage.setText("");
                            }
                        }
                        , new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                Log.d("hata", "mesaj" + error.getMessage());
                            }
                        }
                );
            }
        });

        //fotograf seçme
        btnAddImage.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        requestCameraPermission();

                    }
                }
        );
    }

    private void requestCameraPermission() {
        Dexter.withActivity(MainActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted, open the camera
                        ChooseImage();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private ArrayList<Message> getDatasRealm() {
        final ArrayList<Message> messageList = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<MessageRO> realmResults = realm.where(MessageRO.class).findAll();
        realm.commitTransaction();
        for (MessageRO messageRO : realmResults) {
            Message messageItem = new Message();
            messageItem.setDate(messageRO.getDate());
            messageItem.setImage_path(messageRO.getImage_path());
            messageItem.setMessage_body(messageRO.getMessage_body());
            messageItem.setStatus(messageRO.isStatus());
            messageList.add(messageItem);
        }
        return messageList;
    }

    public void ChooseImage() {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(data.getData(), filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(columnIndex);
            cursor.close();

            File file;
            if (path != null) {
                file = new File(path);
            } else {
                return;
            }
            selectImage = path;
            pre_image_view.setVisibility(View.VISIBLE);
            editTextMessage.setVisibility(View.GONE);

            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            pre_image_view.setImageBitmap(myBitmap);
            imageName = file.getName();

        }
    }


}
