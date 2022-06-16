
import java.math.BigInteger;

public interface Execute {
	void apply(BigInteger total, BigInteger checked, Point[] pts, int[] best);
}