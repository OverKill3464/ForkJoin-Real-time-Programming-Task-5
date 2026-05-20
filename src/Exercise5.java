import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
import java.util.Arrays;

// Class that extends RecursiveTask<int[]>
class MinMaxTask extends RecursiveTask<int[]> {
    private int[] numbers;
    private int start, end;
    // Threshold: 4
    private static final int THRESHOLD = 4;

    public MinMaxTask(int[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    protected int[] compute() {
        // If the segment size is less than or equal threshold
        if ((end - start) <= THRESHOLD) {
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;

            for (int i = start; i < end; i++) {
                if (numbers[i] < min) min = numbers[i];
                if (numbers[i] > max) max = numbers[i];
            }
            return new int[]{min, max};
        } else {
            // If the segment size is more than threshold
            int mid = start + (end - start) / 2;

            MinMaxTask leftTask = new MinMaxTask(numbers, start, mid);
            MinMaxTask rightTask = new MinMaxTask(numbers, mid, end);

            leftTask.fork();
            int[] rightResult = rightTask.compute();
            int[] leftResult = leftTask.join();

            int finalMin = Math.min(leftResult[0], rightResult[0]);
            int finalMax = Math.max(leftResult[1], rightResult[1]);

            return new int[]{finalMin, finalMax};
        }
    }
}

public class Exercise5 {
    public static void main(String[] args) {
        // Input the given data into the array
        int[] data = {12, 5, 88, 19, 20, 3, 40, 7, 18, 21, 50, 60};

        ForkJoinPool pool = new ForkJoinPool();
        int[] result = pool.invoke(new MinMaxTask(data, 0, data.length));

        System.out.println("Array: " + Arrays.toString(data));
        System.out.println("Minimum Value: " + result[0]);
        System.out.println("Maximum Value: " + result[1]);
    }
}