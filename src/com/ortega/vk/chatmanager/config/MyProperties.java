package com.ortega.vk.chatmanager.config;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

@SuppressWarnings("serial")
public class MyProperties extends Properties {

    public Enumeration<Object> keys() {
        Enumeration<Object> keysEnum = super.keys();
        Vector<Object> keyList = new Vector<Object>();

        while (keysEnum.hasMoreElements()) {
            keyList.add(keysEnum.nextElement());
        }

        Collections.sort(keyList, (a, b) -> a.toString().compareTo(b.toString()));

        return keyList.elements();
    }
}
