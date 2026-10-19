package com.library.thread;

import com.library.service.Library;

public class OverdueCheckerThread implements Runnable {
    private Library library;
    private volatile boolean running = true;
    private int intervalSeconds;

    public OverdueCheckerThread(Library library, int intervalSeconds) {
        this.library = library;
        this.intervalSeconds = intervalSeconds;
    }

    public void stopChecker() {
        this.running = false;
    }

    @Override
    public void run() {
        System.out.println("[Background Thread] OverdueCheckerThread started (runs every " + intervalSeconds + "s)...");
        while (running) {
            try {
                Thread.sleep(intervalSeconds * 1000L);
                if (!running) break;
                // Periodic background check
                // In a real system, it would notify students via email/SMS
            } catch (InterruptedException e) {
                // Thread interrupted on shutdown
                break;
            }
        }
        System.out.println("[Background Thread] OverdueCheckerThread stopped.");
    }
}
