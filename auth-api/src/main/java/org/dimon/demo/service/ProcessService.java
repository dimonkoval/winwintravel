package org.dimon.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.dimon.demo.dto.ProcessingLogDto;
import org.dimon.demo.exception.TextProcessingException;
import org.dimon.demo.log.LoggingInterceptor;
import org.dimon.demo.model.ProcessingLog;
import org.dimon.demo.model.User;
import org.dimon.demo.repository.ProcessingLogRepository;
import org.dimon.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ProcessService {
    private static final Logger logger = LoggerFactory.getLogger(ProcessService.class);

    private final RestTemplate restTemplate;
    private final ProcessingLogRepository logRepo;
    private final String dataApiUrl;
    private final String internalToken;
    private final UserRepository userRepository;

    public ProcessService(ProcessingLogRepository logRepo,
                          UserRepository userRepository,
                          @Value("${internal.token}") String internalToken,
                          @Value("${data.api.url:http://data-api:8081/api/transform}") String dataApiUrl) {

        this.logRepo = logRepo;
        this.userRepository = userRepository;
        this.internalToken = internalToken;
        this.dataApiUrl = dataApiUrl;
        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add(new LoggingInterceptor());
    }

    @Transactional
    public String processText(String text) {
        logger.info("Processing text: {}", text);
        logger.info("Using internal token: {}", internalToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-Token", internalToken);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(
                Map.of("text", text),
                headers
        );

        try {
            logger.info("Sending request to: {}", dataApiUrl);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    dataApiUrl,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>() {}
            );
            Map<String, Object> body = response.getBody();

            if (body == null || !body.containsKey("result")) {
                throw new TextProcessingException("Empty or invalid response from data-api");
            }

            String result = body.get("result").toString();
            logger.info("Received result: {}", result);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
            ProcessingLog log = ProcessingLog.builder()
                    .user(user)
                    .inputText(text)
                    .outputText(result)
                    .createdAt(LocalDateTime.now())
                    .build();
            logRepo.save(log);
            logger.info("Saved: {}, {}", log.getUser(), log.getInputText());
            return result;
        } catch (Exception e) {
            throw new TextProcessingException("Failed to process text due to internal error", e);
        }
    }

    public List<ProcessingLogDto> getAllLogs() {
        return logRepo.findAll().stream()
                .map(log -> new ProcessingLogDto(
                        log.getId(),
                        log.getUser().getUsername(),
                        log.getInputText(),
                        log.getOutputText(),
                        log.getCreatedAt()
                ))
                .toList();
    }
}