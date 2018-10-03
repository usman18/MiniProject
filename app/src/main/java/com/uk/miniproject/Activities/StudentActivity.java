package com.uk.miniproject.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uk.miniproject.Constants;
import com.uk.miniproject.Model.Student;
import com.uk.miniproject.R;

public class StudentActivity extends AppCompatActivity {

    private String grNumber;

    private TextView tvName;
    private TextView tvGrNumber;
    private TextView tvExam;
    private TextView tvScore;
    private TextView tvUniversity;

    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        getExtras();
        initialize();
        setDataToViews();
    }

    private void getExtras() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            if (bundle.getString("GrNumber") != null)
                grNumber = bundle.getString("GrNumber");
            else
                displayMsg();

        }else {
            displayMsg();
        }


    }

    private void displayMsg() {

        Toast.makeText(StudentActivity.this,"Something went wrong !",Toast.LENGTH_SHORT)
                .show();
        finish();
    }

    private void initialize() {

        tvName = findViewById(R.id.tvName);
        tvGrNumber = findViewById(R.id.tvGrNumber);
        tvExam = findViewById(R.id.tvExam);
        tvUniversity = findViewById(R.id.tvUniversity);
        tvScore = findViewById(R.id.tvScore);

    }

    private void setDataToViews() {

        reference = FirebaseDatabase.getInstance().getReference(Constants.USERS)
                .child(grNumber);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Student student = dataSnapshot.getValue(Student.class);

                if (student != null) {

                    tvName.setText(student.getName());
                    tvGrNumber.setText(student.getGrNumber());
                    tvExam.setText(student.getExamName());
                    tvScore.setText(student.getExamScore());
                    tvUniversity.setText(student.getUniversity());

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
