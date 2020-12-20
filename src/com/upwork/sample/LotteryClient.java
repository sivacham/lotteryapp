package com.upwork.sample;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Scanner;

public class LotteryClient {
	
	public static void main(String[] args) {
		boolean sendMsgFlag = true;
		System.out.println("Please enter 6 numbers between 1 and 50");
		int arr[] = new int[6];
		Scanner sc = new Scanner(System.in);
		for (int i = 0; i < arr.length; i++) {
			System.out.println("Please enter number:" + (i + 1));
			int input = sc.nextInt();
			if (LotteryUtils.inRange(input, LotteryUtils.MIN_VALUE, LotteryUtils.MAX_VALUE)) {//Range check API called
				arr[i] = input;
				continue;
			} else {
				System.out.println("Please enter a valid value in the range 1 to 50");
				sendMsgFlag = false;
				break;
			}
		}
		if(arr.length!=6) {//Check if 6 inputs entered
			System.out.println("Please enter 6 numbers in-between 1 to 50");
			sendMsgFlag = false;
		}
		
		if (sendMsgFlag) {//Check if all are valid inputs and convert to String witth commas
			String lottoVals = (Arrays.toString(arr).replaceAll("\\[|\\]|\\s", ""));
			lotteryInputSender(lottoVals);
			sc.close();
		}else {
			System.out.println("Unable to send Message due to bad input");
		}
	}
	
	
	public static void lotteryInputSender(String inputValues) {
		byte[] buf = new byte[256];
		try (DatagramSocket ds = new DatagramSocket();) {
			InetAddress addr = InetAddress.getByName("127.0.0.1");
			System.out.println("Sending values:" + inputValues);
			DatagramPacket dp = new DatagramPacket(inputValues.getBytes(), inputValues.length(), addr, 3000);
			ds.send(dp);
			
			// get response
			dp = new DatagramPacket(buf, buf.length);
			ds.receive(dp);
	 
	        // display response
	        String received = new String(dp.getData(), 0, dp.getLength());
	        System.out.println("Result Summary: " + received);
			ds.close();
			System.out.println("Do you want to play Again!!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
