# Student Library Management System (Pure Core Java)

A clean, realistic console-based Student Library Management System written in standard Java SE without any external frameworks. It cleanly demonstrates all fundamental Core Java concepts: **OOP principles**, **Java Collections Framework**, **Custom Exception Handling**, and **Multi-threading/Concurrency**.

---

## 📁 Project Structure

```text
StudentLibrarySystemCoreJava/
├── src/
│   └── com/
│       └── library/
│           ├── model/
│           │   ├── User.java                 # Abstract class (OOP Abstraction & Inheritance)
│           │   ├── Student.java              # Inherits User, manages borrowed books list
│           │   ├── Librarian.java            # Inherits User, contains employee details
│           │   ├── Book.java                 # Implements Comparable<Book> for sorting
│           │   └── Transaction.java          # Model for tracking issue/return history
│           ├── exception/
│           │   ├── BookNotFoundException.java       # Custom Checked Exception
│           │   ├── BookNotAvailableException.java   # Custom Checked Exception
│           │   ├── BookLimitExceededException.java  # Custom Checked Exception
│           │   └── StudentNotFoundException.java    # Custom Checked Exception
│           ├── service/
│           │   ├── Borrowable.java           # Interface (OOP Abstraction)
│           │   └── Library.java              # Business logic, Collections, synchronized methods
│           ├── thread/
│           │   ├── OverdueCheckerThread.java # Background daemon thread for overdue checks
│           │   └── BorrowTask.java           # Runnable task to test concurrent borrowing
│           └── Main.java                     # Interactive CLI menu & simulation driver
├── bin/                                      # Compiled .class files
└── README.md
```

---

## 💡 Core Java Concepts Demonstrated

### 1. Object-Oriented Programming (OOP)
* **Abstraction**:
  * `User` is an `abstract` class defining common fields (`id`, `name`, `email`) and an abstract method `displayDetails()`.
  * `Borrowable` interface defines contract methods for borrowing and returning books.
* **Inheritance**:
  * `Student` and `Librarian` inherit from `User`.
* **Polymorphism**:
  * Overridden `displayDetails()` in `Student` and `Librarian`.
  * Method overloading for search criteria in `Library`.
* **Encapsulation**:
  * Private member variables with standard getters/setters and input validation logic.

### 2. Java Collections Framework
* **`HashMap<Integer, Book>`**: Fast $O(1)$ lookup of books by ID.
* **`HashMap<Integer, Student>`**: Fast lookup of registered students by ID.
* **`ArrayList<Transaction>`**: Dynamic ordered list storing all loan/return logs.
* **`Map<Integer, Queue<Student>>`**: Uses `LinkedList` as a `Queue` for book waitlists when stock reaches 0.
* **`Collections.sort()` & `Comparable<Book>`**: Sorts catalog alphabetically by title.

### 3. Exception Handling
* Custom exception hierarchy: `BookNotFoundException`, `BookNotAvailableException`, `BookLimitExceededException`, `StudentNotFoundException`.
* Standard `try-catch` blocks throughout service layers and CLI to gracefully inform users without crashing.
* `InputMismatchException` handled in console scanner to prevent user input crashes.

### 4. Multithreading & Concurrency
* **`synchronized` Methods**: `borrowBook()` and `returnBook()` in `Library.java` are synchronized to prevent race conditions (no two threads can borrow the last copy simultaneously).
* **`Runnable` Interface**: `BorrowTask` implements `Runnable` to simulate concurrent students trying to borrow simultaneously.
* **Background Daemon Thread**: `OverdueCheckerThread` runs in the background (`setDaemon(true)`), periodically scanning active loans.
* **Thread Coordination**: `Thread.join()` is used in `Main.java` to synchronize simulation execution.

---

## 🚀 How to Compile and Run

### 1. Open Terminal / PowerShell in the project root:
```bash
cd "c:\Users\Imran Jamadar\OneDrive\Desktop\StudentLibrarySystemCoreJava"
```

### 2. Compile all source files into the `bin/` directory:
```bash
javac -d bin src/com/library/model/*.java src/com/library/exception/*.java src/com/library/service/*.java src/com/library/thread/*.java src/com/library/*.java
```

### 3. Run the application:
```bash
java -cp bin com.library.Main
```

---

## 🧪 Menu Options & Features

1. **View All Books**: Displays catalog sorted alphabetically.
2. **Search Books**: Search by partial book title or author name.
3. **Borrow a Book**: Enforces borrow limits (max 3 per student) and availability.
4. **Return a Book**: Marks book returned and notifies if students are on the waitlist.
5. **View Student Details**: Displays student info and active borrowed books.
6. **Add New Book**: Add new inventory with stock count.
7. **Register New Student**: Add new students.
8. **View Transaction History**: Displays full audit log of issues and returns.
9. **Check Overdue Books & Fines**: Scans and calculates fines (Rs. 5/day past 7-day period).
10. **Run Multi-threading Demo**: Simulates 3 student threads concurrently competing for 1 remaining book copy to demonstrate thread-safety and queue waitlists.
11. **Exit**: Gracefully shuts down the background daemon thread and exits.
