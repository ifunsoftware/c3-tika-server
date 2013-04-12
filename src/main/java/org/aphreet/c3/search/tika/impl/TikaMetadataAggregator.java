package org.aphreet.c3.search.tika.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

class TikaMetadataAggregator {

    private final Map<String, String> metadataTranslationMap = new HashMap<>();

    private final Log logger = LogFactory.getLog(getClass());

    public TikaMetadataAggregator(){
        Properties properties = new Properties();

        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("metadata.properties"));

            for(Object keyObj : properties.keySet()){
                String key = (String) keyObj;

                String[] values = properties.getProperty(key).split(",");

                for(String value : values){
                    metadataTranslationMap.put(value, key);
                }
            }

            logger.debug(metadataTranslationMap);
        } catch (IOException e) {
            logger.warn("Failed to load metadata mapping definition", e);
        }
    }

    public String translateMetadataKey(String key){

        if(metadataTranslationMap.containsKey(key)){
            logger.debug("Translating metadata key: " + key);
            return metadataTranslationMap.get(key);
        }else{
            return null;
        }
    }
}
