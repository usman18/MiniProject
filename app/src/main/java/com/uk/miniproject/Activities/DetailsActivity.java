package com.uk.miniproject.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.uk.miniproject.Constants;
import com.uk.miniproject.Model.Student;
import com.uk.miniproject.R;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName;
    private EditText etGrNumber;
    private EditText etUniversity;
    private EditText etExamScore;
    private EditText etExamName;

    private ImageView imgLetter;

    private Button btnSubmit;

    private ProgressBar progressBar;

    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initialize();

    }

    private void initialize() {

        etUniversity = findViewById(R.id.etUniversity);
        etGrNumber = findViewById(R.id.etGrNo);
        etExamName = findViewById(R.id.etExamName);
        etExamScore = findViewById(R.id.etExamScore);
        etName = findViewById(R.id.etName);

        imgLetter = findViewById(R.id.imgLetter);

        btnSubmit = findViewById(R.id.btnSubmit);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        btnSubmit.setOnClickListener(this);
        imgLetter.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSubmit:

                if (!IfNull()) {
                    uploadAcceptanceLetter();
                }

                break;
            case R.id.imgLetter:
                selectImage();
                break;
        }

    }

    private void selectImage() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();
                imgLetter.setImageURI(imageUri);
            }

        }

    }

    private void uploadAcceptanceLetter() {

        progressBar.setVisibility(View.VISIBLE);


        final StorageReference reference = FirebaseStorage.getInstance().getReference(Constants.DOCUMENTS)
                .child(etGrNumber.getText().toString());

        UploadTask task = reference.putFile(imageUri);

        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Log.d("Check","Exception " + e.getMessage());
                Toast.makeText(DetailsActivity.this,"Could not upload, please try again",Toast.LENGTH_LONG)
                        .show();
            }
        });

        task = reference.putFile(imageUri);

        Task<Uri> uriTask = task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful())
                    throw  task.getException();

                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()) {

                    progressBar.setVisibility(View.GONE);
                    Uri uri = task.getResult();
                    String imageUrl = uri.toString();
    
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    
                    Student student = new Student();
                    student.setName(user.getDisplayName());
                    student.setEmail(user.getEmail());
                    student.setGrNumber(etGrNumber.getText().toString());
                    student.setUniversity(etUniversity.getText().toString());
                    student.setExamName(etExamName.getText().toString());
                    student.setExamScore(etExamScore.getText().toString());
                    student.setAcceptanceLetterUrl(imageUrl);

                    saveToDb(student);


                }

            }
        });





    }

    private void saveToDb(Student student) {

        DatabaseReference reference
                = FirebaseDatabase.getInstance().getReference(Constants.USERS);

        String grNo = student.getGrNumber();

        reference.child(grNo)
                .setValue(student);

        progressBar.setVisibility(View.GONE);

        Snackbar.make(findViewById(R.id.root_layout),"Successfully Uploaded",Snackbar.LENGTH_SHORT)
                .show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(DetailsActivity.this,MainActivity.class));
                finish();
            }
        },2000);


    }


    private boolean IfNull() {
        boolean isNull = false;

       
        if (TextUtils.isEmpty(etGrNumber.getText().toString())) {
            isNull = true;
            etGrNumber.setError("Please enter GR Number");
        }

        if (TextUtils.isEmpty(etUniversity.getText().toString())) {
            isNull = true;
            etUniversity.setError("Please enter university name");
        }

        if (TextUtils.isEmpty(etExamName.getText().toString())) {
            isNull = true;
            etExamName.setError("Please enter exam name");
        }

        if (TextUtils.isEmpty(etExamScore.getText().toString())) {
            isNull = true;
            etExamScore.setError("Please enter exam score");
        }


        if (imageUri == null) {
            isNull = true;
            Toast.makeText(DetailsActivity.this,"Please attach acceptance letter",Toast.LENGTH_LONG)
                    .show();
        }

        return isNull;
    }

}
