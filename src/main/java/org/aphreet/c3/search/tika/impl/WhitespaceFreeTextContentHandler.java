package org.aphreet.c3.search.tika.impl;

import org.apache.tika.sax.ToTextContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Writer;

public class WhitespaceFreeTextContentHandler extends ToTextContentHandler {

    private Writer writer;

    char previousChar = 0;

    public WhitespaceFreeTextContentHandler(Writer writer) {
        super(writer);
        this.writer = writer;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        previousChar = 0;
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if(previousChar != '\n'){
            for(int i=start; i<start + length; i++){
                if(ch[i] == '\n'){
                    append(ch[i]);
                    return;
                }
            }
            append(' ');
        }
    }

    private void append(char ch){
        if(previousChar != ch){
            previousChar = ch;
            try {
                writer.append(ch);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
