package org.aphreet.c3.search.tika;

import org.aphreet.c3.search.tika.impl.metadata.DateMetadataTransformer;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestDateParse {

    @Test
    public void testDateParse(){

        Date date = new Date();

        DateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");

        System.out.println(dateFormat.format(date));

        DateMetadataTransformer transformer = new DateMetadataTransformer();

        System.out.println(transformer.transform("Tue Jan 04 17:56:16 MSK 2011"));

    }

}
