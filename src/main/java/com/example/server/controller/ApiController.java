package com.example.server.controller;

import com.example.server.dto.CreateNews;
import com.example.server.testinterface.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ApiController {

    @Autowired
    private TestMapper testMapper;

    @GetMapping("/api/createNews/list")
    @ResponseBody
    public ResponseEntity<?> getNews(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        try {
            int offset = page * size;
            List<CreateNews> result = testMapper.selectCreateNewsListPaginated(offset, size);

            List<CreateNews> filteredResult = result.stream()
                    .filter(news -> news.getTitle() != null && !news.getTitle().isEmpty()
                            && news.getDescription() != null && !news.getDescription().isEmpty())
                    .toList();

            long totalCount = testMapper.countCreateNews();
            int totalPages = (int) Math.ceil((double) totalCount / size);

            Map<String, Object> response = new HashMap<>();
            response.put("content", filteredResult);
            response.put("currentPage", page);
            response.put("totalItems", totalCount);
            response.put("totalPages", totalPages);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("데이터베이스 연결 실패: " + e.getMessage());
        }
    }
}
