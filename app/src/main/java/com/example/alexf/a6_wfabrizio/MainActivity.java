package com.example.alexf.a6_wfabrizio;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DBHelper myDBHelper;
    SQLiteDatabase db;
    String txt;
    Intent myIntent;

    ListView myList;
    Spinner mySpinner;
    ArrayAdapter<String> myListAdapter;
    ArrayAdapter<String> mySpinnerAdapter;

    EditText searchText;
    Button searchBtn;
    TextView noResult;

    ArrayList<String> mNameList;
    ArrayList<Integer> mDateList;
    ArrayList<String> mDetailsList;
    ArrayList<String> mImgList;
    ArrayList<Integer>mIdList;
    ArrayList<Float> mReviewList;
    ArrayList<String> mRatingList;
    int arrayIndex = -1;
    Boolean first = true;



    String[] categoryArr ={"All", "Sort by Rating", "Sort by Date", "Sort By Stars", "Sort Alphabetically", "None"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myIntent = new Intent(this, Movie_Details.class);
        final String allQuary = "Select * from movies";


        myList = findViewById(R.id.myList);
        mySpinner = findViewById(R.id.mySpinner);
        searchBtn = findViewById(R.id.searchBtn);
        searchText = findViewById(R.id.searchText);
        noResult = findViewById(R.id.noResult);
        noResult.setVisibility(View.GONE);

        mySpinnerAdapter=new ArrayAdapter<String>(this, R.layout.spinner_item, categoryArr);
        mySpinner.setAdapter(mySpinnerAdapter);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myIntent.putExtra("movieId", mIdList.get(position));
                myIntent.putExtra("movieName", mNameList.get(position));
                myIntent.putExtra("movieDate", mDateList.get(position));
                myIntent.putExtra("movieDetails", mDetailsList.get(position));
                myIntent.putExtra("movieRating", mRatingList.get(position));
                myIntent.putExtra("movieReview", mReviewList.get(position));
                myIntent.putExtra("movieImage", mImgList.get(position));
                arrayIndex = position;
                MainActivity.this.startActivityForResult(myIntent, 123);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt = searchText.getText().toString();
                String searchQuery = "select * from movies where " + "title like '%" + txt + "%';";
                getResult(searchQuery);

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                mySpinner.setSelection(5);
            }
        });

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cat = categoryArr[position];
                String catQuery = allQuary;
                if (position != 0 && position !=5){
                    switch (position){
                        case 1:
                            cat = "rating";
                            break;
                        case 2:
                            cat = "date desc";
                            break;
                        case 3:
                            cat = "review desc";
                            break;
                        case 4:
                            cat = "title";
                            break;
                    }
                    catQuery = "Select * from movies order by " + cat + " ;";

                }if (first) first=false;
                if (!first && position !=5) getResult(catQuery);
                if (position!=5) searchText.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        db = myDBHelper.getReadableDatabase();


        getResult(allQuary);
    }
    public void getResult(String q){
        Cursor result = db.rawQuery(q, null);
        result.moveToFirst();
        int count = result.getCount();
        Log.i("count=", String.valueOf(count));
        mNameList = new ArrayList<>();
        mDateList = new ArrayList<>();
        mDetailsList = new ArrayList<>();
        mImgList = new ArrayList<>();
        mIdList = new ArrayList<>();
        mReviewList = new ArrayList<>();
        mRatingList = new ArrayList<>();

        if (count >= 1){
            //I have results
            noResult.setVisibility(View.GONE);
            do{
                mIdList.add(result.getInt(0));
                mNameList.add(result.getString(1));
                mDateList.add(result.getInt(2));

                mImgList.add(result.getString(3));
                mDetailsList.add(result.getString(4));
                mRatingList.add(result.getString(5));

                mReviewList.add(result.getFloat(6));
            }while (result.moveToNext());

        } else {
            //no results
            noResult.setVisibility(View.VISIBLE);
        }
        myListAdapter = new ArrayAdapter<String>(this, R.layout.list_item, mNameList);
        myList.setAdapter(myListAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Float newRDate;

        if (requestCode == 123){
            if (requestCode==RESULT_OK){
                newRDate = data.getFloatExtra("newRating", 0);

                mReviewList.set(arrayIndex, newRDate);
            }
        }
    }
}
