import java.util.concurrent.atomic.AtomicInteger;

/*
 * Демонстрация гонки данных (race condition).
 * Два потока одновременно инкрементируют общий счётчик.
 * Решение: synchronized на методе increment().
 */
public class Zadacha_dop {

    // Сколько раз суммарно должны инкрементировать оба потока
    private static final int HUNDRED_MILLION = 100_000_000;

    // Счётчик остановки: два потока вместе делают ровно HUNDRED_MILLION вызовов stop()
    private final AtomicInteger stopCounter = new AtomicInteger();

    // Общий счётчик инкрементов
    private int sharedCounter = 0;

    // stop() возвращает true, когда лимит вызовов превышен
    boolean stop() {
        return stopCounter.incrementAndGet() > HUNDRED_MILLION;
    }

    // synchronized — только один поток за раз может войти сюда
    synchronized void increment() {
        sharedCounter++;
    }

    int getCounter() {
        return sharedCounter;
    }

    // Тело потока
    private void runLoop() {
        while (!stop()) {
            increment();
        }
    }

    void example() throws InterruptedException {
        Thread thread1 = new Thread(this::runLoop);
        Thread thread2 = new Thread(this::runLoop);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Expected: " + HUNDRED_MILLION);
        System.out.println("Result:   " + getCounter());

        if (getCounter() == HUNDRED_MILLION) {
            System.out.println("Гонки данных нет — счётчик корректен.");
        } else {
            System.out.println("Обнаружена гонка данных!");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Zadacha_dop().example();
    }
}