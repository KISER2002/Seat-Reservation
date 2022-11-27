package com.example.seatreservation.Request;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SeatEditRequest extends StringRequest {

    //서버 url 설정(php파일 연동)
    final static  private String URL="http://3.34.45.193/SeatEdit.php";
    private Map<String, String> parameters;


    public SeatEditRequest(String officeNum, String layout, String layout_width, String layout_height, Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("officeNum", officeNum);
        parameters.put("layout", layout);
        parameters.put("layout_width", layout_width);
        parameters.put("layout_height", layout_height);
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}

