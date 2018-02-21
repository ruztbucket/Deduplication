package org.ruzt.bucket.zip;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;

public class Compressor {
	private static ByteBuffer buffer = ByteBuffer.allocate(8);
	private static byte[] convert(long x) {
		buffer.putLong(0, x);
		return buffer.array();
	}
	private static long convert(byte[] x) {
		buffer.put(x, 0, 8);
		buffer.flip();
		return buffer.getLong();
	}
	public static String hashF(String input_file) {
		return "";
	}
	public static String hash(byte[] dataBytes){ 
        MessageDigest md = null;
        try{
            md = MessageDigest.getInstance("SHA1");
        } catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
        }
		md.update(dataBytes);
		byte[] mdbytes = md.digest();
		String result = byteToHex(mdbytes);
		return result;
	}	
	private static String byteToHex(byte[] mdbytes) {
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16)
					.substring(1));
		}
		return sb.toString();
	}
	public static void zip(String input_file, String output_file) {
		Hashtable<String, Long> indexTable = new Hashtable<String,Long>();
        ArrayList<Long> pattern = new ArrayList<Long> ();
        FileInputStream fis = null;
        FileOutputStream fout = null;
        byte[] chunk = new byte[1024];
        long count = 0;        
        try {   
                fout = new FileOutputStream(output_file);
                byte[] unique_chunks = new byte[8];
                ByteBuffer.wrap(unique_chunks).putLong(0);
                fout.write(unique_chunks);
				fis = new FileInputStream(input_file);
				while (fis.read(chunk) != -1) {		                    
					String hashvalue = hash(chunk);		                    
					if (!indexTable.containsKey(hashvalue)) {
                        count++;
						indexTable.put(hashvalue, count);  
                        fout.write(chunk);
					}
                    pattern.add(indexTable.get(hashvalue));
				}
			
                byte[] a = new byte[8];
                for(Long num: pattern){
                    ByteBuffer.wrap(a).putLong(num);
                    fout.write(a);
                }
                close(fis);close(fout);
                RandomAccessFile rin = new RandomAccessFile(output_file,"rw");
                rin.seek(0);
                rin.write(convert(count));
                
                fis = new FileInputStream(output_file);
                fis.read(a);
                long test = convert(a);
                close(fis);close(rin);
        } catch (Exception e) {				
		  e.printStackTrace();
        }
	}
	public static void test2(String file) {
		try {
			FileInputStream fin = new FileInputStream(file);
			FileOutputStream fout = new FileOutputStream("test_"+file);
			byte[] chunk = new byte[32];
			if(fin.read(chunk)==-1)
				throw new Exception("fuck");
			fout.write(chunk);
		}catch (Exception e) {				
		  e.printStackTrace();
        }
	}
	public static void test(String file) {
		FileInputStream fin = null;
        FileOutputStream fout = null;
        try {
        	fout = new FileOutputStream(file);
        	long value = 0;
        	fout.write(convert(value));
        	close(fout);
        	fout = new FileOutputStream(file,true);
        	value = 12;
        	fout.write(convert(value));
        	close(fout);
        	fin = new FileInputStream(file);
        	byte data[] = new byte[8];
        	fin.read(data);
        	long ovalue = convert(data);
        	close(fin);
        }catch (Exception e) {				
		  e.printStackTrace();
        }
	}
	public static boolean unzip(String input_file, String output_file) {
		boolean status = true;
        RandomAccessFile fin = null;
        RandomAccessFile fin2 = null;
        FileOutputStream fout = null;        
        long count = 0;
        try{
            fin = new RandomAccessFile(input_file,"r");
            fin2 = new RandomAccessFile(input_file,"r");
            fout = new FileOutputStream(output_file);
            byte[] small = new byte[8];
            byte[] big = new byte[1024];
            if(fin.read(small) == -1)
                throw new Exception("Unknown format 1");
            count = ByteBuffer.wrap(small).getLong();            
            fin.seek(8+1024*count);
            while(fin.read(small)!=-1){
                long position = ByteBuffer.wrap(small).getLong();
                fin2.seek(8+(position-1)*1024);
                if(fin2.read(big)==-1)
                    throw new Exception("Unknown format 2");
                fout.write(big);
            }
        }catch(Exception e){
        	System.out.println(e.getMessage());        	
            status = false;
        }finally {
        	close(fin);
        	close(fin2);
        	close(fout);
        }
        return status;
    }	
	private static void close(Object fout) {
		try {
			if(fout!=null && fout.getClass().equals(RandomAccessFile.class))			
				((RandomAccessFile)fout).close();
			else if(fout!=null && fout.getClass().equals(FileOutputStream.class))
				((FileOutputStream)fout).close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
