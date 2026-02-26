package study.all.aiintegration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * AI服务集成服务类
 * 演示如何在Java应用中集成AI能力
 */
public class AIIntegrationService {

    private final ObjectMapper objectMapper;

    public AIIntegrationService() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 调用Python AI服务进行文本分类
     */
    public String classifyText(String text) {
        try {
            // 构造请求
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("text", text);
            
            // 调用Python Flask API
            String pythonApiUrl = "http://localhost:5000/api/classify";
            String jsonResponse = sendPostRequest(pythonApiUrl, objectMapper.writeValueAsString(requestBody));
            Map<String, Object> response = objectMapper.readValue(jsonResponse, Map.class);
            
            return (String) response.get("classification");
        } catch (Exception e) {
            // 降级处理
            return fallbackClassification(text);
        }
    }

    /**
     * 降级分类方法
     */
    private String fallbackClassification(String text) {
        // 简单的基于关键词的分类
        if (text.contains("好") || text.contains("棒") || text.contains("喜欢")) {
            return "positive";
        } else if (text.contains("坏") || text.contains("差") || text.contains("讨厌")) {
            return "negative";
        }
        return "neutral";
    }

    /**
     * 图像处理服务调用
     */
    public ProcessedImageResult processImage(byte[] imageData) {
        try {
            // 这里应该调用实际的图像处理服务
            // 示例返回模拟结果
            return ProcessedImageResult.builder()
                .objectsDetected(5)
                .processingTime(120)
                .success(true)
                .build();
        } catch (Exception e) {
            return ProcessedImageResult.builder()
                .success(false)
                .errorMessage(e.getMessage())
                .build();
        }
    }

    /**
     * 聊天机器人服务
     */
    public ChatResponse chatWithBot(String userInput) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("message", userInput);
            
            String chatApiUrl = "http://localhost:5000/api/chat";
            String jsonResponse = sendPostRequest(chatApiUrl, objectMapper.writeValueAsString(requestBody));
            Map<String, Object> response = objectMapper.readValue(jsonResponse, Map.class);
            
            return ChatResponse.builder()
                .response((String) response.get("reply"))
                .confidence(((Number) response.get("confidence")).doubleValue())
                .build();
        } catch (Exception e) {
            // 返回默认回复
            return ChatResponse.builder()
                .response("抱歉，我现在无法回答您的问题。")
                .confidence(0.0)
                .build();
        }
    }

    /**
     * 发送HTTP POST请求
     */
    private String sendPostRequest(String urlString, String jsonPayload) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        
        return response.toString();
    }

    /**
     * 处理图像结果类
     */
    public static class ProcessedImageResult {
        private boolean success;
        private int objectsDetected;
        private long processingTime;
        private String errorMessage;

        // 构造器模式
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private ProcessedImageResult result = new ProcessedImageResult();

            public Builder success(boolean success) {
                result.success = success;
                return this;
            }

            public Builder objectsDetected(int objectsDetected) {
                result.objectsDetected = objectsDetected;
                return this;
            }

            public Builder processingTime(long processingTime) {
                result.processingTime = processingTime;
                return this;
            }

            public Builder errorMessage(String errorMessage) {
                result.errorMessage = errorMessage;
                return this;
            }

            public ProcessedImageResult build() {
                return result;
            }
        }

        // Getters
        public boolean isSuccess() { return success; }
        public int getObjectsDetected() { return objectsDetected; }
        public long getProcessingTime() { return processingTime; }
        public String getErrorMessage() { return errorMessage; }
    }

    /**
     * 聊天响应类
     */
    public static class ChatResponse {
        private String response;
        private double confidence;

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private ChatResponse responseObj = new ChatResponse();

            public Builder response(String response) {
                responseObj.response = response;
                return this;
            }

            public Builder confidence(double confidence) {
                responseObj.confidence = confidence;
                return this;
            }

            public ChatResponse build() {
                return responseObj;
            }
        }

        // Getters
        public String getResponse() { return response; }
        public double getConfidence() { return confidence; }
    }
}