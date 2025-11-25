package com.example.modam.global.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;

@Component
public class DefineHashtag {

    private HashSet<String> hashtag = new HashSet<>();

    @PostConstruct
    public void init() {
        Collections.addAll(hashtag,
                //감정 키워드
                "감동적인",
                "따뜻한",
                "여운이 남는",
                "위로가 되는",
                "웃긴",
                "스릴 있는",
                "무거운",
                "희망적인",

                //경험 키워드
                "잘 읽히는",
                "어려운",
                "다시 읽고 싶은",
                "집중이 필요한",
                "출퇴근길에 딱",
                "잠들기 전에 딱",
                "생각하게 되는",
                "한 번에 읽은",

                //문체 키워드
                "서정적인",
                "직설적인",
                "속도감 있는 전개",
                "유머러스한",
                "간결한",
                "사실적인",
                "추상적인",
                "비유적인"
        );
    }

    public boolean isHashtag(String s) {
        if (s == null || !hashtag.contains(s)) {
            return false;
        }

        return true;
    }
}
