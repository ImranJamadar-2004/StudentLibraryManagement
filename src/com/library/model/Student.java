package com.library.model;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private List<Book> borrowedBooks;
    public static final int MAX_BORROW_LIMIT = 3;

    public Student(int id, String name, String email) {
        super(id, name, email);
        this.borrowedBooks = new ArrayList<>();
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public boolean canBorrow() {
        return borrowedBooks.size() < MAX_BORROW_LIMIT;
    }

    public void addBorrowedBook(Book book) {
        borrowedBooks.add(book);
    }

    public void removeBorrowedBook(Book book) {
        borrowedBooks.remove(book);
    }

    @Override
    public void displayDetails() {
        System.out.println("----------------------------------------");
        System.out.println("Student ID    : " + id);
        System.out.println("Name          : " + name);
        System.out.println("Email         : " + email);
        System.out.println("Books Borrowed: " + borrowedBooks.size() + " / " + MAX_BORROW_LIMIT);
        if (!borrowedBooks.isEmpty()) {
            System.out.println("Current Books : ");
            for (Book b : borrowedBooks) {
                System.out.println("  - [" + b.getBookId() + "] " + b.getTitle());
            }
        }
        System.out.println("----------------------------------------");
    }
}
