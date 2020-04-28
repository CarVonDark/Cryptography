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
		parseInput(input);
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
		int blockLength = this.blockSize.intValue();
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
			
		}
		
	}

}
