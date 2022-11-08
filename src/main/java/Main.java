import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        Thread threadGenerator = new Thread(() -> { //наполняет очереди текстом
            for (int i = 0; i < 10000; i++) {
                String texts = generateText("abc", 100000);
                try {
                    queueA.put(texts);
                    queueB.put(texts);
                    queueC.put(texts);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        threadGenerator.start();

        Thread threadA = new Thread(() -> { //a
            char letter = 'a';
            int maxA = findMaxChar(queueA, letter);
            System.out.println("Максимально колличество " + letter + " во всем тексте: " + maxA);
        });
        threadA.start();

        Thread threadB = new Thread(() -> { //b
            char letter = 'b';
            int maxB = findMaxChar(queueB, letter);
            System.out.println("Максимально колличество " + letter + " во всем тексте: " + maxB);
        });
        threadB.start();

        Thread threadC = new Thread(() -> { //c
            char letter = 'c';
            int maxC = findMaxChar(queueC, letter);
            System.out.println("Максимально колличество " + letter + " во всем тексте: " + maxC);
        });
        threadC.start();

        threads.add(threadA);
        threads.add(threadB);
        threads.add(threadC);

        for (Thread thread : threads) {
            thread.join(); // зависаем, ждём когда поток объект которого лежит в thread завершится
        }
    }

    private static int findMaxChar(BlockingQueue<String> queue, char letter) {
        int count = 0;
        int max = 0;
        String text;
        try {
            for (int i = 0; i < 10000; i++) {
                text = queue.take();
                for (char c : text.toCharArray()) {
                    if (c == letter) count++;
                }
                if (count > max) max = count;
                count = 0;
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " был прерван.");
            return -1;
        }
        return max;
    }


    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
