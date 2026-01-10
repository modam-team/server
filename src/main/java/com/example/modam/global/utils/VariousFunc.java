package com.example.modam.global.utils;

import com.example.modam.domain.report.Domain.Place;
import com.example.modam.domain.report.Presentation.dto.ReadReportGroup;
import com.example.modam.domain.report.Presentation.dto.ReportBlock;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class VariousFunc {

    private final TendencyMapping tendencyMapping;

    private static final Pattern SEARCH_PATTERN =
            Pattern.compile("^[a-zA-Z0-9가-힣\\s\\-:.,()!?&']+$");


    public VariousFunc(TendencyMapping tendencyMapping) {
        this.tendencyMapping = tendencyMapping;
    }

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

    public boolean isInvalidQuery(String query) {
        if (query == null) {
            return true;
        }

        String trimmed = query.trim();

        if (trimmed.isEmpty() || trimmed.length() > 100) {
            return true;
        }

        return !SEARCH_PATTERN.matcher(trimmed).matches();
    }


    public String[] decideCharacter(ReportBlock<Map<String, Map<String, List<ReadReportGroup>>>> block) {

        String forCharacter[] = new String[2];

        if (block == null || !"OK".equals(block.getCode())) {
            forCharacter[0] = "empty_data";
            forCharacter[1] = "empty_data";
            return forCharacter;
        }

        Map<String, Map<String, List<ReadReportGroup>>> data = block.getData();

        HashMap<String, Integer> categoryNum = new HashMap<>();
        HashMap<Place, Integer> placeNum = new HashMap<>();

        LocalDateTime current = LocalDateTime.now();
        String year = String.valueOf(current.getYear());
        String month = String.valueOf(current.getMonthValue());

        List<ReadReportGroup> reportData = data.get(year).get(month);
        if (reportData.isEmpty()) {
            forCharacter[0] = "empty_data";
            forCharacter[1] = "empty_data";
        } else {
            for (ReadReportGroup r : reportData) {
                String currentCategory = r.getCategory();
                Place currentPlace = r.getPlace();
                categoryNum.merge(currentCategory, 1, Integer::sum);
                placeNum.merge(currentPlace, 1, Integer::sum);
            }

            String mostCategory = categoryNum.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            Place mostPlace = placeNum.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            if (mostPlace != null) {
                forCharacter[0] = mostPlace.name();
            } else {
                forCharacter[0] = "empty_data";
            }
            if (mostCategory != null) {
                forCharacter[1] = tendencyMapping.mapCategory(mostCategory);
            } else {
                forCharacter[1] = "empty_data";
            }
        }

        return forCharacter;
    }
}
