package ir.payebash.modelviewsChat;

import java.io.Serializable;

public class MessageViewModel implements Serializable {
    public String Username = "";
    public String Content;
    public String From = "";
    public String Avatar = "";
    public String Timestamp = "";
    public int IsMine;
}
