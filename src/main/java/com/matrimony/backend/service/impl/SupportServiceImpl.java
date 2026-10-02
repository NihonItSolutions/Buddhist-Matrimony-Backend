package com.matrimony.backend.service.impl;

import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.SupportRequests;
import com.matrimony.backend.entity.*;
import com.matrimony.backend.enums.SuccessStoryStatus;
import com.matrimony.backend.enums.SupportTicketStatus;
import com.matrimony.backend.exception.ForbiddenOperationException;
import com.matrimony.backend.exception.ResourceNotFoundException;
import com.matrimony.backend.repository.*;
import com.matrimony.backend.security.CurrentUser;
import com.matrimony.backend.service.SupportService;
import com.matrimony.backend.util.TextSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {
    private final CurrentUser currentUser;
    private final SupportTicketRepository ticketRepository;
    private final SupportTicketMessageRepository messageRepository;
    private final SuccessStoryRepository successStoryRepository;
    private final MatrimonyProfileRepository profileRepository;

    @Override
    @Transactional
    public Object createTicket(SupportRequests.TicketRequest request) {
        SupportTicket ticket = new SupportTicket();
        ticket.setUser(currentUser.get());
        ticket.setCategory(request.category());
        ticket.setSubject(request.subject());
        ticket.setDescription(TextSanitizer.clean(request.description()));
        ticketRepository.save(ticket);
        return ticketResponse(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<?> myTickets(Pageable pageable) {
        return PageResponse.from(ticketRepository.findByUserId(currentUser.get().getId(), pageable).map(this::ticketResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public Object ticket(Long id) {
        return ticketResponse(ownedTicket(id));
    }

    @Override
    @Transactional
    public Object addMessage(Long id, SupportRequests.TicketMessageRequest request) {
        SupportTicket ticket = ownedTicket(id);
        SupportTicketMessage message = new SupportTicketMessage();
        message.setTicket(ticket);
        message.setSender(currentUser.get());
        message.setMessage(TextSanitizer.clean(request.message()));
        messageRepository.save(message);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", message.getId());
        response.put("ticketId", ticket.getId());
        response.put("message", message.getMessage());
        response.put("createdAt", message.getCreatedAt());
        return response;
    }

    @Override
    @Transactional
    public void closeTicket(Long id) {
        SupportTicket ticket = ownedTicket(id);
        ticket.setStatus(SupportTicketStatus.CLOSED);
        ticket.setResolvedAt(java.time.LocalDateTime.now());
    }

    @Override
    @Transactional
    public void feedback(SupportRequests.FeedbackRequest request) {
        SupportTicket ticket = new SupportTicket();
        ticket.setUser(currentUser.get());
        ticket.setCategory("FEEDBACK");
        ticket.setSubject("Feedback");
        ticket.setDescription(TextSanitizer.clean(request.message()));
        ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void contactUs(SupportRequests.ContactUsRequest request) {
        SupportTicket ticket = new SupportTicket();
        ticket.setUser(currentUser.get());
        ticket.setCategory("CONTACT_US");
        ticket.setSubject("Contact from " + request.name());
        ticket.setDescription(TextSanitizer.clean(request.email() + "\n" + request.message()));
        ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public Object createSuccessStory(SupportRequests.SuccessStoryRequest request) {
        SuccessStory story = new SuccessStory();
        story.setSubmittedBy(currentUser.get());
        story.setBrideName(request.brideName());
        story.setGroomName(request.groomName());
        story.setStory(TextSanitizer.clean(request.story()));
        story.setMarriageDate(request.marriageDate());
        story.setPhotoUrl(request.photoUrl());
        if (request.brideMatrimonyId() != null) story.setBrideProfile(profileRepository.findByUserMatrimonyId(request.brideMatrimonyId()).orElse(null));
        if (request.groomMatrimonyId() != null) story.setGroomProfile(profileRepository.findByUserMatrimonyId(request.groomMatrimonyId()).orElse(null));
        successStoryRepository.save(story);
        return storyResponse(story);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<?> publicStories(Pageable pageable) {
        return PageResponse.from(successStoryRepository.findByStatus(SuccessStoryStatus.APPROVED, pageable).map(this::storyResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<?> allStories(Pageable pageable) {
        return PageResponse.from(successStoryRepository.findAll(pageable).map(this::storyResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public Object story(Long id) {
        SuccessStory story = successStoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Success story not found"));
        if (story.getStatus() != SuccessStoryStatus.APPROVED) {
            throw new ResourceNotFoundException("Success story not found");
        }
        return storyResponse(story);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<?> myStories(Pageable pageable) {
        return PageResponse.from(successStoryRepository.findBySubmittedById(currentUser.get().getId(), pageable).map(this::storyResponse));
    }

    @Override
    @Transactional
    public Object updateStory(Long id, SupportRequests.SuccessStoryRequest request) {
        SuccessStory story = ownedStory(id);
        story.setBrideName(request.brideName());
        story.setGroomName(request.groomName());
        story.setStory(TextSanitizer.clean(request.story()));
        story.setMarriageDate(request.marriageDate());
        story.setPhotoUrl(request.photoUrl());
        story.setStatus(SuccessStoryStatus.PENDING);
        return storyResponse(story);
    }

    @Override
    @Transactional
    public void approveStory(Long id) {
        SuccessStory story = successStoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Success story not found"));
        story.setStatus(SuccessStoryStatus.APPROVED);
        story.setApprovedBy(currentUser.get());
        story.setApprovedAt(java.time.LocalDateTime.now());
        successStoryRepository.save(story);
    }

    @Override
    @Transactional
    public void rejectStory(Long id, String reason) {
        SuccessStory story = successStoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Success story not found"));
        story.setStatus(SuccessStoryStatus.REJECTED);
        successStoryRepository.save(story);
    }

    @Override
    @Transactional
    public void deleteStory(Long id) {
        // Admin can delete any story; user can only delete their own
        SuccessStory story = successStoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Success story not found"));
        successStoryRepository.delete(story);
    }

    private SupportTicket ownedTicket(Long id) {
        SupportTicket ticket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        if (!ticket.getUser().getId().equals(currentUser.get().getId())) {
            throw new ForbiddenOperationException("Ticket unavailable");
        }
        return ticket;
    }

    private SuccessStory ownedStory(Long id) {
        SuccessStory story = successStoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Success story not found"));
        if (story.getSubmittedBy() == null || !story.getSubmittedBy().getId().equals(currentUser.get().getId())) {
            throw new ForbiddenOperationException("Success story unavailable");
        }
        return story;
    }

    private Object ticketResponse(SupportTicket ticket) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", ticket.getId());
        response.put("category", ticket.getCategory());
        response.put("subject", ticket.getSubject());
        response.put("description", ticket.getDescription());
        response.put("status", ticket.getStatus());
        response.put("priority", ticket.getPriority());
        response.put("createdAt", ticket.getCreatedAt());
        return response;
    }

    private Object storyResponse(SuccessStory story) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", story.getId());
        response.put("brideName", story.getBrideName());
        response.put("groomName", story.getGroomName());
        response.put("story", story.getStory());
        response.put("marriageDate", story.getMarriageDate());
        response.put("photoUrl", story.getPhotoUrl());
        response.put("status", story.getStatus());
        response.put("createdAt", story.getCreatedAt());
        return response;
    }
}
