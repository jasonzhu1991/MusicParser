package edu.gatech.jason.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class TxtWriter {

	/**
	 * @param textList content to write (ArrayList<String>)
	 * @param path path for data storage
	 * @return if the operation is successful
	 */
	public boolean write(List<String> textList, String path) {
		File file = new File(path);
		StringBuffer existingContent = null;
		if(file.exists()) {
			TxtReader txtReader = new TxtReader();
			existingContent = txtReader.readAsBufferString(path);
		} else {
			try {
				existingContent = new StringBuffer();
				if(file.createNewFile()) {
				} else {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
				
				return false;
			}
		}
		
		/*
		 * create content
		 */
		StringBuffer tempWrite = new StringBuffer();
		for(int i=0; i<textList.size(); i++) {
			tempWrite.append(textList.get(i) + "\r\n");
		}
		StringBuffer newContent = existingContent.append(tempWrite);
		
		/*
		 * output
		 */
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			fos = new FileOutputStream(path);
			osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(newContent.toString());
			osw.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        /*
         * post-process
         */
		try {
			osw.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return true;
	}
	
	/**
	 * @param textMap content to write (Map<String, Boolean>)
	 * @param path path for data storage
	 * @return if the operation is successful
	 */
	public boolean write(Map<String, Boolean> textMap, String path) {
		return write(new ArrayList<String>(textMap.keySet()), path);
	}
	
	public boolean write(Iterator<Entry<String, Integer>> mapIterator, String path) {
		ArrayList<String> result = new ArrayList<String>();
		
		while(mapIterator.hasNext()) {
			result.add(mapIterator.next().toString());
		}
		
		return write(result, path);
	}
	
	/**
	 * @param text content to write (String)
	 * @param path path for data storage
	 * @return if the operation is successful
	 */
	public boolean write(String text, String path) {
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(text);

		return write(temp, path);
	}
	
	public boolean overwrite(List<String> textList, String path) {
		/*
		 * create content
		 */
		StringBuffer tempWrite = new StringBuffer();
		for(int i=0; i<textList.size(); i++) {
			tempWrite.append(textList.get(i) + "\r\n");
		}
		
		/*
		 * output
		 */
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			fos = new FileOutputStream(path);
			osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(tempWrite.toString());
			osw.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        /*
         * post-process
         */
		try {
			osw.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return true;
	}
	
	public boolean overwrite(String text, String path) {
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(text);
		
		return overwrite(temp, path);
	}
}
