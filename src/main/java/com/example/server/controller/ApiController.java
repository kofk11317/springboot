package com.example.server.controller;

import com.example.server.dto.CreateNews;
import com.example.server.dto.Member;
import com.example.server.service.JwtService;
import com.example.server.testinterface.TestMapper;
import com.example.server.token.JwtIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ApiController {

    @Autowired
    private TestMapper testMapper;

    @Autowired
    private JwtIssueService jwtIssueService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/api/user/validation/id")
    @ResponseBody
    public ResponseEntity<?> checkIDExists(@RequestParam String id) {
        Map<String, String> response = new HashMap<>();
        try {
            if (testMapper.checkIDExists(id) > 0) {
                response.put("result", "중복");
            } else {
                response.put("result", "사용가능");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "데이터베이스 연결 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/api/user/validation/email")
    @ResponseBody
    public ResponseEntity<?> checkEmailExists(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        try {
            if (testMapper.checkEmailExists(email) > 0) {
                response.put("result", "중복");
            } else {
                response.put("result", "사용가능");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "데이터베이스 연결 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

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

    @GetMapping("/api/createNews/list/{category}")
    @ResponseBody
    public ResponseEntity<?> getCreateNewsListByCategory(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @PathVariable String category) {
        try {
            int offset = page * size;

            String mappedCategory;
            switch (category) {
                case "culture":
                    mappedCategory = "생활/문화";
                    break;
                case "society":
                    mappedCategory = "정치/사회";
                    break;
                case "science":
                    mappedCategory = "IT과학";
                    break;
                default:
                    return ResponseEntity.badRequest().body("Invalid category");
            }

            List<CreateNews> result = testMapper.selectCreateNewsListByCategoryPaginated(offset, size, mappedCategory);

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

    @GetMapping("/api/createNews/list/search")
    @ResponseBody
    public ResponseEntity<?> getCreateNewsListBySearch(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam String query) {
        try {
            int offset = page * size;

            List<CreateNews> result = testMapper.selectCreateNewsListBySearchPaginated(offset, size, query);

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
    public ResponseEntity<?> getCreateNewsDetail(@PathVariable int id, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader != null) {
                String token = jwtService.extractToken(authHeader);

                // member 가져와서 member_join에 insert 하기
            }

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


    @PostMapping("/api/createNews/like/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateLikeToNews(@PathVariable int id, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtService.extractToken(authHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 제공되지 않았습니다.");
            }

            String userId = jwtService.validateTokenAndGetUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
            }

            Member member = testMapper.findByUserId(userId);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }

            Integer result = testMapper.selectLike(member.getMemberNum(), id);


            if(result == null) { // result가 0이면 like 가 없다는 뜻
                testMapper.insertLike(member.getMemberNum(), id, 1);
            } else if(result == 1) { // LIKE_OR_NOT을 3으로
                testMapper.updateLike(member.getMemberNum(), id, 3);
            } else if(result == 2 || result == 3) { // LIKE_OR_NOT을 1로
                testMapper.updateLike(member.getMemberNum(), id, 1);
            }

            return ResponseEntity.ok("좋아요 업데이트 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("데이터베이스 연결 실패: " + e.getMessage());
        }
    }

    @PostMapping("/api/createNews/dislike/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateDislikeToNews(@PathVariable int id, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtService.extractToken(authHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 제공되지 않았습니다.");
            }

            String userId = jwtService.validateTokenAndGetUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
            }

            Member member = testMapper.findByUserId(userId);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }

            Integer result = testMapper.selectLike(member.getMemberNum(), id);

            if(result == null) { // result가 0이면 like 가 없다는 뜻
                testMapper.insertLike(member.getMemberNum(), id, 2);
            } else if(result == 2) { // LIKE_OR_NOT을 3으로
                testMapper.updateLike(member.getMemberNum(), id, 3);
            } else if(result == 1 || result == 3) { // LIKE_OR_NOT을 2로
                testMapper.updateLike(member.getMemberNum(), id, 2);
            }

            return ResponseEntity.ok("싫어요 업데이트 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("데이터베이스 연결 실패: " + e.getMessage());
        }
    }

    @GetMapping("/api/createNews/like/get/{id}")
    @ResponseBody
    public ResponseEntity<?> getLikeFromNews(@PathVariable int id) {
        try {
            int result = testMapper.selectAllLike(id);
            Map<String, Integer> response = new HashMap<>();
            response.put("count", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "데이터베이스 연결 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/api/createNews/dislike/get/{id}")
    @ResponseBody
    public ResponseEntity<?> getDislikeFromNews(@PathVariable int id) {
        try {
            int result = testMapper.selectAllDislike(id);
            Map<String, Integer> response = new HashMap<>();
            response.put("count", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "데이터베이스 연결 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}
