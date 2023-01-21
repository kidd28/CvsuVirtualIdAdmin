package com.example.cvsuvirtualidadmin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class StudentUi extends AppCompatActivity {
    TextView name, email, SecCode, StudentNum, Uid;
    Button verify, decline;
    FirebaseDatabase database;
    DatabaseReference reference;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_ui);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        SecCode = findViewById(R.id.SecCode);
        StudentNum = findViewById(R.id.StudentNum);
        Uid = findViewById(R.id.Uid);
        verify = findViewById(R.id.verify);
        decline = findViewById(R.id.decline);

        uid = getIntent().getExtras().getString("Uid");
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Students");

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(uid).child("Verified").setValue("y");
                Toast.makeText(StudentUi.this,"Request approved!", Toast.LENGTH_SHORT).show();
                Intent intent  = new Intent(StudentUi.this, StudentList.class);
                startActivity(intent);
                StudentUi.this.finish();
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(StudentUi.this)
                        .setTitle("Decline?")
                        .setMessage("Are you sure you want to decline?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                reference.child(uid).child("Verified").setValue("declined");
                                Toast.makeText(StudentUi.this,"Request declined!", Toast.LENGTH_SHORT).show();
                                Intent intent  = new Intent(StudentUi.this, StudentList.class);
                                startActivity(intent);
                                StudentUi.this.finish();
                            }
                        }).create().show();
            }
        });

        Query query = reference.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String stuname = "" + ds.child("name").getValue();
                    String stuemail = "" + ds.child("email").getValue();
                    String stuseccode = "" + ds.child("SecCode").getValue();
                    String stunum = "" + ds.child("StudentNumber").getValue();
                    String stuuid = "" + ds.child("uid").getValue();
                    name.setText(stuname);
                    email.setText(stuemail);
                    SecCode.setText(stuseccode);
                    StudentNum.setText(stunum);
                    Uid.setText(stuuid);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



}