package com.example.book_master.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.book_master.R;
import com.example.book_master.models.Book;

import java.util.ArrayList;

public class CustomBorrowList extends ArrayAdapter<Book> {
    private ArrayList<Book> bookData;
    private Context context;

    public CustomBorrowList(Context context, ArrayList<Book> bookData){
        super(context, 0, bookData);
        this.context = context;
        this.bookData = bookData;
    }

    @Nullable
    @Override
    public View getView(int position, @androidx.annotation.Nullable View convertView, @NonNull final ViewGroup parent){
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.borrow_list_content, parent,false);
        }

        final Book book = bookData.get(position);
        TextView name = view.findViewById(R.id.Borrow_list_name);
        TextView author = view.findViewById(R.id.Borrow_list_author);
        name.setText(book.getTitle());
        author.setText(book.getAuthor());

        return view;
    }
}
