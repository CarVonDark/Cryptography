import java.util.ArrayList;
import java.util.Stack;

public class OFM_ADD extends Converter{
	
	ArrayList<Integer> inputText;
	ArrayList<Integer> prepared;
	int constant;
	int initialVector;

	public OFM_ADD(String input, String key, String initial) {
		super(input);
		this.constant = Integer.parseInt(key);
		this.inputText = new ArrayList<Integer>();
		makeInputList(sanitize(input));
		this.initialVector = parseBinaryString(sanitize(initial));
		prepared = new ArrayList<Integer>();
		prepare();
	}

	private void prepare() {
		int vector = this.initialVector;
		for(int i = 0; i < inputText.size(); i++) {
			int prep = selectTop4Bits((vector + constant)%256);
			prepared.add(prep);
			vector =  ((vector << 4) + prep) % 256;
		}
	}
	
	private int selectTop4Bits(int input) {
		int result = 0;
		Stack<Integer> s = new Stack<Integer>();
		while(input > 0) {
			s.add(input%2);
			input/=2;
		}
		for(int i = 7; i > 3; i--) {
			if(i < s.size() && !s.isEmpty()) {
				result += s.pop() * Math.pow(2, i - 4);
			}
		}
		return result;
	}

	private int parseBinaryString(String str) {
		int sum = 0;
		for(int i = 0; i < str.length(); i++) {
			try {
				sum += Math.pow(2,str.length() - i - 1)*Integer.parseInt(str.substring(i,i+1));
			}catch(NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return sum;
	}
	
	private void makeInputList(String str) {
		for(int i  = 0; i < str.length() - 3; i+=4) {
			inputText.add(parseBinaryString(str.substring(i,i+4)));
		}
	}
	
	public String encrypt() {
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < inputText.size(); i++) {
			sb.append(Int2BinaryString(inputText.get(i) ^ prepared.get(i)));
		}
		
		return sb.toString();
	}
	
	public String decrypt() {
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < inputText.size(); i++) {
			sb.append(Int2BinaryString(inputText.get(i) ^ prepared.get(i)));
		}
		
		return sb.toString();
	}
	
	private String Int2BinaryString(int num) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 4; i++) {
			sb.append(num%2);
			num/=2;
		}
		return sb.reverse().toString();
	}
	
	private String sanitize(String str) {
		StringBuilder sb = new StringBuilder();
		for(char c : str.toCharArray()) {
			if(c == '1' || c == '0')
			sb.append(c);
		}
		return sb.toString();
	}
	
}
