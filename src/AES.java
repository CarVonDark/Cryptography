import java.util.ArrayList;

public class AES extends Converter{

	private int[] key;
	private ArrayList<int[][]> inputList;
	private Sbox sBox;
	private ArrayList<int[]> keyList;
	private Polynomial[][] mixMatrix;
	
	public AES(String input, String key) {
		super(input);
		this.sBox = new Sbox();
		this.key = parseHexString2Int(key.toLowerCase());
		keyList = new ArrayList<int[]>();
		roundKeysGeneration();
		this.inputList = new ArrayList<int[][]>();
		parseInput(input);
		this.mixMatrix = new Polynomial[4][4];
		initializeMixMatrix();
//		for(int[][] ints : inputList) {
//			for(int i = 0; i < ints.length;  i++) {
//				for(int j = 0; j < ints[0].length; i++) {
//					System.out.print(ints[i][j]);
//				}
//				System.out.println();
//			}
//		}
	}
	
	public String encrypt() {
		StringBuilder sb = new StringBuilder();
		for(int[][] block : inputList) {
			block = addRoundKey(block, 0);
			for(int round = 1; round < 11; round++) {
				block = substituteBytes(block);
				block = shiftRows(block);
				block = mixCols(block);
				block = addRoundKey(block, round);
			}
			sb.append(parseInts2String(block));
		}
		return sb.toString();
	}
	
	private int[][] addRoundKey(int[][] nums, int round){
		for(int col = 0; col < nums[0].length; col++) {
			int[] roundKey = keyList.get(round * 4 + col);
			for(int row = 0; row < nums.length; row++) {
				nums[row][col] = nums[row][col] ^ roundKey[row];
			}
		}
		return nums;
	}
	
	private int[][] substituteBytes(int[][] nums) {
		for(int i = 0; i < nums.length; i++) {
			for(int j = 0; j < nums.length; j++) {
				int row = (nums[i][j] & (15 << 4)) >> 4;
				int col = nums[i][j] & 15;
				nums[i][j] = sBox.getSbox(row, col);
			}
		}
		return nums;
	}
	
	private int[][] shiftRows(int[][] nums){
		for(int i = 0; i < nums[1].length-1; i++) {
			int temp = nums[1][i];
			nums[1][i] = nums[1][i+1];
			nums[1][i+1] = temp;
		}
		for(int i = 0; i < nums[2].length-2; i++) {
			int temp = nums[2][i];
			nums[2][i] = nums[2][i+2];
			nums[2][i+2] = temp;
		}
		for(int i = 1; i < nums[3].length+1; i++) {
			int temp = nums[3][i%4];
			nums[3][i-1] = nums[3][i%4];
			nums[3][i%4] = temp;
		}
		return nums;
	}
	
	private void initializeMixMatrix() {
		this.mixMatrix[0][0] = new Polynomial(2);
		this.mixMatrix[0][1] = new Polynomial(3);
		this.mixMatrix[0][2] = new Polynomial(1);
		this.mixMatrix[0][3] = new Polynomial(1);
		
		this.mixMatrix[1][0] = new Polynomial(1);
		this.mixMatrix[1][1] = new Polynomial(2);
		this.mixMatrix[1][2] = new Polynomial(3);
		this.mixMatrix[1][3] = new Polynomial(1);
		
		this.mixMatrix[2][0] = new Polynomial(1);
		this.mixMatrix[2][1] = new Polynomial(1);
		this.mixMatrix[2][2] = new Polynomial(2);
		this.mixMatrix[2][3] = new Polynomial(3);
		
		this.mixMatrix[3][0] = new Polynomial(3);
		this.mixMatrix[3][1] = new Polynomial(1);
		this.mixMatrix[3][2] = new Polynomial(1);
		this.mixMatrix[3][3] = new Polynomial(2);
	}
	
	private int[][] mixCols(int[][] nums){
		for(int col = 0; col < 4; col++) {
			Polynomial row0 = new Polynomial(nums[0][col]);
			Polynomial row1 = new Polynomial(nums[1][col]);
			Polynomial row2 = new Polynomial(nums[2][col]);
			Polynomial row3 = new Polynomial(nums[3][col]);
			row0 = Polynomial.add(
					Polynomial.add(Polynomial.mul(row0, mixMatrix[0][0]), Polynomial.mul(row1, mixMatrix[0][1])),
					Polynomial.add(Polynomial.mul(row2, mixMatrix[0][2]), Polynomial.mul(row3, mixMatrix[0][3])));
			row1 = Polynomial.add(
					Polynomial.add(Polynomial.mul(row0, mixMatrix[1][0]), Polynomial.mul(row1, mixMatrix[1][1])),
					Polynomial.add(Polynomial.mul(row2, mixMatrix[1][2]), Polynomial.mul(row3, mixMatrix[1][3])));
			row2 = Polynomial.add(
					Polynomial.add(Polynomial.mul(row0, mixMatrix[2][0]), Polynomial.mul(row1, mixMatrix[2][1])),
					Polynomial.add(Polynomial.mul(row2, mixMatrix[2][2]), Polynomial.mul(row3, mixMatrix[2][3])));
			row3 = Polynomial.add(
					Polynomial.add(Polynomial.mul(row0, mixMatrix[3][0]), Polynomial.mul(row1, mixMatrix[3][1])),
					Polynomial.add(Polynomial.mul(row2, mixMatrix[3][2]), Polynomial.mul(row3, mixMatrix[3][3])));
			nums[0][col] = row0.get();
			nums[1][col] = row1.get();
			nums[2][col] = row2.get();
			nums[3][col] = row3.get();
		}
		return nums;
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
			re[(i/2)%4][i/8] = (firstHalf << 4) + secondHalf;
		}
		return re;
	}
	
	private String parseInts2String(int[][] nums) {
		StringBuilder sb = new StringBuilder();
		for(int col = 0; col < nums[0].length; col++) {
			for(int row = 0; row < nums.length; row++) {
				int firstHalf = (nums[row][col] & (15 << 4)) >> 4;
				int secondHalf = nums[row][col] & 15;
				sb.append(Integer.toHexString(firstHalf));
				sb.append(Integer.toHexString(secondHalf));
			}
		}
		return sb.toString();
	}

}
