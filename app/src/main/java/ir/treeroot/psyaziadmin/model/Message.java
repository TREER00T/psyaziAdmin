package ir.treeroot.psyaziadmin.model;

import android.content.Context;

public class Message {

    Context Context;
    String Message, Format, TimeZone, groupByTime;

    public String getGroupByTime() {
        return groupByTime;
    }


    public Message(Context context, String message, String format, String TimeZone, String groupByTime) {

        this.Context = context;
        this.Message = message;
        this.Format = format;
        this.TimeZone = TimeZone;
        this.groupByTime = groupByTime;

    }

    public Context getContext() {
        return Context;
    }

    public String getMessage() {
        return Message;
    }

    public String getFormat() {
        return Format;
    }

    public String getTimeZone() {
        return TimeZone;
    }
}
