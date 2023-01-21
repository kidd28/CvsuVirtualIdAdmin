package com.example.cvsuvirtualidadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
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
import com.google.firebase.storage.UploadTask;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;

public class PublishAnnouncement extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, PickiTCallbacks {
    EditText Caption;
    ImageView ImageV;
    Button uploadImg, Uploadbtn;
    PickiT pickiT;

    FirebaseAuth mAuth;
    FirebaseUser user;
    String timeStamp;


    Boolean uploadimage = false;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int PICK_FILE_REQUEST = 100;
    static DriveServiceHelper mDriveServiceHelper;
    static String folderId = "";
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_announcement);


        Caption = findViewById(R.id.Caption);
        ImageV = findViewById(R.id.ImageV);
        uploadImg = findViewById(R.id.uploadImg);
        Uploadbtn = findViewById(R.id.uploadbtn);

        pickiT = new PickiT(this, this, this);


        timeStamp = String.valueOf(System.currentTimeMillis());

        requestSignIn();
        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimage = true;
                createFolder();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    requestPermission();
                } else {
                    requestPermissionBelowR();
                }
            }
        });
        Uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                try {
                    calendar.setTimeInMillis(Long.parseLong(timeStamp));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (uploadimage = false ){
                    String pTime = android.text.format.DateFormat.format("dd/MM/yyyy", calendar).toString();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("Caption", Caption.getText().toString().trim());
                    hashMap.put("Date", pTime);
                    hashMap.put("PostId", timeStamp);
                    hashMap.put("FileId", "null");
                    hashMap.put("FileName", "null");
                    hashMap.put("Filelink", "null");
                    hashMap.put("Publish", "y");
                    reference.child(timeStamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            startActivity(new Intent( PublishAnnouncement.this,Dashboard.class));
                            PublishAnnouncement.this.finish();
                        }
                    });
                }else if (uploadimage = true){
                    String pTime = android.text.format.DateFormat.format("dd/MM/yyyy", calendar).toString();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("Caption", Caption.getText().toString().trim());
                    hashMap.put("Date", pTime);
                    hashMap.put("Publish", "y");
                    reference.child(timeStamp).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            startActivity(new Intent( PublishAnnouncement.this,Dashboard.class));
                            PublishAnnouncement.this.finish();
                        }
                    });
                }
            }
        });
    }

    private void requestPermissionBelowR() {
        String[] perms = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            uploadFile();
        } else {
            EasyPermissions.requestPermissions(this, "We need permissions because this and that",
                    123, perms);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestPermission() {
        String[] perms = {android.Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                if (EasyPermissions.hasPermissions(this, perms)) {
                    uploadFile();
                } else {
                    EasyPermissions.requestPermissions(this, "We need permissions because this and that",
                            123, perms);
                    showSettingsDialog();
                }
            } else {
                uploadFile();
            }
        } else {
            if (EasyPermissions.hasPermissions(this, perms)) {
                uploadFile();
            } else {
                EasyPermissions.requestPermissions(this, "We need permissions because this and that",
                        123, perms);
                showSettingsDialog();
            }
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PublishAnnouncement.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    openSettings();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // navigating user to app settings
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void openSettings() {
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
        intent.setData(uri);
        startActivity(intent);
        startActivityForResult(intent, 101);
    }

    /**
     * Starts a sign-in activity using {@link #REQUEST_CODE_SIGN_IN}.
     */
    private void requestSignIn() {
        Log.d(TAG, "Requesting sign-in");

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .requestEmail()
                        .build();
        googleSignInClient = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
                break;
            case PICK_FILE_REQUEST:
                if (resultCode == RESULT_OK) {
                    pickiT.getPath(resultData.getData(), Build.VERSION.SDK_INT);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }
    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    Log.d(TAG, "Signed in as " + googleAccount.getEmail());
                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());

                    Drive googleDriveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("Drive API Migration")
                                    .build();
                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                    // Its instantiation is required before handling any onClick actions.
                    mDriveServiceHelper = new DriveServiceHelper(googleDriveService, timeStamp);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Unable to sign in." + exception, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // This method will get call when user click on upload file button
    public void uploadFile() {

        Intent intent;
        if (android.os.Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
            intent.putExtra("CONTENT_TYPE", "*/*");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            Log.e(TAG, "uploadFile: if");
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT); // or ACTION_OPEN_DOCUMENT
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            Log.e(TAG, "uploadFile: else");
        }
        startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);

    }

    // This method will get call when user click on create folder button
    public void createFolder() {
        if (mDriveServiceHelper != null) {
            // check folder present or not
            mDriveServiceHelper.isFolderPresent()
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String id) {
                            if (id.isEmpty()) {
                                mDriveServiceHelper.createFolder()
                                        .addOnSuccessListener(new OnSuccessListener<String>() {
                                            @Override
                                            public void onSuccess(String fileId) {
                                                Log.e(TAG, "folder id: " + fileId);
                                                folderId = fileId;
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {

                                                Log.e(TAG, "Couldn't create file.", exception);
                                                System.out.println(exception);
                                            }
                                        });
                            } else {
                                folderId = id;

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e(TAG, "Couldn't create file..", exception);
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
        requestSignIn();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        showSettingsDialog();
    }

    @Override
    public void PickiTonUriReturned() {

    }

    @Override
    public void PickiTonStartListener() {

    }

    @Override
    public void PickiTonProgressUpdate(int progress) {
    }

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {
        // Get the Uri of the selected file
        if (path != null && !path.equals("")) {
            if (mDriveServiceHelper != null) {
                mDriveServiceHelper.uploadFileToGoogleDrive(path, timeStamp, "Post")
                        .addOnSuccessListener(new OnSuccessListener<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                Toast.makeText(getApplicationContext(), "File uploaded ...!!", Toast.LENGTH_SHORT).show();
                                Query query = FirebaseDatabase.getInstance().getReference("Post").orderByChild("PostId").equalTo(timeStamp);
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            String fileId = "" + ds.child("FileId").getValue();
                                            Glide
                                                    .with(PublishAnnouncement.this)
                                                    .load("https://docs.google.com/uc?id="+fileId)
                                                    .centerCrop()
                                                    .into(ImageV);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Couldn't able to upload file, error: " + e, Toast.LENGTH_SHORT).show();
                                System.out.println(e);
                            }
                        });
            }
        } else {
            Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void PickiTonMultipleCompleteListener(ArrayList<String> paths, boolean wasSuccessful, String Reason) {
    }
}