package net.matrixhome.kino.data;

import android.os.AsyncTask;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class DataLoaderXML {

    private final String TAG = "DataDownloaderXML";
    private final Download download = new Download();
    private final ArrayList<Integer> hidenList = new ArrayList<>();
    ExecutorService service;
    private int offset = 0;


    public DataLoaderXML(int numThreads) {
        service = Executors.newFixedThreadPool(numThreads);
    }



    //работает в асинк таске, только парсит строку
    public ArrayList<FilmList> parseJSON(String str) {
        Parser parse = new Parser();
        ArrayList<FilmList> film = new ArrayList<>();
        try {
            film = parse.execute(str + Constants.API_KEY).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return film;
    }


    //берем лист без загрузки, только парсит строку
    public ArrayList<FilmList> parseJSONCallable(String str) {
        ArrayList<FilmList> film = new ArrayList<>();
        Future<ArrayList<FilmList>> getFilmListFuture;
        getFilmListFuture = service.submit(new ParseListCallable(str));
        //3 ждем пока задача выполнится
        try {
            film = getFilmListFuture.get();
        } catch (Exception ie) {
            ie.printStackTrace(System.err);
        }

        //5 останавливаем ThreadPool.
        //service.shutdown();
        return film;
    }


    public ArrayList<FilmList> getAllDataCallable(String url) {
        ArrayList<FilmList> film = new ArrayList<>();
        Future<ArrayList<FilmList>> getListFuture;
        getListFuture = service.submit(new GEtAllData(url + Constants.API_KEY));
        offset = offset + Integer.parseInt(Constants.UPDATE_COUNT);
        Log.d(TAG, "getAllDataCallable: " + url);
        Log.d(TAG, "getAllDataCallable: offset count  is " + offset);
        try {
            film = getListFuture.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return film;
    }


    public ArrayList<FilmList> parseSerialJSON(String str) {
        ParserSerial parse = new ParserSerial();
        ArrayList<FilmList> film = new ArrayList<>();
        try {
            film = parse.execute(str + Constants.API_KEY).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return film;
    }


    public ArrayList<ArrayList<FilmList>> getAllData(String[] data) {
        ArrayList<ArrayList<FilmList>> listFilm = new ArrayList<>();
        GetData getData = new GetData();
        try {
            listFilm = getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return listFilm;
    }


    public ArrayList<Genre> parseGenres(String genresString) {
        ParseGenres parseGenres = new ParseGenres();
        ArrayList<Genre> genreArrayList = new ArrayList<>();
        try {
            genreArrayList = parseGenres.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, genresString).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return genreArrayList;
    }


    public String getList(String url) {
        String line = "";
        Download load = new Download();
        try {
            line = load.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url + Constants.API_KEY).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return line;
    }


    public String searchFilm(String url) {
        String line = "";
        Download load = new Download();
        try {
            line = load.execute(url + Constants.API_KEY).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return line;
    }

    public String getLinkByID(String id) {
        Download loading = new Download();
        String line = "null";
        JSONObject jsonRoot;
        try {
            line = loading.execute("?action=link&video_id=" + id + Constants.API_KEY).get();
            jsonRoot = new JSONObject(line);
            line = "https:" + jsonRoot.getString("results");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    public String getSerialLinkByID(String id, String episode) {
        Download loading = new Download();
        String line = "null";
        JSONObject jsonRoot;
        try {
            line = loading.execute("?action=link&video_id=" + id + "&episode=" + episode + Constants.API_KEY).get();
            Log.d(TAG, "getSerialLinkByID: " + "?action=link&video_id=" + id + "&episode=" + episode + Constants.API_KEY);
            jsonRoot = new JSONObject(line);
            line = "https:" + jsonRoot.getString("results");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getSerialLinkByID: " + line);
        return line;
    }


    private class GetData extends AsyncTask<String, Void, ArrayList<ArrayList<FilmList>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<FilmList>> arrayLists) {
            super.onPostExecute(arrayLists);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<ArrayList<FilmList>> doInBackground(String... strings) {

            ArrayList<ArrayList<FilmList>> list = new ArrayList<>();
            StringBuilder result = new StringBuilder();
            URL url = null;
            HttpURLConnection connection;

            ArrayList<FilmList> arrayList = new ArrayList<>();
            JSONObject jsonRoot;
            String lastSerialID = "null";


            for (int i = 0; i < strings.length; i++) {
                try {
                    url = new URL(Constants.BASE_URL + strings[i]);
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
                    if (isCancelled())
                        return null;
                    connection.disconnect();
                    url = null;
                    connection = null;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    jsonRoot = new JSONObject(result.toString());
                    JSONArray jsonArray = jsonRoot.getJSONArray("results");
                    String[] array = new String[jsonArray.length()];
                    for (int l = 0; l < jsonArray.length(); l++) {
                        array[l] = jsonArray.getString(i);
                    }
                    for (int k = 0; k < array.length; k++) {
                        FilmList filmList = new FilmList();
                        JSONObject jsonObject = new JSONObject(array[k]);
                        filmList.id = jsonObject.getString("id");
                        filmList.name = jsonObject.getString("name");
                        filmList.description = jsonObject.getString("description");
                        filmList.time = jsonObject.getString("time");
                        filmList.cover = jsonObject.getString("cover");
                        filmList.cover_200 = jsonObject.getString("cover_200");
                        filmList.age = jsonObject.getString("age");
                        filmList.rating = jsonObject.getString("rating");
                        filmList.year = jsonObject.getString("year");
                        filmList.country = jsonObject.getString("country");
                        filmList.director = jsonObject.getString("director");
                        filmList.added = jsonObject.getInt("added");
                        filmList.hd = jsonObject.getInt("hd");
                        filmList.actors = jsonObject.getString("actors");
                        filmList.genres = jsonObject.getString("genres");
                        filmList.kinopoisk_id = jsonObject.getString("kinopoisk_id");
                        filmList.original_name = jsonObject.getString("original_name");
                        filmList.translate_id = jsonObject.getString("translate_id");
                        //TODO array with trailers for activity
                        //create an array for film's trailers in filmList
                        //filmList.trailer_urls = jsonObject.getString("trailer_urls");
                        filmList.serial_id = jsonObject.getString("serial_id");
                        filmList.serial_count_seasons = jsonObject.getString("serial_count_seasons");
                        filmList.season_number = jsonObject.getString("season_number");
                        filmList.serial_name = jsonObject.getString("serial_name");
                        filmList.serial_o_name = jsonObject.getString("serial_o_name");
                        filmList.translate = jsonObject.getString("translate");
                        filmList.date_premiere = jsonObject.getString("date_premiere");
                        filmList.vote_percent = jsonObject.getString("vote_percent");
                        filmList.rating_vote_avg = jsonObject.getString("rating_vote_avg");
                        filmList.made = jsonObject.getString("made");
                        filmList.count_torrents = jsonObject.getString("count_torrents");
                        filmList.category = jsonObject.getString("category");
                        filmList.views = jsonObject.getString("views");
                        filmList.views_month = jsonObject.getString("views_month");
                        filmList.mpaa = jsonObject.getString("mpaa");
                        filmList.serial_views = jsonObject.getString("serial_views");
                        filmList.video_views = jsonObject.getString("video_views");
                        filmList.url = jsonObject.getString("url");
                        arrayList.add(filmList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                list.add(arrayList);
            }


            return list;
        }
    }


    private class Download extends AsyncTask<String, Void, String> {

        long startTime, endTime;

        @Override
        protected void onPreExecute() {
            startTime = SystemClock.elapsedRealtime();
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            endTime = SystemClock.elapsedRealtime() - startTime;
            Log.d("testParser", "onPostExecuteDownload: " + endTime);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            final String TAG = "myLogs";
            Log.d(TAG, "doInBackground: " + strings[0]);
            StringBuilder result = new StringBuilder();
            URL url = null;
            HttpURLConnection connection;
            Log.d(TAG, "doInBackground: " + "start downloading");
            try {
                url = new URL(Constants.BASE_URL + strings[0]);
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
                if (isCancelled())
                    return null;
                connection.disconnect();
                url = null;
                connection = null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "doInBackground: " + "finish downloading");

            return result.toString();
        }
    }

   /*private class Parser extends AsyncTask<String, Void, ArrayList<FilmList>> {
        long startTime, endTime;

        @Override
        protected void onPreExecute() {
            startTime = SystemClock.elapsedRealtime();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<FilmList> filmLists) {
            endTime = SystemClock.elapsedRealtime() - startTime;
            Log.d("testParser", "onPostExecute: " + endTime);
            super.onPostExecute(filmLists);
        }

        @Override
        protected ArrayList<FilmList> doInBackground(String... strings) {
            ArrayList<FilmList> arrayList = new ArrayList<>();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            JSONObject jsonRoot;
            try {
                jsonRoot = new JSONObject(strings[0]);
                JSONArray jsonArray = jsonRoot.getJSONArray("results");
                String[] array = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    array[i] = jsonArray.getString(i);
                }

                for (int i = 0; i < array.length; i++) {
                    JSONObject jsonObject = new JSONObject(array[i]);
                    Log.d(TAG, "doInBackground: " + jsonObject.toString());
                    FilmList filmList = gson.fromJson(jsonObject.toString(), FilmList.class);
                    arrayList.add(filmList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return arrayList;
        }
    }*/

    private class Parser extends AsyncTask<String, Void, ArrayList<FilmList>> {
        long startTime, endTime;

        @Override
        protected void onPreExecute() {
            startTime = SystemClock.elapsedRealtime();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<FilmList> filmLists) {
            endTime = SystemClock.elapsedRealtime() - startTime;
            Log.d("testParser", "onPostExecute: " + endTime);
            super.onPostExecute(filmLists);
        }

        @Override
        protected ArrayList<FilmList> doInBackground(String... strings) {
            ArrayList<FilmList> arrayList = new ArrayList<>();
            JSONObject jsonRoot;
            try {
                jsonRoot = new JSONObject(strings[0]);
                JSONArray jsonArray = jsonRoot.getJSONArray("results");
                String[] array = new String[jsonArray.length()];
                for (int k = 0; k < array.length; k++) {
                    FilmList filmList = new FilmList();
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(k));
                    filmList.id = jsonObject.getString("id");
                    filmList.name = jsonObject.getString("name");
                    filmList.description = jsonObject.getString("description");
                    filmList.time = jsonObject.getString("time");
                    filmList.cover = jsonObject.getString("cover");
                    filmList.cover_200 = jsonObject.getString("cover_200");
                    filmList.age = jsonObject.getString("age");
                    filmList.rating = jsonObject.getString("rating");
                    filmList.year = jsonObject.getString("year");
                    filmList.country = jsonObject.getString("country");
                    filmList.director = jsonObject.getString("director");
                    filmList.added = jsonObject.getInt("added");
                    filmList.hd = jsonObject.getInt("hd");
                    filmList.actors = jsonObject.getString("actors");
                    filmList.genres = jsonObject.getString("genres");
                    filmList.kinopoisk_id = jsonObject.getString("kinopoisk_id");
                    filmList.original_name = jsonObject.getString("original_name");
                    filmList.translate_id = jsonObject.getString("translate_id");
                    //TODO array with trailers for activity
                    //create an array for film's trailers in filmList
                    //filmList.trailer_urls = jsonObject.getString("trailer_urls");
                    filmList.serial_id = jsonObject.getString("serial_id");
                    filmList.serial_count_seasons = jsonObject.getString("serial_count_seasons");
                    filmList.season_number = jsonObject.getString("season_number");
                    filmList.serial_name = jsonObject.getString("serial_name");
                    filmList.serial_o_name = jsonObject.getString("serial_o_name");
                    filmList.translate = jsonObject.getString("translate");
                    filmList.date_premiere = jsonObject.getString("date_premiere");
                    filmList.vote_percent = jsonObject.getString("vote_percent");
                    filmList.rating_vote_avg = jsonObject.getString("rating_vote_avg");
                    filmList.made = jsonObject.getString("made");
                    filmList.count_torrents = jsonObject.getString("count_torrents");
                    filmList.category = jsonObject.getString("category");
                    filmList.views = jsonObject.getString("views");
                    filmList.views_month = jsonObject.getString("views_month");
                    filmList.mpaa = jsonObject.getString("mpaa");
                    filmList.serial_views = jsonObject.getString("serial_views");
                    filmList.video_views = jsonObject.getString("video_views");
                    filmList.url = jsonObject.getString("url");
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

            return arrayList;
        }
    }


    private class ParserSerial extends AsyncTask<String, Void, ArrayList<FilmList>> {
        long startTime, endTime;

        @Override
        protected void onPreExecute() {
            startTime = SystemClock.elapsedRealtime();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<FilmList> filmLists) {
            endTime = SystemClock.elapsedRealtime() - startTime;
            Log.d("testParser", "onPostExecute: " + endTime);
            super.onPostExecute(filmLists);
        }

        @Override
        protected ArrayList<FilmList> doInBackground(String... strings) {
            ArrayList<FilmList> arrayList = new ArrayList<>();
            JSONObject jsonRoot;
            try {
                jsonRoot = new JSONObject(strings[0]);
                JSONArray jsonArray = jsonRoot.getJSONArray("results");
                String[] array = new String[jsonArray.length()];
                for (int k = 0; k < array.length; k++) {
                    FilmList filmList = new FilmList();
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(k));
                    filmList.id = jsonObject.getString("id");
                    filmList.name = jsonObject.getString("name");
                    filmList.description = jsonObject.getString("description");
                    filmList.time = jsonObject.getString("time");
                    filmList.cover = jsonObject.getString("cover");
                    filmList.cover_200 = jsonObject.getString("cover_200");
                    filmList.age = jsonObject.getString("age");
                    filmList.rating = jsonObject.getString("rating");
                    filmList.year = jsonObject.getString("year");
                    filmList.country = jsonObject.getString("country");
                    filmList.director = jsonObject.getString("director");
                    filmList.added = jsonObject.getInt("added");
                    filmList.hd = jsonObject.getInt("hd");
                    filmList.actors = jsonObject.getString("actors");
                    filmList.genres = jsonObject.getString("genres");
                    filmList.kinopoisk_id = jsonObject.getString("kinopoisk_id");
                    filmList.original_name = jsonObject.getString("original_name");
                    filmList.translate_id = jsonObject.getString("translate_id");
                    //TODO array with trailers for activity
                    //create an array for film's trailers in filmList
                    //filmList.trailer_urls = jsonObject.getString("trailer_urls");
                    filmList.serial_id = jsonObject.getString("serial_id");
                    filmList.serial_count_seasons = jsonObject.getString("serial_count_seasons");
                    filmList.season_number = jsonObject.getString("season_number");
                    filmList.serial_name = jsonObject.getString("serial_name");
                    filmList.serial_o_name = jsonObject.getString("serial_o_name");
                    filmList.translate = jsonObject.getString("translate");
                    filmList.date_premiere = jsonObject.getString("date_premiere");
                    filmList.vote_percent = jsonObject.getString("vote_percent");
                    filmList.rating_vote_avg = jsonObject.getString("rating_vote_avg");
                    filmList.made = jsonObject.getString("made");
                    filmList.count_torrents = jsonObject.getString("count_torrents");
                    filmList.category = jsonObject.getString("category");
                    filmList.views = jsonObject.getString("views");
                    filmList.views_month = jsonObject.getString("views_month");
                    filmList.mpaa = jsonObject.getString("mpaa");
                    filmList.serial_views = jsonObject.getString("serial_views");
                    filmList.video_views = jsonObject.getString("video_views");
                    filmList.url = jsonObject.getString("url");
                    filmList.series = new ArrayList<>();
                    for (int i = 0; i < jsonObject.getJSONArray("series").length(); i++) {
                        filmList.series.add(jsonObject.getJSONArray("series").get(i).toString());
                    }

                    arrayList.add(filmList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return arrayList;
        }
    }


    private class ParseGenres extends AsyncTask<String, Void, ArrayList<Genre>> {

        @Override
        protected ArrayList<Genre> doInBackground(String... strings) {
            ArrayList<Genre> arrayList = new ArrayList<>();
            JSONObject jsonRoot;
            try {
                jsonRoot = new JSONObject(strings[0]);
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


   /* private class ParseGson extends AsyncTask<String, Void, ArrayList<FilmList>> {
        long startTime, endTime;

        @Override
        protected void onPreExecute() {
            startTime = SystemClock.elapsedRealtime();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<FilmList> filmLists) {
            endTime = SystemClock.elapsedRealtime() - startTime;
            Log.d("testParser", "onPostExecute: " + endTime);
            super.onPostExecute(filmLists);
        }

        @Override
        protected ArrayList<FilmList> doInBackground(String... strings) {
            ArrayList<FilmList> arrayList = new ArrayList<>();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            JSONObject jsonRoot;
            Log.d(TAG, "doInBackground: " + "start parsing");
            try {
                jsonRoot = new JSONObject(strings[0]);
                JSONArray jsonArray = jsonRoot.getJSONArray("results");
                String[] array = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    array[i] = jsonArray.getString(i);
                }
                for (int i = 0; i < array.length; i++) {
                    FilmList filmList = new FilmList();
                    JSONObject jsonObject = new JSONObject(array[i]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return arrayList;
        }
    }*/

///////////////////////////////////////////////////////////////////////

    class ParseListCallable implements Callable<ArrayList<FilmList>> {
        String str;

        ParseListCallable(String str) {
            this.str = str;
        }

        public ArrayList<FilmList> call() throws Exception {
            ArrayList<FilmList> arrayList = new ArrayList<>();
            JSONObject jsonRoot;
            try {
                jsonRoot = new JSONObject(str);
                JSONArray jsonArray = jsonRoot.getJSONArray("results");
                String[] array = new String[jsonArray.length()];
                for (int k = 0; k < array.length; k++) {
                    FilmList filmList = new FilmList();
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(k));
                    filmList.id = jsonObject.getString("id");
                    filmList.name = jsonObject.getString("name");
                    filmList.description = jsonObject.getString("description");
                    filmList.time = jsonObject.getString("time");
                    filmList.cover = jsonObject.getString("cover");
                    filmList.cover_200 = jsonObject.getString("cover_200");
                    filmList.age = jsonObject.getString("age");
                    filmList.rating = jsonObject.getString("rating");
                    filmList.year = jsonObject.getString("year");
                    filmList.country = jsonObject.getString("country");
                    filmList.director = jsonObject.getString("director");
                    filmList.added = jsonObject.getInt("added");
                    filmList.hd = jsonObject.getInt("hd");
                    filmList.actors = jsonObject.getString("actors");
                    filmList.genres = jsonObject.getString("genres");
                    filmList.kinopoisk_id = jsonObject.getString("kinopoisk_id");
                    filmList.original_name = jsonObject.getString("original_name");
                    filmList.translate_id = jsonObject.getString("translate_id");
                    //TODO array with trailers for activity
                    //create an array for film's trailers in filmList
                    //filmList.trailer_urls = jsonObject.getString("trailer_urls");
                    filmList.serial_id = jsonObject.getString("serial_id");
                    filmList.serial_count_seasons = jsonObject.getString("serial_count_seasons");
                    filmList.season_number = jsonObject.getString("season_number");
                    filmList.serial_name = jsonObject.getString("serial_name");
                    filmList.serial_o_name = jsonObject.getString("serial_o_name");
                    filmList.translate = jsonObject.getString("translate");
                    filmList.date_premiere = jsonObject.getString("date_premiere");
                    filmList.vote_percent = jsonObject.getString("vote_percent");
                    filmList.rating_vote_avg = jsonObject.getString("rating_vote_avg");
                    filmList.made = jsonObject.getString("made");
                    filmList.count_torrents = jsonObject.getString("count_torrents");
                    filmList.category = jsonObject.getString("category");
                    filmList.views = jsonObject.getString("views");
                    filmList.views_month = jsonObject.getString("views_month");
                    filmList.mpaa = jsonObject.getString("mpaa");
                    filmList.serial_views = jsonObject.getString("serial_views");
                    filmList.video_views = jsonObject.getString("video_views");
                    filmList.url = jsonObject.getString("url");
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
            return arrayList;
        }
    }
    ////////////////////////////////////////////////////////////////////////
}


class ParseJSONString implements Callable<ArrayList<FilmList>> {
    String str;

    ParseJSONString(String str) {
        this.str = str;
    }

    public ArrayList<FilmList> call() throws Exception {
        ArrayList<FilmList> arrayList = new ArrayList<>();
        JSONObject jsonRoot;
        try {
            jsonRoot = new JSONObject(str);
            JSONArray jsonArray = jsonRoot.getJSONArray("results");
            for (int k = 0; k < jsonArray.length(); k++) {
                FilmList filmList = new FilmList();
                JSONObject jsonObject = new JSONObject(jsonArray.getString(k));
                filmList.id = jsonObject.getString("id");
                filmList.name = jsonObject.getString("name");
                filmList.description = jsonObject.getString("description");
                filmList.time = jsonObject.getString("time");
                filmList.cover = jsonObject.getString("cover");
                filmList.cover_200 = jsonObject.getString("cover_200");
                filmList.age = jsonObject.getString("age");
                filmList.rating = jsonObject.getString("rating");
                filmList.year = jsonObject.getString("year");
                filmList.country = jsonObject.getString("country");
                filmList.director = jsonObject.getString("director");
                filmList.added = jsonObject.getInt("added");
                filmList.hd = jsonObject.getInt("hd");
                filmList.actors = jsonObject.getString("actors");
                filmList.genres = jsonObject.getString("genres");
                filmList.kinopoisk_id = jsonObject.getString("kinopoisk_id");
                filmList.original_name = jsonObject.getString("original_name");
                filmList.translate_id = jsonObject.getString("translate_id");
                //TODO array with trailers for activity
                //create an array for film's trailers in filmList
                //filmList.trailer_urls = jsonObject.getString("trailer_urls");
                filmList.serial_id = jsonObject.getString("serial_id");
                filmList.serial_count_seasons = jsonObject.getString("serial_count_seasons");
                filmList.season_number = jsonObject.getString("season_number");
                filmList.serial_name = jsonObject.getString("serial_name");
                filmList.serial_o_name = jsonObject.getString("serial_o_name");
                filmList.translate = jsonObject.getString("translate");
                filmList.date_premiere = jsonObject.getString("date_premiere");
                filmList.vote_percent = jsonObject.getString("vote_percent");
                filmList.rating_vote_avg = jsonObject.getString("rating_vote_avg");
                filmList.made = jsonObject.getString("made");
                filmList.count_torrents = jsonObject.getString("count_torrents");
                filmList.category = jsonObject.getString("category");
                filmList.views = jsonObject.getString("views");
                filmList.views_month = jsonObject.getString("views_month");
                filmList.mpaa = jsonObject.getString("mpaa");
                filmList.serial_views = jsonObject.getString("serial_views");
                filmList.video_views = jsonObject.getString("video_views");
                filmList.url = jsonObject.getString("url");
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
        return arrayList;
    }
}
////////////////////////////////////////////////////////////////////////


class DownloadString implements Callable<String> {

    String str;

    DownloadString(String str) {
        this.str = str;
    }

    public String call() throws Exception {
        final String TAG = "DataDownloaderXML";
        StringBuilder result = new StringBuilder();
        URL url = null;
        HttpURLConnection connection;
        Log.d(TAG, "doInBackground: " + "start downloading");
        try {
            url = new URL(Constants.BASE_URL + str);
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
        Log.d(TAG, "doInBackground: " + "finish downloading");

        return result.toString();
    }
}


class GEtAllData implements Callable<ArrayList<FilmList>> {
    String str;
    //
    //класс в котором выполняется запрос
    //
    GEtAllData(String str) {
        this.str = str;
    }

    public ArrayList<FilmList> call() throws Exception {

        float start = SystemClock.elapsedRealtime();
        final String TAG = "DataDownloaderXML";
        StringBuilder result = new StringBuilder();
        URL url = null;
        HttpURLConnection connection;
        Log.d(TAG, "call: " + "start downloading");
        try {
            url = new URL(Constants.BASE_URL + str);
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
                //а если jsonObject зовернуть в массив и потом разобрать как ниже сделано
                //также завернуть массив с filmlist  и обращаться к ним по номерам

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
}

class GetJSONObjectArray implements Callable<ArrayList<JSONObject>> {
    String str;

    GetJSONObjectArray(String url) {
        this.str = url;
    }

    @Override
    public ArrayList<JSONObject> call() throws Exception {
        float start = SystemClock.elapsedRealtime();
        final String TAG = "DataDownloaderXML";
        StringBuilder result = new StringBuilder();
        URL url = null;
        HttpURLConnection connection;
        Log.d(TAG, "call: " + "start downloading");
        try {
            url = new URL(Constants.BASE_URL + str);
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
        Log.d(TAG, "call: " + "finish downloading");

        //////////////////////////////////////////////////////////////

        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
        ArrayList<FilmList> arrayList = new ArrayList<>();
        JSONObject jsonRoot;
        FilmList filmList = new FilmList();
        try {
            jsonRoot = new JSONObject(result.toString());
            JSONArray jsonArray = jsonRoot.getJSONArray("results");
            //получаем массив объектов JSONObject
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObjectArrayList.add(new JSONObject(jsonArray.getString(i)));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        float endtime = SystemClock.elapsedRealtime() - start;
        Log.d(TAG, "call: elapsed time " + endtime);
        return jsonObjectArrayList;
    }
}

