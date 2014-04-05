package edu.gatech.jason.io;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class TxtReader {

	/**
	 * @param path
	 * @return existing content (StringBuffer)
	 */
	public ArrayList<String> readAsArrayList(String path) {
		ArrayList<String> list = new ArrayList<String>();
		File readFile = new File(path);
		if(readFile.exists()) {
			/*
			 * configure input stream
			 */
			FileInputStream fis = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			try {
				fis = new FileInputStream(readFile);
				isr = new InputStreamReader(fis, "UTF-8");
				br = new BufferedReader(isr);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			/*
			 * read
			 */
			String tempRead = "";
			try {
				while((tempRead=br.readLine()) != null) {
					list.add(tempRead);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			/*
			 * post-process
			 */
			try {
				br.close();
				isr.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return list;
		} else {
			return null;
		}
	}
	
	public StringBuffer readAsBufferString(String path) {
		ArrayList<String> list = readAsArrayList(path);
		StringBuffer temp = new StringBuffer();
		for(int i=0; i<list.size(); i++) {
			temp.append(list.get(i) + "\r\n");
		}
		
		return temp;
	}
}
