package com.example.seatreservation.Request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ViewsCheckRequest extends StringRequest {

    //서버 url 설정(php파일 연동)
    final static  private String URL="http://3.34.45.193/ViewsCheck.php";
    private Map<String, String> parameters;


    public ViewsCheckRequest(String idx, Integer hit, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        String hit_string = String.valueOf(hit);

        parameters = new HashMap<>();
        parameters.put("idx", idx);
        parameters.put("hit", hit_string);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError{
        return parameters;
    }
}
