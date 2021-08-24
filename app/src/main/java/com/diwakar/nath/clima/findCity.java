package com.diwakar.nath.clima;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class findCity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_city);

        EditText editText = (EditText) findViewById(R.id.searchCity);
        ImageView back = (ImageView) findViewById(R.id.back);

        // going back to main activity through back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // this will automatically go back to MainActivity
                // Intent mIntent = new Intent(findCity.this, MainActivity.class);
                // startActivity(mIntent);
            }
        });

        // extracting city from the editText and putting in the intent
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                final String cityName = editText.getText().toString().toLowerCase();
                Intent intent = new Intent(findCity.this, MainActivity.class);
                intent.putExtra("cityName", cityName);
                startActivity(intent);
                return false;
            }
        });
    }
}