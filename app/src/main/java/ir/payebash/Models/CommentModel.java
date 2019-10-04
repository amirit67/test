package ir.payebash.Models;

import java.io.Serializable;

public class CommentModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private String UserName = "";
    private String Comment1 = "";
    private String Image = "";

    public String getName() {
        return UserName;
    }

    public String getDescription() {
        return Comment1;
    }

    public String getImage() {
        return Image;
    }
}
