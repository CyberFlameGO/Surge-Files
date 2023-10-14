import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/11/2021 / 3:55 PM
 * LBuddyBoy Development / PACKAGE_NAME
 */
public class Test {

	public static void main(String[] args) {
		System.out.println("----------------------------------------".length());
		System.out.println("----------------------------------------".length());
		int width = 21, height = 9;

		List<String> texts = new ArrayList<>();

		for (int z = -(height - 1); z < (height); z++) {
			String text = "";

			for (int x = -(width - 1); x < width; x++) {
				if (z == 0 && x == 0) {
					text += "+";
					continue;
				}
				text += "-";
			}

			texts.add(text);
		}

		for (String text : texts) {
			System.out.println(text);
		}
	}

}
