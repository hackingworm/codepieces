package test;

import sun.misc.Unsafe;
import java.lang.reflect.Field;

public class MyObject {
	static Unsafe unsafe;

	static Unsafe getUnsafeInstance() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
		theUnsafeInstance.setAccessible(true);
		return (Unsafe)theUnsafeInstance.get(Unsafe.class);
	}

	static long addressOf(Object O) {
		Object[] OA = new Object[]{O};
		long baseOffset = unsafe.arrayBaseOffset(Object[].class);
		long address = 0;
		switch(unsafe.addressSize()) {
		case 4:
			address = unsafe.getInt(OA, baseOffset);
			break;
		case 8:
			address = unsafe.getLong(OA, baseOffset);
			break;
		}

		return address;
	}

	final static String TS = "  ";
	final static int MAXLEVEL = 2;
	int level;
	MyObject son, daughter;

	MyObject(int level) {
		this.level = level;
		if (MAXLEVEL > level) {
			son = new MyObject(level + 1);
			daughter = new MyObject(level + 1);
		}
	}

	static MyObject[] createArray(int size) {
		MyObject[] OA = new MyObject[size];
		for (int i = 0; i < size; ++i) {
			OA[i] = new MyObject(0);
		}
		return OA;
	}

	public String toString() {
		String str = Integer.toString(level) + " ";

		for (int i = 0; i < level; ++i) {
			str += TS;
		}
		str += Long.toString(addressOf(this));

		if (MAXLEVEL > level) {
			str += "\n" + son + "\n" + daughter;
		}

		return str;
	}

	static String layout(MyObject[] OA) {
		String str = "[" + OA.length + "] "
				+ Long.toString(addressOf(OA)) + "\n";
		for (int i = 0; i < OA.length; ++i) {
			str += "[" + i + "]\n" + OA[i] + "\n";
		}
		return str;
	}

	public static void main(String[] args) {
		try {
			MyObject[] OA1 = createArray(2), OA2 = createArray(3);
			MyObject son = new MyObject(0),
				 daughter = new MyObject(0);

			unsafe = getUnsafeInstance();

			System.out.println("Before GC\n\n"
				+ son + "\n\n"
				+ daughter + "\n\n"
				+ layout(OA1) + "\n"
				+ layout(OA2));

			System.gc();

			System.out.println("\nAfter GC\n\n"
				+ son + "\n\n"
				+ daughter + "\n\n"
				+ layout(OA1) + "\n"
				+ layout(OA2));
		} catch (NoSuchFieldException | IllegalAccessException ex) {
			System.err.println(ex);
			System.exit(-1);
		}
	}
}
