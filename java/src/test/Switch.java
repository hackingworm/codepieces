package test;

public class Switch {
	static final int i = 0x80000000;
	static final int j =0xc0000000;
	static final int k = 0x40000000;

	public static void main(String[] args) {
		System.out.println(i + " " + (i >> 1) + "/" + j + " " + (i >>> 1) + "/" + k);

		for (int i = 0; i < args.length; ++i) {
			String toWhom;

			switch(args[i]) {
				case "day":
					toWhom = "Sun";
					break;
				case "night":
					toWhom = "Moon";
					break;
				default:
					toWhom = "Hell";
			}

			System.out.println("Hello, " + toWhom + "!");
		}
	}
}
