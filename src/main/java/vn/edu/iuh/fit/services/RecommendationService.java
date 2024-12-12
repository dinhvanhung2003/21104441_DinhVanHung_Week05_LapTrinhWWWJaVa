package vn.edu.iuh.fit.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {

    public List<Map<String, Object>> getRecommendedJobs(String candidateSkills) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:5000/recommend";

        // Gửi yêu cầu POST đến Flask API
        Map<String, String> request = new HashMap<>();
        request.put("candidate_skills", candidateSkills);

        // Nhận danh sách công việc gợi ý
        ResponseEntity<List> response = restTemplate.postForEntity(url, request, List.class);
        return response.getBody();
    }
}

