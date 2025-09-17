package com.jefferson.books_jdbc.repository;

import com.jefferson.books_jdbc.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
@Import(BookRepositoryImpl.class)
@Sql(scripts = "/test-data.sql")
public class BookRepositoryImplTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void getBookById_ExistingId_ReturnsBook() {

        Optional<Book> result = bookRepository.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals("Effective Java", result.get().getTitle());
        assertEquals("Joshua Bloch", result.get().getAuthor());
        assertEquals(2018, result.get().getPublicationYear());
    }

    @Test
    void getBookById_NonExistingId_ReturnsEmpty() {

        Optional<Book> result = bookRepository.getBookById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void getBookById_NullId_ReturnsEmpty() {

        Optional<Book> result = bookRepository.getBookById(null);

        assertFalse(result.isPresent());
    }

    @Test
    void getBookById_NegativeId_ReturnsEmpty() {

        Optional<Book> result = bookRepository.getBookById(-1L);

        assertFalse(result.isPresent());
    }

    @Test
    void createNewBook_ValidBook_ReturnsBookWithGeneratedId() {

        Book newBook = new Book();
        newBook.setTitle("Test Book");
        newBook.setAuthor("Test Author");
        newBook.setPublicationYear(2024);

        Book result = bookRepository.createNewBook(newBook);

        assertNotNull(result.getId());
        assertTrue(result.getId() > 0);
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        assertEquals(2024, result.getPublicationYear());

        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM books WHERE id = ? AND title = ?",
                Long.class,
                result.getId(),
                "Test Book"
        );
        assertEquals(1L, count);
    }

    @Test
    void createNewBook_WithNullTitle_ThrowsException() {

        Book newBook = new Book();
        newBook.setTitle(null);
        newBook.setAuthor("Test Author");
        newBook.setPublicationYear(2024);

        assertThrows(DataAccessException.class, () -> {
            bookRepository.createNewBook(newBook);
        });
    }

    @Test
    void updateBookInfo_ExistingBook_ReturnsUpdatedBook() {

        Book updateData = new Book();
        updateData.setId(1L);
        updateData.setTitle("Updated Title");
        updateData.setAuthor("Updated Author");
        updateData.setPublicationYear(2024);

        Optional<Book> result = bookRepository.updateBookInfo(updateData);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Updated Title", result.get().getTitle());
        assertEquals("Updated Author", result.get().getAuthor());
        assertEquals(2024, result.get().getPublicationYear());

        Book dbBook = jdbcTemplate.queryForObject(
                "SELECT * FROM books WHERE id = ?",
                (rs, rowNum) -> {
                    Book book = new Book();
                    book.setId(rs.getLong("id"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    book.setPublicationYear(rs.getObject("publication_year", Integer.class));
                    return book;
                },
                1L
        );

        assertNotNull(dbBook);
        assertEquals("Updated Title", dbBook.getTitle());
        assertEquals("Updated Author", dbBook.getAuthor());
        assertEquals(2024, dbBook.getPublicationYear());
    }

    @Test
    void updateBookInfo_NonExistingBook_ReturnsEmpty() {

        Book nonExistingBook = new Book();
        nonExistingBook.setId(999L);
        nonExistingBook.setTitle("Non Existing");
        nonExistingBook.setAuthor("Author");
        nonExistingBook.setPublicationYear(2024);

        Optional<Book> result = bookRepository.updateBookInfo(nonExistingBook);

        assertFalse(result.isPresent());

        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM books WHERE title = 'Non Existing'",
                Long.class
        );
        assertEquals(0L, count);
    }

    @Test
    void deleteBookById_ExistingId_ReturnsTrue() {

        boolean result = bookRepository.deleteBookById(1L);

        assertTrue(result);

        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM books WHERE id = ?",
                Long.class,
                1L
        );
        assertEquals(0L, count, "Книга должна быть удалена из БД");
    }

    @Test
    void deleteBookById_NonExistingId_ReturnsFalse() {

        Long nonExistingId = 999L;

        boolean result = bookRepository.deleteBookById(nonExistingId);

        assertFalse(result);

        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM books",
                Long.class
        );
        assertEquals(2L, count);
    }
}
