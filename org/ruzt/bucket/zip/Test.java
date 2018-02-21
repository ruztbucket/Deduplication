package org.ruzt.bucket.zip;

import java.util.Scanner;

public class Test {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter File name with extension (example.jpg)  : ");
		String filename = sc.nextLine();
		String fn[] = filename.split("\\.");
		String encoded_filename = filename.substring(0,filename.length()-fn[fn.length-1].length()-1);
		//Compressor.zip(filename,encoded_filename+".ruzt"); 
		Compressor.unzip(encoded_filename+".ruzt", encoded_filename+"_out."+fn[fn.length-1]);
		System.out.print("Done! Your are welcome");
		//Compressor.test2("yoyo.txt");
	}

}
