package edu.stevens.cs522.bookstore.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstore.contracts.BookContract;

public class Book implements Parcelable {

// TODO Modify this to implement the Parcelable interface.

// TODO redefine toString() to display book title and price (why?).

    public int id;

    public String title;

    public String isbn;

    public String price;
    public String[] firstName = new String[10];
    public String[] middleName= new String[10];
    public String[] lastName= new String[10];

    public String authors;

    public String[] author;
    public String[] author_name;
    //private ArrayList<Author> authorsT;

    public Book(String title, String Author, String isbn, String price) {
        //this.id = id;
        this.title = title;
        this.authors = Author;
        this.isbn = isbn;
        this.price = price;
        author = BookContract.readStringArray(this.authors);
        for(int i=0; i<author.length;i++){
            author_name = author[i].split(" ");
            if(author_name.length == 1){
                this.lastName[i] = author_name[0];
            }
            else if(author_name.length == 2){
                this.firstName[i] = author_name[0];
                this.lastName[i] = author_name[1];
            }
            else if(author_name.length == 3){
                this.lastName[i] = author_name[2];
                this.firstName[i] = author_name[0];
                this.middleName[i] = author_name[1];
            }
            else if(author_name.length == 0){

            }
        }
    }

    public Book(Cursor cursor){
       this.title= BookContract.getTitle(cursor);
       this.id= BookContract.getId(cursor);
       //this.authors= BookContract.getAuthor(cursor);
       this.price= BookContract.getPrice(cursor);
       this.isbn= BookContract.getIsbn(cursor);
       this.authors = BookContract.getAuthors(cursor);
   }

    public void writeToProvider(ContentValues values){
        BookContract.setTitle(values, title);
        BookContract.setPrice(values, price);
        BookContract.setIsbn(values, isbn);

    }

    public void writeToProviderAuthor(ContentValues values, int index, long Book_FK){
        //BookContract.setAuthors(values, authors);
        BookContract.setFirstName(values,firstName[index]);
        BookContract.setMiddleName(values,middleName[index]);
        BookContract.setLastName(values,lastName[index]);
        BookContract.setBook_FK(values, Book_FK);
    }


    public Book(Parcel in) {
        int intdata = in.readInt();
        this.id = intdata;

        String[] data = new String[4];
        in.readStringArray(data);
        this.title = data[0];
        this.isbn = data[1];
        this.price = data[2];
        this.authors = data[3];

        //Author[] authorsT = (Author[]) in.readParcelableArray(Author.class.getClassLoader());
        //authorsT = new ArrayList(Arrays.asList(in.readParcelableArray(Author.class.getClassLoader())));
        //this.authors = new Author[authorsT.size()];
        //for (int i = 0; i < authorsT.size(); i++) {
          //  authors[i] = authorsT.get(i);
        //}
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        String[] data = new String[4];
        data[0] = title;
        data[1] = isbn;
        data[2] = price;
        data[3] = authors;
        dest.writeStringArray(data);


    }

    @Override
    public String toString() {
        return this.title + "   " + "$" + this.price;

    }
}