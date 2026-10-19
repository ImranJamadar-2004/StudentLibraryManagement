package com.library.service;

import com.library.exception.BookLimitExceededException;
import com.library.exception.BookNotAvailableException;
import com.library.exception.BookNotFoundException;
import com.library.exception.StudentNotFoundException;

public interface Borrowable {
    void borrowBook(int studentId, int bookId) 
            throws BookNotFoundException, BookNotAvailableException, BookLimitExceededException, StudentNotFoundException;

    void returnBook(int studentId, int bookId) 
            throws BookNotFoundException, StudentNotFoundException;
}
