package com.example.seatreservation.Request;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class NameValidateRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="http://3.34.45.193/NameValidate.php";
    private Map<String,String> map;

    public NameValidateRequest(String userName, Response.Listener<String>listener){
        super(Request.Method.POST,URL,listener,null);

        map=new HashMap<>();
        map.put("userID",userName);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
