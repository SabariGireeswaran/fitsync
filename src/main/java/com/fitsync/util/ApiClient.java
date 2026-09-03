package com.fitsync.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import com.fitsync.config.AppConfig;

/**
 * Minimal client for the Google Gemini {@code generateContent} REST API,
 * built on the java.net.http.HttpClient that ships with Java 11+. No
 * external JSON library is used - the request body is assembled by hand
 * and the response text is extracted with simple string parsing.
 *
 * <p>When the model is overloaded (HTTP 503) or rate-limited (HTTP 429)
 * the request is retried a few times, then the call falls back to the
 * secondary model URL before giving up.
 */
public class ApiClient {

    /** How many times a single model URL is tried before falling back. */
    private static final int MAX_ATTEMPTS = 3;
    /** Pause between attempts on a retryable error. */
    private static final Duration RETRY_DELAY = Duration.ofSeconds(2);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    /**
     * Sends the given prompt to Gemini and returns the plain-text reply.
     * On any failure a human-readable message is returned instead of
     * throwing, so callers can display it directly.
     */
    public String getRecommendation(String prompt) {
        return getRecommendation(prompt, msg -> { });
    }

    /**
     * Same as {@link #getRecommendation(String)} but reports progress
     * (e.g. "Gemini is busy, retrying...") to {@code onProgress} so the
     * caller can surface it in the UI while the call is in flight.
     */
    public String getRecommendation(String prompt, Consumer<String> onProgress) {
        if (AppConfig.GEMINI_API_KEY == null
                || AppConfig.GEMINI_API_KEY.isBlank()
                || AppConfig.GEMINI_API_KEY.equals("your-gemini-key-here")) {
            return "AI recommendations are not available yet.\n\n"
                 + "A Google Gemini API key has not been configured. Set the "
                 + "GEMINI_API_KEY environment variable to enable the AI Wellness Advisor.";
        }

        String requestBody = buildRequestBody(prompt);
        List<String> modelUrls = List.of(
                AppConfig.GEMINI_API_URL, AppConfig.GEMINI_API_URL_FALLBACK);

        String lastError = null;
        boolean sawBusy = false;

        for (int m = 0; m < modelUrls.size(); m++) {
            boolean isFallback = m > 0;
            String url = modelUrls.get(m) + "?key=" + AppConfig.GEMINI_API_KEY;

            for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
                Outcome outcome = tryOnce(url, requestBody);

                if (outcome.text != null) {
                    return outcome.text;
                }
                if (!outcome.retryable) {
                    // 400/403/404 etc. - retrying the same model is pointless,
                    // but the fallback model may still work.
                    lastError = outcome.error;
                    break;
                }

                sawBusy = true;
                lastError = outcome.error;
                if (attempt < MAX_ATTEMPTS) {
                    onProgress.accept((isFallback ? "Primary model busy - trying backup. " : "")
                            + "Gemini is busy, retrying... (attempt "
                            + attempt + " of " + MAX_ATTEMPTS + ")");
                    if (!sleep(RETRY_DELAY)) {
                        return "The AI request was interrupted. Please try again.";
                    }
                } else if (!isFallback) {
                    onProgress.accept("Primary model still busy - trying backup model...");
                }
            }
        }

        if (sawBusy) {
            return "AI service is currently busy. Please click Refresh in a few seconds.";
        }
        return "The AI service returned an error.\n\n"
             + (lastError != null ? lastError : "Please try again.");
    }

    /** Result of a single HTTP attempt: exactly one of {@code text}/{@code error} is set. */
    private static final class Outcome {
        final String text;      // non-null on success
        final String error;     // non-null on failure
        final boolean retryable;

        private Outcome(String text, String error, boolean retryable) {
            this.text = text;
            this.error = error;
            this.retryable = retryable;
        }

        static Outcome ok(String text) { return new Outcome(text, null, false); }
        static Outcome retry(String error) { return new Outcome(null, error, true); }
        static Outcome fail(String error) { return new Outcome(null, error, false); }
    }

    private Outcome tryOnce(String url, String requestBody) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(60))
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            if (status == 200) {
                String text = extractText(response.body());
                return Outcome.ok(text.isBlank()
                        ? "The AI service returned an empty response. Please try again."
                        : text);
            }

            String detail = "The AI service returned an error (HTTP " + status + ").\n\n"
                    + extractErrorMessage(response.body());
            // 429 (rate limited), 500, 503 (overloaded) are worth retrying.
            boolean retryable = status == 429 || status == 500 || status == 503;
            return retryable ? Outcome.retry(detail) : Outcome.fail(detail);

        } catch (IOException e) {
            return Outcome.retry("Could not reach the AI service. Please check your "
                    + "internet connection and try again.\n\nDetails: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Outcome.fail("The AI request was interrupted. Please try again.");
        } catch (RuntimeException e) {
            return Outcome.fail("An unexpected error occurred while fetching the "
                    + "recommendation.\n\nDetails: " + e.getMessage());
        }
    }

    /** Sleeps for {@code d}; returns false if the thread was interrupted. */
    private boolean sleep(Duration d) {
        try {
            Thread.sleep(d.toMillis());
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Gemini request shape:
     * {"contents":[{"parts":[{"text":"..."}]}]}
     */
    private String buildRequestBody(String prompt) {
        return "{"
             + "\"contents\":[{\"parts\":[{\"text\":\""
             + escapeJson(prompt) + "\"}]}]"
             + "}";
    }

    /**
     * Pulls the reply out of the Gemini response JSON, which looks like:
     * {"candidates":[{"content":{"parts":[{"text":"..."}],"role":"model"}}]}
     */
    private String extractText(String json) {
        if (json == null || json.isBlank()) {
            return "";
        }
        // If the prompt was blocked there are no candidates.
        if (json.indexOf("\"candidates\"") < 0 && json.indexOf("\"blockReason\"") >= 0) {
            return "The request was blocked by the AI service's safety filters. "
                 + "Please try again.";
        }
        int partsIdx = json.indexOf("\"parts\"");
        int start = valueStart(json, "text", partsIdx >= 0 ? partsIdx : 0);
        return start < 0 ? "" : readJsonString(json, start);
    }

    private String extractErrorMessage(String json) {
        if (json == null || json.isBlank()) {
            return "No error details were provided.";
        }
        int start = valueStart(json, "message", 0);
        if (start < 0) {
            return json.length() > 500 ? json.substring(0, 500) + "..." : json;
        }
        return readJsonString(json, start);
    }

    /**
     * Finds {@code "key"} at or after {@code from}, then skips the colon and
     * any whitespace to return the index just after the opening quote of the
     * string value. Returns -1 if the key or a string value is not found.
     */
    private int valueStart(String json, String key, int from) {
        int k = json.indexOf("\"" + key + "\"", Math.max(from, 0));
        if (k < 0) {
            return -1;
        }
        int i = k + key.length() + 2;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
            i++;
        }
        if (i >= json.length() || json.charAt(i) != ':') {
            return -1;
        }
        i++;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
            i++;
        }
        return (i < json.length() && json.charAt(i) == '"') ? i + 1 : -1;
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
