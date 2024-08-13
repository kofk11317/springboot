package com.example.server.controller;

import com.example.server.dto.CreateNews;
import com.example.server.testinterface.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ApiController {

    @Autowired
    private TestMapper testMapper;

    @GetMapping("/api/createNews/list")
    @ResponseBody
    public ResponseEntity<?> getCreateNewsList(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        try {
            int offset = page * size;
            List<CreateNews> result = testMapper.selectCreateNewsListPaginated(offset, size);

            List<CreateNews> filteredResult = result.stream()
                    .filter(news -> news.getTitle() != null && !news.getTitle().isEmpty()
                            && news.getDescription() != null && !news.getDescription().isEmpty())
                    .toList();

            for (CreateNews news : filteredResult) {
                if (news.getThumbnailData() != null) {
                    news.setThumbnailURL("/api/createNews/thumbnail/" + news.getCreateNewsNum());
                }
            }

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

    @GetMapping("/api/createNews/detail/{id}")
    @ResponseBody
    public ResponseEntity<?> getCreateNewsDetail(@PathVariable int id) {
        try {
            CreateNews createNews = testMapper.selectCreateNewsDetail(id);
            if (createNews != null) {
                if(createNews.getThumbnailData() != null) {
                    String base64Image = Base64.getEncoder().encodeToString(createNews.getThumbnailData());
                    createNews.setThumbnailData(base64Image.getBytes());
                    createNews.setThumbnailURL("/api/createNews/thumbnail/" + id);
                }
                return ResponseEntity.ok(createNews);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("해당 ID의 뉴스를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("데이터베이스 연결 실패: " + e.getMessage());
        }
    }

    @GetMapping("/api/createNews/thumbnail/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable int id) {
        CreateNews news = testMapper.selectCreateNewsDetail(id);
        if (news != null && news.getThumbnailData() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // 또는 적절한 미디어 타입
                    .body(news.getThumbnailData());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/api/createNews/trend/list")
    @ResponseBody
    public ResponseEntity<?> getTrendNewsList() {
        try {
            List<CreateNews> result = testMapper.selectTrendNewsList();

            List<CreateNews> filteredResult = result.stream()
                    .filter(news -> news.getTitle() != null && !news.getTitle().isEmpty()
                            && news.getDescription() != null && !news.getDescription().isEmpty())
                    .toList();

            // 최대 3개의 항목만 선택
            List<CreateNews> limitedResult = filteredResult.stream()
                    .limit(3)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(limitedResult);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("데이터베이스 연결 실패: " + e.getMessage());
        }
    }

    @GetMapping("/api/createNews/joind/update/{id}")
    public ResponseEntity<String> updateCreateNewsJoind(@PathVariable int id) {
        try {
            testMapper.updateCreateNewsJoind(id);
            return ResponseEntity.ok("Update completed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error occurred during update: " + e.getMessage());
        }
    }
}
