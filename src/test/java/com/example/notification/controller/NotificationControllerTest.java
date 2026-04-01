package com.example.notification.controller;

import com.example.notification.dto.CreateEmailNotificationRequest;
import com.example.notification.dto.NotificationResponse;
import com.example.notification.entity.NotificationStatus;
import com.example.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    void createEmailNotification_returnsCreatedResponse() throws Exception {
        NotificationResponse response = new NotificationResponse(
                100L,
                "j_n_camp@hotmail.com",
                "Controller test",
                "welcome-email",
                NotificationStatus.PENDING,
                Integer.valueOf(0),
                null,
                OffsetDateTime.parse("2026-03-30T12:00:00Z")
        );

        when(notificationService.createEmailNotification(any(CreateEmailNotificationRequest.class)))
                .thenReturn(response);

        String requestJson = """
                {
                  "to": "j_n_camp@hotmail.com",
                  "subject": "Controller test",
                  "templateId": "welcome-email",
                  "templateData": "{\\"firstName\\":\\"John\\"}"
                }
                """;

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.to").value("j_n_camp@hotmail.com"))
                .andExpect(jsonPath("$.subject").value("Controller test"))
                .andExpect(jsonPath("$.templateId").value("welcome-email"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.retryCount").value(0))
                .andExpect(jsonPath("$.lastError").doesNotExist());
    }
}
