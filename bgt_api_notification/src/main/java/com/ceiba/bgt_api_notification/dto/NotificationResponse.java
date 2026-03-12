package com.ceiba.bgt_api_notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private String messageId;
    private String subscriptionArn;
    private String status;
    private String detail;
}
