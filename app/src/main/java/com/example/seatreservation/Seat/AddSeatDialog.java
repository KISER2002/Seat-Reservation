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

public class AddSeatDialog extends Activity {
    EditText width_et, height_et, angle_et;
    Button okBtn, cancelBtn;
    int getWidth, getHeight;

    int finalWidth = 100;
    int finalHeight = 100;
    int finalAngle = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_seat_dialog);

        width_et = findViewById(R.id.width_et);
        height_et = findViewById(R.id.height_et);
        angle_et = findViewById(R.id.angle_et);
        okBtn = findViewById(R.id.ok_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        Intent intent = getIntent();
        getWidth = intent.getIntExtra("width", 0);
        getHeight = intent.getIntExtra("height", 0);

        width_et.setText(String.valueOf(finalWidth));
        height_et.setText(String.valueOf(finalHeight));
        angle_et.setText(String.valueOf(finalAngle));

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(width_et.getText().toString().equals("")){
                    finalWidth = 0;
                } if(height_et.getText().toString().equals("")){
                    finalHeight = 0;
                } if(angle_et.getText().toString().equals("")){
                    finalAngle = 0;
                } else {
                    finalWidth = Integer.parseInt(width_et.getText().toString());
                    finalHeight = Integer.parseInt(height_et.getText().toString());
                    finalAngle = Integer.parseInt(angle_et.getText().toString());
                }

                if(finalWidth > getWidth){
                    Toast.makeText(getApplicationContext(), "레이아웃의 가로 길이보다 길 수 없습니다.\n 현재 레이아웃의 가로 길이 : " + getWidth + "dp" , Toast.LENGTH_SHORT).show();
                } if (finalWidth < 100) {
                    Toast.makeText(getApplicationContext(), "좌석의 최소 가로 길이는 100cm 입니다", Toast.LENGTH_SHORT).show();
                } if (finalHeight > getHeight) {
                    Toast.makeText(getApplicationContext(), "레이아웃의 세로 길이보다 길 수 없습니다.\n 현재 레이아웃의 세로 길이 : " + getHeight + "dp", Toast.LENGTH_SHORT).show();
                } if (finalHeight < 100) {
                    Toast.makeText(getApplicationContext(), "좌석의 최소 세로 길이는 100cm 입니다", Toast.LENGTH_SHORT).show();
                } if (finalAngle > 359) {
                    Toast.makeText(getApplicationContext(), "좌석의 회전 범위는 0~359도 까지 입니다.", Toast.LENGTH_SHORT).show();
                } else if(finalWidth <= getWidth && finalWidth >= 100 && finalHeight <=getHeight && finalHeight >= 100 && finalAngle <= 359){
                    Intent i = new Intent(getApplicationContext(), SeatEdit.class);
                    i.putExtra("width", finalWidth);
                    i.putExtra("height", finalHeight);
                    i.putExtra("angle", finalAngle);

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

    @Override
    public void onBackPressed(){

    }
}
