package com.example.alexf.a6_wfabrizio;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.IOException;

public class Movie_Details extends AppCompatActivity {

    DBHelper myDBHelper;
    SQLiteDatabase db;

    TextView movieName;
    TextView movingRatingTV;
    TextView movieDateTV;


    ImageView movieImg;
    TextView movieDetailsTv;
    RatingBar movieRatingBar;
    Button rateBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie__details);

        Intent myIntent = getIntent();
        final Intent sentToMain = new Intent(this, MainActivity.class);

        final int movieId = myIntent.getIntExtra("movieId", 0);
        String movieNameString=myIntent.getStringExtra("movieName");
        String MovieImgSte=myIntent.getStringExtra("movieImage");
        final int movieDate=myIntent.getIntExtra("movieDate", 0);
        final Float movieReview = myIntent.getFloatExtra("movieReview", 0);
        String movieDetail = myIntent.getStringExtra("movieDetails");
        String movieRating = myIntent.getStringExtra("movieRating");

        movieName = findViewById(R.id.movieNameTV);
        movieImg = findViewById(R.id.movieImg);
        movieDateTV = findViewById(R.id.dateTV);
        movingRatingTV = findViewById(R.id.ratingTV);
        movieRatingBar = findViewById(R.id.ratingBar);
        rateBtn = findViewById(R.id.rateBtn);
        movieDetailsTv = findViewById(R.id.movieDetailsTV);

        String extension = "";
        int i = MovieImgSte.lastIndexOf(".");
        if (i > 0){
            extension = MovieImgSte.substring(i);
        }
        MovieImgSte = MovieImgSte.replace(extension, "");
        Log.i("movieImg=", MovieImgSte);
        int id = getResources().getIdentifier(getPackageName()+":drawable/"+MovieImgSte, null, null);
        movieImg.setImageResource(id);



        movieName.setText(movieNameString);
        movieDateTV.setText(Integer.toString(movieDate));
        movingRatingTV.setText(movieRating);

        movieDetailsTv.setText(movieDetail);
        movieRatingBar.setRating(movieReview);

        createDB();
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float r = movieRatingBar.getRating();
                String updateQuery="";
                if (movieId != 0) {
                    updateQuery = "update movies set review= " + r + " where id =" + movieId + ";";
                }
                db.execSQL(updateQuery);

                sentToMain.putExtra("newRating", r);
                setResult(RESULT_OK, sentToMain);
                Movie_Details.this.startActivity(sentToMain);

            }
        });




    }
    private void createDB(){
        myDBHelper = new DBHelper(this);

        try {
            myDBHelper.createDataBase();
        }   catch (IOException e){
            throw new Error("Unable to Connect");
        }
        try {
            myDBHelper.openDataBase();
        }   catch (SQLException sql){

        }
        db = myDBHelper.getWritableDatabase();
    }
}
