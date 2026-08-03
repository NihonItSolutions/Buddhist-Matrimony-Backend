package com.matrimony.backend.service;

import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.InteractionRequests;
import com.matrimony.backend.dto.response.InteractionResponses.*;
import com.matrimony.backend.dto.response.ProfileCardResponse;
import com.matrimony.backend.enums.NotificationType;
import org.springframework.data.domain.Pageable;

public interface InteractionService {
    InterestResponse sendInterest(String receiverMatrimonyId, InteractionRequests.InterestMessage request);

    PageResponse<InterestResponse> sentInterests(Pageable pageable);

    PageResponse<InterestResponse> receivedInterests(Pageable pageable);

    PageResponse<InterestResponse> interestsByStatus(String status, Pageable pageable);

    InterestResponse acceptInterest(Long interestId);

    InterestResponse declineInterest(Long interestId);

    InterestResponse cancelInterest(Long interestId);

    void deleteInterest(Long interestId);

    void shortlist(String matrimonyId);

    PageResponse<ProfileCardResponse> shortlists(Pageable pageable);

    void removeShortlist(String matrimonyId);

    BlockResponse block(String matrimonyId, InteractionRequests.BlockRequest request);

    PageResponse<BlockResponse> blocks(Pageable pageable);

    void unblock(String matrimonyId);

    ReportResponse report(String matrimonyId, InteractionRequests.ReportRequest request);

    PageResponse<ReportResponse> myReports(Pageable pageable);

    ContactRequestResponse requestContact(String matrimonyId);

    PageResponse<ContactRequestResponse> sentContactRequests(Pageable pageable);

    PageResponse<ContactRequestResponse> receivedContactRequests(Pageable pageable);

    ContactRequestResponse approveContact(Long requestId);

    ContactRequestResponse rejectContact(Long requestId);

    ContactRequestResponse cancelContact(Long requestId);

    ContactDetailsResponse contactDetails(String matrimonyId);

    PageResponse<ConversationResponse> conversations(Pageable pageable);

    ConversationResponse createConversation(String matrimonyId);

    PageResponse<MessageResponse> messages(Long conversationId, Pageable pageable);

    MessageResponse sendMessage(Long conversationId, InteractionRequests.MessageRequest request);

    void markConversationRead(Long conversationId);

    void deleteMessage(Long messageId);

    PageResponse<NotificationResponse> notifications(NotificationType type, Pageable pageable);

    long unreadNotificationCount();

    void readNotification(Long id);

    void readAllNotifications();

    void deleteNotification(Long id);
}
