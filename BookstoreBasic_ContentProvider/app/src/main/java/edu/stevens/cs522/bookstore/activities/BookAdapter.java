package edu.stevens.cs522.bookstore.activities;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.util.regex.Pattern;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.contracts.BookContract;


public class BookAdapter extends ResourceCursorAdapter {

    protected final static int ROW_LAYOUT = android.R.layout.simple_list_item_2;

    public BookAdapter(Context context, Cursor cursor){
        super(context, ROW_LAYOUT, cursor,0);
    }

    @Override
    public View newView(Context context, Cursor cur, ViewGroup parent){
        //View view = super.newView(context,cur,parent);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(ROW_LAYOUT, parent, false);
        bindView(view,context,cur);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        TextView titleLine = (TextView)view.findViewById(android.R.id.text1);
        TextView authorLine = (TextView)view.findViewById(android.R.id.text2);
        String Title = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.TITLE));
        titleLine.setText(Title);
        String Authors = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.Authors));
        if(Authors!=null && Authors.length()>0){
            String[] Author = Authors.split(Pattern.quote("|"));
            authorLine.setText(Author[0]);
        }
        else{authorLine.setText("");}

    }
}
