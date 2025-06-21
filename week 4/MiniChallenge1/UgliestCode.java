public class UgliestCode {

    public static void main(String[] args) {
        int a = 5, b = 10, sum = 0;
        if (a == b); // Useless semicolon, does nothing even if true

        for (int i = 0; i < b; i += a - 5) sum = sum + i; // Weird loop that actually adds multiples of 5

        String s = "Result:" + null + (Object) null; // Weird and confusing concatenation

        int[] arr = new int[1];
        try { arr[1] = 99; } catch (Exception e) {} // Silent index error, nothing logged

        Object o = (Runnable & java.io.Closeable) () -> {}; // Useless intersection type

        try {
            sum += Integer.parseInt("notANumber"); // Always throws
        } catch (Exception e) {
            System.out.println("Sum failed: " + e); // No recovery logic
        }

        System.out.println("Final Sum: " + sum); // Prints 0 or valid partial sum
    }
}
