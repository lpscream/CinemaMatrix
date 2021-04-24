package net.matrixhome.kino.data;

import android.os.SystemClock;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewModelDataDownloader {
    private final String TAG = "ViewModelDataDownloader";

    public ArrayList<FilmList> getAllData(String url){
        return downloadAndParse(url);
    }

    public ArrayList<FilmList> downloadAndParse(String strUrl){
        float start = SystemClock.elapsedRealtime();
        StringBuilder result = new StringBuilder();
        URL url = null;
        HttpURLConnection connection;
        Log.d(TAG, "call: " + "start downloading");
        try {
            url = new URL(Constants.BASE_URL + strUrl + Constants.API_KEY);
            Log.d(TAG, "call: " + url.toString());
            connection = (HttpURLConnection) url.openConnection();
            //connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            //connection.setConnectTimeout(1000);
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            connection.disconnect();
            url = null;
            connection = null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        float endtime = SystemClock.elapsedRealtime() - start;
        Log.d(TAG, "call: elapsed time " + endtime);
        Log.d(TAG, "call: " + "finish downloading");
        Log.d(TAG, "call: " + Thread.currentThread().getName());
        //////////////////////////////////////////////////////////////

        ArrayList<FilmList> arrayList = new ArrayList<>();
        JSONObject jsonRoot;
        FilmList filmList = new FilmList();
        try {
            jsonRoot = new JSONObject(result.toString());
            JSONArray jsonArray = jsonRoot.getJSONArray("results");
            ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObjectArrayList.add(new JSONObject(jsonArray.getString(i)));
            }

            for (JSONObject k : jsonObjectArrayList) {
                if (filmList != null) {
                    filmList = null;
                    filmList = new FilmList();
                }
                filmList.id = k.getString("id");
                filmList.name = k.getString("name");
                filmList.description = k.getString("description");
                filmList.time = k.getString("time");
                filmList.cover = k.getString("cover");
                filmList.cover_200 = k.getString("cover_200");
                filmList.age = k.getString("age");
                filmList.rating = k.getString("rating");
                filmList.year = k.getString("year");
                filmList.country = k.getString("country");
                filmList.director = k.getString("director");
                filmList.added = k.getInt("added");
                filmList.hd = k.getInt("hd");
                filmList.actors = k.getString("actors");
                filmList.genres = k.getString("genres");
                filmList.kinopoisk_id = k.getString("kinopoisk_id");
                filmList.original_name = k.getString("original_name");
                filmList.translate_id = k.getString("translate_id");
                //TODO array with trailers for activity
                //create an array for film's trailers in filmList
                //filmList.trailer_urls = jsonObject.getString("trailer_urls");
                filmList.serial_id = k.getString("serial_id");
                filmList.serial_count_seasons = k.getString("serial_count_seasons");
                filmList.season_number = k.getString("season_number");
                filmList.serial_name = k.getString("serial_name");
                filmList.serial_o_name = k.getString("serial_o_name");
                filmList.translate = k.getString("translate");
                filmList.date_premiere = k.getString("date_premiere");
                filmList.vote_percent = k.getString("vote_percent");
                filmList.rating_vote_avg = k.getString("rating_vote_avg");
                filmList.made = k.getString("made");
                filmList.count_torrents = k.getString("count_torrents");
                filmList.category = k.getString("category");
                filmList.views = k.getString("views");
                filmList.views_month = k.getString("views_month");
                filmList.mpaa = k.getString("mpaa");
                filmList.serial_views = k.getString("serial_views");
                filmList.video_views = k.getString("video_views");
                filmList.url = k.getString("url");
                arrayList.add(filmList);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String str;
        String str2;
        String nul = "null";

        for (int i = 0; i < arrayList.size(); i++) {
            if (i < arrayList.size()) {
                str = arrayList.get(i).serial_id;
                if (str != nul) {
                    for (int j = 0; j < arrayList.size(); j++) {
                        if (j < arrayList.size() && i < arrayList.size()) {
                            str2 = arrayList.get(j).serial_id;
                            if (str2 != nul) {
                                if (str.equalsIgnoreCase(str2)) {
                                    if (j != i) {
                                        arrayList.remove(i);
                                        if (j != 0) {
                                            j = j - 1;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        endtime = SystemClock.elapsedRealtime() - start;
        Log.d(TAG, "call: elapsed time " + endtime);
        return arrayList;
    }

    public ArrayList<Genre> getAllGenres(String genresUrl){
        final String TAG = "myLogs";
        StringBuilder result = new StringBuilder();
        URL url = null;
        HttpURLConnection connection;
        Log.d(TAG, "doInBackground: " + "start downloading");
        try {
            url = new URL(Constants.BASE_URL + genresUrl + Constants.API_KEY);
            connection = (HttpURLConnection) url.openConnection();
            //connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            //connection.setConnectTimeout(1000);
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            connection.disconnect();
            url = null;
            connection = null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Genre> arrayList = new ArrayList<>();
        JSONObject jsonRoot;
        try {
            jsonRoot = new JSONObject(String.valueOf(result));
            JSONArray jsonArray = jsonRoot.getJSONArray("results");
            String[] array = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                array[i] = jsonArray.getString(i);
            }
            for (int i = 0; i < array.length; i++) {
                Genre genreList = new Genre();
                JSONObject jsonObject = new JSONObject(array[i]);
                genreList.id = jsonObject.getInt("id");
                genreList.title = jsonObject.getString("title");
                genreList.category = jsonObject.getString("category");
                arrayList.add(genreList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}