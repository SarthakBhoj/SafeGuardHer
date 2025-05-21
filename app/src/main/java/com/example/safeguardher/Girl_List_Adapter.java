package com.example.safeguardher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Girl_List_Adapter extends RecyclerView.Adapter<Girl_List_Adapter.MyViewHolder>{
    private LayoutInflater inflater;
    private ArrayList<GirlClass> girlClassArrayList;
    Context context;


    public Girl_List_Adapter(Context context,ArrayList<GirlClass> leaveArrayList){
        this.girlClassArrayList=leaveArrayList;
        inflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =inflater.inflate(R.layout.stud_list,parent,false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.studName.setText(girlClassArrayList.get(position).Stud_Name);
        holder.studNumber.setText(girlClassArrayList.get(position).Stud_Number);
        holder.LayoutList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(context, StudentDetail.class);
                    intent.putExtra("number", "" + girlClassArrayList.get(position).Stud_Number);
//                    Toast.makeText(context, girlClassArrayList.get(position).Stud_Number, Toast.LENGTH_SHORT).show();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                catch (Exception ex) {
                    Log.e("Ex", "onClick: " + ex);
                }
            }
        });
    }



    @Override
    public int getItemViewType(int position){
        return position;
    }




    @Override
    public int getItemCount() {
        return girlClassArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView studName,studNumber;
        LinearLayout LayoutList;
        public MyViewHolder(View view) {
            super(view);
            context= itemView.getContext();
            studNumber = view.findViewById(R.id.studNumber);
            studName = view.findViewById(R.id.studName);
            LayoutList  = view.findViewById(R.id.layoutStudList);
        }
    }


}
