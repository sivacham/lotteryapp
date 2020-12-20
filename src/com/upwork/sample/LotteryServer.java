package com.upwork.sample;

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

	public static void main(String[] args) {

		boolean rcvMsgFlag = true;
		System.out.println("Please enter 6 winnning numbers between 1 and 50");
		int arr[] = new int[6];
		List<Integer> winningNumbers = new ArrayList<>(arr.length);
		Scanner sc = new Scanner(System.in);
		for (int i = 0; i < arr.length; i++) {
			System.out.println("Please enter number:" + (i + 1));
			int input = sc.nextInt();
			if (LotteryUtils.inRange(input, LotteryUtils.MIN_VALUE, LotteryUtils.MAX_VALUE)) {// Range check API called
				winningNumbers.add(input);
				continue;
			} else {
				System.out.println("Please enter a valid value in the range 1 to 50");
				rcvMsgFlag = false;
				break;
			}
		}
		
		if (winningNumbers.size() != 6) {// Check if 6 inputs entered
			System.out.println("Please enter 6 numbers in-between 1 to 50");
			rcvMsgFlag = false;
		}

		if (!rcvMsgFlag) {// Check if all are valid inputs and create a winning combination List
			System.out.println("Unable to validate lottery due to bad input");
		}

		lotteryInputReceiver(winningNumbers);
		sc.close();
	}

	public static void lotteryInputReceiver(List<Integer> winningNumbers) {
		String lotteryNumber = null;
		try (DatagramSocket ds = new DatagramSocket(3000);) {
			byte[] buf = new byte[1024];
			DatagramPacket dp = new DatagramPacket(buf, 1024);
			ds.receive(dp);
			lotteryNumber = new String(dp.getData(), 0, dp.getLength());
			System.out.println(lotteryNumber);
			String resultSummary = null;
			if(lotteryNumber!=null)
				resultSummary = getResultSummary(lotteryNumber,winningNumbers);
			
			// send the response to the client
			buf = resultSummary.getBytes();
            InetAddress address = dp.getAddress();
            int port = dp.getPort();
            dp = new DatagramPacket(buf, buf.length, address, port);
            ds.send(dp);
			
			ds.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getResultSummary(String lotteryNumber,List<Integer> winningNumbers) {
		
		if (lotteryNumber != null) {

			List<Integer> lottoValsfromClient = Stream.of(lotteryNumber.split(",")).map(String::trim)
					.map(Integer::parseInt).collect(Collectors.toList());

			List<Integer> listCommon = winningNumbers.stream().filter(e -> lottoValsfromClient.contains(e))
					.collect(Collectors.toList());

			StringBuilder sb = new StringBuilder();
			sb.append("The Winning Combination Numbers today were :" + winningNumbers.toString());
			sb.append("\n");
			sb.append("The Numbers entered via Lottery Were :" + lottoValsfromClient.toString());
			sb.append("\n");
			//No matches found . Better Luck message
			if (listCommon.size() == 0)
				sb.append("Better Luck next time! Please continue playing and enjoy the rewards.");
			sb.append("\n");
			//matches found . Displaying matched numbers
			if (listCommon.size() > 0)
				sb.append("The Lucky Numbers matched were :" + listCommon.toString());
			sb.append("\n");
			//More than 3 matches found . Displaying Congratulations Message
			if (listCommon.size() > 3)
				sb.append("Congratulations you have more than 3 numbers matched!! You are eligible for lucky prize!");
			sb.append("\n");
			return sb.toString();
		}else {
			return "Unable to validate results";
		}
		
	}

}
