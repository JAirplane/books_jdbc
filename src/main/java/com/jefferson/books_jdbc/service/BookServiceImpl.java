package com.jefferson.books_jdbc.service;

import com.jefferson.books_jdbc.dto.BookDto;
import com.jefferson.books_jdbc.dto.BookRequest;
import com.jefferson.books_jdbc.exception.BookNotFoundException;
import com.jefferson.books_jdbc.mapper.BookMapper;
import com.jefferson.books_jdbc.model.Book;
import com.jefferson.books_jdbc.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto getBookById(Long id) {

        Book book = bookRepository.getBookById(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found for id: " + id));

        return bookMapper.toDto(book);
    }

    @Override
    public BookDto createNewBook(BookRequest bookRequest) {

        Book book = bookMapper.toEntity(bookRequest);

        Book savedBook = bookRepository.createNewBook(book);

        return bookMapper.toDto(savedBook);
    }

    @Override
    public BookDto updateBookInfo(Long id, BookRequest bookRequest) {

        Book book = bookMapper.toEntity(bookRequest);
        book.setId(id);

        Book updatedBook = bookRepository.updateBookInfo(book)
                .orElseThrow(() -> new BookNotFoundException("Book not found for id: " + id));

        return bookMapper.toDto(updatedBook);
    }

    @Override
    public boolean deleteBookById(Long id) {

        return bookRepository.deleteBookById(id);
    }
}
