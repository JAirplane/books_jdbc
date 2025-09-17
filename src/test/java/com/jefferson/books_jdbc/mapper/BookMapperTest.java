package com.jefferson.books_jdbc.mapper;

import com.jefferson.books_jdbc.dto.BookDto;
import com.jefferson.books_jdbc.dto.BookRequest;
import com.jefferson.books_jdbc.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = BookMapperImpl.class)
public class BookMapperTest {

    @Autowired
    private BookMapper bookMapper;

    @Test
    void toDto_WithValidEntity_ReturnsCorrectDto() {
        Book book = new Book(1L, "Effective Java", "Joshua Bloch", 2018);

        BookDto result = bookMapper.toDto(book);

        assertNotNull(result);
        assertEquals(book.getId(), result.id());
        assertEquals(book.getTitle(), result.title());
        assertEquals(book.getAuthor(), result.author());
        assertEquals(book.getPublicationYear(), result.publicationYear());
    }

    @Test
    void toEntity_WithValidRequest_ReturnsCorrectEntity() {

        BookRequest request = new BookRequest("Clean Code", "Robert Martin", 2008);

        Book result = bookMapper.toEntity(request);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(request.title(), result.getTitle());
        assertEquals(request.author(), result.getAuthor());
        assertEquals(request.publicationYear(), result.getPublicationYear());
    }
}
