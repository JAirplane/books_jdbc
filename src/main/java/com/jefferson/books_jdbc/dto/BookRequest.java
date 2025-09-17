package com.jefferson.books_jdbc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequest(@NotBlank(message = "Book request: title is null or empty")
                          String title,

                          @NotBlank(message = "Book request: author is null or empty")
                          String author,

                          @NotNull(message = "Book request: publicationYear is null")
                          Integer publicationYear) {
}
