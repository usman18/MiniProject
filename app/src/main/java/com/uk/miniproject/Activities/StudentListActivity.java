package com.uk.miniproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.uk.miniproject.Constants;
import com.uk.miniproject.Model.Student;
import com.uk.miniproject.R;

public class StudentListActivity extends AppCompatActivity {

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

    private void initialize() {

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
