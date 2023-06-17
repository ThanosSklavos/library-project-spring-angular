package gr.aueb.cf.libraryapp.service.exceptions;

import gr.aueb.cf.libraryapp.model.Book;

public class OutOfStockException extends Exception{
    private static final long serialVersionUID = 1L;

    public OutOfStockException(Book book) {
        super("Title " + book.getTitle() + " with id " + book.getId() + " is out of stock");
    }
}
