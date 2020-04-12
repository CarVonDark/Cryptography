import java.util.ArrayList;
import java.util.Stack;

public class CFB_ADD extends Converter {

	ArrayList<Integer> inputText;
	int constant;
	int initialVector;

	public CFB_ADD(String input, String key, String initial) {
		super(input);
		this.constant = Integer.parseInt(key);
		this.inputText = new ArrayList<Integer>();
		makeInputList(sanitize(input));
		System.out.println(inputText.toString());
		this.initialVector = parseBinaryString(sanitize(initial));
		System.out.println("CFB_Cons");
	}

	private void makeInputList(String str) {
		for (int i = 0; i < str.length() - 3; i += 4) {
			inputText.add(parseBinaryString(str.substring(i, i + 4)));
		}
	}

	private int parseBinaryString(String str) {
		int sum = 0;
		for (int i = 0; i < str.length(); i++) {
			try {
				sum += Math.pow(2, str.length() - i - 1) * Integer.parseInt(str.substring(i, i + 1));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return sum;
	}

	private String sanitize(String str) {
		StringBuilder sb = new StringBuilder();
		for (char c : str.toCharArray()) {
			if (c == '1' || c == '0')
				sb.append(c);
		}
		return sb.toString();
	}

	private int selectTop4Bits(int input) {
		int result = 0;
		Stack<Integer> s = new Stack<Integer>();
		while (input > 0) {
			s.add(input % 2);
			input /= 2;
		}
		for (int i = 7; i > 3; i--) {
			if (i < s.size() && !s.isEmpty()) {
				result += s.pop() * Math.pow(2, i - 4);
			}
		}
		return result;
	}

	private String Int2BinaryString(int num) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			sb.append(num % 2);
			num /= 2;
		}
		return sb.reverse().toString();
	}

	private int makeNewVector(boolean mode, int ciphertext, int vector) {
		if (mode) {
			return vector % 256;
		} else {
			return ((vector << 4) + ciphertext) % 256;
		}
	}

	public String encrypt() {
		StringBuilder sb = new StringBuilder();
		int vector = this.initialVector;
		int ciphertext = 0;
		boolean mode = true;
		for (int i = 0; i < inputText.size(); i++) {
			vector = makeNewVector(mode, ciphertext, vector);
			ciphertext = inputText.get(i) ^ selectTop4Bits((vector + this.constant) % 256);
			sb.append(Int2BinaryString(ciphertext));
			mode = false;
		}
		return sb.toString();
	}

	public String decrypt() {
		StringBuilder sb = new StringBuilder();
		int vector = this.initialVector;
		boolean mode = true;
		for (int i = 0; i < inputText.size(); i++) {
			if (mode) {

			} else {
				vector = makeNewVector(mode, inputText.get(i - 1), vector);
			}
			int plaintext = inputText.get(i) ^ selectTop4Bits((vector + this.constant) % 256);
			sb.append(Int2BinaryString(plaintext));
			mode = false;
		}
		return sb.toString();
	}
}
