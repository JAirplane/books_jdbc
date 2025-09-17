package com.jefferson.books_jdbc.service;

import com.jefferson.books_jdbc.dto.BookDto;
import com.jefferson.books_jdbc.dto.BookRequest;
import com.jefferson.books_jdbc.exception.BookNotFoundException;
import com.jefferson.books_jdbc.mapper.BookMapper;
import com.jefferson.books_jdbc.model.Book;
import com.jefferson.books_jdbc.repository.BookRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationInterceptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    private Book book;
    private BookDto bookDto;
    private BookRequest bookRequest;

    @BeforeEach
    void initTests() {

        bookService = new BookServiceImpl(bookRepository, bookMapper);

        var validatorFactory = new LocalValidatorFactoryBean();
        validatorFactory.afterPropertiesSet();

        var validationInterceptor = new MethodValidationInterceptor(validatorFactory.getValidator());

        var proxyFactory = new ProxyFactory(bookService);

        proxyFactory.addAdvice(validationInterceptor);

        bookService = (BookService) proxyFactory.getProxy();
    }

    @BeforeEach
    void setUp() {
        book = new Book(1L, "Test Book", "Test Author", 2024);
        bookDto = new BookDto(1L, "Test Book", "Test Author", 2024);
        bookRequest = new BookRequest("Test Book", "Test Author", 2024);
    }

    @Test
    void getBookById_ExistingId_ReturnsBookDto() {
        // Given
        when(bookRepository.getBookById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        // When
        BookDto result = bookService.getBookById(1L);

        // Then
        assertNotNull(result);
        assertEquals(bookDto, result);
        verify(bookRepository).getBookById(1L);
        verify(bookMapper).toDto(book);
    }

    @Test
    void getBookById_NonExistingId_ThrowsBookNotFoundException() {
        // Given
        when(bookRepository.getBookById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () ->
                bookService.getBookById(999L)
        );
    }

    @Test
    void getBookById_NullId_ThrowsConstraintViolation() {
        assertThrows(ConstraintViolationException.class, () ->
                bookService.getBookById(null)
        );
    }

    @Test
    void getBookById_NegativeId_ThrowsConstraintViolation() {
        assertThrows(ConstraintViolationException.class, () ->
                bookService.getBookById(-1L)
        );
    }

    @Test
    void createNewBook_ValidRequest_ReturnsBookDto() {
        // Given
        when(bookMapper.toEntity(bookRequest)).thenReturn(book);
        when(bookRepository.createNewBook(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        // When
        BookDto result = bookService.createNewBook(bookRequest);

        // Then
        assertNotNull(result);
        assertEquals(bookDto, result);
        verify(bookMapper).toEntity(bookRequest);
        verify(bookRepository).createNewBook(book);
        verify(bookMapper).toDto(book);
    }

    @Test
    void createNewBook_NullRequest_ThrowsConstraintViolation() {
        assertThrows(ConstraintViolationException.class, () ->
                bookService.createNewBook(null)
        );
    }

    @Test
    void createNewBook_InvalidRequest_ThrowsConstraintViolation() {
        // Given
        BookRequest invalidRequest = new BookRequest("", "", null); // Все поля невалидны

        assertThrows(ConstraintViolationException.class, () ->
                bookService.createNewBook(invalidRequest)
        );
    }

    @Test
    void updateBookInfo_ValidData_ReturnsUpdatedBookDto() {
        // Given
        Book updatedBook = new Book(1L, "Updated Book", "Updated Author", 2025);
        BookDto updatedBookDto = new BookDto(1L, "Updated Book", "Updated Author", 2025);

        when(bookMapper.toEntity(bookRequest)).thenReturn(book);
        when(bookRepository.updateBookInfo(any(Book.class))).thenReturn(Optional.of(updatedBook));
        when(bookMapper.toDto(updatedBook)).thenReturn(updatedBookDto);

        // When
        BookDto result = bookService.updateBookInfo(1L, bookRequest);

        // Then
        assertNotNull(result);
        assertEquals(updatedBookDto, result);
        verify(bookRepository).updateBookInfo(argThat(b -> b.getId().equals(1L)));
    }

    @Test
    void updateBookInfo_NonExistingId_ThrowsBookNotFoundException() {
        // Given
        when(bookMapper.toEntity(bookRequest)).thenReturn(book);
        when(bookRepository.updateBookInfo(any(Book.class))).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () ->
                bookService.updateBookInfo(999L, bookRequest)
        );
    }

    @Test
    void updateBookInfo_NullId_ThrowsConstraintViolation() {
        assertThrows(ConstraintViolationException.class, () ->
                bookService.updateBookInfo(null, bookRequest)
        );
    }

    @Test
    void updateBookInfo_NullRequest_ThrowsConstraintViolation() {
        assertThrows(ConstraintViolationException.class, () ->
                bookService.updateBookInfo(1L, null)
        );
    }

    @Test
    void updateBookInfo_InvalidRequest_ThrowsConstraintViolation() {
        // Given
        BookRequest invalidRequest = new BookRequest("", "", null); // Все поля невалидны

        assertThrows(ConstraintViolationException.class, () ->
                bookService.updateBookInfo(1L, invalidRequest)
        );
    }

    @Test
    void deleteBookById_ExistingId_ReturnsTrue() {
        // Given
        when(bookRepository.deleteBookById(1L)).thenReturn(true);

        // When
        boolean result = bookService.deleteBookById(1L);

        // Then
        assertTrue(result);
        verify(bookRepository).deleteBookById(1L);
    }

    @Test
    void deleteBookById_NullId_ThrowsConstraintViolation() {
        assertThrows(ConstraintViolationException.class, () ->
                bookService.deleteBookById(null)
        );
    }

    @Test
    void getBookById_NullId_ValidationMessage() {
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () ->
                bookService.getBookById(null)
        );

        assertTrue(exception.getMessage().contains("Book service: book id mustn't be null"));
    }

    @Test
    void getBookById_NegativeId_ValidationMessage() {
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () ->
                bookService.getBookById(-1L)
        );

        assertTrue(exception.getMessage().contains("Book service: book id must be positive"));
    }

    @Test
    void createNewBook_NullRequest_ValidationMessage() {
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () ->
                bookService.createNewBook(null)
        );

        assertTrue(exception.getMessage().contains("Book service: book request mustn't be null"));
    }
}
