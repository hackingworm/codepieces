package test;

public class FoolAdder extends Thread {
	static final String VEROPT = "-verbose";
	static Object lock = new Object();
	static long addend1;
	static long addend2;
	boolean verbose;
	long used = 0;

	FoolAdder(boolean verbose) {
		this.verbose = verbose;
	}

	long used() {
		return used;
	}

	public void run () {
		for (;;) {
			long value;
			synchronized(lock) {
				if (0 < addend2) {
					++addend1;
					--addend2;
				} else if (0 > addend2) {
					--addend1;
					++addend2;
				} else {
					break;
				}

				value = addend1;
			}

			++used;
			if (verbose) {
				System.out.println(getId() + " " + value);
			}
		}
	}

	public static void main(String[] args) {
		boolean verbose = false;
		int start = 0;

		if (0 < args.length && VEROPT.equals(args[start])) {
			verbose = true;
			++start;
		}
			
		if (start + 3 > args.length) {
			System.err.println("Usuage: java FoolAdder [-verbose] \"number of adders\" \"addend1\" \"addend2\"");
			System.exit(-1);
		}

		try {
			long operand1 = Long.parseLong(args[start + 1]);
			addend1 = operand1;
			long operand2 = Long.parseLong(args[start + 2]);
			addend2 = operand2;

			int adderNum = Integer.parseInt(args[start]);
			if (1 > adderNum) {
				System.err.println("Need 1 adder at least");
				System.exit(-1);
			}

			FoolAdder[] adders = new FoolAdder[adderNum];

			for (int i = 0; i < adderNum; ++i) {
				adders[i] = new FoolAdder(verbose);
			}

			long startTime = System.currentTimeMillis();

			for (int i = 0; i < adderNum; ++i) {
				adders[i].start();
			}

			for (int i = 0; i < adderNum; ++i) {
				adders[i].join();
			}

			System.err.println("Time elapsed " + (System.currentTimeMillis() - startTime));

			for (int i = 0; i < adderNum; ++i) {
				System.err.println("Adder " + adders[i].getId() + " is used " + adders[i].used() + " times");
			}

			System.err.println(operand1 + " + " + operand2 + " = " + addend1);
		} catch (NumberFormatException | IllegalThreadStateException | InterruptedException ex) {
			System.err.println(ex);
		}
	}
}
