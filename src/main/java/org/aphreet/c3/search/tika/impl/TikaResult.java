package org.aphreet.c3.search.tika.impl;

import java.util.Map;

public class TikaResult {

    public final String content;
    public final Map<String, String> metadata;

    public TikaResult(String content, Map<String, String> metadata) {
        this.content = content;
        this.metadata = metadata;
    }
}
