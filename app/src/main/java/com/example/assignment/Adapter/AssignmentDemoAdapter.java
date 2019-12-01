package com.example.assignment.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.R;
import com.example.assignment.dataModel.AssignmentModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AssignmentDemoAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private List<AssignmentModel> mDatalist;

    public AssignmentDemoAdapter(List<AssignmentModel> list) {
        this.mDatalist = list;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.testdemo_raw, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {

        AssignmentModel assignmentModel = mDatalist.get(position);
        holder.testTitle.setText(assignmentModel.getmTilte());
        holder.testDescription.setText(assignmentModel.getmDecription());
        //holder.imageView.setText(eq.getMagnitude() + "!");

        Picasso.get()
                .load(assignmentModel.getImageHref())
                .fit()
                .into(holder.imageView,new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //Success image already loaded into the view
                    }

                    @Override
                    public void onError(Exception e) {
                        //Error placeholder image already loaded into the view, do further handling of this situation here
                        holder.imageView.setImageResource(R.drawable.ic_launcher);
                    }


                });


    }

    @Override
    public int getItemCount() {
        return mDatalist.size();
    }
}
