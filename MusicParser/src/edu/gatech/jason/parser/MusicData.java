package edu.gatech.jason.parser;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MusicData {
	private String uid;
	private String post;
	private String url;
	private String artist = "";
	private String album = "";
	private String song = "";
	private String location;
	private String gender;
	private String errorMessage = "";
	private boolean success;
	
	public MusicData(String jsonStr) {
		JSONObject json = null;
		try {
			json = new JSONObject(jsonStr);
		} catch (JSONException e) {
			errorMessage = "Json file not readable";
			success = false;
			return;
		}
		try {
			uid = json.getString("uid");
			post = json.getString("originalTweet");
		} catch (JSONException e) {
			errorMessage = "Json format wrong";
			success = false;
			return;
		}
		success = true;
		return;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("uid", uid);
		json.put("post", post);
		json.put("url", url);
		json.put("song", song);
		json.put("album", album);
		json.put("artist", artist);
		json.put("location", location);
		json.put("gender", gender);
		return json;
	}
	
	public void retrieveLocationGender(User user) {
		location = user.getLoaction();
		gender = user.getGender();
	}
	
	public void retrieveURL() {
		String postStr = post;
		
		int idx0 = postStr.indexOf("http://t.cn");
		if (idx0 == -1) {
			errorMessage = "URL not found";
			success = false;
			return;
		}
		
		int idx1 = postStr.indexOf(" ", idx0);
		int idx2 = postStr.indexOf("���", idx0);
		if (idx1 == -1 && idx2 == -1) {
			url = postStr.substring(idx0, postStr.length());
		} else if (idx1 == -1) {
			url = postStr.substring(idx0, idx2);
		} else if (idx2 == -1) {
			url = postStr.substring(idx0, idx1);
		} else {
			url = postStr.substring(idx0, Math.min(idx1, idx2));
		}
		return;
	}
	
	public void retrieveMusicData() {
		if (!success) {
			return;
		}
		
		Document doc = null;
		if (url == null) {
			errorMessage = "URL not found";
			success = false;
			return;
		}
		try {
			doc = Jsoup.connect(url).timeout(10000).get();
		} catch (IOException e) {
			errorMessage = "HTML not retrieved";
			success = false;
			return;
		}
		String baseUrl = doc.baseUri();
		baseUrl = baseUrl.replace("http://www.xiami.com/", "");
		int idx = baseUrl.indexOf("/");
		String category = baseUrl.substring(0, idx);
		if (category.equals("song")) {
			extraxtSong(doc);
		} else if (category.equals("album")) {
			extractAlbum(doc);
		} else if (category.equals("artist")) {
			extractArtist(doc);
		} else {
			errorMessage = "URL not parseable";
			success = false;
			return;
		}
		return;
	}

	private void extractArtist(Document doc) {
		artist = "";
		album = "";
		song = "";
		success = false;
		errorMessage = "Extracting artists not implemented";
		return;
	}

	private void extractAlbum(Document doc) {
		try {
			artist = doc.getElementsByAttributeValue("id", "album_info")
					.get(0).child(2).child(0).child(0).child(1).child(0).text();
			album = doc.getElementsByAttributeValue("id", "title").get(0).child(0).text();
			song = "";
		} catch (IndexOutOfBoundsException e) {
			errorMessage = "Extract album data failed";
			success = false;
		} catch (NullPointerException e) {
			errorMessage = "Extract album data failed";
			success = false;
		}
		return;
	}

	private void extraxtSong(Document doc) {
		try {
			artist = doc.getElementsByAttributeValue("id", "albums_info")
					.get(0).child(0).child(1).child(1).child(0).child(0).text();
			album = doc.getElementsByAttributeValue("id", "albums_info")
					.get(0).child(0).child(0).child(1).child(0).child(0).text();
			song = doc.getElementsByAttributeValue("id", "title").get(0).child(0).text();
		} catch (IndexOutOfBoundsException e) {
			errorMessage = "Extract song data failed";
			success = false;
		} catch (NullPointerException e) {
			errorMessage = "Extract song data failed";
			success = false;
		}
	}
	
	public void cleanURL() {
		url = null;
	}
	
	public void cleanPost() {
		post = null;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSong() {
		return song;
	}

	public void setSong(String song) {
		this.song = song;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}	
}
