package ir.payebash.Models;

import java.io.Serializable;

public class BaseResponse implements Serializable {

    public int statusCode = 0;
    public String message = "";

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
