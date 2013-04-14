package org.aphreet.c3.search.tika.impl.metadata;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class DateMetadataTransformer implements MetadataTransformer{

    @Override
    public boolean supports(String key) {
        return key.equals("created") || key.equals("modified");
    }

    private DateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy"); //Tue Jan 04 17:56:16 MSK 2011

    private DateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX"); //2011-01-04T20:01:16Z

    {
        outputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }


    @Override
    public String transform(String value) {
        try {
            return outputDateFormat.format(dateFormat.parse(value));
        } catch (ParseException e) {
            return value;
        }
    }
}
