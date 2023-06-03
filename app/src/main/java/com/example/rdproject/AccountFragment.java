package com.example.rdproject;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import android.hardware.Camera;
import android.Manifest;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int CAMERA_REQUEST_CODE = 100, STORAGE_REQUEST_CODE = 200;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 300, STORAGE_PERMISSION_REQUEST_CODE = 400;

    private String[] camerapermissions;
    private String[] storagepermissions;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference().child("images");

    private ProgressDialog pd;
    private StorageReference storageProfilePicsref;

    private ImageView user_profile_photo;
    private TextView username, email, description;
    private FloatingActionButton edit_button;


    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        //get Firebase instances
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        storageProfilePicsref = FirebaseStorage.getInstance().getReference().child("Profile Pic");

        //initialize arrays with permission
        camerapermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagepermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //initialize views
        username = view.findViewById((R.id.username));
        email = view.findViewById(R.id.email);
        description = view.findViewById(R.id.description);
        user_profile_photo = view.findViewById(R.id.user_profile);
        edit_button = view.findViewById(R.id.nice_button);

        //initialize progress dialog
        pd = new ProgressDialog(getActivity());

        //look in the database for the email of the current user in order to get his personal information
        Query checkemail = reference.orderByChild("email").equalTo(user.getEmail());
        checkemail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // get user`s information stored in the database
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String username_text = "" + dataSnapshot.child("username").getValue();
                    String description_text = "" + dataSnapshot.child("description").getValue();
                    String image_text = "" + dataSnapshot.child("image").getValue();

                    //set text of views based on user`s details
                    username.setText(username_text);
                    email.setText(user.getEmail());
                    description.setText(description_text);

                    // create a round image using Glide
                    Glide.with(getActivity())
                            .load(image_text)
                            .apply(RequestOptions.circleCropTransform())
                            .into(user_profile_photo);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //set the usability of the edit_button
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
        return view;
    }

    //display and model the options provided by the edit button
    private void showEditProfileDialog() {
        String[] options = {"Select photo from gallery", "Select photo from camera", "Edit description", "Change username", "Change password", "Log Out"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Action");

        //model the usability of each action
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    pd.setMessage("Updating profile photo");
                    pickFromStorage();
                    if(!checkStoragePermission())
                    {
                        requestStoragePermission();
                        pickFromStorage();
                    }
                    else {
                        pickFromStorage();
                    }
                } else if (which == 1) {
                    pd.setMessage("Updating profile photo");
                    if(!checkCameraPermission())
                    {
                        requestCameraPermission();
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(getActivity(), "Testttt", Toast.LENGTH_SHORT).show();
                        pickFromCamera();
                    }
                } else if (which == 2) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    View dialogView = inflater.inflate(R.layout.description_dialog, null);

                    final EditText input = dialogView.findViewById(R.id.descriptionEditText);
                    final TextView characterCount = dialogView.findViewById(R.id.characterCountTextView);

                    final DatabaseReference descriptionRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("description");

                    final int MAX_DESCRIPTION_LENGTH = 100;

                    AlertDialog.Builder descriptionDialog = new AlertDialog.Builder(getActivity());
                    descriptionDialog.setView(dialogView);
                    descriptionDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newDescription = input.getText().toString().trim();

                            if (newDescription.length() > MAX_DESCRIPTION_LENGTH) {
                                // Description exceeds the maximum character limit
                                Toast.makeText(getActivity(), "Description must be less than " + MAX_DESCRIPTION_LENGTH + " characters", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            descriptionRef.setValue(newDescription)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Description updated successfully
                                            // Update the profile description on the page here.
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle the error
                                        }
                                    });
                        }
                    });
                    descriptionDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    final AlertDialog descriptionAlert = descriptionDialog.create();

                    input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_DESCRIPTION_LENGTH)});
                    input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            int remainingChars = MAX_DESCRIPTION_LENGTH - s.length();
                            characterCount.setText("Remaining characters: " + remainingChars);
                            descriptionAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!s.toString().trim().isEmpty() && s.length() <= MAX_DESCRIPTION_LENGTH);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    descriptionAlert.show();

                } else if (which == 3) {
                    pd.setMessage("Changing username");

                    // Inflate the XML layout file
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    View dialogView = inflater.inflate(R.layout.username_dialog, null);

                    final EditText input = dialogView.findViewById(R.id.usernameEditText);
                    final TextView characterCount = dialogView.findViewById(R.id.characterCountTextView);

                    final DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("username");

                    // Define the maximum character limit for the username
                    final int MAX_USERNAME_LENGTH = 20;

                    AlertDialog.Builder usernameDialog = new AlertDialog.Builder(getActivity());
                    usernameDialog.setView(dialogView);
                    usernameDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String newUsername = input.getText().toString().trim();

                            if (newUsername.length() > MAX_USERNAME_LENGTH) {
                                // Username exceeds the maximum character limit
                                Toast.makeText(getActivity(), "Username must be less than " + MAX_USERNAME_LENGTH + " characters", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Check if the new username already exists in the database
                            Query checkUsernameQuery = FirebaseDatabase.getInstance().getReference().child("Users")
                                    .orderByChild("username").equalTo(newUsername);
                            checkUsernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        // Username already exists, handle the case
                                        Toast.makeText(getActivity(), "Username already exists", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Username is unique, proceed with updating the username
                                        usernameRef.setValue(newUsername)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Username updated successfully
                                                        // Update the profile username on the page here.
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Handle the failure to update the username
                                                        Toast.makeText(getActivity(), "Failed to update username", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle the database error
                                    Toast.makeText(getActivity(), "Database error occurred", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    usernameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    final AlertDialog usernameAlert = usernameDialog.create();

                    input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_USERNAME_LENGTH)});
                    input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // Calculate the remaining characters
                            int remainingChars = MAX_USERNAME_LENGTH - s.length();
                            characterCount.setText("Remaining characters: " + remainingChars);

                            // Enable or disable the positive button based on the input length
                            usernameAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!s.toString().trim().isEmpty() && s.length() <= MAX_USERNAME_LENGTH);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    usernameAlert.show();

                }else if (which == 4) {
                    pd.setMessage("Changing password");
                    final EditText input = new EditText(getActivity());
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    // Get the current password of the user and set it as the initial text of the EditText
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String currentPassword = ""; // Replace this with the user's current password
                    input.setText(currentPassword);
                    input.setSelection(currentPassword.length());

                    AlertDialog.Builder passwordDialog = new AlertDialog.Builder(getActivity());
                    passwordDialog.setTitle("Edit Password");
                    passwordDialog.setView(input);
                    passwordDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newPassword = input.getText().toString().trim();

                            // Make sure the user is authenticated before updating their password
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getActivity(), "Password has been successfully updated", Toast.LENGTH_SHORT).show();
                                                    final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                                                    userRef.child("password").setValue(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // The password has been updated in the database
                                                        }
                                                    });

                                                } else {
                                                    Toast.makeText(getActivity(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                pd.setTitle("Logging in");
                                startActivity(new Intent(getActivity(), LogIn.class));
                            }
                        }
                    });
                    passwordDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    final AlertDialog passwordAlert = passwordDialog.create();

                    input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            passwordAlert.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!s.toString().trim().isEmpty());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                    passwordAlert.show();

                } else {
                    pd.setTitle("Logging out");
                    startActivity(new Intent(getActivity(), LogIn.class));
                }
            }
        });
        builder.create().show();
    }

    // when taking a photo, its source is delivered as a constant and handled further
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == STORAGE_REQUEST_CODE) {
                uploadPhotoFromStorage(data);
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                uploadPhotoFromCamera(data);
            }

        }
    }


    public void uploadPhotoFromStorage(Intent data) {
        Uri imageUri = data.getData();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            Glide.with(this)
                    .load(bitmap)
                    .apply(RequestOptions.centerCropTransform())
                    .into(user_profile_photo);


            // get a reference to the user's node in the database
            DatabaseReference userRef = reference.child(user.getUid());

            // upload the photo to Firebase Storage and get a reference to the uploaded file
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference photoRef = storageRef.child("profile_photos/" + user.getUid() + ".jpg");
            UploadTask uploadTask = photoRef.putFile(imageUri);

            // set up a listener to get the download URL of the uploaded image after the upload is complete
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // get the download URL of the uploaded image from the task snapshot
                    photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // update the "image" field of the user's node in the database with the download URL
                            userRef.child("image").setValue(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // handle any errors
                    Toast.makeText(getActivity(), "Failed to upload profile photo", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadPhotoFromCamera(Intent data) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        user_profile_photo.setImageBitmap(photo);

        // get a reference to the Storage node where the photo will be uploaded
        StorageReference storageRef = storage.getReference().child("profile_photos/" + user.getUid() + ".jpg");

        // upload the photo to Firebase Storage
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] dataBytes = baos.toByteArray();
        UploadTask uploadTask = storageRef.putBytes(dataBytes);

        // add a listener to track the upload progress
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // get a reference to the user's node in the database
                DatabaseReference userRef = reference.child(user.getUid());

                // get the download URL of the uploaded image from the Storage reference
                storageRef.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Update the "image" field of the user's node in the database with the download URL
                                userRef.child("image").setValue(uri.toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any errors
                                Toast.makeText(getActivity(), "Failed to upload profile photo", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    //check whether permission has been granted by the Program Manager
    private boolean checkStoragePermission ()
    {
       boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
       return result;
    }

    //request permission if needed
    public void requestStoragePermission ()
    {
        ActivityCompat.requestPermissions(getActivity(), storagepermissions, STORAGE_PERMISSION_REQUEST_CODE);
    }

    //check whether permission has been granted by the Program Manager
    private boolean checkCameraPermission ()
    {
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    //request permission if needed
    public void requestCameraPermission ()
    {
        ActivityCompat.requestPermissions(getActivity(), camerapermissions, CAMERA_PERMISSION_REQUEST_CODE);
    }

    //handling the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // there are two permission request codes that specify which one the user requested from the Package Manager
        switch(requestCode){
            case CAMERA_PERMISSION_REQUEST_CODE:{
                if(grantResults.length>0)
                {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    // if permission for Camera and Gallery has been granted, pick a photo from Camera
                    if(cameraAccepted && storageAccepted)
                    {
                        pickFromCamera();
                    }
                    else {
                        //display an error if permission has not been granted
                        Toast.makeText(getActivity(), "Please enable camera and storage permissions", Toast.LENGTH_SHORT).show();
                    }
                }
            } break;
            case STORAGE_PERMISSION_REQUEST_CODE:{
                boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                // if permission for Gallery has been granted, pick a photo from Gallery
                if(storageAccepted)
                {
                    pickFromStorage();
                }
                else {
                    //display an error if permission has not been granted
                    Toast.makeText(getActivity(), "Please enable storage permissions", Toast.LENGTH_SHORT).show();
                }
            }break;
        }
    }

    //take photo from Gallery
    private void pickFromStorage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, STORAGE_REQUEST_CODE);
    }

    //take photo from Camera
    private void pickFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }
}

