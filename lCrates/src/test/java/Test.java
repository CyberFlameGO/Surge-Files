import java.util.Arrays;
import java.util.List;

public class Test {

    public static List<Integer> non_nulls = Arrays.asList(
            2, 5, 10, 53
    );

    public static void main(String[] args) {
        int rows = 6;
        int actualRows = 0;

        for (int y = 0; y < rows; y++) {
            String s = "";
            int total_valid = 0;
            for (int x = 0; x < 9; x++) {
                if (non_nulls.contains((9 * y + x))) {
                    total_valid++;
                }
                s += (9 * y + x) + ":";
            }
            if (total_valid > 0) actualRows = y + 1;
            System.out.println(s);
        }

        System.out.println(actualRows);
    }

}
