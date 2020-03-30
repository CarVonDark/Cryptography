import java.util.HashMap;

public class Affine extends Converter {

	private int multiple;
	private int constant;
	private HashMap<Character, Integer> map;
	private HashMap<Integer, Character> map2;

	public Affine(String input, int mulitple, int constant) {
		super(input.toLowerCase());
		this.multiple = mulitple;
		this.constant = constant;
		map = new HashMap<Character, Integer>();
		map2 = new HashMap<Integer, Character>();
		createMap();
	}

	private void createMap() {
		int a = 97;
		for (int i = 0; i < 26; i++) {
			map.put((char) a, i);
			map2.put(i, (char) a);
			a++;
		}
	}

	public String encrypt() {
		StringBuilder sb = new StringBuilder();
		String input = getInput();
		for (int i = 0; i < input.length(); i++) {
			if (!map.containsKey(input.charAt(i))) {
				sb.append(input.charAt(i));
				continue;
			}
			int value = map.get(input.charAt(i));
			value = (value * multiple + constant) % 26;
			sb.append(map2.get(value));
		}
		return sb.toString().toUpperCase();
	}

	public String decrypt() {
		StringBuilder sb = new StringBuilder();
		String input = getInput();
		int flag = 0;
		int inverse = 0;
		for (int i = 0; i < 26; i++) {
			flag = (multiple * i) % 26;
			if (flag == 1) {
				inverse = i;
			}
		}
		for (int i = 0; i <= input.length()-1; i++) {
			if (!map.containsKey(input.charAt(i))) {
				sb.append(input.charAt(i));
				continue;
			}
			int value = map.get(input.charAt(i));
			if (value - constant < 0) {
				value = ((value - constant + 26) * inverse) % 26;
			} else {
				value = ((value - constant) * inverse) % 26;
			}
			sb.append(map2.get(value));
		}
		return sb.toString().toLowerCase();
	}

}
