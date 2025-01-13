package task;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSynchronizationExample {
    // Define three locks
    private static final Lock lock1 = new ReentrantLock();
    private static final Lock lock2 = new ReentrantLock();
    private static final Lock lock3 = new ReentrantLock();

    public static void main(String[] args) {
        // Create three threads
        Thread thread1 = new Thread(new Task("Task1", lock1, lock2, lock3), "Thread-1");
        Thread thread2 = new Thread(new Task("Task2", lock2, lock3, lock1), "Thread-2");
        Thread thread3 = new Thread(new Task("Task3", lock1, lock2, lock3), "Thread-3");

        // Start threads
        thread1.start();
        thread2.start();
        thread3.start();
    }

    // Generic Task class for reusability
    static class Task implements Runnable {
        private final String taskName;
        private final Lock firstLock;
        private final Lock secondLock;
        private final Lock thirdLock;

        public Task(String taskName, Lock firstLock, Lock secondLock, Lock thirdLock) {
            this.taskName = taskName;
            this.firstLock = firstLock;
            this.secondLock = secondLock;
            this.thirdLock = thirdLock;
        }

        @Override
        public void run() {
            try {
                // Attempt to acquire the first lock
                if (firstLock.tryLock(500, TimeUnit.MILLISECONDS)) {
                    System.out.println(Thread.currentThread().getName() + " acquired " + taskName + " - Lock 1");
                    Thread.sleep(50); // Simulate work

                    // Attempt to acquire the second lock
                    if (secondLock.tryLock(500, TimeUnit.MILLISECONDS)) {
                        System.out.println(Thread.currentThread().getName() + " acquired " + taskName + " - Lock 2");
                        Thread.sleep(50); // Simulate work

                        // Attempt to acquire the third lock
                        if (thirdLock.tryLock(500, TimeUnit.MILLISECONDS)) {
                            System.out.println(Thread.currentThread().getName() + " acquired " + taskName + " - Lock 3");
                            Thread.sleep(50); // Simulate work
                        } else {
                            System.out.println(Thread.currentThread().getName() + " failed to acquire Lock 3");
                        }
                    } else {
                        System.out.println(Thread.currentThread().getName() + " failed to acquire Lock 2");
                    }
                } else {
                    System.out.println(Thread.currentThread().getName() + " failed to acquire Lock 1");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println(Thread.currentThread().getName() + " was interrupted");
            } finally {
                // Release locks in reverse order of acquisition
                if (((ReentrantLock) thirdLock).isHeldByCurrentThread()) {
                    thirdLock.unlock();
                    System.out.println(Thread.currentThread().getName() + " released Lock 3");
                }
                if (((ReentrantLock) secondLock).isHeldByCurrentThread()) {
                    secondLock.unlock();
                    System.out.println(Thread.currentThread().getName() + " released Lock 2");
                }
                if (((ReentrantLock) firstLock).isHeldByCurrentThread()) {
                    firstLock.unlock();
                    System.out.println(Thread.currentThread().getName() + " released Lock 1");
                }
            }
        }
    }
}
