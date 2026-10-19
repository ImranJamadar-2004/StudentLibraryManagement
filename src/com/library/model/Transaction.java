package com.library.model;

import java.time.LocalDate;

public class Transaction {
    private int transactionId;
    private int studentId;
    private int bookId;
    private String bookTitle;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private String status; // "ISSUED", "RETURNED"

    public Transaction(int transactionId, int studentId, int bookId, String bookTitle, LocalDate issueDate) {
        this.transactionId = transactionId;
        this.studentId = studentId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.issueDate = issueDate;
        this.status = "ISSUED";
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void markReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.status = "RETURNED";
    }

    @Override
    public String toString() {
        return String.format("Txn #%-3d | Student: %-4d | Book: [%-3d] %-25s | Issue: %s | Return: %-10s | Status: %s",
                transactionId, studentId, bookId, bookTitle, issueDate,
                (returnDate != null ? returnDate.toString() : "Not Yet"), status);
    }
}
