package se.ju.myapplication.Create.MemeTemplate;

import android.annotation.SuppressLint;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import se.ju.myapplication.API.Connection;
import se.ju.myapplication.R;

import static android.support.v4.content.FileProvider.getUriForFile;
import static android.widget.Toast.makeText;

public class CreateMemeTemplateActivity extends Activity {

    private static final int TAKE_PICTURE = 1;
    private static final int PICK_PHOTO_FOR_TEMPLATE = 2;

    private static final int PERMISSION_FILES_CODE = 1;
    private static final int PERMISSION_CAMERA_CODE = 2;


    private Uri imageUri;
    private ImageView templateImage;
    private boolean validPicture;

    private Bitmap localBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meme_template);

        this.templateImage = findViewById(R.id.createMemeTemplateImage);

        findViewById(R.id.createMemetemplateButton).setOnClickListener((View v) -> {
            createMemeTemplateButtonClicked();
        });
    }

    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(this, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission, int responseCode) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            // Permission is not granted
            makeText(this, getString(R.string.external_storage_explaination), Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, responseCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_FILES_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
            case PERMISSION_CAMERA_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    public void takePhoto(View view) {
        if (Build.VERSION.SDK_INT < 23) {
            makeText(this, R.string.no_file_access, Toast.LENGTH_LONG).show();
            return;
        }

        if (!checkPermission(Manifest.permission.CAMERA)) {
            requestPermission(android.Manifest.permission.CAMERA, PERMISSION_CAMERA_CODE);
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(new File(this.getFilesDir(), "images"), "Pic.jpg");
        Uri imageUri = getUriForFile(this, "se.ju.myapplication.fileprovider", photo);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    public void pickImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_PHOTO_FOR_TEMPLATE);
    }

    public void selectPhoto(View view) {
        if (Build.VERSION.SDK_INT < 23) {
            makeText(this, R.string.no_file_access, Toast.LENGTH_LONG).show();
            return;
        }

        if (!checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_FILES_CODE);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_PHOTO_FOR_TEMPLATE);
        // makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;

                    File imgFile = new File(imageUri.getPath());
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imageUri.getPath());

                        //Drawable d = new BitmapDrawable(getResources(), myBitmap);
                        try {
                            myBitmap = rotateImageCorrectly(myBitmap, imageUri.getPath());

                            ImageView myImage = (ImageView) findViewById(R.id.createMemeTemplateImage);
                            myImage.setImageBitmap(myBitmap);


                        } catch (Exception e) {
                            System.out.println("xxxxxxxxx: " + e.getCause().toString());
                        }


                    }
                }
                break;
            case PICK_PHOTO_FOR_TEMPLATE:
                if (resultCode == Activity.RESULT_OK) {
//                    Uri selectedImage = data.getData();
                    imageUri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(imageUri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                    localBitmap = bitmap;

                    ImageView myImage = (ImageView) findViewById(R.id.createMemeTemplateImage);
                    myImage.setImageBitmap(bitmap);
                }
        }
    }

    private static Bitmap rotateImageCorrectly(Bitmap bitmap, String path) {
        Bitmap myBitmap = bitmap;
        try {
            ExifInterface exif = new ExifInterface(path);
            final int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    myBitmap = rotateImage(myBitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    myBitmap = rotateImage(myBitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    myBitmap = rotateImage(myBitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    break;
            }
            return myBitmap;
        } catch (Exception e) {
            return null;
        }


    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void createMemeTemplateButtonClicked() {
        String title = ((EditText) findViewById(R.id.editMemeTemplateTitle)).getText().toString();

        File imgFile = new File(imageUri.getPath());

        try {
            Connection.getInstance().createMemeTemplate(title, Connection.getInstance().getSignedInUsername(), imgFile, callback  -> {
                Handler mainHandler = new Handler(getBaseContext().getMainLooper());

                Runnable myRunnable = () -> { };

                mainHandler.post(myRunnable);
            });
            System.out.println("###### UPLOAD TEMPLATE WORKS");
        } catch (JsonProcessingException e) {
            Toast.makeText(this, R.string.meme_template_unable_to_create, Toast.LENGTH_SHORT).show();
        }
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
