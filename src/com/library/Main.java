package com.library;

import com.library.exception.BookLimitExceededException;
import com.library.exception.BookNotAvailableException;
import com.library.exception.BookNotFoundException;
import com.library.exception.StudentNotFoundException;
import com.library.model.Book;
import com.library.model.Librarian;
import com.library.model.Student;
import com.library.model.Transaction;
import com.library.service.Library;
import com.library.thread.BorrowTask;
import com.library.thread.OverdueCheckerThread;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Library library = new Library();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Preload sample data for quick testing
        preloadData();

        // Start background daemon thread for overdue checking
        OverdueCheckerThread overdueChecker = new OverdueCheckerThread(library, 60);
        Thread backgroundThread = new Thread(overdueChecker, "OverdueChecker-Thread");
        backgroundThread.setDaemon(true); // Will terminate when main program exits
        backgroundThread.start();

        boolean exit = false;

        System.out.println("\n=======================================================");
        System.out.println("   WELCOME TO STUDENT LIBRARY MANAGEMENT SYSTEM (JAVA)  ");
        System.out.println("=======================================================");

        while (!exit) {
            printMenu();
            int choice = readIntegerInput("Enter your choice (1-11): ");

            switch (choice) {
                case 1:
                    library.displayAllBooks();
                    break;

                case 2:
                    searchBookOption();
                    break;

                case 3:
                    borrowBookOption();
                    break;

                case 4:
                    returnBookOption();
                    break;

                case 5:
                    viewStudentDetailsOption();
                    break;

                case 6:
                    addNewBookOption();
                    break;

                case 7:
                    registerStudentOption();
                    break;

                case 8:
                    viewTransactionsOption();
                    break;

                case 9:
                    library.checkOverdueBooks();
                    break;

                case 10:
                    runMultithreadingDemo();
                    break;

                case 11:
                    exit = true;
                    overdueChecker.stopChecker();
                    System.out.println("\nThank you for using Student Library System. Goodbye!");
                    break;

                default:
                    System.out.println(">> Invalid choice! Please select an option between 1 and 11.");
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n----------------- MAIN MENU -----------------");
        System.out.println("1.  View All Books (Alphabetical Order)");
        System.out.println("2.  Search Books (By Title or Author)");
        System.out.println("3.  Borrow a Book");
        System.out.println("4.  Return a Book");
        System.out.println("5.  View Student Details");
        System.out.println("6.  Add New Book (Librarian Action)");
        System.out.println("7.  Register New Student");
        System.out.println("8.  View Transaction History");
        System.out.println("9.  Check Overdue Books & Fines");
        System.out.println("10. Run Multi-threading Demo (Concurrent Borrow)");
        System.out.println("11. Exit");
        System.out.println("---------------------------------------------");
    }

    private static void searchBookOption() {
        System.out.print("Enter book title or author to search: ");
        String query = scanner.nextLine().trim();

        List<Book> results = library.searchBooks(query);
        if (results.isEmpty()) {
            System.out.println(">> No books found matching \"" + query + "\".");
        } else {
            System.out.println("\nFound " + results.size() + " matching book(s):");
            for (Book b : results) {
                System.out.println(b);
            }
        }
    }

    private static void borrowBookOption() {
        int studentId = readIntegerInput("Enter Student ID: ");
        int bookId = readIntegerInput("Enter Book ID to borrow: ");

        try {
            library.borrowBook(studentId, bookId);
        } catch (BookNotFoundException | BookNotAvailableException | 
                 BookLimitExceededException | StudentNotFoundException e) {
            System.out.println(">> Borrow Failed: " + e.getMessage());
        }
    }

    private static void returnBookOption() {
        int studentId = readIntegerInput("Enter Student ID: ");
        int bookId = readIntegerInput("Enter Book ID to return: ");

        try {
            library.returnBook(studentId, bookId);
        } catch (BookNotFoundException | StudentNotFoundException e) {
            System.out.println(">> Return Failed: " + e.getMessage());
        }
    }

    private static void viewStudentDetailsOption() {
        int studentId = readIntegerInput("Enter Student ID: ");
        Student student = library.getStudent(studentId);

        if (student != null) {
            student.displayDetails();
        } else {
            System.out.println(">> Student ID " + studentId + " not found!");
        }
    }

    private static void addNewBookOption() {
        int bookId = readIntegerInput("Enter New Book ID: ");
        if (library.getBook(bookId) != null) {
            System.out.println(">> Book with ID " + bookId + " already exists!");
            return;
        }

        System.out.print("Enter Book Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter Author Name: ");
        String author = scanner.nextLine().trim();

        int copies = readIntegerInput("Enter Number of Copies: ");

        if (copies <= 0) {
            System.out.println(">> Number of copies must be at least 1.");
            return;
        }

        Book newBook = new Book(bookId, title, author, copies);
        library.addBook(newBook);
        System.out.println(">> Success: Book \"" + title + "\" added successfully to library!");
    }

    private static void registerStudentOption() {
        int studentId = readIntegerInput("Enter New Student ID: ");
        if (library.getStudent(studentId) != null) {
            System.out.println(">> Student with ID " + studentId + " already exists!");
            return;
        }

        System.out.print("Enter Student Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Student Email: ");
        String email = scanner.nextLine().trim();

        Student newStudent = new Student(studentId, name, email);
        library.registerStudent(newStudent);
        System.out.println(">> Success: Student " + name + " registered successfully!");
    }

    private static void viewTransactionsOption() {
        List<Transaction> transactions = library.getTransactions();
        if (transactions.isEmpty()) {
            System.out.println(">> No transactions recorded yet.");
            return;
        }

        System.out.println("\n================= Transaction History =================");
        for (Transaction txn : transactions) {
            System.out.println(txn);
        }
        System.out.println("========================================================");
    }

    // Demonstrates Multi-threading and Concurrency
    private static void runMultithreadingDemo() {
        System.out.println("\n-------------------------------------------------------------");
        System.out.println(">>> MULTI-THREADING SIMULATION: CONCURRENT BORROWING TEST <<<");
        System.out.println("Scenario:");
        System.out.println("Book ID 104 ('Core Java Volume I') has only 1 copy available.");
        System.out.println("3 separate threads will simulate Student 1, Student 2, and");
        System.out.println("Student 3 simultaneously attempting to borrow that single copy.");
        System.out.println("Using synchronized methods, only ONE thread will succeed.");
        System.out.println("The other two threads will be placed in the waitlist queue.");
        System.out.println("-------------------------------------------------------------");

        // Ensure Book 104 has exactly 1 available copy for the test
        Book testBook = library.getBook(104);
        if (testBook == null) {
            testBook = new Book(104, "Core Java Volume I", "Cay Horstmann", 1);
            library.addBook(testBook);
        } else {
            testBook.setAvailableCopies(1);
        }

        Thread thread1 = new Thread(new BorrowTask(library, 1, 104), "Student-Rahul-Thread");
        Thread thread2 = new Thread(new BorrowTask(library, 2, 104), "Student-Priya-Thread");
        Thread thread3 = new Thread(new BorrowTask(library, 3, 104), "Student-Amit-Thread");

        System.out.println(">> Launching 3 threads simultaneously...");
        thread1.start();
        thread2.start();
        thread3.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
            System.out.println("Simulation interrupted: " + e.getMessage());
        }

        System.out.println(">> Multi-threading simulation completed.");
        System.out.println("Remaining copies for Book 104: " + testBook.getAvailableCopies());
    }

    // Helper method to safely read integers without scanner crashing
    private static int readIntegerInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = scanner.nextInt();
                scanner.nextLine(); // Clear newline buffer
                return value;
            } catch (InputMismatchException e) {
                System.out.println(">> Invalid input! Please enter a valid number.");
                scanner.nextLine(); // Clear bad token
            }
        }
    }

    // Seed initial sample data
    private static void preloadData() {
        // Books
        library.addBook(new Book(101, "Effective Java", "Joshua Bloch", 3));
        library.addBook(new Book(102, "Clean Code", "Robert C. Martin", 2));
        library.addBook(new Book(103, "Head First Java", "Kathy Sierra", 2));
        library.addBook(new Book(104, "Core Java Volume I", "Cay Horstmann", 1));
        library.addBook(new Book(105, "Introduction to Algorithms", "Thomas Cormen", 4));

        // Students
        library.registerStudent(new Student(1, "Rahul Sharma", "rahul.sharma@college.edu"));
        library.registerStudent(new Student(2, "Priya Patel", "priya.patel@college.edu"));
        library.registerStudent(new Student(3, "Amit Verma", "amit.verma@college.edu"));
        library.registerStudent(new Student(4, "Sneha Kulkarni", "sneha.k@college.edu"));

        // Librarian
        Librarian librarian = new Librarian(101, "Mrs. Sunita Deshmukh", "sunita.lib@college.edu", "LIB-2024");
    }
}
