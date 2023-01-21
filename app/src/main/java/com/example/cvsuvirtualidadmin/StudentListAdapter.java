package com.example.cvsuvirtualidadmin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.HolderAdapter> {
    Context context;
    ArrayList<StudentListModel> studentlist;

    public StudentListAdapter(Context context , ArrayList<StudentListModel> studentlist){
        this.context= context;
        this.studentlist=studentlist;
    }

    @NonNull
    @Override
    public HolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_list, parent, false);
        return new HolderAdapter(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAdapter holder, int position) {
        StudentListModel model = studentlist.get(position);
        String name = model.getName();

        holder.name.setText(name);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StudentUi.class);
                intent.putExtra("Uid", model.getUid());
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return studentlist.size();
    }

    public class HolderAdapter extends RecyclerView.ViewHolder {
        private TextView name;
        private RelativeLayout layout;
        public HolderAdapter(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}
