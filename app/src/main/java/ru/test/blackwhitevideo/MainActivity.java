package ru.test.blackwhitevideo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import ru.test.blackwhitevideo.model.ImageMatrix;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private final int PICK_IMAGE = 0;

    private Button mPickImageButton;
    private ImageView mSourceImageView;
    private ImageView mFilteredImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setOnClickListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                mSourceImageView.setImageURI(data.getData());
                try {
                    filterImage(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void filterImage(Bitmap bitmap) {
        int pixelsCount = bitmap.getWidth() * bitmap.getHeight();
        int[] pixels = new int[pixelsCount];

        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        short[] reds = new short[pixelsCount];
        short[] greens = new short[pixelsCount];
        short[] blues = new short[pixelsCount];

        for (int i = 0; i < pixelsCount; i++) {
            reds[i] = (short) Color.red(pixels[i]);
            greens[i] = (short) Color.green(pixels[i]);
            blues[i] = (short) Color.blue(pixels[i]);
        }

        int newPixelsWidth = bitmap.getWidth() - 2;
        int newPixelsHeight = bitmap.getHeight() - 2;

        ImageMatrix oneColorImageMatrix = new ImageMatrix(reds, bitmap.getWidth(), bitmap.getHeight());

        int[] filter = {
                1, 2, 1,
                0, 0, 0,
                -1, -2, -1
        };

        oneColorImageMatrix.setFilter(filter);

        oneColorImageMatrix.colorMatrixToBlackAndWhite();
        reds = oneColorImageMatrix.getMatrix();

        oneColorImageMatrix.setImageMatrix(greens, bitmap.getWidth(), bitmap.getHeight());
        oneColorImageMatrix.colorMatrixToBlackAndWhite();

        greens = oneColorImageMatrix.getMatrix();
        oneColorImageMatrix.setImageMatrix(blues, bitmap.getWidth(), bitmap.getHeight());
        oneColorImageMatrix.colorMatrixToBlackAndWhite();

        blues = oneColorImageMatrix.getMatrix();
        oneColorImageMatrix = null;
        
        pixels = new int[reds.length];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = Color.rgb(reds[i], greens[i], blues[i]);
        }

        bitmap = Bitmap.createBitmap(newPixelsWidth, newPixelsHeight, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, newPixelsWidth, newPixelsHeight);
        mFilteredImageView.setImageBitmap(bitmap);
    }

    private void findViews() {
        mPickImageButton = findViewById(R.id.pick_image_button);
        mSourceImageView = findViewById(R.id.source_image_view);
        mFilteredImageView = findViewById(R.id.filtered_image_view);
    }

    private void setOnClickListeners() {
        mPickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }
}