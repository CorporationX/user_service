package school.faang.user_service.dmitrrysprint1.libraryofwesterros;

import java.util.HashMap;

public class LibrarySystem {

    private HashMap<Book, String> books;


    public LibrarySystem() {
    }

    public LibrarySystem(HashMap<Book, String> books) {
        this.books = books;
    }

    public void addBook(String title, String author, int year, String location) {
        books.put(new Book(title, author, year), location);

    }

    public void removeBook(String title, String author, int year) {
        Book testBook = new Book(title, author, year);
        if (books.containsKey(testBook)) {
            books.remove(testBook);
        }

    }

    public boolean findBook(String title, String author, int year) {
        Book testBook = new Book(title, author, year);
        if (books.containsKey(testBook)) {
            return true;
        }
        return false;
    }

    public void printAllBooks() {
        books.forEach((book, s) -> System.out.println(book.toString() + " " + s + " ;"));

    }

}
