import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A refactored version of UgliestCode.java that is clean, correct, and maintainable.
 */
public class RefactoredCode {

    // It's good practice to use a proper logger instead of System.out.println for errors.
    private static final Logger LOGGER = Logger.getLogger(RefactoredCode.class.getName());

    public static void main(String[] args) {
        int b = 10;
        int sum = 0;

        // --- ISSUE FIXED: Infinite Loop ---
        // The original loop `i += a - 5` (where a=5) was an infinite loop because the increment was 0.
        // Assuming the intent was to iterate with a step of 5, the loop now correctly
        // iterates and adds 0 and 5 to the sum.
        for (int i = 0; i < b; i += 5) {
            sum += i;
        }
        System.out.println("Sum after loop: " + sum); // Expected: 5 (from 0 + 5)

        // --- ISSUE FIXED: Useless and Confusing Code Removed ---
        // The following lines from the original code were removed as they were
        // either non-functional, confusing, or served no purpose:
        // - `if (a == b);`
        // - `String s = "Result:" + null + (Object) null;`
        // - `Object o = (Runnable & java.io.Closeable) () -> {};`

        // --- ISSUE FIXED: Proper Error Handling ---
        // The original code silently caught an exception, hiding a bug.
        // Now, it correctly logs the error to provide visibility.
        int[] arr = new int[1];
        try {
            // This line will throw an exception because the array size is 1 (valid index is 0).
            arr[1] = 99;
        } catch (ArrayIndexOutOfBoundsException e) {
            // Log the specific exception with a clear message.
            LOGGER.log(Level.SEVERE, "Error: Attempted to access an invalid array index.", e);
        }

        // --- ISSUE FIXED: Proper Error Handling ---
        // The original code caught a generic 'Exception'.
        // It's better to catch the most specific exception type, in this case, NumberFormatException.
        try {
            // This line will always throw an exception.
            sum += Integer.parseInt("notANumber");
        } catch (NumberFormatException e) {
            // Log the specific exception. No recovery logic is implemented as per the original,
            // but logging makes the failure clear.
            LOGGER.log(Level.SEVERE, "Error: Failed to parse a number.", e);
        }

        System.out.println("Final Sum: " + sum);
    }
} 