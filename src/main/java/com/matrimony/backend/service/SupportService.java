package com.matrimony.backend.service;

import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.SupportRequests;
import org.springframework.data.domain.Pageable;

public interface SupportService {
    Object createTicket(SupportRequests.TicketRequest request);

    PageResponse<?> myTickets(Pageable pageable);

    Object ticket(Long id);

    Object addMessage(Long id, SupportRequests.TicketMessageRequest request);

    void closeTicket(Long id);

    void feedback(SupportRequests.FeedbackRequest request);

    void contactUs(SupportRequests.ContactUsRequest request);

    Object createSuccessStory(SupportRequests.SuccessStoryRequest request);

    PageResponse<?> publicStories(Pageable pageable);

    Object story(Long id);

    PageResponse<?> myStories(Pageable pageable);

    Object updateStory(Long id, SupportRequests.SuccessStoryRequest request);

    void deleteStory(Long id);
}
