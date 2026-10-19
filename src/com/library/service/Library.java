package com.library.service;

import com.library.exception.BookLimitExceededException;
import com.library.exception.BookNotAvailableException;
import com.library.exception.BookNotFoundException;
import com.library.exception.StudentNotFoundException;
import com.library.model.Book;
import com.library.model.Student;
import com.library.model.Transaction;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Library implements Borrowable {
    // Collections
    private Map<Integer, Book> books;
    private Map<Integer, Student> students;
    private List<Transaction> transactions;
    private Map<Integer, Queue<Student>> waitlist; // BookId -> Waiting Students Queue
    
    private int transactionCounter = 1001;

    public Library() {
        this.books = new HashMap<>();
        this.students = new HashMap<>();
        this.transactions = new ArrayList<>();
        this.waitlist = new HashMap<>();
    }

    // Add a new book to the library
    public void addBook(Book book) {
        books.put(book.getBookId(), book);
        waitlist.put(book.getBookId(), new LinkedList<>());
    }

    // Register a student
    public void registerStudent(Student student) {
        students.put(student.getId(), student);
    }

    public Student getStudent(int studentId) {
        return students.get(studentId);
    }

    public Book getBook(int bookId) {
        return books.get(bookId);
    }

    // Display all books (sorted using Comparable implementation)
    public void displayAllBooks() {
        if (books.isEmpty()) {
            System.out.println("No books available in the library.");
            return;
        }

        List<Book> bookList = new ArrayList<>(books.values());
        Collections.sort(bookList); // Uses compareTo() from Book

        System.out.println("\n============== Library Catalog ==============");
        for (Book b : bookList) {
            System.out.println(b);
        }
        System.out.println("=============================================");
    }

    // Search books by title or author
    public List<Book> searchBooks(String keyword) {
        List<Book> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (Book b : books.values()) {
            if (b.getTitle().toLowerCase().contains(lowerKeyword) || 
                b.getAuthor().toLowerCase().contains(lowerKeyword)) {
                results.add(b);
            }
        }
        return results;
    }

    // Thread-safe borrowing using synchronized keyword
    @Override
    public synchronized void borrowBook(int studentId, int bookId) 
            throws BookNotFoundException, BookNotAvailableException, BookLimitExceededException, StudentNotFoundException {
        
        Student student = students.get(studentId);
        if (student == null) {
            throw new StudentNotFoundException("Student with ID " + studentId + " is not registered.");
        }

        Book book = books.get(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " does not exist in library.");
        }

        if (!student.canBorrow()) {
            throw new BookLimitExceededException("Student " + student.getName() + " has already borrowed maximum books (" 
                    + Student.MAX_BORROW_LIMIT + ").");
        }

        // Check if student already has this book
        for (Book b : student.getBorrowedBooks()) {
            if (b.getBookId() == bookId) {
                throw new BookNotAvailableException("Student already has a copy of \"" + book.getTitle() + "\".");
            }
        }

        // Check book copy availability
        if (book.getAvailableCopies() <= 0) {
            Queue<Student> queue = waitlist.get(bookId);
            if (!queue.contains(student)) {
                queue.offer(student);
            }
            throw new BookNotAvailableException("No copies available for \"" + book.getTitle() 
                    + "\". Student " + student.getName() + " added to waitlist (Queue position: " + queue.size() + ").");
        }

        // Process borrowing
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        student.addBorrowedBook(book);

        Transaction txn = new Transaction(transactionCounter++, studentId, bookId, book.getTitle(), LocalDate.now());
        transactions.add(txn);

        System.out.println(">> SUCCESS: [" + Thread.currentThread().getName() + "] " 
                + student.getName() + " borrowed \"" + book.getTitle() + "\" (Copies remaining: " + book.getAvailableCopies() + ")");
    }

    // Thread-safe returning using synchronized keyword
    @Override
    public synchronized void returnBook(int studentId, int bookId) 
            throws BookNotFoundException, StudentNotFoundException {
        
        Student student = students.get(studentId);
        if (student == null) {
            throw new StudentNotFoundException("Student with ID " + studentId + " is not registered.");
        }

        Book book = books.get(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " does not exist.");
        }

        boolean hasBook = false;
        for (Book b : student.getBorrowedBooks()) {
            if (b.getBookId() == bookId) {
                hasBook = true;
                break;
            }
        }

        if (!hasBook) {
            System.out.println(">> Error: Student " + student.getName() + " did not borrow this book.");
            return;
        }

        student.removeBorrowedBook(book);
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        // Update transaction
        for (Transaction txn : transactions) {
            if (txn.getStudentId() == studentId && txn.getBookId() == bookId && "ISSUED".equals(txn.getStatus())) {
                txn.markReturned(LocalDate.now());
                break;
            }
        }

        System.out.println(">> SUCCESS: Student " + student.getName() + " returned \"" + book.getTitle() + "\".");

        // Check waitlist queue
        Queue<Student> queue = waitlist.get(bookId);
        if (queue != null && !queue.isEmpty()) {
            Student nextStudent = queue.peek();
            System.out.println(">> ALERT: Book \"" + book.getTitle() + "\" is now available for next waiting student: " 
                    + nextStudent.getName() + " (ID: " + nextStudent.getId() + ")");
        }
    }

    // Check overdue transactions and calculate fine
    public void checkOverdueBooks() {
        System.out.println("\n--------- Overdue Books & Fine Report ---------");
        boolean foundOverdue = false;
        long finePerDay = 5; // Rs. 5 per day

        for (Transaction txn : transactions) {
            if ("ISSUED".equals(txn.getStatus())) {
                long days = ChronoUnit.DAYS.between(txn.getIssueDate(), LocalDate.now());
                if (days > 7) { // 7 days borrowing period
                    foundOverdue = true;
                    long overdueDays = days - 7;
                    long fine = overdueDays * finePerDay;
                    System.out.println("Txn #" + txn.getTransactionId() + " | Student ID: " + txn.getStudentId() 
                            + " | Book: " + txn.getBookTitle() + " | Overdue by: " + overdueDays + " days | Fine: Rs. " + fine);
                }
            }
        }

        if (!foundOverdue) {
            System.out.println("No overdue books at the moment.");
        }
        System.out.println("-----------------------------------------------");
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Map<Integer, Student> getAllStudents() {
        return students;
    }
}
