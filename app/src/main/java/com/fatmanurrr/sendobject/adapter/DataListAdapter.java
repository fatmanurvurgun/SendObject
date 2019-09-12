package com.fatmanurrr.sendobject.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fatmanurrr.sendobject.R;
import com.fatmanurrr.sendobject.item.Message;
import com.fatmanurrr.sendobject.item.MessageRO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2.09.2019.
 */

public class DataListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> mesajItems;
    private static int TYPE_IMAGE = 1;
    private static int TYPE_TEXT = 2;
    Context context;

    public DataListAdapter(List<Message> mesajItems) {
        this.mesajItems = mesajItems;
    }


    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(mesajItems.get(position).getMessage_body()))
            return TYPE_IMAGE;
        else
            return TYPE_TEXT;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        if (viewType == TYPE_IMAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_image_item, parent, false);
            return new ImageViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_text_item, parent, false);
            return new TextViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == TYPE_IMAGE) {
//            ((ImageViewHolder) holder).txtMessage_Body.setText("abc");

            File imgFile = new File(mesajItems.get(position).getImage_path());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ((ImageViewHolder) holder).ivImage.setImageBitmap(myBitmap);
            }
        } else {

            ((TextViewHolder) holder).setTextDetails(mesajItems.get(position));
        }

    }


    @Override
    public int getItemCount() {
        return mesajItems.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        //        private TextView txtID;
        private TextView txtMessage_Body;
        private ImageView ivImage;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            //txtImageı bagla
//            txtID=(TextView) itemView.findViewById(R.id.txtId);
            txtMessage_Body = (TextView) itemView.findViewById(R.id.txt_Message_Body);
            ivImage = (ImageView) itemView.findViewById(R.id.send_İmage);
        }

//        private void setImageDetails(MessageRO messageRO)
//        {
////            txtID.setText("2");
//            txtMessage_Body.setText("abc");
//            Uri uri = Uri.parse(messageRO.getImage_path());
//            ivImage.setImageURI(uri);
//        }
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        //         private TextView txtID;
        private TextView txtMessage_Body;
        private ImageView ivImage;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
//              txtID=(TextView) itemView.findViewById(R.id.txtId);
            txtMessage_Body = (TextView) itemView.findViewById(R.id.txt_Message_Body);
            ivImage = (ImageView) itemView.findViewById(R.id.send_İmage);

        }

        private void setTextDetails(Message message) {
//             txtID.setText("1");
            txtMessage_Body.setText(message.getMessage_body());
        }
    }
}
