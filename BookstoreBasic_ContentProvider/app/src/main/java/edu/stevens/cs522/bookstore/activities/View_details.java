package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.regex.Pattern;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Book;

public class View_details extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);
        setTitle("Book Details");

        Book book;
        Intent intent=getIntent();
        Bundle b = intent.getExtras();
        book = b.getParcelable("Book");

        //book = book_details.get(position);
        TextView book_name = (TextView)findViewById(R.id.bookName);
        //String name = book.title;
        book_name.setText("Book:" + "    " + book.title);

        TextView authorName1 = (TextView)findViewById(R.id.authorName1);
        TextView authorName2 = (TextView)findViewById(R.id.authorName2);
        if(book.authors!=null && book.authors.length()>0){
            String[] Author = book.authors.split(Pattern.quote("|"));
            authorName1.setText("Author1: " + Author[0]);
            authorName2.setText("Author2: " + Author[1]);
        }
        else{
            authorName1.setText("");
            authorName2.setText("");
        }


        TextView isbn = (TextView)findViewById(R.id.isbn);
        isbn.setText("ISBN:" + "    " + book.isbn);

        TextView price = (TextView)findViewById(R.id.price);
        price.setText("Price:" + "    " + "$" + book.price);

        Button Back = (Button)findViewById(R.id.BackButton);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), BookStoreActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //setContentView(R.layout.activity_view_details);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        // SEARCH: return the book details to the BookStore activity

        // CANCEL: cancel the search request
        return false;
    }


}
