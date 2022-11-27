package com.example.seatreservation.Seat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.seatreservation.R;

public class Seat_Layout_Size extends Activity {
    EditText width_et, height_et;
    Button okBtn, cancelBtn;
    int getWidth, getHeight;

    int finalWidth;
    int finalHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.seat_layout_size);

        width_et = findViewById(R.id.width_et);
        height_et = findViewById(R.id.height_et);
        okBtn = findViewById(R.id.ok_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        Intent intent = getIntent();
        getWidth = intent.getIntExtra("width", 0);
        getHeight = intent.getIntExtra("height", 0);

        width_et.setText(String.valueOf(getWidth));
        height_et.setText(String.valueOf(getHeight));

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(width_et.getText().toString().equals("")){
                    finalWidth = 0;
                } if(height_et.getText().toString().equals("")){
                    finalHeight = 0;
                } else {
                    finalWidth = Integer.parseInt(width_et.getText().toString());
                    finalHeight = Integer.parseInt(height_et.getText().toString());
                }

                if(finalWidth < 1000){
                    Toast.makeText(getApplicationContext(), "가로 길이는 1000(cm) 이상이어야 합니다", Toast.LENGTH_SHORT).show();
                } if (finalHeight < 1000) {
                    Toast.makeText(getApplicationContext(), "세로 길이는 1000(cm) 이상이어야 합니다", Toast.LENGTH_SHORT).show();
                } else if(finalWidth >= 1000 && finalHeight >=1000){
                    Intent i = new Intent(getApplicationContext(), SeatEdit.class);
                    i.putExtra("width", finalWidth);
                    i.putExtra("height", finalHeight);

                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
