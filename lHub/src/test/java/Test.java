import org.apache.commons.lang3.StringUtils;

public class Test {

    public static void main(String[] args) {
        String title = "             MINESURGE             ";
        System.out.println(title);
        System.out.println(StringUtils.center("5/12/23 9:18 PM", title.length()));
    }

}
