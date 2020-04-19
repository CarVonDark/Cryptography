import java.util.ArrayList;

public class AES extends Converter{

	private int[] key;
	private ArrayList<int[][]> inputList;
	private Sbox sBox;
	private ArrayList<int[]> keyList;
	
	public AES(String input, String key) {
		super(input);
		this.sBox = new Sbox();
		this.key = parseHexString2Int(key.toLowerCase());
		keyList = new ArrayList<int[]>();
		roundKeysGeneration();
		this.inputList = new ArrayList<int[][]>();
		parseInput(input);
		for(int[][] ints : inputList) {
			for(int i = 0; i < ints.length;  i++) {
				for(int j = 0; j < ints[0].length; i++) {
					System.out.print(ints[i][j]);
				}
				System.out.println();
			}
		}
	}
	
	private void roundKeysGeneration() {
		for(int i = 0; i < key.length - 4; i+=4) {
			int[] w = new int[4];
			for(int j = 0; j < 4; j++) {
				w[j] = this.key[i + j];
			}
			this.keyList.add(w);
		}
		for(int i = 0; i < 40; i+=4) {
			int[] current0 = keyList.get(i);
			int[] current1 = keyList.get(i+1);
			int[] current2 = keyList.get(i+2);
			int[] current3 = keyList.get(i+3);
			int[] gOut = gFunction(current3, (i+4)/4);
			int[] current4 = xor(current0, gOut);
			keyList.add(current4);
			int[] current5 = xor(current1, current4);
			keyList.add(current5);
			int[] current6 = xor(current2, current5);
			keyList.add(current6);
			int[] current7 = xor(current3, current6);
			keyList.add(current7);
		}
	}
	
	private int[] gFunction(int[] w3, int j) {
		for(int i  = 0; i < w3.length; i++) {
			int temp = w3[i];
			w3[i] = w3[(i+1)%w3.length];
			w3[(i+1)%w3.length] = temp;
		}
		
		for(int i  = 0; i < w3.length; i++) {
			int row = (w3[i] & ( 15 << 4)) >> 4;
			int col = w3[i] & 15;
			w3[i] = sBox.getSbox(row, col);
		}
		Polynomial p = new Polynomial((int) Math.pow(2, j - 1));
		w3[0] = w3[0] ^ p.get();
		return w3;
	}
	
	private int[] xor(int[] first, int[] second) {
		if(first.length != second.length) {
			return null;
		} else {
			int[] re = new int[first.length];
			for(int i = 0; i < first.length; i++) {
				re[i] = (int) (first[i] ^ second[i]);
			}
			return re;
		}
	}
	
	private int[] parseHexString2Int(String hex){
		int[] re = new int[hex.length()/2];
		for(int i = 0; i < hex.length()-1; i+=2) {
			int firstHalf = parseHexchar2Int(hex.charAt(i));
			int secondHalf = parseHexchar2Int(hex.charAt(i+1));
			re[i/2] = (firstHalf << 4) + secondHalf;
			
		}
		return re;
	}
	
	private int parseHexchar2Int(char c) {
		int charval=Character.getNumericValue(c);
		int zeroval=Character.getNumericValue('0');
		int a=Character.getNumericValue('a');

		if(charval-zeroval>=0 && charval-zeroval<=9)
			return charval-zeroval;

		return charval-a+10;
	}
	
	private void parseInput(String input) {
		for(int i = 0; i < input.length()/32; i++) {
			inputList.add(parseInputBlock(input.substring(i,i+32)));
		}
	}
	
	private int[][] parseInputBlock(String block){
		int[][] re = new int[4][4];
		for(int i = 0; i < block.length()-1; i+=2){
			int firstHalf = parseHexchar2Int(block.charAt(i));
			int secondHalf = parseHexchar2Int(block.charAt(i+1));
			re[i/8][(i/2)%4] = (firstHalf << 4) + secondHalf;
		}
		return re;
	}

}
