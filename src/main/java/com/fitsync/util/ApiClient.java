package com.fitsync.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.fitsync.config.AppConfig;

/**
 * Minimal client for the Anthropic Claude Messages API built on the
 * java.net.http.HttpClient that ships with Java 11+. No external JSON
 * library is used - the request body is assembled by hand and the
 * response text is extracted with simple string parsing.
 */
public class ApiClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    /**
     * Sends the given prompt to Claude and returns the plain-text reply.
     * On any failure a human-readable message is returned instead of
     * throwing, so callers can display it directly.
     */
    public String getRecommendation(String prompt) {
        if (AppConfig.API_KEY == null
                || AppConfig.API_KEY.isBlank()
                || AppConfig.API_KEY.equals("your-api-key-here")) {
            return "AI recommendations are not available yet.\n\n"
                 + "An Anthropic API key has not been configured. Add your key to "
                 + "AppConfig.API_KEY to enable the AI Wellness Advisor.";
        }

        try {
            String requestBody = buildRequestBody(prompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AppConfig.ANTHROPIC_API_URL))
                    .timeout(Duration.ofSeconds(60))
                    .header("x-api-key", AppConfig.API_KEY)
                    .header("anthropic-version", AppConfig.ANTHROPIC_VERSION)
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return "The AI service returned an error (HTTP "
                     + response.statusCode() + ").\n\n"
                     + extractErrorMessage(response.body());
            }

            String text = extractText(response.body());
            return text.isBlank()
                    ? "The AI service returned an empty response. Please try again."
                    : text;

        } catch (IOException e) {
            return "Could not reach the AI service. Please check your internet "
                 + "connection and try again.\n\nDetails: " + e.getMessage();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "The AI request was interrupted. Please try again.";
        } catch (RuntimeException e) {
            return "An unexpected error occurred while fetching the recommendation.\n\n"
                 + "Details: " + e.getMessage();
        }
    }

    private String buildRequestBody(String prompt) {
        return "{"
             + "\"model\":\"" + AppConfig.ANTHROPIC_MODEL + "\","
             + "\"max_tokens\":2048,"
             + "\"messages\":[{\"role\":\"user\",\"content\":\""
             + escapeJson(prompt) + "\"}]"
             + "}";
    }

    /**
     * Pulls the first text block out of the Claude response JSON, which
     * looks like: {"content":[{"type":"text","text":"..."}], ...}
     */
    private String extractText(String json) {
        int typeIdx = json.indexOf("\"type\":\"text\"");
        int searchFrom = typeIdx >= 0 ? typeIdx : 0;
        int key = json.indexOf("\"text\":\"", searchFrom);
        if (key < 0) {
            return "";
        }
        return readJsonString(json, key + "\"text\":\"".length());
    }

    private String extractErrorMessage(String json) {
        if (json == null || json.isBlank()) {
            return "No error details were provided.";
        }
        int key = json.indexOf("\"message\":\"");
        if (key < 0) {
            return json.length() > 500 ? json.substring(0, 500) + "..." : json;
        }
        return readJsonString(json, key + "\"message\":\"".length());
    }

    /**
     * Reads a JSON string value starting at {@code start} (just after the
     * opening quote) up to the next unescaped quote, decoding escapes.
     */
    private String readJsonString(String json, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(++i);
                switch (next) {
                    case 'n':  sb.append('\n'); break;
                    case 't':  sb.append('\t'); break;
                    case 'r':  sb.append('\r'); break;
                    case 'b':  sb.append('\b'); break;
                    case 'f':  sb.append('\f'); break;
                    case '"':  sb.append('"');  break;
                    case '\\': sb.append('\\'); break;
                    case '/':  sb.append('/');  break;
                    case 'u':
                        if (i + 4 < json.length()) {
                            try {
                                sb.append((char) Integer.parseInt(
                                        json.substring(i + 1, i + 5), 16));
                                i += 4;
                            } catch (NumberFormatException ex) {
                                sb.append(next);
                            }
                        }
                        break;
                    default: sb.append(next);
                }
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String escapeJson(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}
