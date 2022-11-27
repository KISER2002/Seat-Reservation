package com.example.seatreservation.Request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class BoardRequest extends StringRequest {

    //서버 url 설정(php파일 연동)
    final static  private String URL="http://3.34.45.193/Board.php";
    private Map<String, String> parameters;


    public BoardRequest(String writer, String title, String content, String image,String date, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("writer", writer);
        parameters.put("title", title);
        parameters.put("content", content);
        parameters.put("image", image);
        parameters.put("date", date);
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError{
        return parameters;
    }
}
