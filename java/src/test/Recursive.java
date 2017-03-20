package test;

public class Recursive {
	final static int MAXLEVEL = 3;
	int level;
	Recursive son, daughter;

	Recursive(int level) {
		this.level = level;
		if (MAXLEVEL > level) {
			son = new Recursive(level + 1);
			daughter = new Recursive(level + 1);
		}
	}

	public String toString() {
		String str = "";

		for (int i = 0; i < level; ++i) {
			str += ' ';
		}
		str += Integer.toString(level);

		if (MAXLEVEL > level) {
			str += '\n';
			str += son.toString();
			str += '\n';
			str += daughter.toString();
		}

		return str;
	}

	public static void main(String[] args) {
		Recursive son = new Recursive(0), daughter = new Recursive(0);
		System.gc();
		System.out.println(son + "\n" + daughter);
	}
}
