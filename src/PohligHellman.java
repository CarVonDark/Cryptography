import java.math.BigInteger;
import java.util.ArrayList;

public class PohligHellman extends Converter{

	private BigInteger blockSize;
	private BigInteger prime;
	private int key;
	private BigInteger lowerBound;
	private ArrayList<BigInteger> inputList;
	private MillerRabin mr;
	
	public PohligHellman(String input, String key, String size) {
		super(input);
		parseSize(size);
		parseInput(parseStringToNumbers(input.toLowerCase()));
		try {
			this.key = Integer.parseInt(key);
		}catch(ArithmeticException e) {
			e.printStackTrace();
		}
		BigInteger thirty = new BigInteger("30");
		BigInteger hundred = new BigInteger("100");
		this.lowerBound = thirty.multiply(hundred.pow(blockSize.intValue()-1));
		this.mr = new MillerRabin(30);
		generateKeys();
	}

	private void parseInput(String input) {
		this.inputList = new ArrayList<BigInteger>();
		int blockLength = this.blockSize.intValue()*2;
		for(int i = 0; i < input.length()-blockLength; i+=blockLength) {
			try {
				BigInteger block = new BigInteger(input.substring(i, i + blockLength));
				this.inputList.add(block);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void parseSize(String size) {
		try {
			blockSize = new BigInteger(size);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateKeys() {
		this.prime = lowerBound.abs();
		while(true) {
			if(mr.isPrime(prime))
				break;
			else 
				prime.add(BigInteger.ONE);
		}
		
	}
	
	private String parseStringToNumbers(String str) {
		//Assuming str in lowercase
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < str.length(); i++) {
			int re = str.charAt(i) - 97;
			if(re < 10) {
				sb.append("0" + re);
			} else {
				sb.append(re);
			}
		}
		
		return sb.toString();
	}
	
	private String parseNumbersToString(ArrayList<BigInteger> ls) {
		StringBuilder sb = new StringBuilder();
		int length = this.blockSize.intValue();
		for(BigInteger num : ls) {
			int numValue = num.intValue();
			StringBuilder sb2 = new StringBuilder();
			for(int i = 0; i < length; i++) {
				sb2.append(numValue%100);
				numValue/=100;
			}
			sb.append(sb2.reverse().toString());
		}
		
		return sb.toString();
	}

}
