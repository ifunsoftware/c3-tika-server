package org.aphreet.c3.search.tika;

import org.aphreet.c3.search.tika.impl.TikaProvider;
import org.aphreet.c3.search.tika.impl.TikaResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class TikaServlet extends HttpServlet{

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Path path = Files.createTempFile("_", "extract");

        try{
            Files.copy(req.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            TikaResult result = new TikaProvider().extractMetadata(path.toFile());

            for(Map.Entry<String, String> entry : result.metadata.entrySet()){
                resp.addHeader("x-tika-extracted_"+entry.getKey(), entry.getValue());
            }

            resp.getWriter().write(result.content);
        }catch(Throwable e){
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }finally {
            resp.getWriter().close();
            Files.deleteIfExists(path);
        }
    }
}
