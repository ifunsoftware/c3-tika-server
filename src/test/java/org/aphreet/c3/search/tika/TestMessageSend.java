package org.aphreet.c3.search.tika;

import org.apache.tika.exception.TikaException;
import org.aphreet.c3.search.tika.impl.TikaNotifier;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TestMessageSend {

    @Test
    public void testMe(){

        try{
            throw new Exception("Test exception");
        }catch(Exception e){
            new TikaNotifier().notifyAboutError("resource addresss", new TikaException("Failed to parse document", e));
        }
    }
}
