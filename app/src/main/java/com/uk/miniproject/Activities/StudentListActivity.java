package com.uk.miniproject.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uk.miniproject.Constants;
import com.uk.miniproject.Model.Student;
import com.uk.miniproject.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class StudentListActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    private ArrayList<Student> students;
    private RecyclerView rvStudentList;

    private Query query;
    private FirebaseRecyclerOptions<Student> recyclerOptions;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        initialize();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.stopListening();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export_to_csv:
                exportToCSV();
                return true;
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this,MainActivity.class));
                finish();
        }
        return false;
    }

    private void initialize() {


        students = new ArrayList<>();
        rvStudentList = findViewById(R.id.rvStudentList);
        rvStudentList.setLayoutManager(new LinearLayoutManager(this));

        query = FirebaseDatabase.getInstance()
                .getReference(Constants.USERS)
                .orderByChild("name");


        recyclerOptions = new FirebaseRecyclerOptions.Builder<Student>()
                .setQuery(query,Student.class)
                .build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Student,StudentViewHolder>(recyclerOptions) {

            @NonNull
            @Override
            public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new StudentViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_row,parent,false));
            }

            @Override
            protected void onBindViewHolder(@NonNull StudentViewHolder holder, int position, @NonNull final Student model) {

                holder.tvName.setText(model.getName());
                holder.tvGrNumber.setText(model.getGrNumber());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(StudentListActivity.this,StudentActivity.class);
                        intent.putExtra("GrNumber",model.getGrNumber());
                        startActivity(intent);

                    }
                });

            }
        };

        rvStudentList.setAdapter(firebaseRecyclerAdapter);


    }

    private void exportToCSV() {


        //previous entries if any
        students.clear();

        DatabaseReference
                reference = FirebaseDatabase.getInstance().getReference(Constants.USERS);

        reference.orderByChild("name")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Student student = snapshot.getValue(Student.class);
                            students.add(student);
                        }

                        int result = ContextCompat.checkSelfPermission(StudentListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                        if (result == PackageManager.PERMISSION_GRANTED){
                            createCSV();
                        }else {
                            requestForPermission();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });






    }

    private void requestForPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(StudentListActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){

        }else {
            ActivityCompat.requestPermissions(StudentListActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSIONS_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    createCSV();
                }
        }
    }

    private void createCSV() {

        String header = "Name,Email,GrNumber,Exam Name,Exam Score,University";

        File folder = new File(
                Environment.getExternalStorageDirectory()
                        + "/" + getResources().getString(R.string.app_name)
        );

        if (!folder.exists()){
            boolean value = folder.mkdirs();
            Log.d("Check","Created " + value);
        }else {
            Log.d("Check","Already present");
        }

        String fileName = folder.toString() + File.separator + "Alumni.csv";

        File file = new File(fileName);

        if (!file.exists())
            try {
                boolean created = file.createNewFile();
                Log.d("Check", "File status " + created);
            } catch (IOException e) {
                e.printStackTrace();
            }


        try {

            FileWriter fw = new FileWriter(file);

            fw.append(header);

            final String COMMA = ",";
            final String NEW_LINE = "\n";

            for (Student student : students) {

                fw.append(NEW_LINE);
                fw.append(student.getName());
                fw.append(COMMA);
                fw.append(student.getEmail());
                fw.append(COMMA);
                fw.append(student.getGrNumber());
                fw.append(COMMA);
                fw.append(student.getExamName());
                fw.append(COMMA);
                fw.append(student.getExamScore());
                fw.append(COMMA);
                fw.append(student.getUniversity());
            }

            fw.flush();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(StudentListActivity.this,"CSV Created !",Toast.LENGTH_SHORT)
                .show();

    }

    class StudentViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        TextView tvGrNumber;
        View mView;

        public StudentViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            tvName = itemView.findViewById(R.id.tvName);
            tvGrNumber = itemView.findViewById(R.id.tvGrNumber);

            
            
        }
    }


}
