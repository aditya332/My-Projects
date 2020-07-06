package com.somanibrothersservices.lokalmall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateUserInfoActivity extends AppCompatActivity {
    private CircleImageView circleImageView;
    private Button changePhotoButton, removeButton, updateButton;
    private EditText nameField, mobileNoField;
    private Dialog loadingDialog;
    private String name;
    private String mobileNo;
    private String photo;
    private Uri imageUri;
    private boolean updatePhoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(UpdateUserInfoActivity.this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loadingDialog.show();
        //passwordDialog.show();

        circleImageView = findViewById(R.id.profile_image_update_info_fragment);
        changePhotoButton = findViewById(R.id.change_photo_button);
        removeButton = findViewById(R.id.remove_photo_button);
        updateButton = findViewById(R.id.update_info_button);
        nameField = findViewById(R.id.name);
        mobileNoField = findViewById(R.id.mobileNo);
        name = getIntent().getStringExtra("Name") ;
        mobileNo = getIntent().getStringExtra("MobileNo") ;
        photo = getIntent().getStringExtra("Photo") ;

        Glide.with(UpdateUserInfoActivity.this).load(photo).apply(new RequestOptions().placeholder(R.drawable.ic_person_outline_black_24dp)).into(circleImageView);
        nameField.setText(name);
        mobileNoField.setText(mobileNo);

        changePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (UpdateUserInfoActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, 1);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    }
                } else {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, 1);
                }
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUri = null;
                updatePhoto = true;
                Glide.with(UpdateUserInfoActivity.this).load(R.drawable.ic_person_outline_black_24dp).apply(new RequestOptions().placeholder(R.drawable.ic_person_outline_black_24dp)).into(circleImageView);
            }
        });

        nameField.addTextChangedListener(checkInputs);
        mobileNoField.addTextChangedListener(checkInputs);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmail();
            }
        });

    }

    private TextWatcher checkInputs = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(nameField.getText())) {
                if (!TextUtils.isEmpty(mobileNoField.getText())) {
                    updateButton.setEnabled(true);
                } else {
                    mobileNoField.requestFocus();
                    updateButton.setEnabled(false);
                }
            } else {
                nameField.requestFocus();
                updateButton.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    imageUri = data.getData();
                    updatePhoto = true;
                    Glide.with(UpdateUserInfoActivity.this).load(imageUri).apply(new RequestOptions().placeholder(R.drawable.ic_person_outline_black_24dp)).into(circleImageView);
                } else {
                    Toast.makeText(UpdateUserInfoActivity.this, "Image not found", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            } else {
                Toast.makeText(UpdateUserInfoActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkEmail() {
        if (mobileNoField.getText().toString().length() == 10) {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            loadingDialog.show();
            updatePhoto(user);
        } else {
            mobileNoField.setError("Invalid Mobile Number");
        }
    }

    private void updatePhoto(final FirebaseUser user) {
        if (updatePhoto) {
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile/" + user.getUid() + ".jpg");
            if (imageUri != null) {

                Glide.with(UpdateUserInfoActivity.this).asBitmap().load(imageUri).circleCrop().into(new ImageViewTarget<Bitmap>(circleImageView) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = storageReference.putBytes(data);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                imageUri = task.getResult();
                                                DBqueries.profile = task.getResult().toString();
                                                Glide.with(UpdateUserInfoActivity.this).load(DBqueries.profile).into(circleImageView);

                                                Map<String, Object> updateData = new HashMap<>();
                                                updateData.put("fullName", nameField.getText().toString());
                                                updateData.put("mobileNo", mobileNoField.getText().toString());
                                                updateData.put("profile", DBqueries.profile);

                                                updateFields(user, updateData);
                                            } else {
                                                DBqueries.profile = "";
                                                loadingDialog.dismiss();
                                                Toast.makeText(UpdateUserInfoActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    loadingDialog.dismiss();
                                    Toast.makeText(UpdateUserInfoActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        return;
                    }

                    @Override
                    protected void setResource(@Nullable Bitmap resource) {
                        circleImageView.setImageResource(R.drawable.ic_person_outline_black_24dp);
                    }
                });
            } else {
                storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DBqueries.profile = "";
                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("fullName", nameField.getText().toString());
                            updateData.put("mobileNo", mobileNoField.getText().toString());
                            updateData.put("profile", "");

                            updateFields(user, updateData);
                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(UpdateUserInfoActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("fullName", nameField.getText().toString());
            updateData.put("mobileNo", mobileNoField.getText().toString());
            updateFields(user, updateData);
        }
    }

    private void updateFields(FirebaseUser user, final Map<String, Object> updateData) {
        FirebaseFirestore.getInstance().collection("USERS").document(user.getUid()).update(updateData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (updateData.size() > 1) {
                                DBqueries.fullName = nameField.getText().toString();
                                DBqueries.mobileNo = mobileNoField.getText().toString();
                            } else {
                                DBqueries.fullName = nameField.getText().toString();
                            }
                            finish();
                            Toast.makeText(UpdateUserInfoActivity.this, "Information Updated successfully ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UpdateUserInfoActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }
}
