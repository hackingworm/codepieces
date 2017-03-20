package test;

import sun.misc.Unsafe;
import java.lang.reflect.Field;

public class ArrayLayout {
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

	static String layout(Object[] OA) {
		String str = "[" + OA.length + "] " + Long.toString(addressOf(OA)) + "\n";
		for (int i = 0; i < OA.length; ++i) {
			str += "[" + i + "] " + Long.toString(addressOf(OA[i])) + "\n";
		}
		return str;
	}

	static ArrayLayout[] createArrary (int size) {
		ArrayLayout[] OA = new ArrayLayout[size];
		for (int i = 0; i < size; ++i) {
			OA[i] = new ArrayLayout();
		}
		return OA;
	}

	public static void main(String[] args) {
		try {
			ArrayLayout[] OA1 = createArrary(3), OA2 = createArrary(5);

			unsafe = getUnsafeInstance();

			System.out.println("\n" + layout(OA1) + "\n" + layout(OA2));
			System.gc();
			System.out.println("\n" + layout(OA1) + "\n" + layout(OA2));
		} catch (NoSuchFieldException | IllegalAccessException ex) {
			System.err.println(ex);
			System.exit(-1);
		}
	}
}
