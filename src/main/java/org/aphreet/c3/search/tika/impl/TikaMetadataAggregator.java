package org.aphreet.c3.search.tika.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class TikaMetadataAggregator {

    private Map<String, String> metadataTranslationMap = new HashMap<>();

    private Set<String> ignoredKeys = new HashSet<>();

    private Log logger = LogFactory.getLog(getClass());

    public TikaMetadataAggregator(){
        Properties properties = new Properties();

        Properties ignorredProeprties = new Properties();

        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("metadata.properties"));

            for(Object keyObj : properties.keySet()){
                String key = (String) keyObj;

                String[] values = properties.getProperty(key).split(",");

                for(String value : values){
                    metadataTranslationMap.put(value, key);
                }
            }

            ignorredProeprties.load(getClass().getClassLoader().getResourceAsStream("ignored-metadata.properties"));

            String propertiesToIgnore[] = ignorredProeprties.getProperty("ignored").split(",");

            ignoredKeys.addAll(Arrays.asList(propertiesToIgnore));

            logger.debug(metadataTranslationMap);
        } catch (IOException e) {
            logger.warn("Failed to load metadata mapping definition", e);
        }
    }

    public String translateMetadataKey(String key){

        if(metadataTranslationMap.containsKey(key)){
            logger.debug("Translating metadata " + key + " to " + metadataTranslationMap.get(key));

            return metadataTranslationMap.get(key);
        }else{
            return key;
        }
    }

    public Set<String> getIgnoredKeys() {
        return ignoredKeys;
    }
}
