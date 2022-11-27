package com.example.seatreservation.Request;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CommentBoardReadRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="http://3.34.45.193/CommentBoardRead.php";
    private Map<String,String> map;

    public CommentBoardReadRequest(String idx, Response.Listener<String>listener){
        super(Request.Method.POST,URL,listener,null);

        map=new HashMap<>();
        map.put("idx",idx);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
