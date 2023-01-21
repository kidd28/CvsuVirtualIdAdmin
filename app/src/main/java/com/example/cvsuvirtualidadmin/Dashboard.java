package com.example.cvsuvirtualidadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Dashboard extends AppCompatActivity {
    Button studentlist, ValidateStudent, schedule,Post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        studentlist = findViewById(R.id.studentlist);
        ValidateStudent = findViewById(R.id.validate);
        schedule = findViewById(R.id.schedule);
        Post = findViewById(R.id.Post);

        studentlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, StudentList.class);
                startActivity(intent);
                Dashboard.this.finish();

            }
        });
        ValidateStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, ValidateStudentList.class);
                startActivity(intent);
                Dashboard.this.finish();

            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, UploadSchedule.class));
                Dashboard.this.finish();

            }
        });
        Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, PublishAnnouncement.class));
                Dashboard.this.finish();

            }
        });

    }
}