import java.util.ArrayList;

public class AES extends Converter {

	private int[] key;
	private ArrayList<int[][]> inputList;
	private ArrayList<int[]> keyList;
	private Polynomial[][] mixMatrix;
	private Polynomial[][] invMixMatrix;
	private Polynomial[] roundConstants;

	public AES(String input, String key) {
		super(input);
		roundConstants = new Polynomial[11];
		initRoundConstants();
		this.key = parseHexString2Int(key.toLowerCase());
		keyList = new ArrayList<int[]>();
		roundKeysGeneration();
		this.inputList = new ArrayList<int[][]>();
		parseInput(input);
		this.mixMatrix = new Polynomial[4][4];
		initializeMixMatrix();
		this.invMixMatrix = new Polynomial[4][4];
		initializeInvMixMatrix();
	}

	public String encrypt() {
		StringBuilder sb = new StringBuilder();
		for (int[][] block : inputList) {
			for (int round = 0; round < 9; round++) {
				block = addRoundKey(block, round);
				block = substituteBytes(block);
				block = shiftRows(block);
				block = mixCols(block);
			}
			block = addRoundKey(block, 9);
			block = substituteBytes(block);
			block = shiftRows(block);
			block = addRoundKey(block, 10);
			sb.append(parseInts2String(block));
		}
		return sb.toString().toUpperCase();
	}
	
	public String decrypt() {
		StringBuilder sb = new StringBuilder();	
		for (int[][] block : inputList) {
			block = addRoundKey(block, 10);
			for (int round = 9; round > 0; round--) {
				block = invShiftRows(block);
				block = invSubBytes(block);
				block = addRoundKey(block, round);
				block = invMixCols(block);
			}
			block = invShiftRows(block);
			block = invSubBytes(block);
			block = addRoundKey(block, 0);
			sb.append(parseInts2String(block));
		}		
		return sb.toString().toUpperCase();
	}
	
	private int[][] addRoundKey(int[][] nums, int round) {
		int[][] re = nums.clone();
		for (int col = 0; col < re[0].length; col++) {
			int[] roundKey = keyList.get(round * 4 + col);
			for (int row = 0; row < re.length; row++) {
				re[row][col] = Polynomial.modulus(nums[row][col] ^ roundKey[row]);
			}
		}
		return nums;
	}

	private int[][] substituteBytes(int[][] nums) {
		for (int i = 0; i < nums.length; i++) {
			for (int j = 0; j < nums.length; j++) {
				int row = (nums[i][j] & (15 << 4)) >> 4;
				int col = nums[i][j] & 15;
				nums[i][j] = Sbox.getSbox(row, col);
			}
		}
		return nums;
	}
	
	private int[][] invSubBytes(int[][] nums){
		for (int i = 0; i < nums.length; i++) {
			for (int j = 0; j < nums.length; j++) {
				int row = (nums[i][j] & (15 << 4)) >> 4;
				int col = nums[i][j] & 15;
				nums[i][j] = Sbox.getInvSbox(row, col);
			}
		}
		return nums;
	}

	private int[][] shiftRows(int[][] nums) {
		for (int i = 0; i < nums[1].length - 1; i++) {
			int temp = nums[1][i];
			nums[1][i] = nums[1][i + 1];
			nums[1][i + 1] = temp;
		}
		for (int i = 0; i < nums[2].length - 2; i++) {
			int temp = nums[2][i];
			nums[2][i] = nums[2][i + 2];
			nums[2][i + 2] = temp;
		}
		for (int i = nums[3].length - 1; i > 0; i--) {
			int temp = nums[3][i - 1];
			nums[3][i - 1] = nums[3][i];
			nums[3][i] = temp;
		}
		return nums;
	}
	
	private int[][] invShiftRows(int[][] nums){
		for (int i = 0; i < nums[3].length - 1; i++) {
			int temp = nums[3][i];
			nums[3][i] = nums[3][i + 1];
			nums[3][i + 1] = temp;
		}
		for (int i = 0; i < nums[2].length - 2; i++) {
			int temp = nums[2][i];
			nums[2][i] = nums[2][i + 2];
			nums[2][i + 2] = temp;
		}
		for (int i = nums[1].length - 1; i > 0; i--) {
			int temp = nums[1][i - 1];
			nums[1][i - 1] = nums[1][i];
			nums[1][i] = temp;
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
	
	private void initializeInvMixMatrix() {
		this.invMixMatrix[0][0] = new Polynomial(14);
		this.invMixMatrix[0][1] = new Polynomial(11);
		this.invMixMatrix[0][2] = new Polynomial(13);
		this.invMixMatrix[0][3] = new Polynomial(9);

		this.invMixMatrix[1][0] = new Polynomial(9);
		this.invMixMatrix[1][1] = new Polynomial(14);
		this.invMixMatrix[1][2] = new Polynomial(11);
		this.invMixMatrix[1][3] = new Polynomial(13);

		this.invMixMatrix[2][0] = new Polynomial(13);
		this.invMixMatrix[2][1] = new Polynomial(9);
		this.invMixMatrix[2][2] = new Polynomial(14);
		this.invMixMatrix[2][3] = new Polynomial(11);

		this.invMixMatrix[3][0] = new Polynomial(11);
		this.invMixMatrix[3][1] = new Polynomial(13);
		this.invMixMatrix[3][2] = new Polynomial(9);
		this.invMixMatrix[3][3] = new Polynomial(14);
	}

	private int[][] mixCols(int[][] nums) {
		for (int col = 0; col < 4; col++) {
			Polynomial row0 = new Polynomial(nums[0][col]);
			Polynomial row1 = new Polynomial(nums[1][col]);
			Polynomial row2 = new Polynomial(nums[2][col]);
			Polynomial row3 = new Polynomial(nums[3][col]);
			nums[0][col] = Polynomial.add(
					Polynomial.add(Polynomial.mul(row0, mixMatrix[0][0]), Polynomial.mul(row1, mixMatrix[0][1])),
					Polynomial.add(Polynomial.mul(row2, mixMatrix[0][2]), Polynomial.mul(row3, mixMatrix[0][3]))).get();
			nums[1][col] = Polynomial.add(
					Polynomial.add(Polynomial.mul(row0, mixMatrix[1][0]), Polynomial.mul(row1, mixMatrix[1][1])),
					Polynomial.add(Polynomial.mul(row2, mixMatrix[1][2]), Polynomial.mul(row3, mixMatrix[1][3]))).get();
			nums[2][col] = Polynomial.add(
					Polynomial.add(Polynomial.mul(row0, mixMatrix[2][0]), Polynomial.mul(row1, mixMatrix[2][1])),
					Polynomial.add(Polynomial.mul(row2, mixMatrix[2][2]), Polynomial.mul(row3, mixMatrix[2][3]))).get();
			nums[3][col] = Polynomial.add(
					Polynomial.add(Polynomial.mul(row0, mixMatrix[3][0]), Polynomial.mul(row1, mixMatrix[3][1])),
					Polynomial.add(Polynomial.mul(row2, mixMatrix[3][2]), Polynomial.mul(row3, mixMatrix[3][3]))).get();
		}
		return nums;
	}
	
	private int[][] invMixCols(int[][] nums){
		for (int col = 0; col < 4; col++) {
			Polynomial row0 = new Polynomial(nums[0][col]);
			Polynomial row1 = new Polynomial(nums[1][col]);
			Polynomial row2 = new Polynomial(nums[2][col]);
			Polynomial row3 = new Polynomial(nums[3][col]);
			nums[0][col] = Polynomial.add(
					Polynomial.add(Polynomial.mul(row0, invMixMatrix[0][0]), Polynomial.mul(row1, invMixMatrix[0][1])),
					Polynomial.add(Polynomial.mul(row2, invMixMatrix[0][2]), Polynomial.mul(row3, invMixMatrix[0][3]))).get();
			nums[1][col] = Polynomial.add(
					Polynomial.add(Polynomial.mul(row0, invMixMatrix[1][0]), Polynomial.mul(row1, invMixMatrix[1][1])),
					Polynomial.add(Polynomial.mul(row2, invMixMatrix[1][2]), Polynomial.mul(row3, invMixMatrix[1][3]))).get();
			nums[2][col] = Polynomial.add(
					Polynomial.add(Polynomial.mul(row0, invMixMatrix[2][0]), Polynomial.mul(row1, invMixMatrix[2][1])),
					Polynomial.add(Polynomial.mul(row2, invMixMatrix[2][2]), Polynomial.mul(row3, invMixMatrix[2][3]))).get();
			nums[3][col] = Polynomial.add(
					Polynomial.add(Polynomial.mul(row0, invMixMatrix[3][0]), Polynomial.mul(row1, invMixMatrix[3][1])),
					Polynomial.add(Polynomial.mul(row2, invMixMatrix[3][2]), Polynomial.mul(row3, invMixMatrix[3][3]))).get();
		}
		return nums;
	}

	private void initRoundConstants() {
		roundConstants[0] = new Polynomial(1);
		Polynomial constant = new Polynomial(2);
		for (int i = 1; i < 11; i++)
			roundConstants[i] = Polynomial.mul(roundConstants[i - 1], constant);
	}

	private void roundKeysGeneration() {
		for (int i = 0; i < key.length - 3; i += 4) {
			int[] w = new int[4];
			for (int j = 0; j < 4; j++) {
				w[j] = this.key[i + j];
			}
			this.keyList.add(w);
		}

		for (int i = 0; i < 40; i += 4) {
			int[] current0 = keyList.get(i);
			int[] current1 = keyList.get(i + 1);
			int[] current2 = keyList.get(i + 2);
			int[] current3 = keyList.get(i + 3);

			int[] gOut = gFunction(current3, i / 4);
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
		int[] re = w3.clone();
		for (int i = 0; i < re.length - 1; i++) {
			int temp = re[i];
			re[i] = re[(i + 1) % re.length];
			re[(i + 1) % re.length] = temp;
		}

		for (int i = 0; i < re.length; i++) {
			int row = (re[i] & (15 << 4)) >> 4;
			int col = re[i] & 15;
			re[i] = Sbox.getSbox(row, col);
		}
		re[0] = re[0] ^ roundConstants[j].get();
		return re;
	}

	private int[] xor(int[] first, int[] second) {
		if (first.length != second.length) {
			return null;
		} else {
			int[] re = new int[first.length];
			for (int i = 0; i < first.length; i++) {
				re[i] = (int) (first[i] ^ second[i]);
			}
			return re;
		}
	}

	private int[] parseHexString2Int(String hex) {
		int[] re = new int[hex.length() / 2];
		for (int i = 0; i < hex.length() - 1; i += 2) {
			int firstHalf = parseHexchar2Int(hex.charAt(i));
			int secondHalf = parseHexchar2Int(hex.charAt(i + 1));
			re[i / 2] = (firstHalf << 4) + secondHalf;

		}
		return re;
	}

	private int parseHexchar2Int(char c) {
		int charval = Character.getNumericValue(c);
		int zeroval = Character.getNumericValue('0');
		int a = Character.getNumericValue('a');

		if (charval - zeroval >= 0 && charval - zeroval <= 9)
			return charval - zeroval;

		return charval - a + 10;
	}

	private void parseInput(String input) {
		for (int i = 0; i < input.length() / 32; i++) {
			inputList.add(parseInputBlock(input.substring(i, i + 32)));
		}
	}

	private int[][] parseInputBlock(String block) {
		int[][] re = new int[4][4];
		for (int i = 0; i < block.length() - 1; i += 2) {
			int firstHalf = parseHexchar2Int(block.charAt(i));
			int secondHalf = parseHexchar2Int(block.charAt(i + 1));
			re[(i / 2) % 4][i / 8] = (firstHalf << 4) + secondHalf;
		}
		return re;
	}

	private String parseInts2String(int[][] nums) {
		StringBuilder sb = new StringBuilder();
		for (int col = 0; col < nums[0].length; col++) {
			for (int row = 0; row < nums.length; row++) {
				int firstHalf = (nums[row][col] & (15 << 4)) >> 4;
				int secondHalf = nums[row][col] & 15;
				sb.append(Integer.toHexString(firstHalf));
				sb.append(Integer.toHexString(secondHalf));
			}
		}
		return sb.toString();
	}

}
