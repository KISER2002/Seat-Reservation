package com.example.seatreservation.Board;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seatreservation.R;

public class CommentList2 extends AppCompatActivity {
    private LinearLayout board_view_btn;
    private String  board_idx, board_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_board_comment2);

        Intent intent = getIntent();
        board_idx = intent.getStringExtra("idx");

    }
}

