package com.example.modam.global.utils;

import org.springframework.stereotype.Component;

@Component
public class VariousFunc {

    public String toFTS(String s) {
        s = s.trim();
        s = s.replaceAll("[\\+\\-><()~\"@*]", " ");
        s = s.replaceAll("[^\\p{L}\\p{N}\\s]", " ");
        String[] tokens = s.split("\\s+");
        StringBuilder out = new StringBuilder();

        for (String t : tokens) {
            if (t == null) {
                continue;
            }
            t = t.trim();
            if (t.isEmpty()) {
                continue;
            }

            if (t.length() == 1) {
                continue;
            }
            out.append("+").append(t).append("*").append(" ");
        }
        return out.toString().trim();
    }
}
