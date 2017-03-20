package test;

import java.util.Random;

enum Status {
	IDLE, READ, WRITE
}

class Sync {
	Status status;

	Sync() {
		status = Status.IDLE;
	}
}

public class ParallelBench extends Thread {
	static volatile boolean stop;
	static Random random;
	static Sync sync;
	static long[] array;

	enum Role {
		READER, WRITER
	}

	Role role;
	long ops;

	ParallelBench(Role role) {
		this.role = role;
	}

	long getOps() {
		return ops;
	}

	void read() {
		Status status;
		synchronized(sync) {
			while (Status.WRITE == sync.status) {
				try {
					sync.wait();
				} catch (InterruptedException ie) {
					System.err.println(ie);
				}
			}

			status = sync.status;
			sync.status = Status.READ;
		}

		++ops;
		int index = random.nextInt(array.length);
		long value = array[index];
		/*
		System.out.println("Reader " + getId() + ", " + ops
					+ ", " + array.length + "/" + index
					+ " value = " + value);
		*/

		if (Status.IDLE == status) {
			synchronized(sync) {
				sync.status = Status.IDLE;
				sync.notifyAll();
			}
		}
	}

	void write() {
		synchronized(sync) {
			while (Status.IDLE != sync.status) {
				try {
					sync.wait();
				} catch (InterruptedException ie) {
					System.err.println(ie);
				}
			}

			sync.status = Status.WRITE;
		}

		++ops;
		int index = random.nextInt(array.length);
		array[index] = ops;
		/*
		System.out.println("Writeer " + getId() + ", " + ops
					+ ", " + array.length + "/" + index
					+ " array[" + index + "] = " + array[index]);
		*/

		synchronized (sync) {
			sync.status = Status.IDLE;
			sync.notifyAll();
		}
	}

	public void run() {
		while (!stop) {
			if (Role.READER == role) {
				read();
			} else {
				write();
			}
		}
	}

	public static void main(String[] args) {
		long runTime = 0;
		int numberOfThreads = 0, logArraySize = 0;

		try {
			if (0 < args.length) {
				runTime = Integer.parseInt(args[0]);
				if (1 < args.length) {
					numberOfThreads = Integer.parseInt(args[1]);
					if (2 < args.length) {
						logArraySize = Integer.parseInt(args[2]);
					}
				}
			}
		} catch (NumberFormatException nfe) {
			System.err.println(nfe);
		}

		if (0 > runTime) {
			runTime = 0;
		}

		if (0 >= numberOfThreads) {
			numberOfThreads = Runtime.getRuntime().availableProcessors();
		}

		if (0 > logArraySize) {
			logArraySize = 0;
		} else if (30 < logArraySize) {
			logArraySize = 30;
		}

		for (;;) {
			try {
				array = new long[1 << logArraySize];
				break;
			} catch (OutOfMemoryError oome) {
				System.err.println(oome);
				--logArraySize;
			}
		}
		System.out.println(logArraySize);

		random = new Random();
		sync = new Sync();

		ParallelBench[] readers = new ParallelBench[numberOfThreads],
						writers = new ParallelBench[numberOfThreads];
		for (int i = 0; i < numberOfThreads; ++i) {
			readers[i] = new ParallelBench(Role.READER);
			writers[i] = new ParallelBench(Role.WRITER);
		}

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < numberOfThreads; ++i) {
			readers[i].start();
			writers[i].start();
		}

		try {
			Thread.sleep(runTime);
		} catch (InterruptedException ie) {
			System.err.println(ie);
		}

		stop = true;

		long stopTime = System.currentTimeMillis();
		for (int i = 0; i < numberOfThreads; ++i) {
			try {
				readers[i].join();
				writers[i].join();
			} catch (InterruptedException ie) {
				System.err.println(ie);
			}
		}

		long readOps = 0, writeOps = 0;
		for (int i = 0; i < numberOfThreads; ++i) {
			long ops = readers[i].getOps();
			System.out.println("Reader " + readers[i].getId() + " " + ops);
			readOps += ops;
		}

		for (int i = 0; i < numberOfThreads; ++i) {
			long ops = writers[i].getOps();
			System.out.println("Writer " + writers[i].getId() + " " + ops);
			writeOps += ops;
		}

		long realRunTime = stopTime - startTime;
		System.out.println("Run time " + runTime
					+ "ms\nReal run time " + realRunTime
					+ "ms\nThreads " + numberOfThreads + "/" + numberOfThreads
					+ "\nArray size " + (1 << logArraySize) 
					+ "\nRead operations " + readOps
					+ "\nRead Operatons per ms " + readOps / realRunTime
					+ "\nRead Operations per ms per thread "
						+ readOps / realRunTime / numberOfThreads
					+ "\nWrite operations " + writeOps
					+ "\nWrite operatons per ms " + writeOps / realRunTime
					+ "\nWrite operations per ms per thread "
						+ writeOps / realRunTime / numberOfThreads);
	}
}
