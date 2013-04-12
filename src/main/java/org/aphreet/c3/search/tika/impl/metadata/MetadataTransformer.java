package org.aphreet.c3.search.tika.impl.metadata;

public interface MetadataTransformer {

    public boolean supports(String key);

    public String transform(String value);
}
