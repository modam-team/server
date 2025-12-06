package com.example.modam.global.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class PreviousQuery {
    private HashSet<String> QuerySet = new HashSet<>();

    @Scheduled(cron = "0 0 0 * * MON")
    public void reset() {
        QuerySet = new HashSet<>();
    }

    public void update(String query) {
        QuerySet.add(query);
    }

    public boolean done(String query) {
        return QuerySet.contains(query);
    }
}
