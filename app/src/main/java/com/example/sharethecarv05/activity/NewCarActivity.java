package com.example.sharethecarv05.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
// Import necessary classes
import com.example.sharethecarv05.user.Parent;
import com.example.sharethecarv05.R;
import com.example.sharethecarv05.user.User;
import com.example.sharethecarv05.user.UserManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class NewCarActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Button btnCancel;
    Button btnAddCar;
    Button btnCarIdImge;
    Button btnGallery;
    EditText etCarNum;
    String carModel;
    User user;

    private static final int CAMERA_REQUEST_CODE = 1001;
    private static final int PERMISSION_REQUEST_CODE = 2000;
    int SELECT_PICTURE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car);
        //שומר את המיקומים
        btnCancel = findViewById(R.id.btnCancel);
        btnAddCar = findViewById(R.id.btnAddCar);
        etCarNum=findViewById(R.id.carnum);
        btnCarIdImge = findViewById(R.id.btnCarIdImge);
        btnGallery = findViewById(R.id.btnGallery);
        //מפעיל מקשיבי לחיצה על הקפתורים
        btnCancel.setOnClickListener(this);
        btnAddCar.setOnClickListener(this);
        btnCarIdImge.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        //מקבל את המידה מהאינתנת
        Intent intent =getIntent();
        user = (User) intent.getSerializableExtra("bili");

        if(ContextCompat.checkSelfPermission(NewCarActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(NewCarActivity.this, new String[]{
                    Manifest.permission.CAMERA
            },100);
        }
        //מפעיל את הספיניר
        Spinner spinner = (Spinner) findViewById(R.id.spinnerCarModels);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinnerCarModels,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        FirebaseApp.initializeApp(this);


    }

    @Override
    public void onClick(View view) {
        //מחזיר אוחורה למסך המחוניות ולא מוסף רחב חדש
        if (btnCancel == view) {
            Intent intent = new Intent(NewCarActivity.this, CarsActivity.class);
            intent.putExtra("bili", user);
            startActivity(intent);
        }//מוסיף מחונית חדשה לעחר הבדיקות הזחוצות
        else if (btnAddCar == view) {
            //בודק שהחניסו את המידה הנחוץ
            if (etCarNum.getText().toString() == null)
                Toast.makeText(this, "Car license plate number cannot be empty", Toast.LENGTH_SHORT).show();
            else if (carModel == null||carModel.equals("Pick A Car Modle"))
                Toast.makeText(this, "Car model cannot be empty", Toast.LENGTH_SHORT).show();
            //שולאך את הרחב להוספה ולאחר תשובה או מחזיר למסך הקודם או אומר שמספר הרחב בשימוש
            else {
                if (((Parent) user).CreateCar(carModel, etCarNum.getText().toString())) {
                    UserManager.Update(user);
                    Intent intent = new Intent(NewCarActivity.this, CarsActivity.class);
                    intent.putExtra("bili", user);
                    startActivity(intent);
                } else
                    Toast.makeText(this, "Car license plate number already in use", Toast.LENGTH_SHORT).show();
            }

        }//כורה לפעולה שתיפתך את המצלמה
        else if(btnCarIdImge == view){
            checkPermissionsAndOpenCamera();
        }//כורה לפעולה שפותאחת את הגלריה לבחירת תמונה
        else if (btnGallery == view) {
            imageChooser();
        }
    }
    //קשבוחרים חברת רחב זה שומר את הרחב הניבחר
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        carModel = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    //בודק אים יש לאפליקציה את הרשות ליפתואך את המצלמה ואים לא עז מבקש ועז פותאך את המצלמה
    private void checkPermissionsAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE);
        } else {
            openCamera();
        }
    }
    //פותאך מצלמה
    private void openCamera()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //מקבל את התמושנה שומר אותה וכורה לפעולת גזיה
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Uri imageUri = getImageUri(imageBitmap); // Save image to gallery and get URI
            if (imageUri != null) {
                startCropActivity(imageUri);
            } else {
                Toast.makeText(this, "Error saving image to gallery", Toast.LENGTH_SHORT).show();
            }
        }//מקבל את התמונה אחרי גזירה שומר אותה ושולאך את התמונה לאיבוד
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                processImage(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error cropping image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    startCropActivity(selectedImageUri);
                }
                else
                    Toast.makeText(this, "Error saving image to gallery", Toast.LENGTH_SHORT).show();
            }
        }

    }//פותאך את הגלריה נותן ליוזר לבחור תמונה ושולאך אותה לגזירה ואיבוד
    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // Helper method to get a URI for a bitmap (assuming you have a helper method for saving the image)
    private Uri getImageUri(Bitmap imageBitmap) {
        String imageFileName = "OCR_Image_" + System.currentTimeMillis() + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName);

        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            // Add the image to the gallery (optional)
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(imageFile);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);

            return contentUri;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    //הפעולה שמפעילה את גזירת התמונה
    private void startCropActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON) // Optional: Set a specific aspect ratio
                .start(this);
    }
    //הפעולה  שמעבדת את התמונה
    private void processImage(Uri imageUri) {
        try {
            FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(this, imageUri);
            FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();
            textRecognizer.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText text) {
                            displayTextFromImage(text);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewCarActivity.this, "Error recognizing text: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IOException e) {
            Toast.makeText(this, "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    //סם את הטקסט המתקבל באדית טקסט
    private void displayTextFromImage(FirebaseVisionText text) {
        StringBuilder textBuilder = new StringBuilder();
        for (FirebaseVisionText.TextBlock block : text.getTextBlocks()) {
            textBuilder.append(block.getText());
            textBuilder.append("\n");
        }
        etCarNum.setText(textBuilder.toString());//end resolt
    }
}