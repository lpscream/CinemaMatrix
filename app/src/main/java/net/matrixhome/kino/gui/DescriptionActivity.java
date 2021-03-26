package net.matrixhome.kino.gui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.matrixhome.kino.R;
import net.matrixhome.kino.data.DataLoaderXML;
import net.matrixhome.kino.data.FilmList;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class DescriptionActivity extends AppCompatActivity {


    private final String TAG = "DescriptonActivity_log";
    private String name, description, id, cover, videoLink, year, serial_name;
    private TextView nameTV, yearTV, descriptionTV, countryTV, directorTV, actorsTV, genresTV, ageTV, ratingTV, translateTV, translateDscrTV, qualityTV, qualityTVDscrp, serialSign, seasonSign;
    private ImageView coverView;
    private Button playVideoBtn, downloadBtn;
    private ArrayList<FilmList> filmLists;
    private FilmList filmList;
    private int currentSeasonNumber;

    private String currentSeasonID, currentEpisodeNumber, seriesCount;


    private Animator mCurrentAnimator;
    private int shortAnimationDuration;
    private ImageView expandedImageView;


    private ImageButton btnPreviousSeason, btnNextSeason, btnPreviousEpisode, btnNextEpisode;
    private TextView episodeNumber, seasonNumber;

    ////serial describer

    private LinearLayout serialDescriber;
    private ArrayList<FilmList> serialList;
    private DataLoaderXML dataLoaderXML;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        //DataLoaderXML loader = new DataLoaderXML(3);
        filmLists = new ArrayList<>();
        serialList = new ArrayList<>();
        currentSeasonID = "";
        currentEpisodeNumber = "";
        seriesCount = "";

        serialDescriber = findViewById(R.id.seasonSelectLayout);
        dataLoaderXML = new DataLoaderXML(3);

        seasonSign = findViewById(R.id.seasonSign);
        serialSign = findViewById(R.id.serialSign);

        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        expandedImageView = findViewById(
                R.id.expanded_image);

        nameTV = findViewById(R.id.filmNameDesActivity);
        yearTV = findViewById(R.id.filmYearDesActivity);
        descriptionTV = findViewById(R.id.descriptionTV);
        coverView = findViewById(R.id.imageView);
        countryTV = findViewById(R.id.filmCountryDesActivity);
        directorTV = findViewById(R.id.filmDirectorDesActivity);
        actorsTV = findViewById(R.id.filmActorsDesActivity);
        genresTV = findViewById(R.id.filmGenreDesActivity);
        playVideoBtn = findViewById(R.id.playVideoBtn);
        ageTV = findViewById(R.id.ageDiscTV);
        ratingTV = findViewById(R.id.ratingDiscTV);
        translateTV = findViewById(R.id.translateTV);
        translateDscrTV = findViewById(R.id.translateDscrTV);
        qualityTV = findViewById(R.id.isHD);
        qualityTVDscrp = findViewById(R.id.isHDDiscrp);

        seasonNumber = findViewById(R.id.seasonNumber);
        episodeNumber = findViewById(R.id.episodeNumber);

        btnNextEpisode = findViewById(R.id.btnNextEpisode);
        btnNextSeason = findViewById(R.id.btnNextSeason);
        btnPreviousEpisode = findViewById(R.id.btnPreviousEpisode);
        btnPreviousSeason = findViewById(R.id.btnPreviousSeason);

        /*downloadBtn = findViewById(R.id.downloadBtn);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filmDownload();
            }
        });*/

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();
        filmList = (FilmList) bundle.getSerializable("array");
        Log.d(TAG, "onCreate: from bundle " + filmList.age);
        if (filmList.serial_id.equalsIgnoreCase("null")) {
            //fill film description
            serialDescriber.setVisibility(View.GONE);
            id = intent.getStringExtra("id");
            serial_name = intent.getStringExtra("serial_name");
            name = intent.getStringExtra("name");
            description = intent.getStringExtra("description");
            year = intent.getStringExtra("year");
            if (!getScreenOrientation()) {
                year = year.concat(" (Премьера: " + filmList.date_premiere + ")");
            }
            cover = intent.getStringExtra("cover");
            countryTV.setText(intent.getStringExtra("country"));
            directorTV.setText(intent.getStringExtra("director"));
            genresTV.setText(intent.getStringExtra("genres"));
            actorsTV.setText(intent.getStringExtra("actors"));
            Picasso.get().load(cover).transform(new RoundedCornersTransformation(30, 0)).resize(700, 1000).into(coverView);
            Picasso.get().load(cover).transform(
                    new RoundedCornersTransformation(30, 0))
                    .into(expandedImageView);
            ageTV.setText(filmList.age);
            ratingTV.setText(filmList.rating);
            nameTV.setText(name + " (" + filmList.original_name + ")");
            yearTV.setText(year);
            descriptionTV.setText(intent.getStringExtra("description"));
            if (filmList.translate != "")
                translateTV.setText(filmList.translate);
            else {
                translateDscrTV.setVisibility(View.GONE);
                translateDscrTV.setVisibility(View.GONE);
                translateTV.setPadding(0, 0, 0, 0);
            }
            if (filmList.hd == 1)
                qualityTV.setText("HD");
            else {
                qualityTV.setVisibility(View.GONE);
                qualityTVDscrp.setVisibility(View.GONE);
            }
        } else {
            //заполняем атрибуты сериала  при старте активити

            //current season ID
            currentSeasonID = "1";
            //current episode number
            currentEpisodeNumber = "1";
            //current number of season
            currentSeasonNumber = 1;

            String str = dataLoaderXML.getList("?action=video&serial_id=" + filmList.serial_id);
            serialList = dataLoaderXML.parseSerialJSON(str);

            //start serial description
            for (int i = 0; i < serialList.size(); i++) {
                if (serialList.get(i).season_number.equalsIgnoreCase(String.valueOf(1))) {
                    //new serial atributes
                    seriesCount = String.valueOf(serialList.get(i).series.size());
                    Log.d(TAG, "onNumberPicked: series: " + serialList.get(i).series.size());
                    Log.d(TAG, "onNumberPicked: " + serialList.get(i).cover);
                    Log.d(TAG, "onNumberPicked: " + serialList.get(i).name);
                    Picasso.get().load(serialList.get(i).cover_200).transform(new RoundedCornersTransformation(30, 0)).resize(700, 1000).into(coverView);
                    Picasso.get().load(serialList.get(i).cover).into(expandedImageView);
                    nameTV.setText(serialList.get(i).name + " (" + serialList.get(i).serial_o_name + ")");
                    descriptionTV.setText(serialList.get(i).description);
                    yearTV.setText(serialList.get(i).year);
                    countryTV.setText(serialList.get(i).country);
                    directorTV.setText(serialList.get(i).director);
                    genresTV.setText(serialList.get(i).genres);
                    actorsTV.setText(serialList.get(i).actors);
                    ageTV.setText(serialList.get(i).age);
                    ratingTV.setText(serialList.get(i).rating);
                    currentSeasonID = serialList.get(i).id;
                    currentEpisodeNumber = "1";
                    if (filmList.translate != "")
                        translateTV.setText(filmList.translate);
                    else {
                        translateDscrTV.setVisibility(View.GONE);
                        translateDscrTV.setVisibility(View.GONE);
                        translateTV.setPadding(0, 0, 0, 0);
                    }
                    if (filmList.hd == 1)
                        qualityTV.setText("HD");
                    else {
                        qualityTV.setVisibility(View.GONE);
                        qualityTVDscrp.setVisibility(View.GONE);
                    }
                }
            }
        }

        coverView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(coverView);
            }
        });


        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandedImageView.setVisibility(View.GONE);
            }
        });

        btnPreviousSeason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: currentSeasonNumber " + currentSeasonNumber);
                if (currentSeasonNumber - 1 > 0) {
                    currentSeasonNumber = currentSeasonNumber - 1;
                    for (int i = 0; i < serialList.size(); i++) {
                        if (serialList.get(i).season_number.equalsIgnoreCase(String.valueOf(currentSeasonNumber))) {
                            Picasso.get().load(serialList.get(i).cover_200).transform(
                                    new RoundedCornersTransformation(30, 0))
                                    .resize(700, 1000).into(coverView);
                            Picasso.get().load(serialList.get(i).cover).into(expandedImageView);
                            nameTV.setText(serialList.get(i).name + " (" + serialList.get(i).serial_o_name + ")");
                            descriptionTV.setText(serialList.get(i).description);
                            yearTV.setText(serialList.get(i).year);
                            countryTV.setText(serialList.get(i).country);
                            directorTV.setText(serialList.get(i).director);
                            genresTV.setText(serialList.get(i).genres);
                            actorsTV.setText(serialList.get(i).actors);
                            ageTV.setText(serialList.get(i).age);
                            ratingTV.setText(serialList.get(i).rating);
                            seasonNumber.setText(String.valueOf(currentSeasonNumber));
                            currentSeasonID = serialList.get(i).id;
                            seriesCount = String.valueOf(serialList.get(i).series.size());
                            name = serialList.get(i).name;
                            episodeNumber.setText("1");
                            currentEpisodeNumber = "1";
                        }
                    }
                }
            }
        });

        btnNextSeason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSeasonNumber + 1 <= serialList.size()) {
                    currentSeasonNumber = currentSeasonNumber + 1;
                    Log.d(TAG, "onClick: " + currentSeasonNumber + 1);
                    for (int i = 0; i < serialList.size(); i++) {
                        Log.d(TAG, "onClick: 3");
                        if (serialList.get(i).season_number.equalsIgnoreCase(String.valueOf(currentSeasonNumber))) {
                            Log.d(TAG, "onClick: 4");
                            Picasso.get().load(serialList.get(i).cover_200).transform(
                                    new RoundedCornersTransformation(30, 0))
                                    .resize(700, 1000).into(coverView);
                            Picasso.get().load(serialList.get(i).cover).into(expandedImageView);
                            nameTV.setText(serialList.get(i).name + " (" + serialList.get(i).serial_o_name + ")");
                            descriptionTV.setText(serialList.get(i).description);
                            yearTV.setText(serialList.get(i).year);
                            countryTV.setText(serialList.get(i).country);
                            directorTV.setText(serialList.get(i).director);
                            genresTV.setText(serialList.get(i).genres);
                            actorsTV.setText(serialList.get(i).actors);
                            ageTV.setText(serialList.get(i).age);
                            ratingTV.setText(serialList.get(i).rating);
                            seasonNumber.setText(String.valueOf(currentSeasonNumber));
                            seriesCount = String.valueOf(serialList.get(i).series.size());
                            episodeNumber.setText("1");
                            name = serialList.get(i).name;
                            currentEpisodeNumber = "1";
                            currentSeasonID = serialList.get(i).id;
                        }
                    }
                }
            }
        });

        btnNextEpisode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(currentEpisodeNumber) + 1 <= Integer.parseInt(seriesCount)) {
                    currentEpisodeNumber = String.valueOf(Integer.parseInt(currentEpisodeNumber) + 1);
                    episodeNumber.setText(currentEpisodeNumber);
                }
            }
        });

        btnPreviousEpisode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(currentEpisodeNumber) - 1 > 0) {
                    currentEpisodeNumber = String.valueOf(Integer.parseInt(currentEpisodeNumber) - 1);
                    episodeNumber.setText(currentEpisodeNumber);
                }
            }
        });


        //intent.getStringArrayExtra("array");

        //TODO what a fuck!!!!
        if (intent.getBooleanExtra("isSerial", false)) {
            //nameTV.setText(serial_name + " (" + filmList.serial_o_name + ")");
        } else {

        }


        videoLink = dataLoaderXML.getLinkByID(id);
        playVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent.getBooleanExtra("isSerial", false)) {
                    Intent intent = new Intent(getApplicationContext(), VideoPlayer.class);
                    intent.putExtra("link", dataLoaderXML.getSerialLinkByID(currentSeasonID, currentEpisodeNumber));
                    intent.putExtra("currentSeasonNumber", currentSeasonID);
                    intent.putExtra("currentEpisodeNumber", currentEpisodeNumber);
                    intent.putExtra("seriesCount", seriesCount);
                    intent.putExtra("isSerial", true);
                    intent.putExtra("name", nameTV.getText().toString());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), VideoPlayer.class);
                    intent.putExtra("link", dataLoaderXML.getLinkByID(id));
                    intent.putExtra("name", name);
                    startActivity(intent);
                    Log.d(TAG, "onClick: trying start video activity");
                }
            }
        });
        playVideoBtn.requestFocus();


        //filmDownload();
        Log.d(TAG, "onCreate: ");
    }

    private void setSerialAttributes(int episodeNum) {
        for (int i = 0; i < serialList.size(); i++) {
            if (serialList.get(i).season_number.equalsIgnoreCase(String.valueOf(episodeNum))) {
                //new serial atributes
                seriesCount = String.valueOf(serialList.get(i).series.size());
                Log.d(TAG, "onNumberPicked: series: " + serialList.get(i).series.size());
                Log.d(TAG, "onNumberPicked: " + serialList.get(i).cover);
                Log.d(TAG, "onNumberPicked: " + serialList.get(i).name);
                Picasso.get().load(serialList.get(i).cover).transform(new RoundedCornersTransformation(30, 0)).resize(700, 1000).into(coverView);
                nameTV.setText(serialList.get(i).name + " (" + serialList.get(i).serial_o_name + ")");
                descriptionTV.setText(serialList.get(i).description);
                yearTV.setText(serialList.get(i).year);
                countryTV.setText(serialList.get(i).country);
                directorTV.setText(serialList.get(i).director);
                genresTV.setText(serialList.get(i).genres);
                actorsTV.setText(serialList.get(i).actors);
                ageTV.setText(serialList.get(i).age);
                ratingTV.setText(serialList.get(i).rating);
                currentSeasonID = serialList.get(i).id;
                currentEpisodeNumber = String.valueOf(episodeNum);
                if (serialList.get(i).translate != "") {
                    translateDscrTV.setVisibility(View.VISIBLE);
                    translateTV.setPadding(0, 0, 0, 0);//???????maybe something need to change
                    translateTV.setText(serialList.get(i).translate);
                } else {
                    translateDscrTV.setVisibility(View.GONE);
                    translateTV.setPadding(0, 0, 0, 0);
                }
                if (serialList.get(i).hd == 1) {
                    qualityTV.setVisibility(View.VISIBLE);
                    qualityTV.setText("HD");
                } else {
                    qualityTV.setVisibility(View.GONE);
                    qualityTVDscrp.setVisibility(View.GONE);
                }
            }
        }
    }

    private void filmDownload() {
        DownloadManager.Request request =
                new DownloadManager.Request(Uri.parse(videoLink))
                        .setTitle(filmList.name)
                        .setDescription("Загрузка")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filmList.name)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        Toast.makeText(this, "Загрузка началась", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private boolean getScreenOrientation() {
        boolean screenState = false;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            screenState = true;
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenState = false;
        }
        return screenState;
    }

    private void zoomImageFromThumb(final View thumbView) {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();


            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(500);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

}