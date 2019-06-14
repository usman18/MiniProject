package com.uk.miniproject.Activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
	
	private ActionBar actionBar;
	
	private TextView tvName;
	private TextView tvEmail;
	private TextView tvGrNumber;
	private TextView tvExam;
	private TextView tvScore;
	private TextView tvUniversity;
	
	private ImageView imgAcceptanceLetter;
	private ProgressBar progressBar;
	
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
			
		} else {
			displayMsg();
		}
		
		
	}
	
	private void displayMsg() {
		
		Toast.makeText(StudentActivity.this, "Something went wrong !", Toast.LENGTH_SHORT)
			.show();
		finish();
	}
	
	private void initialize() {
		
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		tvName = findViewById(R.id.tvName);
		tvEmail = findViewById(R.id.tvEmail);
		tvExam = findViewById(R.id.tvExam);
		tvGrNumber = findViewById(R.id.tvGrNumber);
		tvExam = findViewById(R.id.tvExam);
		tvUniversity = findViewById(R.id.tvUniversity);
		tvScore = findViewById(R.id.tvScore);
		
		imgAcceptanceLetter = findViewById(R.id.imgAcceptanceLetter);
		progressBar = findViewById(R.id.pb);
		
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
					tvEmail.setText(student.getEmail());
					tvGrNumber.setText(student.getGrNumber());
					tvExam.setText(student.getExamName());
					tvScore.setText(student.getExamScore());
					tvUniversity.setText(student.getUniversity());
					
					Glide.with(StudentActivity.this)
						.load(student.getAcceptanceLetterUrl().trim())
						.addListener(new RequestListener<Drawable>() {
							@Override
							public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
								progressBar.setVisibility(View.GONE);
								return false;
							}
							
							@Override
							public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
								progressBar.setVisibility(View.GONE);
								return false;
							}
						})
						.into(imgAcceptanceLetter);
					
					
				}
				
				
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			
			}
		});
		
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
