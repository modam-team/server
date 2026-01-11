package com.example.modam.global.utils;

import com.example.modam.domain.report.Domain.Place;
import com.example.modam.domain.report.Presentation.dto.ReadReportGroup;
import com.example.modam.domain.report.Presentation.dto.ReportBlock;
import com.example.modam.domain.report.Presentation.dto.ReportGroup;
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


    public String[] decideCharacter(ReportBlock<Map<String, Map<String, List<ReadReportGroup>>>> log,
                                    ReportBlock<Map<String, Map<String, List<ReportGroup>>>> finish) {

        String forCharacter[] = new String[2];

        if (log == null || !"OK".equals(log.getCode()) || finish == null || !"OK".equals(finish.getCode())) {
            forCharacter[0] = "empty_data";
            forCharacter[1] = "empty_data";
            return forCharacter;
        }

        Map<String, Map<String, List<ReadReportGroup>>> logData = log.getData();
        Map<String, Map<String, List<ReportGroup>>> finData = finish.getData();

        HashMap<String, Integer> categoryNum = new HashMap<>();
        HashMap<Place, Integer> placeNum = new HashMap<>();

        LocalDateTime current = LocalDateTime.now();
        int numMonth = current.getMonthValue() - 1;
        int numYear = current.getYear();
        if (numMonth == 0) {
            numMonth = 12;
            numYear = numYear - 1;
        }
        String year = String.valueOf(numYear);
        String month = String.valueOf(numMonth);

        Map<String, List<ReadReportGroup>> yearLogData = logData.get(year);
        Map<String, List<ReportGroup>> yearFinData = finData.get(year);
        if (yearLogData == null || yearFinData == null) {
            forCharacter[0] = "empty_data";
            forCharacter[1] = "empty_data";
            return forCharacter;
        }

        List<ReadReportGroup> placeData = yearLogData.get(month);
        List<ReportGroup> categoryData = yearFinData.get(month);
        if (placeData == null || placeData.isEmpty() || categoryData == null || categoryData.isEmpty()) {
            forCharacter[0] = "empty_data";
            forCharacter[1] = "empty_data";
            return forCharacter;
        }

        for (ReadReportGroup r : placeData) {
            Place currentPlace = r.getPlace();
            placeNum.merge(currentPlace, 1, Integer::sum);
        }
        for (ReportGroup r : categoryData) {
            String currentCategory = r.getCategory();
            categoryNum.merge(currentCategory, 1, Integer::sum);
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

        return forCharacter;
    }
}
