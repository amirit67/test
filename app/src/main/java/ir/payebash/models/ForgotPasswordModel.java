package ir.payebash.models;

import java.io.Serializable;

public class ForgotPasswordModel implements Serializable {

    public String mobile = "";
    public String smsCode = "";
    public String password = "";

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
