package org.aphreet.c3.search.tika.impl.metadata;

import java.util.HashSet;
import java.util.Set;

public class ImageMetadataTransformer implements MetadataTransformer {

    private final Set<String> metadataKeys = new HashSet<>();

    public ImageMetadataTransformer(){
        metadataKeys.add("height");
        metadataKeys.add("width");
    }

    @Override
    public boolean supports(String key) {
        return metadataKeys.contains(key);
    }

    @Override
    public String transform(String value) {
        return value.replaceFirst("\\s+pixels", "");
    }
}
