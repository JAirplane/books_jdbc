package com.jefferson.books_jdbc.controller;

import com.jefferson.books_jdbc.dto.BookDto;
import com.jefferson.books_jdbc.dto.BookRequest;
import com.jefferson.books_jdbc.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/book")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<BookDto> getBook(@PathVariable Long id) {

        BookDto bookDto = bookService.getBookById(id);

        return ResponseEntity.ok(bookDto);
    }

    @PostMapping(path = "/new")
    public ResponseEntity<BookDto> newBook(@RequestBody BookRequest bookRequest) {

        BookDto bookDto = bookService.createNewBook(bookRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(bookDto);
    }

    @PutMapping(path = "/update/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @RequestBody BookRequest bookRequest) {

        BookDto bookDto = bookService.updateBookInfo(id, bookRequest);

        return ResponseEntity.ok(bookDto);
    }

    @DeleteMapping(path = "/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {

        bookService.deleteBookById(id);
    }
}
