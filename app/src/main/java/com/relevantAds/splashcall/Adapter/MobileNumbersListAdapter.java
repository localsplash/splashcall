package com.relevantAds.splashcall.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.relevantAds.splashcall.Database.Model.PhoneNumber;
import com.relevantAds.splashcall.R;

import java.util.ArrayList;

public class MobileNumbersListAdapter extends RecyclerView.Adapter<MobileNumbersListAdapter.MyViewHolder> {

    // define global variables.
    private Context context;
    private LayoutInflater inflater;
    public ArrayList<PhoneNumber> mobileNumbers;

    public MobileNumbersListAdapter(Context context, ArrayList<PhoneNumber> mobileNumbers){
        this.context = context;
        this.mobileNumbers = mobileNumbers;
    }
    // accessing item layout and inflating upon creation
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        // create a new view
        if (inflater == null)
            inflater = (LayoutInflater) viewGroup.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.mobile_number_item, viewGroup, false);

        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }
    // binding the values from arrayList to the item containers i.e. image and name
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder,final int position) {
        myViewHolder.mobileNumber.setText(mobileNumbers.get(position).getAddedPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return mobileNumbers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mobileNumber;
        private MyViewHolder(View itemView) {
            super(itemView);
            mobileNumber = itemView.findViewById(R.id.mobile_number_text_view);

        }
    }
}
