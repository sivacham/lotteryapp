import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LotteryServer {

	final static int MIN_VALUE = 1; // Min Value. Can be made via user entry in future
	final static int MAX_VALUE = 50; // Max Value. Can be made via user entry in future
	static boolean newGameStart = false;

	public static void main(String[] args) throws IOException {
		DatagramSocket ds = new DatagramSocket(3000);
		System.out.println("Server Started and listening to the port " + 3000);
		List<Integer> winningNumbersList = getWinningCombinationInputs();
		lotteryInputValidator(ds, winningNumbersList);
	}
	
	public static void restartGame(DatagramSocket ds) throws IOException {
		if (newGameStart) {
			List<Integer> winningNumbersList = getWinningCombinationInputs();
			lotteryInputValidator(ds, winningNumbersList);
			newGameStart= false;
		}
	}
	
	public static List<Integer> getWinningCombinationInputs() {
		System.out.println("Please enter 6 winnning numbers between 1 and 50");
		int count = 1;
		List<Integer> winningNumbers = new ArrayList<>(6);
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Please enter number:" + count);
			int input = sc.nextInt();
			if (inRange(input, MIN_VALUE, MAX_VALUE)) {// Range check API called
				winningNumbers.add(input);
			} else {
				System.out.println("Please enter a valid winning value in the range 1 to 50");
				continue;
			}
			if (winningNumbers.size() == 6)
				break;
			count++;
		}
		System.out.println("Validating results");
		return winningNumbers;
	}
	
	public static void lotteryInputValidator(DatagramSocket ds, List<Integer> winningNumbers) throws IOException {
		String lotteryNumber = null;
		// Enter an infinite loop. Press Ctrl+C to terminate program.
		while (true) {
			byte[] buf = new byte[1024];
			DatagramPacket dp = new DatagramPacket(buf, 1024);
			ds.receive(dp);
			lotteryNumber = new String(dp.getData(), 0, dp.getLength());
			System.out.println(lotteryNumber);
			
			if("Y".equalsIgnoreCase(lotteryNumber) || "Yes".equalsIgnoreCase(lotteryNumber)) {
				newGameStart = true;
				restartGame(ds);
				continue;
			}
			
			String resultSummary = null;
			if (lotteryNumber != null)
				resultSummary = getResultSummary(lotteryNumber, winningNumbers);

			// send the response to the client
			buf = resultSummary.getBytes();
			InetAddress address = dp.getAddress();
			int port = dp.getPort();
			dp = new DatagramPacket(buf, buf.length, address, port);
			ds.send(dp);
		}

	}


	public static String getResultSummary(String lotteryNumber, List<Integer> winningNumbers) {

		if (lotteryNumber != null) {

			List<Integer> lottoValsfromClient = Stream.of(lotteryNumber.split(",")).map(String::trim)
					.map(Integer::parseInt).collect(Collectors.toList()); // Stream opertion to convert string to
																			// integer Lisst

			List<Integer> listCommon = winningNumbers.stream().filter(e -> lottoValsfromClient.contains(e))
					.collect(Collectors.toList()); // Stream operation to compare and find matching numbers

			StringBuilder sb = new StringBuilder();
			sb.append("The Winning Combination Numbers today were :" + winningNumbers.toString());
			sb.append("\n");
			sb.append("The Numbers entered via Lottery Were :" + lottoValsfromClient.toString());
			sb.append("\n");
			// No matches found . Better Luck message
			if (listCommon.size() == 0)
				sb.append("Better Luck next time! Please continue playing and enjoy the rewards.");
			sb.append("\n");
			// More than n matches found . Displaying Congratulations Message
			if (listCommon.size() == 6) {
				sb.append("Bingo!! Congratulations you have hit the jackpot of 50,000,000 PHP");
				sb.append("\n");
			}
			if (listCommon.size() == 5) {
				sb.append("Congratulations!! You are the lucky winner of 10,000 PHP");
				sb.append("\n");
			}
			if (listCommon.size() == 4) {
				sb.append("Congratulations!! You are the lucky winner of 50 PHP. Better Luck Next Time");
				sb.append("\n");
			}
			if (listCommon.size() == 3) {
				sb.append("Congratulations!! You are the lucky winner of 10 PHP. Better Luck Next Time");
				sb.append("\n");
			}
			return sb.toString();
		} else {
			return "Unable to validate results";
		}

	}

	// Util method to check if all entries are betweeen the range of Min and Max
	// values
	public static boolean inRange(int x, int min, int max) {
		return ((x - min) * (x - max) <= 0);
	}

}
