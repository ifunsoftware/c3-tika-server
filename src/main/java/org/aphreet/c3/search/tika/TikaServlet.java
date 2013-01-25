package org.aphreet.c3.search.tika;

import org.aphreet.c3.search.tika.impl.TikaProvider;
import org.aphreet.c3.search.tika.impl.TikaResult;
import org.aphreet.c3.search.tika.impl.TikaStatistics;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class TikaServlet extends HttpServlet{

    private TikaProvider tikaProvider = new TikaProvider();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Path path = Files.createTempFile("_", "extract");

        try{
            Files.copy(req.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            TikaResult result = tikaProvider.extractMetadata(path.toFile());

            for(Map.Entry<String, String> entry : result.metadata.entrySet()){
                resp.addHeader("x-tika-extracted_"+entry.getKey(), entry.getValue());
            }

            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("text/plain");
            resp.getWriter().write(result.content);
        }catch(Throwable e){
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }finally {
            resp.getWriter().close();
            Files.deleteIfExists(path);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        TikaStatistics stats = tikaProvider.getStatistics();

        PrintWriter pw = resp.getWriter();

        pw.println("Service is up and running\n");

        pw.println("Statistics");
        pw.println("Requests processed:       " + stats.processedRequests);
        pw.println("Failed requests:          " + stats.failedRequests);
        pw.println("Bytes processed:          " + stats.processedBytes);
        pw.println("Chars sent:               " + stats.sentChars);
        pw.println("Requests in queue:        " + stats.requestsQueue);
        pw.println("Requests being processed: " + stats.requestsBeingProcessed);

        pw.close();
    }
}
