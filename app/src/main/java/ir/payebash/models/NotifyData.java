package ir.payebash.models;

import java.io.Serializable;

public class NotifyData implements Serializable {

    public static final long serialVersionUID = 1L;
    public String id;
    public String title;
    public String deadLine;
    public String subject;
    public String city;
    public String cost;
    public String tag;
    public String imgUrl;
    public String type;

    public NotifyData(String id, String title, String deadLine, String subject, String city, String cost,/* String tag,*/ String imgUrl, String type) {

        this.id = id;
        this.title = title;
        this.deadLine = deadLine;
        this.subject = subject;
        this.city = city;
        this.cost = cost;
        //this.tag = tag;
        this.imgUrl = imgUrl;
        this.type = type;
    }

    public static class Message {
        String to;
        NotifyData data;

        public Message(String to, NotifyData data) {
            this.to = to;
            this.data = data;
        }
    }

    public static class Data {
        public String to;
        public String[] registration_tokens;

        public Data(String to, String[] registration_tokens) {
            this.to = to;
            this.registration_tokens = registration_tokens;
        }
    }
}
