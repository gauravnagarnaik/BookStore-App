package edu.stevens.cs522.bookstore.activities;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.drm.DrmStore;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.databases.CartDbAdapter;
import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Book;
@SuppressWarnings("deprecation")

public class BookStoreActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

	// Use this when logging errors and warnings.
	@SuppressWarnings("unused")
    private static final int Books_LOADER_ID=1;
    //private static final int SingleBook_LOADER_ID=2;
    protected Object mActionMode;
    public long selectedItem_Long[];
    public String[] selectedItem_String;
	private static final String TAG = BookStoreActivity.class.getCanonicalName();

    private CartDbAdapter adapter;
    //private SimpleCursorAdapter dataAdapter;
    private BookAdapter dataAdapter;
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;
	
	@SuppressWarnings("unused")
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

	// There is a reason this must be an ArrayList instead of a List.
	@SuppressWarnings("unused")
	//private ArrayList<Book> shoppingCart;
    ListView listView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

            setContentView(R.layout.cart);

            listView = (ListView) findViewById(android.R.id.list);

            dataAdapter = new BookAdapter(this,null);
            LoaderManager Lm_Books = getLoaderManager();

            Lm_Books.initLoader(Books_LOADER_ID, null, this);

            listView.setAdapter(dataAdapter);

            //listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                //@Override
                //public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    //if (mActionMode != null) {
                      //  return false;
                    //}
                    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
                    //listView.setItemChecked(position, true);
                    listView.setMultiChoiceModeListener(new ListView.MultiChoiceModeListener() {

                        @Override
                        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                            MenuInflater inflater = actionMode.getMenuInflater();
                            inflater.inflate(R.menu.context_menuitem, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                            return false;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                            selectedItem_String = new String[selectedItem_Long.length];
                            switch (menuItem.getItemId()) {
                                case R.id.menu_delete:
                                    for(int count=0; count < selectedItem_Long.length;count++){
                                        selectedItem_String[count] = String.valueOf(selectedItem_Long[count]);
                                    }
                                    getContentResolver().delete(BookContract.CONTENT_URI_Books,BookContract.Id + "=?",selectedItem_String);
                                    getContentResolver().delete(BookContract.CONTENT_URI_Authors,BookContract.Book_FK + "=?", selectedItem_String);
                                    break;

                            }
                            return false;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            mActionMode = null;
                            selectedItem_Long = null ;
                            selectedItem_String=null;

                        }

                        @Override
                        public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                            actionMode.setTitle(listView.getCheckedItemCount() + " Books Selected");
                            selectedItem_Long = listView.getCheckedItemIds();
                        }
                    });
                    //BookStoreActivity.this.startActionMode(BookStoreActivity.this);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = getContentResolver().query(BookContract.CONTENT_URI(Long.toString(id),BookContract.CONTENT_URI_Books),null,null,null,null);
                    cursor.moveToFirst();
                    Book book = new Book(cursor);
                    //Book book = new Book((Cursor) listView.getAdapter().getItem(position));
                    Intent intent = new Intent(view.getContext(), View_details.class);
                    intent.putExtra("Book", book);
                    //intent.putParcelableArrayListExtra("Books", shoppingCart);
                    startActivity(intent);
                    finish();
                }
            });




            //shoppingCart.add(Book2);
            // TODO check if there is saved UI state, and if so, restore it (i.e. the cart contents)
            //
            // TODO Set the layout (use cart.xml layout)



		// TODO use an array adapter to display the cart contents.
        //registerForContextMenu(listView);

	}



    @Override
    public Loader<Cursor> onCreateLoader(int LoaderID, Bundle bundle) {
        CursorLoader Cl=null;
        switch (LoaderID) {
            case Books_LOADER_ID:
                String[] projection ={BookContract.Id,BookContract.TITLE, BookContract.Authors};
                Cl = new CursorLoader(this, BookContract.CONTENT_URI_Books,projection,null,null,null);
                break;
        }
        return Cl;
    }


    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            this.dataAdapter.swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> loader){
        this.dataAdapter.swapCursor(null);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //dataAdapter.getCursor().close();

    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bookstore_menu, menu);
        //menu.findItem(R.id.delete).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_cancel).setVisible(false);
        menu.findItem(R.id.action_order).setVisible(false);

		// TODO provide ADD, DELETE and CHECKOUT options
		return true;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TODO
		switch(item.getItemId()){
            case R.id.add:
                Intent addIntent = new Intent(this, AddBookActivity.class);
                startActivityForResult(addIntent, ADD_REQUEST);
                break;
            case R.id.checkout:
                Intent CheckoutIntent = new Intent(this, CheckoutActivity.class);
                CheckoutIntent.putExtra("BookCount",1);
                startActivityForResult(CheckoutIntent, CHECKOUT_REQUEST);
                break;
            default:
                super.onOptionsItemSelected(item);
        }

		// ADD provide the UI for adding a book

		
		// DELETE delete the currently selected book
		
		// CHECKOUT provide the UI for checking out
		
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		// TODO Handle results from the Search and Checkout activities.

        if(resultCode==RESULT_OK){
            if(requestCode==ADD_REQUEST){
                dataAdapter.notifyDataSetChanged();
            }

            if(requestCode==CHECKOUT_REQUEST){
                dataAdapter.notifyDataSetChanged();

            }
        }

	}


	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO save the shopping cart contents (which should be a list of parcelables).
        //savedInstanceState.putParcelableArrayList("key", shoppingCart);
        super.onSaveInstanceState(savedInstanceState);
	}


   /* @Override
    public boolean onCreateActionMode(ActionMode Mode, Menu menu){
            MenuInflater inflater = Mode.getMenuInflater();
            inflater.inflate(R.menu.context_menuitem, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false; // Return false if nothing is done
    }
*/


    /*@Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //int menuItemIndex = item.getItemId();
        //String[] menuItems = getResources().getStringArray(R.array.menu);
        switch (item.getItemId()) {
            case R.id.menu_delete:
                 Book book = new Book((Cursor)listView.getAdapter().getItem(selectedItem));
                 adapter.Delete(book);
                dataAdapter.getCursor().requery();

                break;
            default:
                super.onOptionsItemSelected(item);
        }
             return false;
    }
*/

   /* @Override
    public void onDestroyActionMode(ActionMode mode) {
        mActionMode = null;
        selectedItem_Long = null ;
        selectedItem_String=null;

    }
	*/
}