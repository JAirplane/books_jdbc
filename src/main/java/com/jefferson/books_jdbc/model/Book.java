package com.jefferson.books_jdbc.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("books")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Book {

    @Id
    private Long id;

    @NotBlank(message = "Title mustn't be blank")
    @Size(min = 1, max = 255, message = "Title size should be between 1 and 255 characters")
    private String title;

    @NotBlank(message = "Author mustn't be blank")
    @Size(min = 1, max = 255, message = "Author size should be between 1 and 255 characters")
    private String author;

    @Column("publication_year")
    private Integer publicationYear;
}
