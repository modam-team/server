package com.example.modam.global.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TendencyMapping {
    private final Map<String, String> map = new HashMap<>();

    @PostConstruct
    public void init() {
        // 몰입·공감형
        map.put("소설/문학", "몰입·공감형");
        map.put("로맨스", "몰입·공감형");
        map.put("에세이/전기", "몰입·공감형");
        map.put("가족/관계", "몰입·공감형");

        // 사유·탐구형
        map.put("인문/사회/정치/법", "사유·탐구형");
        map.put("역사/종교", "사유·탐구형");
        map.put("과학/기술/공학", "사유·탐구형");

        // 성취·발전형
        map.put("경제/경영", "성취·발전형");
        map.put("교육/어학", "성취·발전형");
        map.put("의학/건강", "성취·발전형");

        // 감각·창의형
        map.put("예술/디자인/건축", "감각·창의형");
        map.put("엔터테인먼트/문화", "감각·창의형");

        // 생활·휴식형
        map.put("라이프스타일/취미", "생활·휴식형");
        map.put("여행", "생활·휴식형");
        map.put("심리/명상", "생활·휴식형");

        // 모험·탐험형
        map.put("판타지/무협", "모험·탐험형");

        // 성장·학습형
        map.put("유아/청소년", "성장·학습형");
    }

    public String mapCategory(String key) {
        return map.get(key.trim());
    }
}
