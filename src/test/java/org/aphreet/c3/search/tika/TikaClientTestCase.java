package org.aphreet.c3.search.tika;

import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Ignore
public class TikaClientTestCase {

    @Test
    public void testResource() throws IOException {

        File file = new File("/Users/aphreet/Documents/About Stacks.pdf");

        URLConnection connection = new URL("http://localhost:8080").openConnection();

        OutputStream os = null;
        InputStream is = null;

        String text = null;

        Map<String, String> metadata = new HashMap<>();

        try{
            connection.setDoOutput(true);

            os = connection.getOutputStream();

            Files.copy(file.toPath(), connection.getOutputStream());

            for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
                if(header.getKey() != null){
                    if(header.getKey().startsWith("x-tika-extracted_")){
                        metadata.put(header.getKey().replaceFirst("x-tika-extracted_", ""),
                                header.getValue().get(0));
                    }
                }
            }

            is = connection.getInputStream();

            System.out.println(metadata);

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line = reader.readLine();
            while(line != null){
                System.out.println(reader.readLine());
                line = reader.readLine();
            }
        }finally {
            if(os !=null){
                os.close();
            }

            if(is != null){
                is.close();
            }
        }
    }

}
