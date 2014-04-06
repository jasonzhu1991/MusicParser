package edu.gatech.jason.parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.gatech.jason.io.TxtReader;
import edu.gatech.jason.io.TxtWriter;

public class MusicParser {

	private static long startTime;
	private static long endTime;
	List<MusicData> list = new ArrayList<MusicData>();
	HashMap<String, User> users = new HashMap<String, User>();

	public static void main(String[] args) throws JSONException {
		boolean test = (args.length == 0);
		startTime = System.currentTimeMillis();
		MusicParser parser = new MusicParser();
		try {
			parser.readFile();
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		if (test) {
			parser.randPerm(100);
		} else {
			int start = Integer.parseInt(args[0]) * 1000;
			int end = Integer.parseInt(args[1]) * 1000;
			if (end == 27000) {
				end = parser.getSize();
			}
			parser.subsequence(start, end);	
		}

		parser.retrieveLocationGender();
		parser.findURL();
		parser.retrieveMusicData();

		endTime = System.currentTimeMillis();
		parser.outputResults();
		parser.outputError();
		parser.outputStatistics();
		return;
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
		String usersRaw = reader.readAsArrayList("res/cse6242-users.txt")
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
	
	private void subsequence(int start, int end) {
		ArrayList<MusicData> temp = new ArrayList<>(end - start);
		for (int i = start; i < end; i++) {
			temp.add(list.get(i));
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
			if (i == 0) {
				System.out.println("Start retrieving data...");
				System.out.println("0% -- " + elapsedTime());
			}
			list.get(i).retrieveMusicData();
			progressReport(i);
		}
	}

	private void progressReport(int i) {
		int progress = (int) Math.floor((double) i) * 100 / list.size();
		int post_progress = (int) Math.floor((double) (i+1)) * 100 / list.size();
		if (post_progress > progress) {
			System.out.println(post_progress + "% -- " + elapsedTime());
		}
		return;
	}

	private String elapsedTime() {
		long timeElapsed = System.currentTimeMillis() - startTime;
		long totalSeconds = timeElapsed / 1000;
		String seconds = String.valueOf(totalSeconds % 60);
		if (seconds.length() < 2) {
			seconds = "0" + seconds;
		}
		long totalMinutes = totalSeconds / 60;
		String minutes = String.valueOf(totalMinutes % 60);
		if (minutes.length() < 2) {
			minutes = "0" + minutes;
		}
		long totalHours = totalMinutes / 60;
		String hours = String.valueOf(totalHours);
		if (hours.length() < 2) {
			hours = "0" + hours;
		}
		return hours + ":" + minutes + ":" + seconds;
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
		String fileName = new SimpleDateFormat("'out/error-'yyyy-MM-dd-hh-mm'.txt'")
				.format(new Date());
		writer.overwrite(errorList, fileName);
		return;
	}

	private void outputResults() throws JSONException {
		TxtWriter writer = new TxtWriter();
		JSONArray results = new JSONArray();
		for (int i = 0; i < getSize(); i++) {
			if (list.get(i).isSuccess()) {
				results.put(list.get(i).toJSON());
			}
		}
		String fileName = new SimpleDateFormat(
				"'out/result-'yyyy-MM-dd-hh-mm'.txt'").format(new Date());
		writer.overwrite(results.toString(), fileName);
		return;
	}

	public void outputStatistics() {
		ArrayList<String> statistics = new ArrayList<String>();
		int successful = 0;
		int errorJsonNotReadable = 0;
		int errorJsonFormatWrong = 0;
		int errorUrlNotFound = 0;
		int errorHtmlNotRetrieved = 0;
		int errorUrlNotParseable = 0;
		int errorArtistsNotParseable = 0;
		int errorExtractAlbum = 0;
		int errorExtractSong = 0;
		int errorOthers = 0;
		
		for (MusicData data : list) {
			if (data.getErrorMessage() == "") {
				successful++;
			} else if (data.getErrorMessage().equals("Json file not readable")) {
				errorJsonNotReadable++;
			} else if (data.getErrorMessage().equals("Json format wrong")) {
				errorJsonFormatWrong++;
			} else if (data.getErrorMessage().equals("URL not found")) {
				errorUrlNotFound++;
			} else if (data.getErrorMessage().equals("HTML not retrieved")) {
				errorHtmlNotRetrieved++;
			} else if (data.getErrorMessage().equals("URL not parseable")) {
				errorUrlNotParseable++;
			} else if (data.getErrorMessage().equals("Extracting artists not implemented")) {
				errorArtistsNotParseable++;
			} else if (data.getErrorMessage().equals("Extract album data failed")) {
				errorExtractAlbum++;
			} else if (data.getErrorMessage().equals("Extract song data failed")) {
				errorExtractSong++;
			} else {
				errorOthers++;
			}
		}
		
		statistics.add("Time elapsed: " + (double) (endTime - startTime) / 1000);
		statistics.add("Successfully Retrieved: " + successful);
		statistics.add("Json file not readable: " + errorJsonNotReadable);
		statistics.add("Json format wrong: " + errorJsonFormatWrong);
		statistics.add("URL not found: " + errorUrlNotFound);
		statistics.add("HTML not retrieved: " + errorHtmlNotRetrieved);
		statistics.add("URL not parseable: " + errorUrlNotParseable);
		statistics.add("Extracting artists not implemented: " + errorArtistsNotParseable);
		statistics.add("Extract album data failed: " + errorExtractAlbum);
		statistics.add("Extract song data failed: " + errorExtractSong);
		statistics.add("Other errors: " + errorOthers);
		statistics.add("---------------------------------");
		statistics.add("Retrieved URLs: " + successURL());
		statistics.add("Retrieved artists: " + successArtist());
		statistics.add("Retrieved albums: " + successAlbum());
		statistics.add("Retrieved songs: " + successSong());
		
		TxtWriter writer = new TxtWriter();
		String fileName = new SimpleDateFormat(
				"'out/statistics-'yyyy-MM-dd-hh-mm'.txt'").format(new Date());
		writer.overwrite(statistics, fileName);
	}
}