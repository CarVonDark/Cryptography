import java.util.ArrayList;
import java.util.HashMap;

public class Baby_DES extends Converter {

	private ArrayList<byte[]> keySet;
	private ArrayList<byte[]> blocks;
	private HashMap<Integer, byte[]> s1;
	private HashMap<Integer, byte[]> s2;

	public Baby_DES(String input, String key) {
		super(input);
		makeKeySet(key);
		makeBlocks(input);
		makeS1();
		makeS2();
	}

	public String encrypt() {
		System.out.println("Baby-DES");
		StringBuilder sb = new StringBuilder();
		for (byte[] bin : blocks) {
			ArrayList<byte[]> lr = makeLR(bin);
			byte[] l = lr.get(0);
			byte[] r = lr.get(1);
			for (byte[] subkey : keySet) {
				byte[] temp = r;
				r = xor(l, function(r, subkey));
				l = temp;
				for(byte b : l) {
					System.out.print(b);
				}
				for(byte b:r) {
					System.out.print(b);
				}
				System.out.println();
			}
			for (byte b : r) {
				sb.append(b);
			}
			for (byte b : l) {
				sb.append(b);
			}
		}
		return sb.toString();
	}
	
	public String decrypt() {
		System.out.println("Baby-DES");
		StringBuilder sb = new StringBuilder();
		for (byte[] bin : blocks) {
			ArrayList<byte[]> lr = makeLR(bin);
			byte[] l = lr.get(0);
			byte[] r = lr.get(1);
			for (int i = 1; i >= 0; i--) {
				byte[] temp = r;
				r = xor(l, function(r, keySet.get(i)));
				l = temp;
				for(byte b : l) {
					System.out.print(b);
				}
				for(byte b:r) {
					System.out.print(b);
				}
				System.out.println();
			}
			for (byte b : r) {
				sb.append(b);
			}
			for (byte b : l) {
				sb.append(b);
			}
		}
		return sb.toString();
	}

	private byte[] str2bin(String key2) {
		ArrayList<Byte> re = new ArrayList<Byte>();
		for (int i = 0; i < key2.length(); i++) {
			if (key2.charAt(i) == '1' || key2.charAt(i) == '0') {
				re.add(Byte.parseByte(key2.substring(i, i + 1)));
			}

		}
		byte[] result = new byte[re.size()];
		for (int i = 0; i < re.size(); i++) {
			result[i] = re.get(i);
		}
		System.out.println(re.toString());
		return result;
	}

	private void makeKeySet(String inputKey) {
		keySet = new ArrayList<byte[]>();
		if (inputKey.length() != 9) {

			for (int i = 0; i < 2; i++) {
				keySet.add(new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 });
			}

		} else {
			byte[] key = str2bin(inputKey);
			for (int i = 0; i < 2; i++) {
				byte[] subkey = new byte[8];
				for (int j = i; j < i + 8; j++) {
					subkey[j - i] = key[j % 9];
				}
				keySet.add(subkey);
			}
		}
	}

	private void makeBlocks(String input) {
		blocks = new ArrayList<byte[]>();
		byte[] plainText = str2bin(input);
		for (int i = 0; i < plainText.length; i += 12) {
			byte[] block = new byte[12];
			for (int j = 0; j < 12; j++) {
				if (i + j > plainText.length) {
					block[j] = 0;
				} else {
					block[j] = plainText[i + j];
				}
			}
			blocks.add(block);
		}
	}

	private void makeS1() {
		s1 = new HashMap<Integer, byte[]>();
		s1.put(0, new byte[] { 1, 0, 1 });
		s1.put(1, new byte[] { 0, 1, 0 });
		s1.put(2, new byte[] { 0, 0, 1 });
		s1.put(3, new byte[] { 1, 1, 0 });
		
		s1.put(4, new byte[] { 0, 1, 1 });
		s1.put(5, new byte[] { 1, 0, 0 });
		s1.put(6, new byte[] { 1, 1, 1 });
		s1.put(7, new byte[] { 0, 0, 0 });

		s1.put(8, new byte[] { 0, 0, 1 });
		s1.put(9, new byte[] { 1, 0, 0 });
		s1.put(10, new byte[] { 1, 1, 0 });
		s1.put(11, new byte[] { 0, 1, 0 });
		
		s1.put(12, new byte[] { 0, 0, 0 });
		s1.put(13, new byte[] { 1, 1, 1 });
		s1.put(14, new byte[] { 1, 0, 1 });
		s1.put(15, new byte[] { 0, 1, 1 });
	}

	private void makeS2() {
		s2 = new HashMap<Integer, byte[]>();
		s2.put(0, new byte[] { 1, 0, 0 });
		s2.put(1, new byte[] { 0, 0, 0 });
		s2.put(2, new byte[] { 1, 1, 0 });
		s2.put(3, new byte[] { 1, 0, 1 });
		
		s2.put(4, new byte[] { 1, 1, 1 });
		s2.put(5, new byte[] { 0, 0, 1 });
		s2.put(6, new byte[] { 0, 1, 1 });
		s2.put(7, new byte[] { 0, 1, 0 });

		s2.put(8, new byte[] { 1, 0, 1 });
		s2.put(9, new byte[] { 0, 1, 1 });
		s2.put(10, new byte[] { 0, 0, 0 });
		s2.put(11, new byte[] { 1, 1, 1 });
		
		s2.put(12, new byte[] { 1, 1, 0 });
		s2.put(13, new byte[] { 0, 1, 0 });
		s2.put(14, new byte[] { 0, 0, 1 });
		s2.put(15, new byte[] { 1, 0, 0 });
	}

	private byte[] function(byte[] in, byte[] subkey) {

		ArrayList<byte[]> xorAns = xor1(expander(in), subkey);
		byte[] re = new byte[6];
		byte[] s1Ans = s1.get(getValueByBytes(xorAns.get(0)));
		byte[] s2Ans = s2.get(getValueByBytes(xorAns.get(1)));
		for (int i = 0; i < 6; i++) {
			if (i < 3) {
				re[i] = s1Ans[i];
			} else {
				re[i] = s2Ans[i % 3];
			}
		}
		return re;
	}

	private byte[] expander(byte[] in) {
		byte[] expand = new byte[8];
		expand[0] = in[0];
		expand[1] = in[1];
		expand[2] = in[3];
		expand[3] = in[2];
		expand[4] = in[3];
		expand[5] = in[2];
		expand[6] = in[4];
		expand[7] = in[5];
		return expand;
	}

	private ArrayList<byte[]> xor1(byte[] inFirst, byte[] inSecond) {
		ArrayList<byte[]> re = new ArrayList<byte[]>();
		for (int i = 0; i < 2; i++) {
			byte[] answer = new byte[4];
			for (int j = i * 4; j < 4 + i * 4; j++) {
				answer[j % 4] = (byte) (inFirst[j] ^ inSecond[j]);
			}
			re.add(answer);
		}
		return re;
	}

	private byte[] xor(byte[] inFirst, byte[] inSecond) {
		byte[] re = new byte[inFirst.length];
		for (int i = 0; i < inFirst.length; i++) {
			re[i] = (byte) (inFirst[i] ^ inSecond[i]);
		}
		return re;
	}

	private ArrayList<byte[]> makeLR(byte[] in) {
		int length = in.length;
		ArrayList<byte[]> re = new ArrayList<byte[]>();
		for (int i = 0; i < 2; i++) {
			byte[] half = new byte[length / 2];
			for (int j = i * length / 2; j < (1 + i) * length / 2; j++) {
				half[j - i * length / 2] = in[j];
			}
			re.add(half);
		}
		return re;
	}

	private int getValueByBytes(byte[] in) {
		int sum = 0;
		int power = 0;
		for (int i = in.length - 1; i >= 0; i--) {
			sum += in[i] * Math.pow(2, power);
			power++;
		}
		return sum;
	}

}
