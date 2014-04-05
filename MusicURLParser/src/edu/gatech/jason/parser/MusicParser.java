package edu.gatech.jason.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.gatech.jason.io.TxtReader;
import edu.gatech.jason.io.TxtWriter;

public class MusicParser {

	List<MusicData> list = new ArrayList<MusicData>();
	HashMap<String, User> users = new HashMap<String, User>();

	public static void main(String[] args) throws JSONException {
		long startTime = System.currentTimeMillis();
		MusicParser parser = new MusicParser();
		try {
			parser.readFile();
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		
		boolean test = (args.length == 0);
		if (test) {
			parser.randPerm(100);
		} else {
			int start = Integer.parseInt(args[0]);
			int end = Integer.parseInt(args[1]);
			parser.extractSublist(start, end);
		}
		
		parser.retrieveLocationGender();
		parser.findURL();
		System.out.println("Successfully Found URL: " + parser.successURL()
				+ " / " + parser.getSize());
		parser.retrieveMusicData();
		System.out.println("Successfully Retrieved Songs: "
				+ parser.successSong() + " / " + parser.successURL());
		System.out.println("Successfully Retrieved Albums: "
				+ parser.successAlbum() + " / " + parser.successURL());
		System.out.println("Successfully Retrieved Artists: "
				+ parser.successArtist() + " / " + parser.successURL());
		
		long endTime = System.currentTimeMillis();
		double timeElapsed = ((double) (endTime - startTime))/1000;
		parser.outputResults();
		parser.outputError();
		System.out.println("Time elapsed: " + timeElapsed + " seconds");
		return;
	}

	private void extractSublist(int start, int end) {
		// TODO Auto-generated method stub
		
	}

	public int getSize() {
		return list.size();
	}

	private void readFile() throws JSONException {
		TxtReader reader = new TxtReader();
		ArrayList<String> data = reader.readAsArrayList("res/cse6242-data");
		for (int i = 0; i < data.size(); i++) {
			list.add(new MusicData(data.get(i)));
		}
		String usersRaw = reader.readAsArrayList("res/cse6242-users-short.txt")
				.get(0);
		JSONArray usersJson = new JSONArray(usersRaw);
		for (int i = 0; i < usersJson.length(); i++) {
			JSONObject userJson = usersJson.getJSONObject(i);
			User user = new User(userJson.getString("uid"),
					userJson.getString("location"),
					userJson.getString("gender"));
			users.put(user.getUid(), user);
		}
		return;
	}

	private void randPerm(int num) {
		java.util.Collections.shuffle(list);
		num = Math.min(num, getSize());
		ArrayList<MusicData> temp = new ArrayList<>(num);
		for (int i = 0; i < num; i++) {
			temp.add(i, list.get(i));
		}
		list.clear();
		list.addAll(temp);
		return;
	}
	
	private void retrieveLocationGender() {
		for (int i = 0; i < list.size(); i++) {
			list.get(i).retrieveLocationGender(users.get(list.get(i).getUid()));
		}
	}

	private void findURL() {
		for (int i = 0; i < list.size(); i++) {
			list.get(i).retrieveURL();
		}
	}

	@SuppressWarnings("unused")
	private void cleanPost() {
		for (int i = 0; i < list.size(); i++) {
			list.get(i).cleanPost();
		}
	}

	private void retrieveMusicData() {
		for (int i = 0; i < list.size(); i++) {
			list.get(i).retrieveMusicData();
		}
	}

	private int successURL() {
		int success = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getUrl() != null) {
				success++;
			}
		}
		return success;
	}

	private int successSong() {
		int success = 0;
		for (int i = 0; i < list.size(); i++) {
			if (!list.get(i).getSong().isEmpty()) {
				success++;
			}
		}
		return success;
	}

	private int successAlbum() {
		int success = 0;
		for (int i = 0; i < list.size(); i++) {
			if (!list.get(i).getAlbum().isEmpty()) {
				success++;
			}
		}
		return success;
	}
	
	private int successArtist() {
		int success = 0;
		for (int i = 0; i < list.size(); i++) {
			if (!list.get(i).getArtist().isEmpty()) {
				success++;
			}
		}
		return success;
	}

	private void outputError() {
		TxtWriter writer = new TxtWriter();
		ArrayList<String> errorList = new ArrayList<String>();
		for (int i = 0; i < getSize(); i++) {
			MusicData data = list.get(i);
			if (!data.isSuccess()) {
				String error = data.getErrorMessage() + "<<" + data.getPost();
				errorList.add(error);
			}
		}
		writer.overwrite(errorList, "out/error.txt");
		return;
	}
	
	private void outputResults() throws JSONException {
		TxtWriter writer = new TxtWriter();
		JSONArray results = new JSONArray();
		for (int i = 0; i < getSize(); i++) {
			results.put(list.get(i).toJSON());
			
		}
		writer.overwrite(results.toString(), "out/result.txt");
		return;
	}
}
