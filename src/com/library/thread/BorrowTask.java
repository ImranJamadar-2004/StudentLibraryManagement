package com.library.thread;

import com.library.exception.BookLimitExceededException;
import com.library.exception.BookNotAvailableException;
import com.library.exception.BookNotFoundException;
import com.library.exception.StudentNotFoundException;
import com.library.service.Library;

public class BorrowTask implements Runnable {
    private Library library;
    private int studentId;
    private int bookId;

    public BorrowTask(Library library, int studentId, int bookId) {
        this.library = library;
        this.studentId = studentId;
        this.bookId = bookId;
    }

    @Override
    public void run() {
        try {
            // Small random sleep to simulate students arriving at counter simultaneously
            Thread.sleep((long) (Math.random() * 200));
            library.borrowBook(studentId, bookId);
        } catch (BookNotFoundException | BookNotAvailableException | 
                 BookLimitExceededException | StudentNotFoundException e) {
            System.out.println(">> FAILED: [" + Thread.currentThread().getName() + "] " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(">> Thread interrupted: " + Thread.currentThread().getName());
        }
    }
}
