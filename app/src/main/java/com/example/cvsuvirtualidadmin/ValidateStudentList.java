package com.example.cvsuvirtualidadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ValidateStudentList extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<StudentListModel> studentListModels;
    StudentListAdapter studentListAdapter;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_student_list);
        recyclerView=findViewById(R.id.sturv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        studentListModels = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        loadstudents();

    }
    private void loadstudents() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Students");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentListModels.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    if(Objects.equals(ds.child("Verified").getValue(), "n") ){
                        StudentListModel model = ds.getValue(StudentListModel.class);
                        studentListModels.add(model);
                    }
                }
                studentListAdapter = new StudentListAdapter(ValidateStudentList.this, studentListModels);
                recyclerView.setAdapter(studentListAdapter);
                studentListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}