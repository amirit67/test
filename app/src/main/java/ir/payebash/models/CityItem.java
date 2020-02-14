package ir.payebash.models;

import java.io.Serializable;

public class CityItem implements Serializable {

    public static final long serialVersionUID = 1L;
    public String _Id = "";
    public String _city_name_fa = "";
    public int parentCode = 0;
    public int hasChild = 0;

    public String toString() {
        return (_city_name_fa);
    }

    public String getCityNameFa() {
        return _city_name_fa;
    }

    public void setCityNameFa(String city_name_fa) {
        _city_name_fa = city_name_fa;
    }

    public String getId() {
        return _Id;
    }

    public void setId(String Id) {
        _Id = Id;
    }

    public int getHasChild() {
        return hasChild;
    }

    public void setHasChild(int hasChild) {
        this.hasChild = hasChild;
    }

    public int getParentCode() {
        return parentCode;
    }

    public void setParentCode(int parentCode) {
        this.parentCode = parentCode;
    }
}
