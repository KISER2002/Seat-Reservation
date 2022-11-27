package com.example.seatreservation.Seat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.seatreservation.R;
import com.example.seatreservation.Request.SeatEditRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SeatEdit extends Activity{
    private static String URL_LOADING = "http://3.34.45.193/LoadSeatLayout.php";

    private int REQUEST_LAYOUT_SIZE = 200;
    private int REQUEST_SEAT_SIZE = 201;
    private int REQUEST_SEAT_SIZE_EDIT = 202;
    private int REQUEST_ITEM_SIZE = 203;
    private int REQUEST_ITEM_SIZE_EDIT = 204;

    private ConstraintLayout dynamicLayout; // 버튼이 생성될 공간

    Button addTableBtn, removeBtn, saveBtn, addSeatBtn, addItemBtn, resetBtn; // 추가, 삭제 버튼
    ImageView heightControlBtn;
    View layout_end, layout_right_end;
    int layoutWidth = 1000;
    int layoutHeight = 1000;
    TextView officeName, widthTv, heightTv;
    private boolean delete_mode = false;

    private ArrayList<SeatLayout> seatLayout;
    private Integer numButton = 0; // 버튼의 개수
    private int position = -1;
    private String location, officeNameSt;
    float oldXvalue; // 드래그 앤 드랍을 위한 X좌표
    float oldYvalue; // 드래그 앤 드랍을 위한 Y좌표

    ScrollView myScroll;

    private PointF mCenter;
    private float mRadius;
    private float mMaxDist;

    private long btnPressTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_seat_edit);

        myScroll = findViewById(R.id.scroll);

        Intent intent = getIntent();
        location = intent.getStringExtra("id");
        officeNameSt = intent.getStringExtra("office_name");

        widthTv = findViewById(R.id.width_tv);
        heightTv = findViewById(R.id.height_tv);

        heightControlBtn = findViewById(R.id.height_control);
        layout_end = findViewById(R.id.layout_end);
        layout_right_end = findViewById(R.id.layout_right_end);

        addTableBtn = findViewById(R.id.addTableButton);
        removeBtn = findViewById(R.id.removeButton);
        saveBtn = findViewById(R.id.saveButton);
        addItemBtn = findViewById(R.id.addItemButton);
        resetBtn = findViewById(R.id.resetButton);

        heightControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeatEdit.this, Seat_Layout_Size.class);
                intent.putExtra("width", layoutWidth);
                intent.putExtra("height", layoutHeight);
                startActivityForResult(intent, 200);
            }
        });

        officeName = findViewById(R.id.office_name);
        dynamicLayout = findViewById(R.id.dynamicArea);

        officeName.setText("좌석 배치 (" + officeNameSt + ")");

        seatLayout = new ArrayList<SeatLayout>();

        Loading(location);
        
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SeatEdit.this);
                builder.setTitle("초기화").setMessage("좌석배치도를 초기화 하시겠습니까?")
                        .setPositiveButton("초기화", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jasonObject = new JSONObject(response);//SeatEdit php에 response
                                            boolean success = jasonObject.getBoolean("success");//SeatEdit php에 sucess
                                            if (success) {//저장에 성공한 경우
                                                Toast.makeText(getApplicationContext(), "배치도가 초기화 되었습니다.", Toast.LENGTH_SHORT).show();
                                                new Thread(new Runnable() {
                                                    public void run() {
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                            }
                                                        });
                                                    }
                                                }).start();
                                                finish();
                                            }
                                            else{//저장에 실패한 경우
                                                Toast.makeText(getApplicationContext(),"초기화에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                //서버로 volley 를 이용해서 요청을 함
                                SeatEditRequest seatEditRequest = new SeatEditRequest(location, "null", "null", "null", responseListener);
                                RequestQueue queue = Volley.newRequestQueue(SeatEdit.this);
                                queue.add(seatEditRequest);
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
            }
        });

        addTableBtn.setOnClickListener(new View.OnClickListener() { // 추가 버튼 클릭 시
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeatEdit.this, AddSeatDialog.class);
                intent.putExtra("width", layoutWidth);
                intent.putExtra("height", layoutHeight);
                startActivityForResult(intent, 201);
            }
        });

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeatEdit.this, AddItemDialog.class);
                intent.putExtra("width", layoutWidth);
                intent.putExtra("height", layoutHeight);
                startActivityForResult(intent, 203);
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() { // 삭제 버튼 클릭 시
            @Override
            public void onClick(View v) {
                if(!delete_mode){
                    delete_mode = true;
                    removeBtn.setText("편집모드 취소");
                    removeBtn.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.cancelBtn));
                } else if(delete_mode){
                    delete_mode = false;
                    removeBtn.setText("편집모드");
                    removeBtn.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.editBtn));
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String seatJsonArray = null;

                try {
                    JSONArray jArray = new JSONArray();//배열이 필요할때
                    for (int i = 0; i < seatLayout.size(); i++) {
                        JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                        sObject.put("idx", seatLayout.get(i).getIdx());
                        sObject.put("id", seatLayout.get(i).getId());
                        sObject.put("name", seatLayout.get(i).getName());
                        sObject.put("x", seatLayout.get(i).getX());
                        sObject.put("y", seatLayout.get(i).getY());
                        sObject.put("width", seatLayout.get(i).getWidth());
                        sObject.put("height", seatLayout.get(i).getHeight());
                        sObject.put("angle", seatLayout.get(i).getAngle());
                        jArray.put(sObject);
                    }

//                    Log.d("JSON Test", jArray.toString());
                    seatJsonArray = jArray.toString();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String layout_width = String.valueOf(layoutWidth);
                String layout_height = String.valueOf(layoutHeight);

                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);//SeatEdit php에 response
                            boolean success = jasonObject.getBoolean("success");//SeatEdit php에 sucess
                            if (success) {//저장에 성공한 경우
                                Toast.makeText(getApplicationContext(), "좌석 레이아웃이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                    new Thread(new Runnable() {
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                }
                                            });
                                        }
                                    }).start();
                                finish();
                            }
                            else{//저장에 실패한 경우
                                Toast.makeText(getApplicationContext(),"레이아웃 저장에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //서버로 volley 를 이용해서 요청을 함
                SeatEditRequest seatEditRequest = new SeatEditRequest(location, seatJsonArray, layout_width, layout_height, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SeatEdit.this);
                queue.add(seatEditRequest);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LAYOUT_SIZE) {
            if (resultCode == Activity.RESULT_OK) {
                int getWidth = data.getIntExtra("width", 1000);
                int getHeight = data.getIntExtra("height", 1000);

                int dpWidth = getWidth/2;
                int dpHeight = getHeight/2;

                final int marginWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpWidth, getResources().getDisplayMetrics());
                final int marginHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpHeight, getResources().getDisplayMetrics());

                ConstraintLayout.LayoutParams layoutParamsWidth = (ConstraintLayout.LayoutParams) layout_right_end.getLayoutParams();
                layoutParamsWidth.leftMargin = marginWidth;
                layout_right_end.setLayoutParams(layoutParamsWidth);

                ConstraintLayout.LayoutParams layoutParamsHeight = (ConstraintLayout.LayoutParams) layout_end.getLayoutParams();
                layoutParamsHeight.topMargin = marginHeight;
                layout_end.setLayoutParams(layoutParamsHeight);

                layoutWidth = getWidth;
                layoutHeight = getHeight;

                widthTv.setText("가로 길이 : " + getWidth + "cm");
                heightTv.setText("세로 길이 : " + getHeight + "cm");

                Toast.makeText(SeatEdit.this, "레이아웃 크기가 설정되었습니다.", Toast.LENGTH_SHORT).show();

            } else {   // RESULT_CANCEL
            }

        } if(requestCode == REQUEST_SEAT_SIZE){
            if(resultCode == Activity.RESULT_OK){
                int getWidth = data.getIntExtra("width", 100);
                int getHeight = data.getIntExtra("height", 100);
                int getAngle = data.getIntExtra("angle", 0);

                int dpWidth = getWidth/2;
                int dpHeight = getHeight/2;

                final int seatWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpWidth, getResources().getDisplayMetrics());
                final int seatHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpHeight, getResources().getDisplayMetrics());

                numButton++; // 버튼 추가할 때마다 버튼의 개수 1씩 증가
                position++;

                Button dynamicButton = new Button(this); // 새로운 버튼 생성
                dynamicButton.setId(position);
                dynamicButton.setText(String.valueOf(numButton)); // 버튼의 이름를 버튼의 개수로 표시
                dynamicButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                dynamicButton.setBackgroundResource(R.drawable.seat_border);
                dynamicButton.setX(0);
                dynamicButton.setY(0);
                dynamicButton.setWidth(seatWidth);
                dynamicButton.setHeight(seatHeight);
                dynamicButton.setRotation(getAngle);
                dynamicButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) { // 버튼을 드래그 앤 드랍하는 메소드(OnTouchListener)
                        int width = ((ViewGroup) v.getParent()).getWidth() - v.getWidth();
                        int height = ((ViewGroup) v.getParent()).getHeight() - v.getHeight();

                        myScroll.requestDisallowInterceptTouchEvent(true);

                        if(!delete_mode) {

                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                v.setRotation(0);
                                oldXvalue = event.getX();
                                oldYvalue = event.getY();
                            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                myScroll.requestDisallowInterceptTouchEvent(false);
                                if (v.getX() > width && v.getY() > height) {
                                    v.setX(width);
                                    v.setY(height);
                                } else if (v.getX() < 0 && v.getY() > height) {
                                    v.setX(0);
                                    v.setY(height);
                                } else if (v.getX() > width && v.getY() < 0) {
                                    v.setX(width);
                                    v.setY(0);
                                } else if (v.getX() < 0 && v.getY() < 0) {
                                    v.setX(0);
                                    v.setY(0);
                                } else if (v.getX() < 0 || v.getX() > width) {
                                    if (v.getX() < 0) {
                                        v.setX(0);
                                        v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                                    } else {
                                        v.setX(width);
                                        v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                                    }
                                } else if (v.getY() < 0 || v.getY() > height) {
                                    if (v.getY() < 0) {
                                        v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                        v.setY(0);
                                    } else {
                                        v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                        v.setY(height);
                                    }
                                }
                                v.setRotation(getAngle);

                                float layoutX = v.getX();
                                float layoutY = v.getY();

                                Button dynamicButton = findViewById(v.getId());
                                String id = dynamicButton.getText().toString();

                                SeatLayout seat_layout = new SeatLayout(String.valueOf(v.getId()), id, "seat", layoutX, layoutY, v.getWidth(), v.getHeight(), v.getRotation());

                                seatLayout.set(v.getId(), seat_layout);

                            }
                            return false;
                        }

                        return false;
                    }
                });

                dynamicButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button dynamicButton = findViewById(v.getId());
                        String number = dynamicButton.getText().toString();

                        if(delete_mode) {
                            Intent intent = new Intent(SeatEdit.this, EditSeatDialog.class);
                            intent.putExtra("seatId", v.getId());
                            intent.putExtra("seatNum", number);
                            intent.putExtra("seatX", v.getX());
                            intent.putExtra("seatY", v.getY());
                            intent.putExtra("width", getWidth);
                            intent.putExtra("height", getHeight);
                            intent.putExtra("angle", getAngle);
                            intent.putExtra("layout_width", layoutWidth);
                            intent.putExtra("layout_height", layoutHeight);
                            startActivityForResult(intent, 202);
                        }
                    }
                });

                dynamicButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Button dynamicButton = findViewById(v.getId());
                        String number = dynamicButton.getText().toString();

                        if (delete_mode) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SeatEdit.this);
                            builder.setTitle("삭제").setMessage("해당 아이템을 삭제하시겠습니까?")
                                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dynamicLayout.removeView(dynamicButton);
                                            SeatLayout seat_layout = new SeatLayout(String.valueOf(v.getId()), number, "deleted", v.getX(), v.getY(), v.getWidth(), v.getHeight(), v.getRotation());
                                            seatLayout.set(v.getId(), seat_layout);
                                        }
                                    })
                                    .setNegativeButton("취소", null)
                                    .show();

                        }

                        return false;
                    }
                });

                SeatLayout seat_layout = new SeatLayout(String.valueOf(position), String.valueOf(numButton), "seat", 0, 0, seatWidth, seatHeight, getAngle);
                seatLayout.add(seat_layout);

                //아까 만든 공간에 크기에 맞는 버튼을 생성함.
                dynamicLayout.addView(dynamicButton, new ConstraintLayout.LayoutParams(seatWidth, seatHeight));

            }
        } if(requestCode == REQUEST_SEAT_SIZE_EDIT){
            if(resultCode == Activity.RESULT_OK){
                int getId = data.getIntExtra("seatId", 0);
                String getNum = data.getStringExtra("seatNum");
                float getX = data.getFloatExtra("seatX", 0);
                float getY = data.getFloatExtra("seatY", 0);
                int getWidth = data.getIntExtra("width", 100);
                int getHeight = data.getIntExtra("height", 100);
                int getAngle = data.getIntExtra("angle", 0);

                int dpWidth = getWidth/2;
                int dpHeight = getHeight/2;

                Button removeDynamicButton = findViewById(getId);

                dynamicLayout.removeView(removeDynamicButton);

                final int seatWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpWidth, getResources().getDisplayMetrics());
                final int seatHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpHeight, getResources().getDisplayMetrics());

                Button dynamicButton = new Button(this); // 새로운 버튼 생성
                dynamicButton.setId(getId);
                dynamicButton.setText(getNum); // 버튼의 이름를 버튼의 개수로 표시
                dynamicButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                dynamicButton.setBackgroundResource(R.drawable.seat_border);
                dynamicButton.setX(getX);
                dynamicButton.setY(getY);
                dynamicButton.setWidth(seatWidth);
                dynamicButton.setHeight(seatHeight);
                dynamicButton.setRotation(getAngle);
                dynamicButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) { // 버튼을 드래그 앤 드랍하는 메소드(OnTouchListener)
                        int width = ((ViewGroup) v.getParent()).getWidth() - v.getWidth();
                        int height = ((ViewGroup) v.getParent()).getHeight() - v.getHeight();

                        myScroll.requestDisallowInterceptTouchEvent(true);

                        if(!delete_mode) {

                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                v.setRotation(0);
                                oldXvalue = event.getX();
                                oldYvalue = event.getY();
                            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                myScroll.requestDisallowInterceptTouchEvent(false);
                                if (v.getX() > width && v.getY() > height) {
                                    v.setX(width);
                                    v.setY(height);
                                } else if (v.getX() < 0 && v.getY() > height) {
                                    v.setX(0);
                                    v.setY(height);
                                } else if (v.getX() > width && v.getY() < 0) {
                                    v.setX(width);
                                    v.setY(0);
                                } else if (v.getX() < 0 && v.getY() < 0) {
                                    v.setX(0);
                                    v.setY(0);
                                } else if (v.getX() < 0 || v.getX() > width) {
                                    if (v.getX() < 0) {
                                        v.setX(0);
                                        v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                                    } else {
                                        v.setX(width);
                                        v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                                    }
                                } else if (v.getY() < 0 || v.getY() > height) {
                                    if (v.getY() < 0) {
                                        v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                        v.setY(0);
                                    } else {
                                        v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                        v.setY(height);
                                    }
                                }
                                v.setRotation(getAngle);

                                float layoutX = v.getX();
                                float layoutY = v.getY();

                                Button dynamicButton = findViewById(v.getId());
                                String id = dynamicButton.getText().toString();

                                SeatLayout seat_layout = new SeatLayout(String.valueOf(v.getId()), id, "seat", layoutX, layoutY, v.getWidth(), v.getHeight(), v.getRotation());

                                seatLayout.set(v.getId(), seat_layout);

                            }
                            return false;
                        }

                        return false;
                    }
                });

                dynamicButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button dynamicButton = findViewById(v.getId());
                        String number = dynamicButton.getText().toString();

                        if(delete_mode) {
                            Intent intent = new Intent(SeatEdit.this, EditSeatDialog.class);
                            intent.putExtra("seatId", v.getId());
                            intent.putExtra("seatNum", number);
                            intent.putExtra("seatX", v.getX());
                            intent.putExtra("seatY", v.getY());
                            intent.putExtra("width", getWidth);
                            intent.putExtra("height", getHeight);
                            intent.putExtra("angle", getAngle);
                            intent.putExtra("layout_width", layoutWidth);
                            intent.putExtra("layout_height", layoutHeight);
                            startActivityForResult(intent, 202);
                        }
                    }
                });

                dynamicButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Button dynamicButton = findViewById(v.getId());
                        String number = dynamicButton.getText().toString();

                        if (delete_mode) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SeatEdit.this);
                            builder.setTitle("삭제").setMessage("해당 아이템을 삭제하시겠습니까?")
                                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dynamicLayout.removeView(dynamicButton);
                                            SeatLayout seat_layout = new SeatLayout(String.valueOf(v.getId()), number, "deleted", v.getX(), v.getY(), v.getWidth(), v.getHeight(), v.getRotation());
                                            seatLayout.set(v.getId(), seat_layout);
                                        }
                                    })
                                    .setNegativeButton("취소", null)
                                    .show();

                        }

                        return false;
                    }
                });

                SeatLayout seat_layout = new SeatLayout(String.valueOf(getId), getNum, "seat", getX, getY, seatWidth, seatHeight, getAngle);
                seatLayout.set(getId, seat_layout);

                //아까 만든 공간에 크기에 맞는 버튼을 생성함.
                dynamicLayout.addView(dynamicButton, new ConstraintLayout.LayoutParams(seatWidth, seatHeight));
            }
        }
        if(requestCode == REQUEST_ITEM_SIZE){
            if(resultCode == Activity.RESULT_OK){
                int getWidth = data.getIntExtra("width", 100);
                int getHeight = data.getIntExtra("height", 100);
                int getAngle = data.getIntExtra("angle", 0);
                String getType = data.getStringExtra("type");
                String getItem_name = data.getStringExtra("item_name");

                int dpWidth = getWidth/2;
                int dpHeight = getHeight/2;

                final int seatWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpWidth, getResources().getDisplayMetrics());
                final int seatHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpHeight, getResources().getDisplayMetrics());

                position++;

                Button dynamicButton = new Button(this); // 새로운 버튼 생성
                dynamicButton.setId(position);
                dynamicButton.setX(0);
                dynamicButton.setY(0);
                dynamicButton.setWidth(seatWidth);
                dynamicButton.setHeight(seatHeight);
                dynamicButton.setRotation(getAngle);
                if(getType.equals("custom")){
                    dynamicButton.setText(getItem_name);
                    dynamicButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                    dynamicButton.setBackgroundResource(R.drawable.seat_border);
                } if(getType.equals("xbox")){
                    dynamicButton.setBackgroundResource(R.drawable.xbox);
                } if(getType.equals("arrow")){
                    dynamicButton.setBackgroundResource(R.drawable.arrow);
                } if(getType.equals("chair")){
                    dynamicButton.setBackgroundResource(R.drawable.seat_left);
                }

                dynamicButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) { // 버튼을 드래그 앤 드랍하는 메소드(OnTouchListener)
                        int width = ((ViewGroup) v.getParent()).getWidth() - v.getWidth();
                        int height = ((ViewGroup) v.getParent()).getHeight() - v.getHeight();

                        myScroll.requestDisallowInterceptTouchEvent(true);

                        if(!delete_mode) {

                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                v.setRotation(0);
                                oldXvalue = event.getX();
                                oldYvalue = event.getY();
                            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                myScroll.requestDisallowInterceptTouchEvent(false);
                                if (v.getX() > width && v.getY() > height) {
                                    v.setX(width);
                                    v.setY(height);
                                } else if (v.getX() < 0 && v.getY() > height) {
                                    v.setX(0);
                                    v.setY(height);
                                } else if (v.getX() > width && v.getY() < 0) {
                                    v.setX(width);
                                    v.setY(0);
                                } else if (v.getX() < 0 && v.getY() < 0) {
                                    v.setX(0);
                                    v.setY(0);
                                } else if (v.getX() < 0 || v.getX() > width) {
                                    if (v.getX() < 0) {
                                        v.setX(0);
                                        v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                                    } else {
                                        v.setX(width);
                                        v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                                    }
                                } else if (v.getY() < 0 || v.getY() > height) {
                                    if (v.getY() < 0) {
                                        v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                        v.setY(0);
                                    } else {
                                        v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                        v.setY(height);
                                    }
                                }
                                v.setRotation(getAngle);

                                float layoutX = v.getX();
                                float layoutY = v.getY();

                                SeatLayout seat_layout = new SeatLayout(String.valueOf(v.getId()), getItem_name, getType, layoutX, layoutY, v.getWidth(), v.getHeight(), v.getRotation());

                                seatLayout.set(v.getId(), seat_layout);

                            }
                            return false;
                        }

                        return false;
                    }
                });

                dynamicButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(delete_mode) {
                            Intent intent = new Intent(SeatEdit.this, EditItemDialog.class);
                            intent.putExtra("id", v.getId());
                            intent.putExtra("item_name", getItem_name);
                            intent.putExtra("item_type", getType);
                            intent.putExtra("seatX", v.getX());
                            intent.putExtra("seatY", v.getY());
                            intent.putExtra("width", getWidth);
                            intent.putExtra("height", getHeight);
                            intent.putExtra("angle", getAngle);
                            intent.putExtra("layout_width", layoutWidth);
                            intent.putExtra("layout_height", layoutHeight);
                            startActivityForResult(intent, 204);
                        }
                    }
                });

                dynamicButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Button dynamicButton = findViewById(v.getId());
                        String number = dynamicButton.getText().toString();

                        if (delete_mode) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SeatEdit.this);
                            builder.setTitle("삭제").setMessage("해당 아이템을 삭제하시겠습니까?")
                                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dynamicLayout.removeView(dynamicButton);
                                            SeatLayout seat_layout = new SeatLayout(String.valueOf(v.getId()), getItem_name, "deleted", v.getX(), v.getY(), v.getWidth(), v.getHeight(), v.getRotation());
                                            seatLayout.set(v.getId(), seat_layout);
                                        }
                                    })
                                    .setNegativeButton("취소", null)
                                    .show();

                        }

                        return false;
                    }
                });

                SeatLayout seat_layout = new SeatLayout(String.valueOf(position), getItem_name, getType, 0, 0, seatWidth, seatHeight, getAngle);
                seatLayout.add(seat_layout);

                //아까 만든 공간에 크기에 맞는 버튼을 생성함.
                dynamicLayout.addView(dynamicButton, new ConstraintLayout.LayoutParams(seatWidth, seatHeight));
            }
        }
        if(requestCode == REQUEST_ITEM_SIZE_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                int getId = data.getIntExtra("id", 0);
                String getItem_name = data.getStringExtra("item_name");
                String getItem_type = data.getStringExtra("item_type");
                int getWidth = data.getIntExtra("width", 100);
                int getHeight = data.getIntExtra("height", 100);
                int getAngle = data.getIntExtra("angle", 0);
                float getX = data.getFloatExtra("seatX", 0);
                float getY = data.getFloatExtra("seatY", 0);

                int dpWidth = getWidth/2;
                int dpHeight = getHeight/2;

                Button removeDynamicButton = findViewById(getId);

                dynamicLayout.removeView(removeDynamicButton);

                final int seatWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpWidth, getResources().getDisplayMetrics());
                final int seatHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpHeight, getResources().getDisplayMetrics());

                Button dynamicButton = new Button(this); // 새로운 버튼 생성
                dynamicButton.setId(getId);
                dynamicButton.setX(getX);
                dynamicButton.setY(getY);
                dynamicButton.setWidth(seatWidth);
                dynamicButton.setHeight(seatHeight);
                dynamicButton.setRotation(getAngle);
                if(getItem_type.equals("custom")){
                    dynamicButton.setText(getItem_name);
                    dynamicButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                    dynamicButton.setBackgroundResource(R.drawable.seat_border);
                } if(getItem_type.equals("xbox")){
                    dynamicButton.setBackgroundResource(R.drawable.xbox);
                } if(getItem_type.equals("arrow")){
                    dynamicButton.setBackgroundResource(R.drawable.arrow);
                } if(getItem_type.equals("chair")){
                    dynamicButton.setBackgroundResource(R.drawable.seat_left);
                }
                dynamicButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) { // 버튼을 드래그 앤 드랍하는 메소드(OnTouchListener)
                        int width = ((ViewGroup) v.getParent()).getWidth() - v.getWidth();
                        int height = ((ViewGroup) v.getParent()).getHeight() - v.getHeight();

                        myScroll.requestDisallowInterceptTouchEvent(true);

                        if(!delete_mode) {

                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                v.setRotation(0);
                                oldXvalue = event.getX();
                                oldYvalue = event.getY();
                            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                myScroll.requestDisallowInterceptTouchEvent(false);
                                if (v.getX() > width && v.getY() > height) {
                                    v.setX(width);
                                    v.setY(height);
                                } else if (v.getX() < 0 && v.getY() > height) {
                                    v.setX(0);
                                    v.setY(height);
                                } else if (v.getX() > width && v.getY() < 0) {
                                    v.setX(width);
                                    v.setY(0);
                                } else if (v.getX() < 0 && v.getY() < 0) {
                                    v.setX(0);
                                    v.setY(0);
                                } else if (v.getX() < 0 || v.getX() > width) {
                                    if (v.getX() < 0) {
                                        v.setX(0);
                                        v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                                    } else {
                                        v.setX(width);
                                        v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                                    }
                                } else if (v.getY() < 0 || v.getY() > height) {
                                    if (v.getY() < 0) {
                                        v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                        v.setY(0);
                                    } else {
                                        v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                        v.setY(height);
                                    }
                                }
                                v.setRotation(getAngle);


                                float layoutX = v.getX();
                                float layoutY = v.getY();

                                SeatLayout seat_layout = new SeatLayout(String.valueOf(v.getId()), getItem_name, getItem_type, layoutX, layoutY, v.getWidth(), v.getHeight(), v.getRotation());

                                seatLayout.set(v.getId(), seat_layout);

                            }
                            return false;
                        }

                        return false;
                    }
                });

                dynamicButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button dynamicButton = findViewById(v.getId());
                        String number = dynamicButton.getText().toString();

                        if(delete_mode) {
                            Intent intent = new Intent(SeatEdit.this, EditItemDialog.class);
                            intent.putExtra("id", v.getId());
                            intent.putExtra("item_name", getItem_name);
                            intent.putExtra("item_type", getItem_type);
                            intent.putExtra("seatX", v.getX());
                            intent.putExtra("seatY", v.getY());
                            intent.putExtra("width", getWidth);
                            intent.putExtra("height", getHeight);
                            intent.putExtra("angle", getAngle);
                            intent.putExtra("layout_width", layoutWidth);
                            intent.putExtra("layout_height", layoutHeight);
                            startActivityForResult(intent, 204);
                        }
                    }
                });

                dynamicButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Button dynamicButton = findViewById(v.getId());
                        String number = dynamicButton.getText().toString();

                        if (delete_mode) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SeatEdit.this);
                            builder.setTitle("삭제").setMessage("해당 아이템을 삭제하시겠습니까?")
                                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dynamicLayout.removeView(dynamicButton);
                                            SeatLayout seat_layout = new SeatLayout(String.valueOf(v.getId()), number, "deleted", v.getX(), v.getY(), v.getWidth(), v.getHeight(), v.getRotation());
                                            seatLayout.set(v.getId(), seat_layout);
                                        }
                                    })
                                    .setNegativeButton("취소", null)
                                    .show();

                        }

                        return false;
                    }
                });

                SeatLayout seat_layout = new SeatLayout(String.valueOf(getId), getItem_name, getItem_type, getX, getY, seatWidth, seatHeight, getAngle);
                seatLayout.set(getId, seat_layout);

                //아까 만든 공간에 크기에 맞는 버튼을 생성함.
                dynamicLayout.addView(dynamicButton, new ConstraintLayout.LayoutParams(seatWidth, seatHeight));

            }
        }
    }

    private void Loading(String idx1){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOADING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("loading");

                            if(success.equals("1")){
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String layoutJsonArray = object.getString("seat_layout".trim());
                                    String layoutWidthNum = object.getString("layout_width".trim());
                                    String layoutHeightNum = object.getString("layout_height".trim());

                                    if(!layoutWidthNum.equals("null")) {
                                        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(layoutWidthNum)/2, getResources().getDisplayMetrics());
                                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) layout_right_end.getLayoutParams();
                                        layoutParams.leftMargin = margin;
                                        layout_right_end.setLayoutParams(layoutParams);

                                        layoutWidth = Integer.parseInt(layoutWidthNum);

                                        widthTv.setText("가로 길이 : " + layoutWidthNum + "cm");
                                    }

                                    if(!layoutHeightNum.equals("null")) {
                                        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(layoutHeightNum)/2, getResources().getDisplayMetrics());
                                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) layout_end.getLayoutParams();
                                        layoutParams.topMargin = margin;
                                        layout_end.setLayoutParams(layoutParams);

                                        layoutHeight = Integer.parseInt(layoutHeightNum);

                                        heightTv.setText("세로 길이 : " + layoutHeightNum + "cm");
                                    }

                                    if(!layoutJsonArray.equals("null")) {

                                        JSONArray jsonArr = new JSONArray(layoutJsonArray);

                                        for (int ii = 0; ii < jsonArr.length(); ii++) {
                                            JSONObject jsonObj = jsonArr.getJSONObject(ii);

                                            String idx = jsonObj.getString("idx".trim());
                                            String id = jsonObj.getString("id".trim());
                                            String name = jsonObj.getString("name".trim());
                                            float x = jsonObj.getInt("x".trim());
                                            float y = jsonObj.getInt("y".trim());
                                            int width = jsonObj.getInt("width".trim());
                                            int height = jsonObj.getInt("height".trim());
                                            int angle = jsonObj.getInt("angle".trim());

                                            if (name.equals("seat")) {
                                                numButton++; // 버튼 추가할 때마다 버튼의 개수 1씩 증가
                                                position++;

                                                Button dynamicButton = new Button(SeatEdit.this); // 새로운 버튼 생성
                                                dynamicButton.setId(position);
                                                dynamicButton.setText(id); // 버튼의 이름를 버튼의 개수로 표시
                                                dynamicButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                                                dynamicButton.setBackgroundResource(R.drawable.seat_border);
                                                dynamicButton.setX(x);
                                                dynamicButton.setY(y);
                                                dynamicButton.setRotation(angle);
                                                    dynamicButton.setOnTouchListener(new View.OnTouchListener() {
                                                        @Override
                                                        public boolean onTouch(View v, MotionEvent event) { // 버튼을 드래그 앤 드랍하는 메소드(OnTouchListener)
                                                            int width = ((ViewGroup) v.getParent()).getWidth() - v.getWidth();
                                                            int height = ((ViewGroup) v.getParent()).getHeight() - v.getHeight();

                                                            myScroll.requestDisallowInterceptTouchEvent(true);

                                                            if(!delete_mode) {

                                                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                                                    v.setRotation(0);
                                                                    oldXvalue = event.getX();
                                                                    oldYvalue = event.getY();
                                                                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                                                    v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                                                    v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                                                                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                                                    myScroll.requestDisallowInterceptTouchEvent(false);
                                                                    if (v.getX() > width && v.getY() > height) {
                                                                        v.setX(width);
                                                                        v.setY(height);
                                                                    } else if (v.getX() < 0 && v.getY() > height) {
                                                                        v.setX(0);
                                                                        v.setY(height);
                                                                    } else if (v.getX() > width && v.getY() < 0) {
                                                                        v.setX(width);
                                                                        v.setY(0);
                                                                    } else if (v.getX() < 0 && v.getY() < 0) {
                                                                        v.setX(0);
                                                                        v.setY(0);
                                                                    } else if (v.getX() < 0 || v.getX() > width) {
                                                                        if (v.getX() < 0) {
                                                                            v.setX(0);
                                                                            v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                                                                        } else {
                                                                            v.setX(width);
                                                                            v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                                                                        }
                                                                    } else if (v.getY() < 0 || v.getY() > height) {
                                                                        if (v.getY() < 0) {
                                                                            v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                                                            v.setY(0);
                                                                        } else {
                                                                            v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                                                            v.setY(height);
                                                                        }
                                                                    }

                                                                    v.setRotation(angle);

                                                                    float layoutX = v.getX();
                                                                    float layoutY = v.getY();

                                                                    Button dynamicButton = findViewById(v.getId());
                                                                    String id = dynamicButton.getText().toString();

                                                                    SeatLayout seat_layout = new SeatLayout(String.valueOf(v.getId()), id, "seat", layoutX, layoutY, v.getWidth(), v.getHeight(), v.getRotation());

                                                                    seatLayout.set(v.getId(), seat_layout);

                                                                }
                                                                return false;
                                                            }

                                                            return false;
                                                        }
                                                    }); // 버튼을 드래그 앤 드랍하기 위해 setOnTouchListener 선언

                                                dynamicButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Button dynamicButton = findViewById(v.getId());
                                                        String number = dynamicButton.getText().toString();

                                                        float density = getResources().getDisplayMetrics().density;

                                                        if (density == 1.0)      // mpdi  (160dpi) -- xxxhdpi (density = 4)기준으로 density 값을 재설정 한다
                                                            density *= 4.0;
                                                        else if (density == 1.5) // hdpi  (240dpi)
                                                            density *= (8 / 3);
                                                        else if (density == 2.0) // xhdpi (320dpi)
                                                            density *= 2.0;

                                                        int finalWidth = Math.round(width / density)*2;

                                                        int finalHeight = Math.round(height / density)*2;

                                                        if(delete_mode) {
                                                            Intent intent = new Intent(SeatEdit.this, EditSeatDialog.class);
                                                            intent.putExtra("seatId", v.getId());
                                                            intent.putExtra("seatNum", number);
                                                            intent.putExtra("seatX", v.getX());
                                                            intent.putExtra("seatY", v.getY());
                                                            intent.putExtra("width", finalWidth);
                                                            intent.putExtra("height", finalHeight);
                                                            intent.putExtra("angle", angle);
                                                            intent.putExtra("layout_width", layoutWidth);
                                                            intent.putExtra("layout_height", layoutHeight);
                                                            startActivityForResult(intent, 202);
                                                        }
                                                    }
                                                });

                                                dynamicButton.setOnLongClickListener(new View.OnLongClickListener() {
                                                    @Override
                                                    public boolean onLongClick(View v) {
                                                        if (delete_mode) {
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(SeatEdit.this);
                                                            builder.setTitle("삭제").setMessage("해당 아이템을 삭제하시겠습니까?")
                                                                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dynamicLayout.removeView(dynamicButton);
                                                                            SeatLayout seat_layout = new SeatLayout(String.valueOf(v.getId()), id, "deleted", v.getX(), v.getY(), v.getWidth(), v.getHeight(), v.getRotation());
                                                                            seatLayout.set(v.getId(), seat_layout);
//                                                                        Log.d("ArrayList Test", seatLayout.toString());
                                                                        }
                                                                    })
                                                                    .setNegativeButton("취소", null)
                                                                    .show();

                                                        }

                                                        return false;
                                                    }
                                                });

                                                //아까 만든 공간에 크기에 맞는 버튼을 생성함.
                                                dynamicLayout.addView(dynamicButton, new ConstraintLayout.LayoutParams(width, height));

                                                SeatLayout seat_layout = new SeatLayout(idx, id, "seat", x, y, width, height, angle);
                                                seatLayout.add(seat_layout);

                                            } else if(name.equals("deleted")){
                                                if(!id.equals("seat_direction") && !id.equals("item")) {
                                                    numButton++; // 버튼 추가할 때마다 버튼의 개수 1씩 증가
                                                }
                                                position++;

                                                SeatLayout seat_layout = new SeatLayout(idx, id, "deleted", x, y, width, height, angle);
                                                seatLayout.add(seat_layout);
                                            } else{
                                                position++;

                                                Button dynamicButton = new Button(SeatEdit.this); // 새로운 버튼 생성
                                                dynamicButton.setId(position);
                                                if(name.equals("custom")){
                                                    dynamicButton.setText(id);
                                                    dynamicButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                                                    dynamicButton.setBackgroundResource(R.drawable.seat_border);
                                                } if(name.equals("xbox")){
                                                    dynamicButton.setBackgroundResource(R.drawable.xbox);
                                                } if(name.equals("arrow")){
                                                    dynamicButton.setBackgroundResource(R.drawable.arrow);
                                                } if(name.equals("chair")){
                                                    dynamicButton.setBackgroundResource(R.drawable.seat_left);
                                                }
                                                dynamicButton.setX(x);
                                                dynamicButton.setY(y);
                                                dynamicButton.setRotation(angle);

                                                dynamicButton.setOnTouchListener(new View.OnTouchListener() {
                                                    @Override
                                                    public boolean onTouch(View v, MotionEvent event) { // 버튼을 드래그 앤 드랍하는 메소드(OnTouchListener)
                                                        int width = ((ViewGroup) v.getParent()).getWidth() - v.getWidth();
                                                        int height = ((ViewGroup) v.getParent()).getHeight() - v.getHeight();

                                                        myScroll.requestDisallowInterceptTouchEvent(true);

                                                        if(!delete_mode) {

                                                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                                                v.setRotation(0);
                                                                oldXvalue = event.getX();
                                                                oldYvalue = event.getY();
                                                            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                                                v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                                                v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                                                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                                                myScroll.requestDisallowInterceptTouchEvent(false);
                                                                if (v.getX() > width && v.getY() > height) {
                                                                    v.setX(width);
                                                                    v.setY(height);
                                                                } else if (v.getX() < 0 && v.getY() > height) {
                                                                    v.setX(0);
                                                                    v.setY(height);
                                                                } else if (v.getX() > width && v.getY() < 0) {
                                                                    v.setX(width);
                                                                    v.setY(0);
                                                                } else if (v.getX() < 0 && v.getY() < 0) {
                                                                    v.setX(0);
                                                                    v.setY(0);
                                                                } else if (v.getX() < 0 || v.getX() > width) {
                                                                    if (v.getX() < 0) {
                                                                        v.setX(0);
                                                                        v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                                                                    } else {
                                                                        v.setX(width);
                                                                        v.setY(v.getY() + (event.getY()) - (v.getHeight() / 2));
                                                                    }
                                                                } else if (v.getY() < 0 || v.getY() > height) {
                                                                    if (v.getY() < 0) {
                                                                        v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                                                        v.setY(0);
                                                                    } else {
                                                                        v.setX(v.getX() + (event.getX()) - (v.getWidth() / 2));
                                                                        v.setY(height);
                                                                    }
                                                                }
                                                                v.setRotation(angle);

                                                                float layoutX = v.getX();
                                                                float layoutY = v.getY();

                                                                SeatLayout seat_layout = new SeatLayout(String.valueOf(v.getId()), id, name, layoutX, layoutY, v.getWidth(), v.getHeight(), angle);

                                                                seatLayout.set(v.getId(), seat_layout);

                                                            }
                                                            return false;
                                                        }

                                                        return false;
                                                    }
                                                });

                                                dynamicButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        float density = getResources().getDisplayMetrics().density;

                                                        if (density == 1.0)      // mpdi  (160dpi) -- xxxhdpi (density = 4)기준으로 density 값을 재설정 한다
                                                            density *= 4.0;
                                                        else if (density == 1.5) // hdpi  (240dpi)
                                                            density *= (8 / 3);
                                                        else if (density == 2.0) // xhdpi (320dpi)
                                                            density *= 2.0;

                                                        int finalWidth = Math.round(width / density)*2;

                                                        int finalHeight = Math.round(height / density)*2;

                                                        if(delete_mode) {
                                                            Intent intent = new Intent(SeatEdit.this, EditItemDialog.class);
                                                            intent.putExtra("id", v.getId());
                                                            intent.putExtra("item_name", id);
                                                            intent.putExtra("item_type", name);
                                                            intent.putExtra("seatX", v.getX());
                                                            intent.putExtra("seatY", v.getY());
                                                            intent.putExtra("width", finalWidth);
                                                            intent.putExtra("height", finalHeight);
                                                            intent.putExtra("angle", angle);
                                                            intent.putExtra("layout_width", layoutWidth);
                                                            intent.putExtra("layout_height", layoutHeight);
                                                            startActivityForResult(intent, 204);
                                                        }
                                                    }
                                                });

                                                dynamicButton.setOnLongClickListener(new View.OnLongClickListener() {
                                                    @Override
                                                    public boolean onLongClick(View v) {

                                                        if (delete_mode) {
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(SeatEdit.this);
                                                            builder.setTitle("삭제").setMessage("해당 아이템을 삭제하시겠습니까?")
                                                                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dynamicLayout.removeView(dynamicButton);
                                                                            SeatLayout seat_layout = new SeatLayout(String.valueOf(v.getId()), id, "deleted", v.getX(), v.getY(), v.getWidth(), v.getHeight(), v.getRotation());
                                                                            seatLayout.set(v.getId(), seat_layout);
                                                                        }
                                                                    })
                                                                    .setNegativeButton("취소", null)
                                                                    .show();

                                                        }

                                                        return false;
                                                    }
                                                });

                                                SeatLayout seat_layout = new SeatLayout(String.valueOf(position), id, name, x, y, width, height, angle);
                                                seatLayout.add(seat_layout);

                                                //아까 만든 공간에 크기에 맞는 버튼을 생성함.
                                                dynamicLayout.addView(dynamicButton, new ConstraintLayout.LayoutParams(width, height));
                                            }

                                        }
                                    } else{

                                    }

                                }
                            } else { // 실패
//                                Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Volley 통신 에러.",Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idx", idx1);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
