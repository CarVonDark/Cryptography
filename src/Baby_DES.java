import java.math.BigInteger;
import java.nio.ByteBuffer;

public class Baby_DES extends Converter {
	
	private byte[] key;
	
	public Baby_DES(String input, String key) {
		super(input);
		this.key = str2bin(key);
	}

	private byte[] str2bin(String key2) {
		byte[] result = new byte[key2.length()];
		for(int i = 0; i < key2.length(); i++) {
			result[i] = Byte.parseByte(key2.substring(i,i+1));
		}
		return result;
	}
	
	

}
