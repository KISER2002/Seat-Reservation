package com.example.seatreservation.Request;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SeatWriteRequest extends StringRequest {

    //서버 url 설정(php파일 연동)
    final static  private String URL="http://3.34.45.193/SeatWrite.php";
    private Map<String, String> parameters;


    public SeatWriteRequest(String id, String userId, String startTime, String endTime,Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("id", id);
        parameters.put("user", userId);
        parameters.put("startTime", startTime);
        parameters.put("endTime", endTime);
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}

