package com.example.assignment.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    TextView testTitle;
    TextView testDescription;
    ImageView imageView;


    ItemViewHolder(View itemView) {
        super(itemView);
        testTitle = itemView.findViewById(R.id.title_text);
        testDescription = itemView.findViewById(R.id.description_text);
        imageView = itemView.findViewById(R.id.image_test);

    }
}
