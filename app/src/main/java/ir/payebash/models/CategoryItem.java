package ir.payebash.models;

import java.io.Serializable;

public class CategoryItem implements Serializable {

    public static final long serialVersionUID = 1L;
    public String id = "";
    public String name = "";
    public String description = "";
    public String img = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
