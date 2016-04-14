package edu.stevens.cs522.bookstore.BookProvider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import edu.stevens.cs522.bookstore.databases.CartDbAdapter;

import edu.stevens.cs522.bookstore.contracts.BookContract;

public class BookProvider extends ContentProvider {
    private static final String Database_Name = "BookCart1";
    private static final int Database_Version = 2;
    private static final String BOOK_TABLE = "books";
    private static final String AUTHOR_TABLE = "authors";

    private static final String Col_Id = "_id";
    private static final String Col_Title = "title";
    private static final String Col_price = "price";
    private static final String Col_isbn = "ISBN";
    private static final String Col_Author = "authors";


    private static final String Col_FirstName = "FirstName";
    private static final String Col_MiddleName = "MiddleName";
    private static final String Col_LastName = "LastName";
    private static final String Col_Book_FK = "Book_FK";

    private DatabaseHelper dbHelper;
    //private final Context Ctx;

    private SQLiteDatabase db;
    private static final int ALL_ROWS=1;
    private static final int SINGLE_ROW=2;
    private static final int Authors=3;
    private static final int Author_SINGLE_ROW=4;
    private static final UriMatcher uriMatcher;
    private CartDbAdapter adapter;

    //private static final String BookId_info = "Select MAX(" + Col_Id + ") as " + Col_Id + " from " + BOOK_TABLE;
    private static final String Books_info = "Select b." + Col_Id + ", b." + Col_Title + ", b." + Col_price + ", b." + Col_isbn +
            ", group_concat(ifnull(" + Col_FirstName + ", '') " + " " + " || ifnull(" + Col_MiddleName + ", ' ') " + " " + " || ifnull(" + Col_LastName  + ", ''),'|') as " + Col_Author + " from " + BOOK_TABLE + " as b left outer join " + AUTHOR_TABLE + " as a on b." +
            Col_Id + " = a." + Col_Book_FK + " group by b." + Col_Id + ", b." + Col_Title + ", b." + Col_price + ", b." + Col_isbn;

    private static final String Book_info = "Select b." + Col_Id + ", b." + Col_Title + ", b." + Col_price + ", b." + Col_isbn +
            ", group_concat(ifnull(" + Col_FirstName + ", '') " + " " + " || ifnull(" + Col_MiddleName + ", ' ') " + " " + " || ifnull(" + Col_LastName  + ", ''),'|') as " + Col_Author + " from " + BOOK_TABLE + " as b left outer join " + AUTHOR_TABLE + " as a on b." +
            Col_Id + " = a." + Col_Book_FK + " where b." + Col_Id + "=";


    private static final String Create_table_book = "CREATE TABLE " + BOOK_TABLE + "(" +
            Col_Id + " INTEGER PRIMARY KEY autoincrement, " + Col_Title + " TEXT, " + Col_price + " TEXT, "
            + Col_isbn + " TEXT" + ")";

    private static final String Create_table_author = "CREATE TABLE " + AUTHOR_TABLE + " (" +
            Col_Id + " INTEGER PRIMARY KEY autoincrement, " + Col_FirstName + " TEXT, " + Col_MiddleName + " TEXT, "
            + Col_LastName + " TEXT, " + Col_Book_FK + " INTEGER NOT NULL, FOREIGN KEY (" + Col_Book_FK +
            ") REFERENCES " +  BOOK_TABLE + "(" + Col_Id + ")" + " ON DELETE CASCADE)";

    private static final String Create_index_AuthorsBookIndex = "CREATE INDEX AuthorsBookIndex ON " + AUTHOR_TABLE +
            "(" + Col_Book_FK + ")";



    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, Database_Name, null, Database_Version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("PRAGMA foreign_keys=ON;");
            db.execSQL(Create_table_book);
            db.execSQL(Create_table_author);
            db.execSQL(Create_index_AuthorsBookIndex);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + BOOK_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + AUTHOR_TABLE);
            onCreate(db);
        }

    }

   @Override
    public boolean onCreate(){
       dbHelper = new DatabaseHelper(getContext());
       db = dbHelper.getWritableDatabase();
       return true;
   }

   static {
       uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
       uriMatcher.addURI(BookContract.AUTHORITY, BookContract.CONTENT_Book_PATH, ALL_ROWS);
       uriMatcher.addURI(BookContract.AUTHORITY, BookContract.CONTENT_Book_PATH_ITEM, SINGLE_ROW);
       uriMatcher.addURI(BookContract.AUTHORITY, BookContract.CONTENT_Author_PATH, Authors);
       uriMatcher.addURI(BookContract.AUTHORITY, BookContract.CONTENT_Author_PATH_ITEM, Author_SINGLE_ROW);
   }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort){
        Cursor cursor=null;
        switch (uriMatcher.match(uri)){
            case ALL_ROWS:
                cursor = db.rawQuery(Books_info, null);
                cursor.setNotificationUri(getContext().getContentResolver(),uri);
                break;
            case SINGLE_ROW:
                String[] Id = {""};
                Id[0]= Long.toString(BookContract.getId(uri));
                cursor = db.rawQuery(Book_info + Id[0],null);
                break;
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){
        int uriType = uriMatcher.match(uri);
        Uri instanceUri = null;
        switch (uriType){
            case ALL_ROWS:
                long row = db.insert(BOOK_TABLE, null, values);
                if(row>0){
                    instanceUri = BookContract.CONTENT_URI(Long.toString(row), BookContract.CONTENT_URI_Books);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceUri, null);
                }
                break;
            case Authors:
                long row1 = db.insert(AUTHOR_TABLE, null, values);
                if(row1>0){
                    instanceUri = BookContract.CONTENT_URI(Long.toString(row1), BookContract.CONTENT_URI_Authors);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceUri, null);
                }
                break;
            default: throw new SQLException("Insertion Failed");

        }
        return instanceUri;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs){
        int count;
        switch (uriMatcher.match(uri)){
            case ALL_ROWS:
                count = db.delete(BOOK_TABLE, where, whereArgs);
                break;
            case Authors:
                count = db.delete(AUTHOR_TABLE, where, whereArgs);
                break;
                //ount = db.delete(BOOK_TABLE,BookContract.Id + "=")
            default:throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs){
        switch (uriMatcher.match(uri)){
            case ALL_ROWS:
            case SINGLE_ROW:
            default:throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public String getType(Uri _uri){
        switch (uriMatcher.match(_uri)){
            case ALL_ROWS:
                return BookContract.contentType("book");
            case SINGLE_ROW:
                return BookContract.contentItemType("book");
            default:throw new IllegalArgumentException("Unsupported URI: " + _uri);
        }

    }

}
