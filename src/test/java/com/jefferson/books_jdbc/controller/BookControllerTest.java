package com.jefferson.books_jdbc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jefferson.books_jdbc.dto.BookDto;
import com.jefferson.books_jdbc.dto.BookRequest;
import com.jefferson.books_jdbc.exception.BookNotFoundException;
import com.jefferson.books_jdbc.service.BookService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    private BookDto bookDto;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        bookDto = new BookDto(1L, "Test Book", "Test Author", 2024);
        bookRequest = new BookRequest("Test Book", "Test Author", 2024);
    }

    @Test
    void getBook_ExistingId_ReturnsBook() throws Exception {
        // Given
        when(bookService.getBookById(1L)).thenReturn(bookDto);

        // When & Then
        mockMvc.perform(get("/api/v1/book/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.publicationYear").value(2024));

        verify(bookService).getBookById(1L);
    }

    @Test
    void getBook_NonExistingId_ReturnsNotFound() throws Exception {
        // Given
        when(bookService.getBookById(999L))
                .thenThrow(new BookNotFoundException("Book not found for id: 999"));

        // When & Then
        mockMvc.perform(get("/api/v1/book/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("Book not found for id: 999"));

        verify(bookService).getBookById(999L);
    }

    @Test
    void getBook_ServiceThrowsConstraintViolation_ReturnsBadRequest() throws Exception {
        // Given
        when(bookService.getBookById(-1L))
                .thenThrow(new ConstraintViolationException("Validation failed", Set.of()));

        // When & Then
        mockMvc.perform(get("/api/v1/book/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookService).getBookById(-1L);
    }

    @Test
    void newBook_ValidRequest_ReturnsCreated() throws Exception {
        // Given
        when(bookService.createNewBook(any(BookRequest.class))).thenReturn(bookDto);

        // When & Then
        mockMvc.perform(post("/api/v1/book/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.publicationYear").value(2024));

        verify(bookService).createNewBook(any(BookRequest.class));
    }

    @Test
    void newBook_ServiceThrowsConstraintViolation_ReturnsBadRequest() throws Exception {
        // Given
        BookRequest invalidRequest = new BookRequest("", "", null);
        when(bookService.createNewBook(invalidRequest))
                .thenThrow(new ConstraintViolationException("Validation failed", Set.of()));

        // When & Then
        mockMvc.perform(post("/api/v1/book/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(bookService).createNewBook(invalidRequest);
    }

    @Test
    void updateBook_ValidRequest_ReturnsOk() throws Exception {
        // Given
        when(bookService.updateBookInfo(eq(1L), any(BookRequest.class))).thenReturn(bookDto);

        // When & Then
        mockMvc.perform(put("/api/v1/book/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.publicationYear").value(2024));

        verify(bookService).updateBookInfo(eq(1L), any(BookRequest.class));
    }

    @Test
    void updateBook_NonExistingId_ReturnsNotFound() throws Exception {
        // Given
        when(bookService.updateBookInfo(eq(999L), any(BookRequest.class)))
                .thenThrow(new BookNotFoundException("Book not found for id: 999"));

        // When & Then
        mockMvc.perform(put("/api/v1/book/update/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.Error").value("Book not found for id: 999"));

        verify(bookService).updateBookInfo(eq(999L), any(BookRequest.class));
    }

    @Test
    void updateBook_ServiceThrowsConstraintViolation_ReturnsBadRequest() throws Exception {
        // Given
        BookRequest invalidRequest = new BookRequest("", "", null);
        when(bookService.updateBookInfo(eq(1L), eq(invalidRequest)))
                .thenThrow(new ConstraintViolationException("Validation failed", Set.of()));

        // When & Then
        mockMvc.perform(put("/api/v1/book/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(bookService).updateBookInfo(eq(1L), eq(invalidRequest));
    }

    @Test
    void deleteBook_ExistingId_ReturnsNoContent() throws Exception {
        // Given
        when(bookService.deleteBookById(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/v1/book/delete/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBookById(1L);
    }

    @Test
    void deleteBook_ServiceThrowsConstraintViolation_ReturnsBadRequest() throws Exception {
        // Given
        when(bookService.deleteBookById(-1L))
                .thenThrow(new ConstraintViolationException("Validation failed", Set.of()));

        // When & Then
        mockMvc.perform(delete("/api/v1/book/delete/{id}", -1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookService).deleteBookById(-1L);
    }
}
