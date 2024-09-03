package com.kcesarp.applistcomprasia;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface OpenAIApi {

    @POST("v1/chat/completions")
    Call<ChatCompletionResponse> createChatCompletion(
            @Header("Authorization") String apiKey,
            @Body ChatCompletionRequest request
    );

    class ChatCompletionRequest {
        private final String model;
        private final List<Message> messages;

        public ChatCompletionRequest(String model, List<Message> messages) {
            this.model = model;
            this.messages = messages;
        }
    }

    class Message {
        private final String role;
        private final String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    class ChatCompletionResponse {
        private List<Choice> choices;

        public List<Choice> getChoices() {
            return choices;
        }

        public static class Choice {
            private Message message;

            public Message getMessage() {
                return message;
            }
        }

        public static class Message {
            private String content;

            public String getContent() {
                return content;
            }
        }
    }
}
