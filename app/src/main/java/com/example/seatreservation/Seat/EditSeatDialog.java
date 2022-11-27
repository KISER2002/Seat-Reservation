package com.example.seatreservation.Seat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seatreservation.R;

public class EditSeatDialog extends Activity {
    TextView title_tv;
    EditText width_et, height_et, angle_et;
    Button okBtn, cancelBtn;
    int getId, getWidth, getHeight, getAngle, getLayoutWidth, getLayoutHeight;
    float getX, getY;
    String getNum;

    int finalWidth;
    int finalHeight;
    int finalAngle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_seat_dialog);

        title_tv = findViewById(R.id.title_tv);
        title_tv.setText("좌석 수정");

        width_et = findViewById(R.id.width_et);
        height_et = findViewById(R.id.height_et);
        angle_et = findViewById(R.id.angle_et);
        okBtn = findViewById(R.id.ok_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        Intent intent = getIntent();
        getId = intent.getIntExtra("seatId", 0);
        getNum = intent.getStringExtra("seatNum");
        getX = intent.getFloatExtra("seatX", 0);
        getY = intent.getFloatExtra("seatY", 0);
        getWidth = intent.getIntExtra("width", 0);
        getHeight = intent.getIntExtra("height", 0);
        getAngle = intent.getIntExtra("angle", 0);
        getLayoutWidth = intent.getIntExtra("layout_width", 0);
        getLayoutHeight = intent.getIntExtra("layout_height", 0);

        width_et.setText(String.valueOf(getWidth));
        height_et.setText(String.valueOf(getHeight));
        angle_et.setText(String.valueOf(getAngle));

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

                if(finalWidth > getLayoutWidth){
                    Toast.makeText(getApplicationContext(), "레이아웃의 가로 길이보다 길 수 없습니다.\n 현재 레이아웃의 가로 길이 : " + getLayoutWidth + "dp" , Toast.LENGTH_SHORT).show();
                } if (finalWidth < 100) {
                    Toast.makeText(getApplicationContext(), "좌석의 최소 가로 길이는 100cm 입니다", Toast.LENGTH_SHORT).show();
                } if (finalHeight > getLayoutHeight) {
                    Toast.makeText(getApplicationContext(), "레이아웃의 세로 길이보다 길 수 없습니다.\n 현재 레이아웃의 세로 길이 : " + getLayoutHeight + "dp", Toast.LENGTH_SHORT).show();
                } if (finalHeight < 100) {
                    Toast.makeText(getApplicationContext(), "좌석의 최소 세로 길이는 100cm 입니다", Toast.LENGTH_SHORT).show();
                } if (finalAngle > 359) {
                    Toast.makeText(getApplicationContext(), "좌석의 회전 범위는 0~359도 까지 입니다.", Toast.LENGTH_SHORT).show();
                } else if(finalWidth <= getLayoutWidth && finalWidth >= 100 && finalHeight <=getLayoutHeight && finalHeight >= 100 && finalAngle <= 359){
                    Intent i = new Intent(getApplicationContext(), SeatEdit.class);
                    i.putExtra("seatId", getId);
                    i.putExtra("seatNum", getNum);
                    i.putExtra("seatX", getX);
                    i.putExtra("seatY", getY);
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
