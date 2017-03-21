package test;

public class Incer {
	static int staticInc(int v) {
		return ++v;
	}

	int dec(int v) {
		return --v;
	}

	public static void main(String[] args) {
		if (1 > args.length) {
			System.err.println("Usuage: java Incer \"stop value\"");
			System.exit(-1);
		}

		int stop;

		try {
			stop = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			System.err.println(ex);
			return;
		}

		int value = 0;
		while (stop > value) {
			value = staticInc(value);
		}
		System.out.println(value);

		Incer incer = new Incer();
		while (0 <  value) {
			value = incer.dec(value);
		}
		System.out.println(value);
	}
}
