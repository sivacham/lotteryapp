import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Scanner;

public class LotteryClient {

	final static int MIN_VALUE = 1; // Min Value. Can be made via user entry in future
	final static int MAX_VALUE = 50; // Max Value. Can be made via user entry in future

	// Main method
	public static void main(String[] args) {
		playGame();
	}
	
	public static void playGame() {
		String lottoVals = getUserInputs();
		lotteryInputSender(lottoVals);
	}
	
	public static String getUserInputs() {
		System.out.println("Please enter 6 numbers between 1 and 50");
		int count = 1;
		int i = 0;
		int arr[] = new int[6];
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Please enter number:" + count);
			int input = sc.nextInt();
			if (inRange(input, MIN_VALUE, MAX_VALUE)) {// Range check API called
				arr[i] = input;
			} else {
				System.out.println("Please enter a valid value in the range 1 to 50");
				continue;
			}
			if (count == 6)
				break;
			i++;
			count++;
		}
		String lottoVals = (Arrays.toString(arr).replaceAll("\\[|\\]|\\s", ""));
		return lottoVals;
	}

	// The client that invokes the Lotter Server and send User inut values
	// Also receives the resultsummmary from the server
	public static void lotteryInputSender(String inputValues) {
		boolean stopgame= false;
		byte[] buf = new byte[1024];
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket();
			InetAddress addr = InetAddress.getByName("127.0.0.1");// Localhost
			System.out.println("Sending values:" + inputValues);
			DatagramPacket dp = new DatagramPacket(inputValues.getBytes(), inputValues.length(), addr, 3000);
			ds.send(dp);

			// get response
			dp = new DatagramPacket(buf, buf.length);
			ds.receive(dp);

			// display response
			String received = new String(dp.getData(), 0, dp.getLength());
			System.out.println("Result Summary: " + received);
			// Write to File the results
			try (PrintWriter out = new PrintWriter("LotteryResults.txt")) {
				out.println(received);
			}
			System.out.println("Do you want to play Again!!Press [Y/n]");
			Scanner sc = new Scanner(System.in);
			String input = sc.nextLine();
			if("Y".equalsIgnoreCase(input) || "Yes".equalsIgnoreCase(input)) {
				dp = new DatagramPacket(input.getBytes(), input.length(), addr, 3000);
				ds.send(dp);
				playGame();
			}else {
				stopgame = true;
			}
			
		} catch (IOException e) {
			System.out.println("Unexpected Exception while playing lottery:" + e.getMessage());
			e.printStackTrace();
		} finally {
			if (stopgame) {
				ds.close();
			}
		}
	}

	// Util method to check if all entries are betweeen the range of Min and Max
	// values
	public static boolean inRange(int x, int min, int max) {
		return ((x - min) * (x - max) <= 0);
	}

}
