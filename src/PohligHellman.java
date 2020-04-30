import java.math.BigInteger;
import java.util.ArrayList;

public class PohligHellman extends Converter {

	private BigInteger blockSize;
	private BigInteger prime;
	private BigInteger key;
	private BigInteger dKey;
	private BigInteger lowerBound;
	private ArrayList<BigInteger> inputList;
	private MillerRabin mr;

	public PohligHellman(String input, String key, String size) {
		super(input);
		parseSize(size);
		try {
			this.key = new BigInteger(key);
		} catch (ArithmeticException e) {
			e.printStackTrace();
		}
		BigInteger thirty = new BigInteger("30");
		BigInteger hundred = new BigInteger("100");
		this.lowerBound = thirty.multiply(hundred.pow(blockSize.intValue() - 1));
		this.mr = new MillerRabin(30);
		generateKeys();

	}

	public String encrypt() {
		parseInput(parseStringToNumbers(input.toLowerCase()));
		StringBuilder sb = new StringBuilder();
		for (BigInteger num : this.inputList) {
			BigInteger re = num.modPow(key, prime);
			sb.append(re.toString());
			sb.append(",");
		}

		return sb.toString().substring(0, sb.length() - 1);
	}

	public String decrypt() {
		parseDecrypt();
		StringBuilder sb = new StringBuilder();
		for (BigInteger num : this.inputList) {
			BigInteger re = num.modPow(dKey, prime);
			sb.append(parseNumberToString(re));
		}
		return sb.toString();
	}

	private void parseInput(String input) {
		this.inputList = new ArrayList<BigInteger>();
		int blockLength = this.blockSize.intValue() * 2;
		for (int i = 0; i < input.length() - blockLength + 1; i += blockLength) {
			try {
				BigInteger block;
				if (i + blockLength > input.length())
					block = new BigInteger(input.substring(i));
				else
					block = new BigInteger(input.substring(i, i + blockLength));
				this.inputList.add(block);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void parseDecrypt() {
		this.inputList = new ArrayList<BigInteger>();
		String[] arr = input.split(",");
		for(String a : arr) {
			try {
				inputList.add(new BigInteger(a.trim()));
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void parseSize(String size) {
		try {
			blockSize = new BigInteger(size);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateKeys() {
		this.prime = lowerBound.abs();
		while (true) {
			if (mr.isPrime(prime))
				break;
			else
				prime = prime.add(BigInteger.ONE);
		}
		this.dKey = this.key.modInverse(prime.subtract(BigInteger.ONE));
	}

	private String parseStringToNumbers(String str) {
		// Assuming str in lowercase
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			int re = str.charAt(i) - 97;
			if (re < 10) {
				sb.append("0" + re);
			} else {
				sb.append(re);
			}
		}

		return sb.toString();
	}
	
	private String parseNumberToString(BigInteger num) {
		StringBuilder sb = new StringBuilder();
			int numValue = num.intValue();
			StringBuilder sb2 = new StringBuilder();
			for(int i = 0; i < this.blockSize.intValue(); i++) {
				sb2.append((char) (numValue%100 + 97));
				numValue/=100;
			}
			sb.append(sb2.reverse().toString());
		return sb.toString();
	}

}
