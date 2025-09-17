package com.jefferson.books_jdbc.service;

import com.jefferson.books_jdbc.dto.BookDto;
import com.jefferson.books_jdbc.dto.BookRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface BookService {

    BookDto getBookById(@NotNull(message = "Book service: book id mustn't be null")
                        @Positive(message = "Book service: book id must be positive")
                        Long id);

    BookDto createNewBook(@NotNull(message = "Book service: book request mustn't be null")
                          @Valid
                          BookRequest bookRequest);

    BookDto updateBookInfo(@NotNull(message = "Book service: book id mustn't be null")
                           @Positive(message = "Book service: book id must be positive")
                           Long id,
                           @NotNull(message = "Book service: book request mustn't be null")
                           @Valid
                           BookRequest bookRequest);

    boolean deleteBookById(@NotNull(message = "Book service: book id mustn't be null")
                           @Positive(message = "Book service: book id must be positive")
                           Long id);
}
