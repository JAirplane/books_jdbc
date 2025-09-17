package com.jefferson.books_jdbc.repository;

import com.jefferson.books_jdbc.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;
import java.util.Optional;

@Repository
public class BookRepositoryImpl implements BookRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Book> bookRowMapper = (rs, rowNum) -> {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));

        int year = rs.getInt("publication_year");
        book.setPublicationYear(rs.wasNull() ? null : year);

        return book;
    };

    @Autowired
    public BookRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Book> getBookById(Long id) {

        if (id == null || id <= 0) {
            return Optional.empty();
        }

        try {
            Book book = jdbcTemplate.queryForObject("SELECT * FROM books WHERE id = ?", bookRowMapper, id);
            return Optional.ofNullable(book);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Book createNewBook(Book book) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO books (title, author, publication_year) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setObject(3, book.getPublicationYear());

            return preparedStatement;
        }, keyHolder);

        book.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return book;
    }

    @Override
    public Optional<Book> updateBookInfo(Book book) {

        if(book == null) {
            return Optional.empty();
        }

        int affectedRows = jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE books SET title = ?, author = ?, publication_year = ? WHERE id = ?"
            );

            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setObject(3, book.getPublicationYear());
            preparedStatement.setLong(4, book.getId());

            return preparedStatement;
        });

        if(affectedRows == 0) return Optional.empty();

        return Optional.of(book);
    }

    @Override
    public boolean deleteBookById(Long id) {

        if(id == null || id < 0) return false;

        int affectedRows = jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(
                    "DELETE FROM books WHERE id = ?"
            );

            preparedStatement.setLong(1, id);
            return preparedStatement;
        });

        return affectedRows > 0;
    }

}
