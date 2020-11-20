package com.example.book_master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.book_master.adapter.CustomImageList;
import com.example.book_master.models.Book;
import com.example.book_master.models.DBHelper;
import com.example.book_master.models.Image;
import com.example.book_master.models.UserList;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class edit_book_activity extends AppCompatActivity {
    private EditText Title;
    private EditText Author;
    private Button Confirm;
    private Button Discard;
    private Button uploadImage;

    private ArrayList<Image> imageList;
    private CustomImageList imageAdapter;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_book);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        book = (Book) bundle.getSerializable("book_edit");

        Title = (EditText) findViewById(R.id.edit_book_name);
        Author = (EditText) findViewById(R.id.edit_book_author);
        Confirm = (Button) findViewById(R.id.edit_confirm_button);
        Discard = (Button) findViewById(R.id.edit_discard_buttom);
        uploadImage = (Button) findViewById(R.id.edit_book_uploadImage);

        imageList = new ArrayList<Image>();
        imageAdapter = new CustomImageList(imageList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.edit_book_imagineRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(imageAdapter);
        DBHelper.retrieveImagine(imageList, imageAdapter, book.getISBN(), this);

        Title.setText(book.getTitle());
        Author.setText(book.getAuthor());

        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String book_title = Title.getText().toString();
                String book_Author = Author.getText().toString();

                if (!book_Author.equals("") && !book_title.equals("")) {
                    book.setTitle(book_title);
                    book.setAuthor(book_Author);

                    DBHelper.setBookDoc(book.getISBN(), book, edit_book_activity.this);

                    Intent intent = new Intent(edit_book_activity.this, check_list_activity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(edit_book_activity.this, "Field is not filled.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        Discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(edit_book_activity.this, check_list_activity.class);
                startActivity(intent);
            }
        });

        // upload an images to Firebase Storage
        // Reference: https://www.geeksforgeeks.org/android-how-to-upload-an-image-on-firebase-storage/
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    // Select Image method
    private void selectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();

            if (filePath != null) {
                DBHelper.uploadImagine(filePath, book.getISBN(), edit_book_activity.this);
            }
        }
    }
}