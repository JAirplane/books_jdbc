package com.jefferson.books_jdbc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookDto(
        @NotNull(message = "Book dto: book id mustn't be null")
        @Positive(message = "Book dto: book id must be positive")
        Long id,

        @NotBlank(message = "Book dto: title is null or empty")
        String title,

        @NotBlank(message = "Book dto: author is null or empty")
        String author,

        @NotNull(message = "Book dto: publicationYear is null")
        Integer publicationYear) {
}
