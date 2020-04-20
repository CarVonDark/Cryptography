/*
 * Get idea the mul form github 
 */

public class Polynomial {
	
	private int polynomial;
	
	private static final int mod = 283;

	public Polynomial(int a) {
		this.polynomial = a;
	}
	public int get() {
		return this.polynomial;
	}

	public void set(int a) {
		this.polynomial = a;
	}
	

	public static Polynomial add(Polynomial p1, Polynomial p2) {
		int a = p1.get();
		int b = p2.get();
		int c = Polynomial.modulus(a ^ b);
		return new Polynomial(c);
	}
	
	public static Polynomial mul(Polynomial p1, Polynomial p2) {
		int product=0;

		int multiplier=p2.get();
		int multiplicand=p1.get();

		while(multiplier>0){
			if(multiplier%2==1){
				product=Polynomial.modulus(product^multiplicand);
			}

			multiplier=multiplier>>1;
			multiplicand=multiplicand<<1;
			multiplicand=Polynomial.modulus(multiplicand);
		}

		return new Polynomial(product);
	}
	
	static int modulus(int m) {
		if (m < 256)
			return m;

		m = m ^ Polynomial.mod;
		return m;
	}

}