import java.math.BigInteger;
import java.util.Random;

public class MillerRabin {
	
	private int iteration;
	private static final BigInteger ZERO = BigInteger.ZERO;
	private static final BigInteger ONE = BigInteger.ONE;
	private static final BigInteger TWO = new BigInteger("2");
	private static final BigInteger THREE = new BigInteger("3");
	
	public MillerRabin(int iteration) {
		this.iteration = iteration;
	}
	
	public boolean isPrime(BigInteger n) {
		if (n.compareTo(ONE) == 0)
			return false;
		if (n.compareTo(THREE) < 0)
			return true;
		int s = 0;
		BigInteger d = n.subtract(ONE);
		while (d.mod(TWO).equals(ZERO)) {
			s++;
			d = d.divide(TWO);
		}
		for (int i = 0; i < this.iteration; i++) {
			BigInteger a = uniformRandom(TWO, n.subtract(ONE));
			BigInteger x = a.modPow(d, n);
			if (x.equals(ONE) || x.equals(n.subtract(ONE)))
				continue;
			int r = 0;
			for (; r < s; r++) {
				x = x.modPow(TWO, n);
				if (x.equals(ONE))
					return false;
				if (x.equals(n.subtract(ONE)))
					break;
			}
			if (r == s) 
				return false;
		}
		return true;
	}
	
	private BigInteger uniformRandom(BigInteger bottom, BigInteger top) {
		Random rnd = new Random();
		BigInteger res;
		do {
			res = new BigInteger(top.bitLength(), rnd);
		} while (res.compareTo(bottom) < 0 || res.compareTo(top) > 0);
		return res;
	}

}
