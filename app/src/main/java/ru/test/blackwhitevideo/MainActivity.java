package ru.test.blackwhitevideo;

import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.res.ResourcesCompat;
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

        int[] reds = new int[pixelsCount];
        int[] greens = new int[pixelsCount];
        int[] blues = new int[pixelsCount];

        for (int i = 0; i < pixelsCount; i++) {
            reds[i] = Color.red(pixels[i]);
            greens[i] = Color.green(pixels[i]);
            blues[i] = Color.blue(pixels[i]);
        }

        int newPixelsWidth = bitmap.getWidth() - 2;
        int newPixelsHeight = bitmap.getHeight() - 2;

        ImageMatrix redPixelsMatrix = new ImageMatrix(reds, bitmap.getWidth(), bitmap.getHeight());
        ImageMatrix greenPixelsMatrix = new ImageMatrix(greens, bitmap.getWidth(), bitmap.getHeight());
        ImageMatrix bluePixelsMatrix = new ImageMatrix(blues, bitmap.getWidth(), bitmap.getHeight());

        int[] filter = {
                1, 2, 1,
                0, 0, 0,
                -1, -2, -1
        };

        redPixelsMatrix.setFilter(filter);
        greenPixelsMatrix.setFilter(filter);
        bluePixelsMatrix.setFilter(filter);

        int[] newReds = redPixelsMatrix.colorMatrixToBlackAndWhite().getMatrix();
        int[] newGreens = greenPixelsMatrix.colorMatrixToBlackAndWhite().getMatrix();
        int[] newBlues = bluePixelsMatrix.colorMatrixToBlackAndWhite().getMatrix();

        int[] newPixels = new int[newReds.length];
        for (int i = 0; i < newPixels.length; i++) {
            newPixels[i] = Color.rgb(newReds[i], newGreens[i], newBlues[i]);
        }

        Bitmap filteredImageBitmap = Bitmap.createBitmap(newPixelsWidth, newPixelsHeight, Bitmap.Config.ARGB_8888);
        filteredImageBitmap.setPixels(newPixels, 0, filteredImageBitmap.getWidth(), 0, 0, newPixelsWidth, newPixelsHeight);
        mFilteredImageView.setImageBitmap(filteredImageBitmap);
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