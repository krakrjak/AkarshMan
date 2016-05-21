package main;

public class Main {

	public static void main(String[] args) {
		// printMem();
		Application.start();
	}

	public static void printMem() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					System.out.println("KB: "
							+ (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

}
