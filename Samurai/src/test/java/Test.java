import java.io.File;
import java.io.InputStream;

public class Test {

    public static void main(String[] args) {
        System.out.println(((long) 1002 << 32) + 1000 - Integer.MIN_VALUE);
    }

    // 4297114780648
    // 4314294649832
    // 4305704715240
    // 4365834257384
}
