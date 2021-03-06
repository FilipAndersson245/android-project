package se.ju.myapplication.Create.MemeTemplate;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import se.ju.myapplication.API.Connection;
import se.ju.myapplication.Models.MemeTemplate;
import se.ju.myapplication.R;

import static android.widget.Toast.makeText;

public class CreateMemeTemplateActivity extends Activity {

    private static final int TAKE_PICTURE = 1;
    private static final int PICK_PHOTO_FOR_TEMPLATE = 2;

    private static final int PERMISSION_FILES_CODE = 1;
    private static final int PERMISSION_CAMERA_CODE = 2;

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meme_template);

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
                    Log.e("value", getString(R.string.permission_granted_local_drive));
                } else {
                    Log.e("value", getString(R.string.permision_denied_local_drive));
                }
                break;
            case PERMISSION_CAMERA_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", getString(R.string.permission_granted_local_drive));
                } else {
                    Log.e("value", getString(R.string.permision_denied_local_drive));
                }
                break;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
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

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri imageUri = FileProvider.getUriForFile(this,
                        "se.ju.myapplication.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, TAKE_PICTURE);
            }
        }

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
        mCurrentPhotoPath = getRealPathFromURI(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, this);
        startActivityForResult(intent, PICK_PHOTO_FOR_TEMPLATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK) {

                    File imgFile = new File(mCurrentPhotoPath);

                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);

                        try {
                            myBitmap = rotateImageCorrectly(myBitmap, mCurrentPhotoPath);

                            ImageView myImage = (ImageView) findViewById(R.id.createMemeTemplateImage);
                            myImage.setImageBitmap(myBitmap);


                        } catch (Exception e) {}
                    }
                }
                break;
            case PICK_PHOTO_FOR_TEMPLATE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = data.getData();

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(imageUri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mCurrentPhotoPath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                    Bitmap localBitmap = bitmap;

                    ImageView myImage = findViewById(R.id.createMemeTemplateImage);
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

        findViewById(R.id.layoutProgressBar).setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        String title = ((EditText) findViewById(R.id.editMemeTemplateTitle)).getText().toString();

        File imgFile = new File(mCurrentPhotoPath);

        try {
            Connection.getInstance().createMemeTemplate(title, Connection.getInstance().getSignedInUsername(), imgFile, returnedObject -> {
                try {
                    this.finish();
                } catch (Exception e)
                {
                    String error = (String) returnedObject;

                    new Handler(getBaseContext().getMainLooper()).post(() -> {
                        findViewById(R.id.layoutProgressBar).setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        } catch (JsonProcessingException e) {
            Toast.makeText(this, R.string.meme_template_unable_to_create, Toast.LENGTH_SHORT).show();
            findViewById(R.id.layoutProgressBar).setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            return s;
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
