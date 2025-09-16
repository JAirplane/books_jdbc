package com.jefferson.books_jdbc.repository;

import com.jefferson.books_jdbc.model.Book;

import java.util.Optional;

public interface BookRepository {

    Optional<Book> getBookById(Long id);
    Optional<Book> createNewBook(Book book);
    Optional<Book> updateBookInfo(Book book);
    boolean deleteBookById(Long id);
}
