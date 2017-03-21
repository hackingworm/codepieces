package test;

public class TestIntrinsics {
	private static final int MAX = (1 << 14);

	private static Thread curThrd () {
		return Thread.currentThread();
	}

	private static double log(double d) {
		return Math.log(d);
	}

	public static void main(String[] args) {
		while (true) {
			curThrd();
		}

		/*
		for (int i = 1; i < MAX; ++i) {
			log(i);
		}
		*/
	}
}
