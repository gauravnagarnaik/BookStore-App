package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.EditText;

import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.databases.CartDbAdapter;
import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Book;

public class AddBookActivity extends Activity {
    private CartDbAdapter adapter;
	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_RESULT_KEY = "book_result";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_book);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO provide SEARCH and CANCEL options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bookstore_menu, menu);
        //menu.findItem(R.id.delete).setVisible(false);
        menu.findItem(R.id.add).setVisible(false);
        menu.findItem(R.id.action_order).setVisible(false);
        menu.findItem(R.id.checkout).setVisible(false);
		return true;
	}

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //adapter.close();
    }


    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TODO
        switch (item.getItemId()) {
            case R.id.action_search:
                //Book resultBook = searchBook();
                searchBook();
                Intent result = new Intent(this,BookStoreActivity.class);
                finish();
                //item.setIntent(result);
                //startActivity(result);
                //result.putExtra("book_result", resultBook);
                setResult(RESULT_OK, result);

                break;
            case R.id.action_cancel:
                setResult(RESULT_CANCELED, null);
                finish();
               break;
            default:
                return super.onOptionsItemSelected(item);


        }

        // SEARCH: return the book details to the BookStore activity
		
		// CANCEL: cancel the search request
		return false;
	}
	
	public void searchBook(){
		/*
		 * Search for the specified book.
		 */
		// TODO Just build a Book object with the search criteria and return that.

        EditText editAuthor1 = (EditText) findViewById(R.id.search_author1);
        EditText editAuthor2 = (EditText) findViewById(R.id.search_author2);

        String authorString = editAuthor1.getText().toString() + "|" + editAuthor2.getText().toString();
        //String[] authors = authorString.split(", ");
        //Author[] authorsArray = new Author[authors.length];
        //for(int i = 0; i < authors.length; i++){
            //authorsArray[i] = new Author(authors[i].split(" "));
        //}

        EditText editTitle = (EditText) findViewById(R.id.search_title);
        EditText editIsbn = (EditText) findViewById(R.id.search_isbn);
        EditText editPrice = (EditText) findViewById(R.id.search_price);
        String title = editTitle.getText().toString();
        String isbn = editIsbn.getText().toString();
        String price = editPrice.getText().toString();

        Book book = new Book(title, authorString, isbn,price);;

        ContentValues values_book = new ContentValues();
        ContentValues values_Authors = new ContentValues();
        book.writeToProvider(values_book);
        Uri Book_ID = getContentResolver().insert(BookContract.CONTENT_URI_Books, values_book);
        long BookID = BookContract.getId(Book_ID);
            //Cursor cursor = db.query(BOOK_TABLE,new String[]{Col_Id}, Col_isbn + " = " + book.isbn,null,null,null,null);
        for(int i=0; i<book.author.length;i++) {
                book.writeToProviderAuthor(values_Authors, i, BookID);
                Uri Author_ID = getContentResolver().insert(BookContract.CONTENT_URI_Authors, values_Authors);
        }


        //return book;

	}

}