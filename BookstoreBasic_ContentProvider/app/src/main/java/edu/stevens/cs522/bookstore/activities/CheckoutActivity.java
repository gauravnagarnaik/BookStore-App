package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.databases.CartDbAdapter;
import edu.stevens.cs522.bookstore.R;

public class CheckoutActivity extends Activity {
    private CartDbAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO display ORDER and CANCEL options.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bookstore_menu, menu);
        //menu.findItem(R.id.delete).setVisible(false);
        menu.findItem(R.id.add).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.checkout).setVisible(false);
		return true;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // TODO
        switch(item.getItemId()) {
            case R.id.action_order:
                int count;
                Intent intent = getIntent();
                count = intent.getIntExtra("BookCount",0);
                getContentResolver().delete(BookContract.CONTENT_URI_Books,null,null);
                getContentResolver().delete(BookContract.CONTENT_URI_Authors,null,null);
                Toast.makeText(getApplicationContext(),
                        "successfully ordered " + count + " " + "Books" , Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.action_cancel:
                setResult(RESULT_CANCELED, null);
                finish();
                break;
            default:
                super.onOptionsItemSelected(item);
        }
		// ORDER: display a toast message of how many books have been ordered and return
		
		// CANCEL: just return with REQUEST_CANCELED as the result code

		return false;
	}
	
}