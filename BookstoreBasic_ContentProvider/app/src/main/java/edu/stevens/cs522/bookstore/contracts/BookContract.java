package edu.stevens.cs522.bookstore.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.regex.Pattern;


public class BookContract {

    public static final String AUTHORITY = "edu.stevens.cs522.bookstore";
    //public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String path="books";
    public static final String pathAuthor="authors";


    public static final String Id = "_id";
    public static final int ID_KEY=0;
    public static final String TITLE = "title";
    public static final int TITLE_KEY=1;
    public static final String Authors = "authors";
    public static final String Price = "price";
    public static final int PRICE_KEY=2;
    public static final String ISBN = "ISBN";
    public static final int ISBN_KEY=3;
    public static final String[] Author = new String[2];
    public static final char SEPERATOR_CHAR = '|';

    private static final Pattern SEPERATOR = Pattern.compile(Character.toString(SEPERATOR_CHAR), Pattern.LITERAL);
    public static final String FirstName = "FirstName";
    public static final String MiddleName = "MiddleName";
    public static final String LastName = "LastName";
    public static final String Book_FK ="Book_FK";


    public static Uri CONTENT_URI(String authority, String Path){
       return new Uri.Builder().build().buildUpon().scheme("content").authority(authority).path(Path).build();
    }

    public static Uri CONTENT_URI_Books = CONTENT_URI(AUTHORITY, path);
    public static Uri CONTENT_URI_Authors = CONTENT_URI(AUTHORITY, pathAuthor);


    public static Uri withExtendedPath(Uri uri, String... Path){
        Uri.Builder builder = uri.buildUpon();
        for(String p : Path)
            builder.appendPath(p);
        return builder.build();
    }


    public static Uri CONTENT_URI(String ID, Uri uri){
        return withExtendedPath(uri,ID);
    }


    public static long getId(Uri uri){
        return Long.parseLong(uri.getLastPathSegment());
    }

    public static String CONTENT_PATH(Uri uri){
        return uri.getPath().substring(1);
    }

    public static final String CONTENT_Book_PATH = CONTENT_PATH(CONTENT_URI_Books);

    public static final String CONTENT_Book_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#",CONTENT_URI_Books));

    public static final String CONTENT_Author_PATH = CONTENT_PATH(CONTENT_URI_Authors);

    public static final String CONTENT_Author_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#",CONTENT_URI_Authors));

    public static String contentType(String content){
        return "vnd.android.cursor/vnd." + AUTHORITY + "." + content + "s";
    }

    public static String contentItemType(String content){
        return "vnd.android.cursor.item/vnd." + AUTHORITY + "." + content;
    }

    public static String getTitle(Cursor cursor){
        return cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
    }

    public static String getAuthors(Cursor cursor){
        return cursor.getString(cursor.getColumnIndexOrThrow(Authors));
    }

    public static int getId(Cursor cursor){
        return cursor.getInt(cursor.getColumnIndexOrThrow(Id));
    }

    public static String getPrice(Cursor cursor){
        return cursor.getString(cursor.getColumnIndexOrThrow(Price));
    }

    public static String getIsbn(Cursor cursor){
        return cursor.getString(cursor.getColumnIndexOrThrow(ISBN));
    }

    public static void setTitle(ContentValues values, String title){
        values.put(TITLE, title);
    }

    public static void setAuthors(ContentValues values, String author){
        values.put(Authors, author);
    }

    public static void setFirstName(ContentValues values, String firstName){
        values.put(FirstName, firstName);
    }

    public static void setMiddleName(ContentValues values, String middleName){
        values.put(MiddleName, middleName);
    }

    public static void setLastName(ContentValues values, String lastName){
        values.put(LastName, lastName);
    }

    public static void setBook_FK(ContentValues values, long FK){
        values.put(Book_FK,FK);
    }
    //public static void setAuthor(ContentValues values, String authors){
        //Author = readStringArray(authors);
    //}

    public static String[] readStringArray(String in){
        return SEPERATOR.split(in);
    }

    //public static void setFirstName(ContentValues values, String author){
      //  values.put(FirstName,);
    //}


    public static void setPrice(ContentValues values, String price){
        values.put(Price, price);
    }

    public static void setIsbn(ContentValues values, String isbn){
        values.put(ISBN, isbn);
    }


}
