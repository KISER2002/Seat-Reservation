package com.example.seatreservation.Seat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seatreservation.R;

public class AddItemDialog extends Activity {
    TextView item_name_tv, width_tv, width_cm;
    EditText item_name_et, width_et, height_et, angle_et;
    Button leftBtn, bottomBtn, rightBtn, topBtn, okBtn, cancelBtn;
    int getWidth, getHeight;

    int finalWidth = 100;
    int finalHeight = 100;
    int finalAngle = 0;

    RadioGroup radioGroup;

    private String selected = "custom";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_item);

        item_name_tv = findViewById(R.id.item_name_tv);
        width_tv = findViewById(R.id.width_tv);
        width_cm = findViewById(R.id.width_cm);

        item_name_et = findViewById(R.id.item_name_et);
        width_et = findViewById(R.id.width_et);
        height_et = findViewById(R.id.height_et);
        angle_et = findViewById(R.id.angle_et);

        leftBtn = findViewById(R.id.button_left);
        bottomBtn = findViewById(R.id.button_bottom);
        rightBtn = findViewById(R.id.button_right);
        topBtn = findViewById(R.id.button_top);

        okBtn = findViewById(R.id.ok_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        radioGroup = findViewById(R.id.radio_group);

        Intent intent = getIntent();
        getWidth = intent.getIntExtra("width", 0);
        getHeight = intent.getIntExtra("height", 0);

        width_et.setText(String.valueOf(finalWidth));
        height_et.setText(String.valueOf(finalHeight));
        angle_et.setText(String.valueOf(finalAngle));

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angle_et.setText("0");
            }
        });
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angle_et.setText("90");
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angle_et.setText("180");
            }
        });
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angle_et.setText("270");
            }
        });

        radioGroup.check(R.id.radio_button_custom);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_button_custom:
                        selected = "custom";
                        item_name_tv.setVisibility(View.VISIBLE);
                        item_name_et.setVisibility(View.VISIBLE);
                        width_tv.setVisibility(View.VISIBLE);
                        width_et.setVisibility(View.VISIBLE);
                        width_cm.setVisibility(View.VISIBLE);
                        leftBtn.setVisibility(View.GONE);
                        bottomBtn.setVisibility(View.GONE);
                        rightBtn.setVisibility(View.GONE);
                        topBtn.setVisibility(View.GONE);
                        break;
                    case R.id.radio_button_xbox:
                        selected = "xbox";
                        item_name_tv.setVisibility(View.GONE);
                        item_name_et.setVisibility(View.GONE);
                        width_tv.setVisibility(View.VISIBLE);
                        width_et.setVisibility(View.VISIBLE);
                        width_cm.setVisibility(View.VISIBLE);
                        leftBtn.setVisibility(View.GONE);
                        bottomBtn.setVisibility(View.GONE);
                        rightBtn.setVisibility(View.GONE);
                        topBtn.setVisibility(View.GONE);
                        break;
                    case R.id.radio_button_arrow:
                        selected = "arrow";
                        item_name_tv.setVisibility(View.GONE);
                        item_name_et.setVisibility(View.GONE);
                        width_tv.setVisibility(View.VISIBLE);
                        width_et.setVisibility(View.VISIBLE);
                        width_cm.setVisibility(View.VISIBLE);
                        leftBtn.setVisibility(View.VISIBLE);
                        bottomBtn.setVisibility(View.VISIBLE);
                        rightBtn.setVisibility(View.VISIBLE);
                        topBtn.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radio_button_seat:
                        selected = "chair";
                        item_name_tv.setVisibility(View.GONE);
                        item_name_et.setVisibility(View.GONE);
                        width_tv.setVisibility(View.GONE);
                        width_et.setVisibility(View.GONE);
                        width_cm.setVisibility(View.GONE);
                        leftBtn.setVisibility(View.VISIBLE);
                        bottomBtn.setVisibility(View.VISIBLE);
                        rightBtn.setVisibility(View.VISIBLE);
                        topBtn.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item_name = item_name_et.getText().toString();
                if(selected.equals("custom") && item_name.equals("")) {
                    Toast.makeText(getApplicationContext(), "아이템에 들어갈 내용을 입력하세요." , Toast.LENGTH_SHORT).show();
                    return;
                } else{

                }

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
                } if (finalWidth < 50) {
                    Toast.makeText(getApplicationContext(), "좌석의 최소 가로 길이는 50cm 입니다", Toast.LENGTH_SHORT).show();
                } if (finalHeight > getHeight) {
                    Toast.makeText(getApplicationContext(), "레이아웃의 세로 길이보다 길 수 없습니다.\n 현재 레이아웃의 세로 길이 : " + getHeight + "dp", Toast.LENGTH_SHORT).show();
                } if (finalHeight < 50) {
                    Toast.makeText(getApplicationContext(), "좌석의 최소 세로 길이는 50cm 입니다", Toast.LENGTH_SHORT).show();
                } if (finalAngle > 359) {
                    Toast.makeText(getApplicationContext(), "좌석의 회전 범위는 0~359도 까지 입니다.", Toast.LENGTH_SHORT).show();
                } else if(finalWidth <= getWidth && finalWidth >= 50 && finalHeight <=getHeight && finalHeight >= 50 && finalAngle <= 359){
                    if(selected.equals("chair")){
                        finalWidth = finalHeight * 3/10;
                    }

                        Intent i = new Intent(getApplicationContext(), SeatEdit.class);
                        i.putExtra("width", finalWidth);
                        i.putExtra("height", finalHeight);
                        i.putExtra("angle", finalAngle);
                        i.putExtra("type", selected);
                        i.putExtra("item_name", item_name);

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
