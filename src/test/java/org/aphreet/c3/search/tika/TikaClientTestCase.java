package org.aphreet.c3.search.tika;

import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Ignore
public class TikaClientTestCase {

    @Test
    public void testResource() throws IOException {

        File file = new File("/Users/aphreet/Documents/About Stacks.pdf");

        URLConnection connection = new URL("http://localhost:8080").openConnection();

        Map<String, String> metadata = new HashMap<>();

        try (InputStream is = connection.getInputStream(); OutputStream os = connection.getOutputStream()) {
            connection.setDoOutput(true);

            Files.copy(file.toPath(), connection.getOutputStream());

            for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
                if (header.getKey() != null) {
                    if (header.getKey().startsWith("x-tika-extracted_")) {
                        metadata.put(header.getKey().replaceFirst("x-tika-extracted_", ""),
                                header.getValue().get(0));
                    }
                }
            }

            System.out.println(metadata);

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line = reader.readLine();
            while (line != null) {
                System.out.println(reader.readLine());
                line = reader.readLine();
            }
        }
    }

}
