import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
import java.util.Arrays;

// Extending RecursiveTask<int[]>
class GradeCountTask extends RecursiveTask<int[]> {
    private int[] scores;
    private int start, end;
    // Threshold: 6
    private static final int THRESHOLD = 6;

    public GradeCountTask(int[] scores, int start, int end) {
        this.scores = scores;
        this.start = start;
        this.end = end;
    }

    @Override
    protected int[] compute() {
        // If the segment size is less than the threshold
        if ((end - start) <= THRESHOLD) {
            int[] counts = new int[5]; // We use 5 because of there is 5 scoring labels (0=A, 1=B, 2=C, 3=D, 4=F)

            for (int i = start; i < end; i++) {
                int score = scores[i];
                if (score >= 85) counts[0]++;      // for A: 85 - 100
                else if (score >= 70) counts[1]++; // for B: 70 - 84
                else if (score >= 55) counts[2]++; // for C: 55 - 69
                else if (score >= 40) counts[3]++; // for D: 40 - 54
                else counts[4]++;                  // for F: 0 - 39
            }
            return counts;
        } else {
            // If the segment size is more than the threshold
            int mid = start + (end - start) / 2;

            GradeCountTask leftTask = new GradeCountTask(scores, start, mid);
            GradeCountTask rightTask = new GradeCountTask(scores, mid, end);

            leftTask.fork();
            int[] rightResult = rightTask.compute();
            int[] leftResult = leftTask.join();

            int[] combinedCounts = new int[5];
            for (int i = 0; i < 5; i++) {
                combinedCounts[i] = leftResult[i] + rightResult[i];
            }

            return combinedCounts;
        }
    }
}

public class Exercise5Task2 {
    public static void main(String[] args) {
        // Input the given data scores into array 'scores'
        int[] scores = {75, 88, 92, 55, 63, 79, 100, 82, 45, 38, 67, 73, 89, 95, 50};

        ForkJoinPool pool = new ForkJoinPool();
        int[] finalCounts = pool.invoke(new GradeCountTask(scores, 0, scores.length));

        // make a simple output to print the results
        System.out.println("Student Scores: " + Arrays.toString(scores));
        System.out.println("Grade Counts");
        System.out.println("Grade A (85-100): " + finalCounts[0]);
        System.out.println("Grade B (70-84):  " + finalCounts[1]);
        System.out.println("Grade C (55-69):  " + finalCounts[2]);
        System.out.println("Grade D (40-54):  " + finalCounts[3]);
        System.out.println("Grade F (0-39):   " + finalCounts[4]);
    }
}