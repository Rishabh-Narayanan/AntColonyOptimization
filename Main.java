import java.util.Random;

public class Main {
	public static void main(String[] args) {
		String[] a = new String[args.length + 1];
		for (int i = 0; i < args.length; i++) {
			a[i + 1] = args[i];
		}
		a[0] = "-s=" + new Random().nextInt(Integer.MAX_VALUE);

		boolean runNaive = false, runAco = false;
		for (String s : args) {
			if (s.startsWith("--naive")) {
				runNaive = true;
			}
			if (s.startsWith("--aco")) {
				runAco = true;
			}
		}
		if (runNaive) NaiveSolution.main(a);
		if (runAco) AcoSolution.main(a);
	}
}
