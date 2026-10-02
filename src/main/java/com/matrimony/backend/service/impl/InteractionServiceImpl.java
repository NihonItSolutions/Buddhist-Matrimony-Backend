package com.matrimony.backend.service.impl;

import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.InteractionRequests;
import com.matrimony.backend.dto.response.InteractionResponses.*;
import com.matrimony.backend.dto.response.ProfileCardResponse;
import com.matrimony.backend.entity.*;
import com.matrimony.backend.enums.*;
import com.matrimony.backend.exception.*;
import com.matrimony.backend.mapper.ProfileMapper;
import com.matrimony.backend.repository.*;
import com.matrimony.backend.security.CurrentUser;
import com.matrimony.backend.service.EntitlementService;
import com.matrimony.backend.service.InteractionService;
import com.matrimony.backend.util.TextSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InteractionServiceImpl implements InteractionService {
    private static final List<InterestStatus> ACTIVE_INTEREST_STATUSES = List.of(InterestStatus.PENDING, InterestStatus.ACCEPTED);

    private final CurrentUser currentUser;
    private final MatrimonyProfileRepository profileRepository;
    private final InterestRepository interestRepository;
    private final ShortlistedProfileRepository shortlistRepository;
    private final BlockedProfileRepository blockedRepository;
    private final ProfileReportRepository reportRepository;
    private final ContactRequestRepository contactRequestRepository;
    private final UserSubscriptionRepository subscriptionRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final EducationDetailsRepository educationRepository;
    private final CareerDetailsRepository careerRepository;
    private final ProfilePhotoRepository photoRepository;
    private final ProfileMapper mapper;
    private final EntitlementService entitlementService;

    @Override
    @Transactional
    public InterestResponse sendInterest(String receiverMatrimonyId, InteractionRequests.InterestMessage request) {
        MatrimonyProfile sender = currentProfile();
        MatrimonyProfile receiver = activeProfile(receiverMatrimonyId);
        ensureDifferent(sender, receiver);
        ensureNotBlocked(sender, receiver);
        interestRepository.findActiveBetween(sender.getId(), receiver.getId(), ACTIVE_INTEREST_STATUSES).ifPresent(existing -> {
            throw new DuplicateResourceException("An active interest already exists");
        });
        Interest interest = new Interest();
        interest.setSenderProfile(sender);
        interest.setReceiverProfile(receiver);
        interest.setMessage(TextSanitizer.clean(request == null ? null : request.message()));
        interest.setSentAt(LocalDateTime.now());
        interestRepository.save(interest);
        String senderLabel = (sender.getFirstName() != null && !sender.getFirstName().isBlank()) ? sender.getFirstName() + " (" + sender.getUser().getMatrimonyId() + ")" : sender.getUser().getMatrimonyId();
        notify(receiver.getUser(), NotificationType.INTEREST_RECEIVED, "Interest received", senderLabel + " has sent you an interest.", "INTEREST", interest.getId());
        return toInterest(interest);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InterestResponse> sentInterests(Pageable pageable) {
        return page(interestRepository.findBySenderProfileId(currentProfile().getId(), pageable).map(this::toInterest));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InterestResponse> receivedInterests(Pageable pageable) {
        return page(interestRepository.findByReceiverProfileId(currentProfile().getId(), pageable).map(this::toInterest));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InterestResponse> interestsByStatus(String status, Pageable pageable) {
        MatrimonyProfile profile = currentProfile();
        InterestStatus interestStatus = InterestStatus.valueOf(status);
        return page(interestRepository.findForProfileByStatus(profile.getId(), interestStatus, pageable).map(this::toInterest));
    }

    @Override
    @Transactional
    public InterestResponse acceptInterest(Long interestId) {
        Interest interest = ownedReceivedInterest(interestId);
        interest.setStatus(InterestStatus.ACCEPTED);
        interest.setRespondedAt(LocalDateTime.now());
        MatrimonyProfile responder = interest.getReceiverProfile();
        String respLabel = (responder.getFirstName() != null && !responder.getFirstName().isBlank()) ? responder.getFirstName() + " (" + responder.getUser().getMatrimonyId() + ")" : responder.getUser().getMatrimonyId();
        notify(interest.getSenderProfile().getUser(), NotificationType.INTEREST_ACCEPTED, "Interest accepted", respLabel + " accepted your interest.", "INTEREST", interest.getId());
        return toInterest(interest);
    }

    @Override
    @Transactional
    public InterestResponse declineInterest(Long interestId) {
        Interest interest = ownedReceivedInterest(interestId);
        interest.setStatus(InterestStatus.DECLINED);
        interest.setRespondedAt(LocalDateTime.now());
        MatrimonyProfile responder = interest.getReceiverProfile();
        String respLabel = (responder.getFirstName() != null && !responder.getFirstName().isBlank()) ? responder.getFirstName() + " (" + responder.getUser().getMatrimonyId() + ")" : responder.getUser().getMatrimonyId();
        notify(interest.getSenderProfile().getUser(), NotificationType.INTEREST_DECLINED, "Interest declined", respLabel + " declined your interest.", "INTEREST", interest.getId());
        return toInterest(interest);
    }

    @Override
    @Transactional
    public InterestResponse cancelInterest(Long interestId) {
        Interest interest = interestRepository.findById(interestId).orElseThrow(() -> new ResourceNotFoundException("Interest not found"));
        if (!interest.getSenderProfile().getId().equals(currentProfile().getId())) {
            throw new ForbiddenOperationException("Only sender can cancel interest");
        }
        interest.setStatus(InterestStatus.CANCELLED);
        interest.setCancelledAt(LocalDateTime.now());
        return toInterest(interest);
    }

    @Override
    @Transactional
    public void deleteInterest(Long interestId) {
        Interest interest = interestRepository.findById(interestId).orElseThrow(() -> new ResourceNotFoundException("Interest not found"));
        MatrimonyProfile profile = currentProfile();
        if (!interest.getSenderProfile().getId().equals(profile.getId()) && !interest.getReceiverProfile().getId().equals(profile.getId())) {
            throw new ForbiddenOperationException("Interest unavailable");
        }
        interestRepository.delete(interest);
    }

    @Override
    @Transactional
    public void shortlist(String matrimonyId) {
        MatrimonyProfile owner = currentProfile();
        MatrimonyProfile target = activeProfile(matrimonyId);
        ensureDifferent(owner, target);
        if (shortlistRepository.existsByOwnerProfileIdAndShortlistedProfileId(owner.getId(), target.getId())) {
            return;
        }
        ShortlistedProfile shortlist = new ShortlistedProfile();
        shortlist.setOwnerProfile(owner);
        shortlist.setShortlistedProfile(target);
        shortlistRepository.save(shortlist);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProfileCardResponse> shortlists(Pageable pageable) {
        MatrimonyProfile owner = currentProfile();
        Page<ShortlistedProfile> page = shortlistRepository.findByOwnerProfileId(owner.getId(), pageable);
        List<ProfileCardResponse> content = page.getContent().stream()
                .map(item -> card(owner, item.getShortlistedProfile()))
                .toList();
        return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
    }

    @Override
    @Transactional
    public void removeShortlist(String matrimonyId) {
        MatrimonyProfile owner = currentProfile();
        MatrimonyProfile target = profileRepository.findByUserMatrimonyId(matrimonyId).orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        shortlistRepository.findByOwnerProfileIdAndShortlistedProfileId(owner.getId(), target.getId()).ifPresent(shortlistRepository::delete);
    }

    @Override
    @Transactional
    public BlockResponse block(String matrimonyId, InteractionRequests.BlockRequest request) {
        MatrimonyProfile owner = currentProfile();
        MatrimonyProfile target = activeProfile(matrimonyId);
        ensureDifferent(owner, target);
        BlockedProfile block = blockedRepository.findByBlockedByProfileIdAndBlockedProfileId(owner.getId(), target.getId()).orElseGet(() -> {
            BlockedProfile created = new BlockedProfile();
            created.setBlockedByProfile(owner);
            created.setBlockedProfile(target);
            return created;
        });
        block.setReason(TextSanitizer.clean(request == null ? null : request.reason()));
        blockedRepository.save(block);
        return toBlock(block);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BlockResponse> blocks(Pageable pageable) {
        return page(blockedRepository.findByBlockedByProfileId(currentProfile().getId(), pageable).map(this::toBlock));
    }

    @Override
    @Transactional
    public void unblock(String matrimonyId) {
        MatrimonyProfile owner = currentProfile();
        MatrimonyProfile target = profileRepository.findByUserMatrimonyId(matrimonyId).orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        blockedRepository.findByBlockedByProfileIdAndBlockedProfileId(owner.getId(), target.getId()).ifPresent(blockedRepository::delete);
    }

    @Override
    @Transactional
    public ReportResponse report(String matrimonyId, InteractionRequests.ReportRequest request) {
        MatrimonyProfile reporter = currentProfile();
        MatrimonyProfile reported = activeProfile(matrimonyId);
        ensureDifferent(reporter, reported);
        reportRepository.findFirstByReporterProfileIdAndReportedProfileIdAndReasonAndCreatedAtAfter(
                reporter.getId(), reported.getId(), request.reason(), LocalDateTime.now().minusDays(7)
        ).ifPresent(existing -> {
            throw new DuplicateResourceException("A similar report was recently submitted");
        });
        ProfileReport report = new ProfileReport();
        report.setReporterProfile(reporter);
        report.setReportedProfile(reported);
        report.setReason(request.reason());
        report.setDescription(TextSanitizer.clean(request.description()));
        reportRepository.save(report);
        return toReport(report);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReportResponse> myReports(Pageable pageable) {
        return page(reportRepository.findByReporterProfileId(currentProfile().getId(), pageable).map(this::toReport));
    }

    @Override
    @Transactional
    public ContactRequestResponse requestContact(String matrimonyId) {
        MatrimonyProfile requester = currentProfile();
        MatrimonyProfile receiver = activeProfile(matrimonyId);
        ensureDifferent(requester, receiver);
        ensureNotBlocked(requester, receiver);
        if (contactRequestRepository.existsByRequesterProfileIdAndReceiverProfileIdAndStatus(requester.getId(), receiver.getId(), ContactRequestStatus.PENDING)) {
            throw new DuplicateResourceException("Contact request is already pending");
        }
        ContactRequest request = new ContactRequest();
        request.setRequesterProfile(requester);
        request.setReceiverProfile(receiver);
        request.setRequestedAt(LocalDateTime.now());
        contactRequestRepository.save(request);
        String reqLabel = (requester.getFirstName() != null && !requester.getFirstName().isBlank()) ? requester.getFirstName() + " (" + requester.getUser().getMatrimonyId() + ")" : requester.getUser().getMatrimonyId();
        notify(receiver.getUser(), NotificationType.CONTACT_REQUEST, "Contact request", reqLabel + " requested your contact details.", "CONTACT_REQUEST", request.getId());
        return toContact(request);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ContactRequestResponse> sentContactRequests(Pageable pageable) {
        return page(contactRequestRepository.findByRequesterProfileId(currentProfile().getId(), pageable).map(this::toContact));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ContactRequestResponse> receivedContactRequests(Pageable pageable) {
        return page(contactRequestRepository.findByReceiverProfileId(currentProfile().getId(), pageable).map(this::toContact));
    }

    @Override
    @Transactional
    public ContactRequestResponse approveContact(Long requestId) {
        ContactRequest request = ownedReceivedContact(requestId);
        request.setStatus(ContactRequestStatus.APPROVED);
        request.setRespondedAt(LocalDateTime.now());
        MatrimonyProfile approver = request.getReceiverProfile();
        String appLabel = (approver.getFirstName() != null && !approver.getFirstName().isBlank()) ? approver.getFirstName() + " (" + approver.getUser().getMatrimonyId() + ")" : approver.getUser().getMatrimonyId();
        notify(request.getRequesterProfile().getUser(), NotificationType.CONTACT_REQUEST_APPROVED, "Contact approved", appLabel + " approved your contact request.", "CONTACT_REQUEST", request.getId());
        return toContact(request);
    }

    @Override
    @Transactional
    public ContactRequestResponse rejectContact(Long requestId) {
        ContactRequest request = ownedReceivedContact(requestId);
        request.setStatus(ContactRequestStatus.REJECTED);
        request.setRespondedAt(LocalDateTime.now());
        return toContact(request);
    }

    @Override
    @Transactional
    public ContactRequestResponse cancelContact(Long requestId) {
        ContactRequest request = contactRequestRepository.findById(requestId).orElseThrow(() -> new ResourceNotFoundException("Contact request not found"));
        if (!request.getRequesterProfile().getId().equals(currentProfile().getId())) {
            throw new ForbiddenOperationException("Only requester can cancel contact request");
        }
        request.setStatus(ContactRequestStatus.CANCELLED);
        request.setRespondedAt(LocalDateTime.now());
        return toContact(request);
    }

    @Override
    @Transactional
    public ContactDetailsResponse contactDetails(String matrimonyId) {
        MatrimonyProfile requester = currentProfile();
        MatrimonyProfile receiver = activeProfile(matrimonyId);
        ensureNotBlocked(requester, receiver);

        // Check if user has any active plan at all
        if (!entitlementService.hasActivePlan(requester.getUser())) {
            throw new SubscriptionRequiredException("Paid membership is required to view contact number and WhatsApp details");
        }

        // Get the active subscription (guaranteed to exist at this point)
        UserSubscription subscription = entitlementService.activeSubscription(requester.getUser())
                .orElseThrow(() -> new SubscriptionRequiredException("Active subscription required to view contact details"));

        boolean alreadyUnlocked = contactRequestRepository.existsByRequesterProfileIdAndReceiverProfileIdAndStatus(requester.getId(), receiver.getId(), ContactRequestStatus.APPROVED)
                || contactRequestRepository.existsByRequesterProfileIdAndReceiverProfileIdAndStatus(receiver.getId(), requester.getId(), ContactRequestStatus.APPROVED);

        if (!alreadyUnlocked) {
            // Check remaining contact view quota
            if (subscription.getRemainingContactViews() == null || subscription.getRemainingContactViews() <= 0) {
                throw new SubscriptionRequiredException("You have reached the contact view limit of your plan. Please purchase a new plan to unlock more contacts.");
            }

            // Decrement remaining views
            subscription.setRemainingContactViews(subscription.getRemainingContactViews() - 1);
            subscriptionRepository.save(subscription);

            // Record as APPROVED contact request (to save the unlock state)
            ContactRequest unlockRequest = new ContactRequest();
            unlockRequest.setRequesterProfile(requester);
            unlockRequest.setReceiverProfile(receiver);
            unlockRequest.setStatus(ContactRequestStatus.APPROVED);
            unlockRequest.setRequestedAt(LocalDateTime.now());
            unlockRequest.setRespondedAt(LocalDateTime.now());
            contactRequestRepository.save(unlockRequest);
        }

        User user = receiver.getUser();
        return new ContactDetailsResponse(user.getMatrimonyId(), user.getEmail(), user.getCountryCode() + user.getMobileNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ConversationResponse> conversations(Pageable pageable) {
        return page(conversationRepository.findForProfile(currentProfile().getId(), pageable).map(this::toConversation));
    }

    @Override
    @Transactional
    public ConversationResponse createConversation(String matrimonyId) {
        MatrimonyProfile owner = currentProfile();
        MatrimonyProfile target = activeProfile(matrimonyId);
        ensureMessagingAllowed(owner, target);
        Conversation conversation = conversationRepository.findBetween(owner.getId(), target.getId()).orElseGet(() -> {
            Conversation created = new Conversation();
            if (owner.getId() < target.getId()) {
                created.setProfileOne(owner);
                created.setProfileTwo(target);
            } else {
                created.setProfileOne(target);
                created.setProfileTwo(owner);
            }
            return conversationRepository.save(created);
        });
        return toConversation(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageResponse> messages(Long conversationId, Pageable pageable) {
        Conversation conversation = ownedConversation(conversationId);
        return page(messageRepository.findByConversationId(conversation.getId(), pageable).map(this::toMessage));
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(Long conversationId, InteractionRequests.MessageRequest request) {
        Conversation conversation = ownedConversation(conversationId);
        MatrimonyProfile sender = currentProfile();
        MatrimonyProfile receiver = conversation.getProfileOne().getId().equals(sender.getId()) ? conversation.getProfileTwo() : conversation.getProfileOne();
        ensureMessagingAllowed(sender, receiver);

        // Deduct one message from the sender's remaining quota
        entitlementService.activeSubscription(sender.getUser()).ifPresent(subscription -> {
            if (subscription.getRemainingMessages() != null && subscription.getRemainingMessages() > 0) {
                subscription.setRemainingMessages(subscription.getRemainingMessages() - 1);
                subscriptionRepository.save(subscription);
            }
        });

        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderProfile(sender);
        message.setMessageText(TextSanitizer.clean(request.messageText()));
        message.setSentAt(LocalDateTime.now());
        messageRepository.save(message);
        String senderLabel = (sender.getFirstName() != null && !sender.getFirstName().isBlank()) ? sender.getFirstName() + " (" + sender.getUser().getMatrimonyId() + ")" : sender.getUser().getMatrimonyId();
        notify(receiver.getUser(), NotificationType.NEW_MESSAGE, "New message", "New message from " + senderLabel + ".", "CONVERSATION", conversation.getId());
        return toMessage(message);
    }

    @Override
    @Transactional
    public void markConversationRead(Long conversationId) {
        Conversation conversation = ownedConversation(conversationId);
        MatrimonyProfile current = currentProfile();
        messageRepository.findByConversationId(conversation.getId(), Pageable.unpaged()).forEach(message -> {
            if (!message.getSenderProfile().getId().equals(current.getId()) && message.getReadAt() == null) {
                message.setReadAt(LocalDateTime.now());
            }
        });
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        MatrimonyProfile current = currentProfile();
        if (message.getSenderProfile().getId().equals(current.getId())) {
            message.setDeletedBySender(true);
        } else if (message.getConversation().getProfileOne().getId().equals(current.getId()) || message.getConversation().getProfileTwo().getId().equals(current.getId())) {
            message.setDeletedByReceiver(true);
        } else {
            throw new ForbiddenOperationException("Message unavailable");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> notifications(NotificationType type, Pageable pageable) {
        Long userId = currentUser.get().getId();
        Page<Notification> page = type == null
                ? notificationRepository.findByUserId(userId, pageable)
                : notificationRepository.findByUserIdAndNotificationType(userId, type, pageable);
        return page(page.map(this::toNotification));
    }

    @Override
    @Transactional(readOnly = true)
    public long unreadNotificationCount() {
        return notificationRepository.countByUserIdAndReadFalse(currentUser.get().getId());
    }

    @Override
    @Transactional
    public void readNotification(Long id) {
        Notification notification = ownedNotification(id);
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void readAllNotifications() {
        notificationRepository.findByUserId(currentUser.get().getId(), Pageable.unpaged()).forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        });
    }

    @Override
    @Transactional
    public void deleteNotification(Long id) {
        notificationRepository.delete(ownedNotification(id));
    }

    private MatrimonyProfile currentProfile() {
        return profileRepository.findByUser(currentUser.get()).orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    private MatrimonyProfile activeProfile(String matrimonyId) {
        MatrimonyProfile profile = profileRepository.findByUserMatrimonyId(matrimonyId).orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        if (profile.getProfileStatus() != ProfileStatus.ACTIVE) {
            throw new ResourceNotFoundException("Profile not found");
        }
        return profile;
    }

    private void ensureDifferent(MatrimonyProfile a, MatrimonyProfile b) {
        if (a.getId().equals(b.getId())) {
            throw new InvalidRequestException("Cannot perform this action on your own profile");
        }
    }

    private void ensureNotBlocked(MatrimonyProfile a, MatrimonyProfile b) {
        if (blockedRepository.existsByBlockedByProfileIdAndBlockedProfileIdOrBlockedByProfileIdAndBlockedProfileId(a.getId(), b.getId(), b.getId(), a.getId())) {
            throw new ForbiddenOperationException("Profiles are blocked");
        }
    }

    private void ensureMessagingAllowed(MatrimonyProfile a, MatrimonyProfile b) {
        ensureNotBlocked(a, b);
        if (!entitlementService.hasActivePlan(a.getUser())) {
            throw new SubscriptionRequiredException("Paid membership is required to send messages");
        }
        if (!entitlementService.canMessage(a.getUser())) {
            throw new SubscriptionRequiredException("You have reached the message limit of your plan. Please purchase a new plan to send more messages.");
        }
    }

    private Interest ownedReceivedInterest(Long id) {
        Interest interest = interestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Interest not found"));
        if (!interest.getReceiverProfile().getId().equals(currentProfile().getId())) {
            throw new ForbiddenOperationException("Only receiver can respond to interest");
        }
        if (interest.getStatus() != InterestStatus.PENDING) {
            throw new InvalidRequestException("Only pending interest can be updated");
        }
        return interest;
    }

    private ContactRequest ownedReceivedContact(Long id) {
        ContactRequest request = contactRequestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contact request not found"));
        if (!request.getReceiverProfile().getId().equals(currentProfile().getId())) {
            throw new ForbiddenOperationException("Only receiver can respond to contact request");
        }
        return request;
    }

    private Conversation ownedConversation(Long id) {
        Conversation conversation = conversationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
        Long profileId = currentProfile().getId();
        if (!conversation.getProfileOne().getId().equals(profileId) && !conversation.getProfileTwo().getId().equals(profileId)) {
            throw new ForbiddenOperationException("Conversation unavailable");
        }
        return conversation;
    }

    private Notification ownedNotification(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!notification.getUser().getId().equals(currentUser.get().getId())) {
            throw new ForbiddenOperationException("Notification unavailable");
        }
        return notification;
    }

    private void notify(User user, NotificationType type, String title, String message, String referenceType, Long referenceId) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setNotificationType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setReferenceType(referenceType);
        notification.setReferenceId(referenceId);
        notificationRepository.save(notification);
    }

    private ProfileCardResponse card(MatrimonyProfile owner, MatrimonyProfile target) {
        boolean hasActivePlan = entitlementService.hasActivePlan(owner.getUser());
        return mapper.toCard(target,
                educationRepository.findByProfileId(target.getId()).orElse(null),
                careerRepository.findByProfileId(target.getId()).orElse(null),
                photoRepository.findDisplayPhoto(target.getId()).orElse(null),
                shortlistRepository.existsByOwnerProfileIdAndShortlistedProfileId(owner.getId(), target.getId()),
                interestRepository.findActiveBetween(owner.getId(), target.getId(), ACTIVE_INTEREST_STATUSES).map(i -> i.getStatus().name()).orElse(null),
                0,
                hasActivePlan);
    }

    private InterestResponse toInterest(Interest interest) {
        return new InterestResponse(interest.getId(), interest.getSenderProfile().getUser().getMatrimonyId(), interest.getReceiverProfile().getUser().getMatrimonyId(), interest.getStatus(), interest.getMessage(), interest.getSentAt(), interest.getRespondedAt());
    }

    private BlockResponse toBlock(BlockedProfile block) {
        return new BlockResponse(block.getId(), block.getBlockedProfile().getUser().getMatrimonyId(), block.getReason(), block.getCreatedAt());
    }

    private ReportResponse toReport(ProfileReport report) {
        return new ReportResponse(report.getId(), report.getReportedProfile().getUser().getMatrimonyId(), report.getReason(), report.getStatus(), report.getDescription(), report.getCreatedAt());
    }

    private ContactRequestResponse toContact(ContactRequest request) {
        return new ContactRequestResponse(request.getId(), request.getRequesterProfile().getUser().getMatrimonyId(), request.getReceiverProfile().getUser().getMatrimonyId(), request.getStatus(), request.getRequestedAt(), request.getRespondedAt());
    }

    private ConversationResponse toConversation(Conversation conversation) {
        return new ConversationResponse(conversation.getId(), conversation.getProfileOne().getUser().getMatrimonyId(), conversation.getProfileTwo().getUser().getMatrimonyId(), conversation.isActive(), conversation.getUpdatedAt());
    }

    private MessageResponse toMessage(Message message) {
        return new MessageResponse(message.getId(), message.getConversation().getId(), message.getSenderProfile().getUser().getMatrimonyId(), message.getMessageText(), message.getMessageType(), message.getAttachmentUrl(), message.getSentAt(), message.getReadAt());
    }

    private NotificationResponse toNotification(Notification notification) {
        return new NotificationResponse(notification.getId(), notification.getNotificationType(), notification.getTitle(), notification.getMessage(), notification.getReferenceType(), notification.getReferenceId(), notification.isRead(), notification.getCreatedAt(), notification.getReadAt());
    }

    private <T> PageResponse<T> page(Page<T> page) {
        return PageResponse.from(page);
    }
}
