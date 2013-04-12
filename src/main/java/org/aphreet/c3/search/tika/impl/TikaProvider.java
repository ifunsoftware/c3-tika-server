/**
 * Copyright (c) 2010, Mikhail Malygin, Ashirbaev Ildar
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the IFMO nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.aphreet.c3.search.tika.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.ToTextContentHandler;
import org.apache.tika.sax.WriteOutContentHandler;
import org.aphreet.c3.search.tika.impl.metadata.ImageMetadataTransformer;
import org.aphreet.c3.search.tika.impl.metadata.MetadataTransformer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

public class TikaProvider {

    private final Log logger = LogFactory.getLog(getClass());

    private final static int PARALLEL_REQUESTS = 15;

    private final Semaphore semaphore = new Semaphore(PARALLEL_REQUESTS);

    private final AtomicLong processedRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    private final AtomicLong processedBytes = new AtomicLong(0);
    private final AtomicLong sentChars = new AtomicLong(0);

    private final TikaMetadataAggregator metadataAggregator = new TikaMetadataAggregator();

    private final MetadataTransformer[] metadataTransformers = new MetadataTransformer[]{new ImageMetadataTransformer()};

    public TikaResult extractMetadata(File file) throws TikaException, SAXException, IOException, InterruptedException {

        try{
            semaphore.acquire();

            logger.info("connection established to tika, filename: " + file.getAbsolutePath());
            InputStream is = null;
            Map<String, String> map = new HashMap<>();

            try {
                Metadata extractedMetadata = new Metadata();

                is = new FileInputStream(file);

                StringWriter writer = new StringWriter();

                ToTextContentHandler toTextContentHandler = new WhitespaceFreeTextContentHandler(writer);

                Parser parser = new AutoDetectParser();
                ContentHandler handler = new WriteOutContentHandler(toTextContentHandler, -1);
                parser.parse(is, handler, extractedMetadata, new ParseContext());

                for (String name : extractedMetadata.names()) {
                    String value = extractedMetadata.get(name);

                    if(!value.isEmpty()){
                        String transformedName = metadataAggregator.translateMetadataKey(name);

                        if(transformedName != null){
                            map.put(transformedName, transformMetadata(transformedName, value));
                        }else{
                            logger.debug("Ignoring metadata " + name + " value: " + extractedMetadata.get(name));
                        }
                    }
                }

                String result = writer.toString();

                processedBytes.addAndGet(file.length());
                sentChars.addAndGet(result.length());
                processedRequests.incrementAndGet();

                return new TikaResult(result, map);
            }catch(Exception e){
                failedRequests.incrementAndGet();
                throw e;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }finally {
            semaphore.release();
        }
    }

    private String transformMetadata(String key, String value){
        for(MetadataTransformer transformer : metadataTransformers){
            if(transformer.supports(key)){
                value = transformer.transform(value);
            }
        }

        return value;
    }

    public TikaStatistics getStatistics(){

        TikaStatistics statistics = new TikaStatistics();

        statistics.requestsQueue = semaphore.getQueueLength();
        statistics.requestsBeingProcessed = PARALLEL_REQUESTS - semaphore.availablePermits();
        statistics.failedRequests = failedRequests.longValue();
        statistics.processedRequests = processedRequests.longValue();
        statistics.sentChars = sentChars.longValue();
        statistics.processedBytes = processedBytes.longValue();

        return statistics;
    }
}
