package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberSequence {
    private static final Logger logger = LoggerFactory.getLogger(NumberSequence.class);
    private static final int FIRST = 1;
    private static final int SECOND = 2;
    private static final int MIN = 1;
    private static final int MAX = 10;
    private int currentThread = FIRST;
    private int currentNumber = 1;
    private int sequenceDirection = 1;

    private synchronized void action(int threadId) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                while (threadId != currentThread) {
                    this.wait();
                }

                logger.info("{}", currentNumber);
                modifyNumber(threadId);

                currentThread = (threadId == FIRST) ? SECOND : FIRST;

                sleep();
                notifyAll();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void modifyNumber(int thread) {
        if (thread == SECOND) {
            int next = currentNumber + sequenceDirection;
            if (next > MAX || next < MIN) {
                sequenceDirection *= -1;
            }

            currentNumber += sequenceDirection;
        }
    }

    public static void main(String[] args) {
        NumberSequence numberSequence = new NumberSequence();
        new Thread(() -> numberSequence.action(FIRST), "first thread").start();
        new Thread(() -> numberSequence.action(SECOND), "second thread").start();
    }

    private static void sleep() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
