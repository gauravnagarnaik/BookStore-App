package edu.stevens.cs522.bookstore.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.database.SQLException;

import edu.stevens.cs522.bookstore.entities.Book;


public class CartDbAdapter {

    private static final String Database_Name = "BookCart";
    private static final int Database_Version = 12;
    private static final String BOOK_TABLE = "table_book";
    private static final String AUTHOR_TABLE = "table_author";

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
    private final Context Ctx;
    private SQLiteDatabase db;

    //private static final String BookId_info = "Select " + Col_Id + " from " + BOOK_TABLE + "where";
    private static final String BookId_info = "Select MAX(" + Col_Id + ") as " + Col_Id + " from " + BOOK_TABLE;
    private static final String Book_info = "Select b." + Col_Id + ", b." + Col_Title + ", b." + Col_price + ", b." + Col_isbn +
            ", Group_concat(ifnull(" + Col_FirstName + ", '') " + " " + " || ifnull(" + Col_MiddleName + ", ' ') " + " " + " || ifnull(" + Col_LastName  + ", ''),'|') as " + Col_Author + " from " + BOOK_TABLE + " as b left outer join " + AUTHOR_TABLE + " as a on b." +
            Col_Id + " = a." + Col_Book_FK + " group by b." + Col_Id + ", b." + Col_Title + ", b." + Col_price + ", b." + Col_isbn;

    private static final String Create_table_book = "CREATE TABLE " + BOOK_TABLE + "(" +
            Col_Id + " INTEGER PRIMARY KEY autoincrement, " + Col_Title + " TEXT, " + Col_price + " TEXT, "
            + Col_isbn + " TEXT" + ")";

    private static final String Create_table_author = "CREATE TABLE " + AUTHOR_TABLE + " (" +
            Col_Id + " INTEGER PRIMARY KEY autoincrement, " + Col_FirstName + " TEXT, " + Col_MiddleName + " TEXT, "
            + Col_LastName + " TEXT, " + Col_Book_FK + " INTEGER NOT NULL, FOREIGN KEY (" + Col_Book_FK +
            ") REFERENCES " +  BOOK_TABLE + "(" + Col_Id + ")" + " ON DELETE CASCADE)";

    private static final String Create_index_AuthorsBookIndex = "CREATE INDEX AuthorsBookIndex ON " + AUTHOR_TABLE +
            "(" + Col_Book_FK + ")";

    public CartDbAdapter(Context context){
        this.Ctx = context;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, CartDbAdapter.Database_Name, null, CartDbAdapter.Database_Version);
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

    public CartDbAdapter open() throws SQLException{
        this.dbHelper = new DatabaseHelper(this.Ctx);
        //this.db.execSQL("PRAGMA	foreign_keys=ON;");
        this.db = this.dbHelper.getWritableDatabase();
        return this;
    }

    public Cursor fetchAllBooks(){
        return db.rawQuery(Book_info, null);
        //return db.query(BOOK_TABLE, new String[]{Col_Id,Col_Title, Col_price, Col_isbn},null,null,null,null,null);
    }

    public Book fetchBook(long rowId){

        Cursor cursor = this.db.query(BOOK_TABLE, new String[]{Col_Id,Col_Title, Col_price, Col_isbn},Col_Id + " = " + rowId,
                null,null,null,null);
        Book book;
        if(cursor !=null){
            cursor.moveToFirst();
            book = new Book(cursor);
            return book;
        }
        else {
            return null;
        }
    }


    public void persist(Book book) throws SQLException{

        ContentValues values_book = new ContentValues();
        ContentValues values_Authors = new ContentValues();
        book.writeToProvider(values_book);
        db.beginTransaction();
        try {
            db.insert(BOOK_TABLE, null, values_book);
            //Cursor cursor = db.query(BOOK_TABLE,new String[]{Col_Id}, Col_isbn + " = " + book.isbn,null,null,null,null);
            Cursor cursor = db.rawQuery(BookId_info, null);
            cursor.moveToFirst();
            int Book_FK = cursor.getInt(cursor.getColumnIndexOrThrow(Col_Id));
            for(int i=0; i<book.author.length;i++) {
                book.writeToProviderAuthor(values_Authors, i, Book_FK);
                db.insert(AUTHOR_TABLE, null, values_Authors);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public boolean Delete(Book book){
        boolean flag=false;
        db.beginTransaction();
        try {
            db.delete(AUTHOR_TABLE, Col_Book_FK + " = " + book.id, null);
            db.delete(BOOK_TABLE, Col_Id + " = " + book.id, null);
            db.setTransactionSuccessful();
            flag=true;
    } finally {
        db.endTransaction();
    }
        return flag;
    }

    public boolean DeleteAll(){
        boolean flag=false;
        db.beginTransaction();
        try {
            db.delete(AUTHOR_TABLE, null, null);
            db.delete(BOOK_TABLE, null,null);
            db.setTransactionSuccessful();
            flag=true;
        } finally {
            db.endTransaction();
        }
        return flag;

    }

    public void close(){
        this.dbHelper.close();
    }

}
