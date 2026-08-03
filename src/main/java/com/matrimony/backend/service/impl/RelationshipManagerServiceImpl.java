package com.matrimony.backend.service.impl;

import com.matrimony.backend.dto.request.AdminRequests;
import com.matrimony.backend.entity.*;
import com.matrimony.backend.enums.Role;
import com.matrimony.backend.exception.ForbiddenOperationException;
import com.matrimony.backend.exception.ResourceNotFoundException;
import com.matrimony.backend.repository.*;
import com.matrimony.backend.security.CurrentUser;
import com.matrimony.backend.service.RelationshipManagerService;
import com.matrimony.backend.util.TextSanitizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RelationshipManagerServiceImpl implements RelationshipManagerService {
    private final CurrentUser currentUser;
    private final UserRepository userRepository;
    private final MatrimonyProfileRepository profileRepository;
    private final RelationshipManagerAssignmentRepository assignmentRepository;
    private final RelationshipManagerNoteRepository noteRepository;
    private final RelationshipManagerSuggestionRepository suggestionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<?> customers() {
        return assignmentRepository.findByManagerId(currentUser.get().getId()).stream().map(a -> userResponse(a.getCustomer())).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Object customer(Long userId) {
        ensureAssigned(currentUser.get().getId(), userId);
        return userResponse(findUser(userId));
    }

    @Override
    @Transactional
    public Object addNote(Long userId, AdminRequests.NoteRequest request) {
        ensureAssigned(currentUser.get().getId(), userId);
        RelationshipManagerNote note = new RelationshipManagerNote();
        note.setManager(currentUser.get());
        note.setCustomer(findUser(userId));
        note.setNote(TextSanitizer.clean(request.note()));
        noteRepository.save(note);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", note.getId());
        response.put("note", note.getNote());
        response.put("createdAt", note.getCreatedAt());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<?> notes(Long userId) {
        ensureAssigned(currentUser.get().getId(), userId);
        return noteRepository.findByManagerIdAndCustomerIdOrderByCreatedAtDesc(currentUser.get().getId(), userId).stream()
                .map(note -> Map.of("id", note.getId(), "note", note.getNote(), "createdAt", note.getCreatedAt()))
                .toList();
    }

    @Override
    @Transactional
    public Object suggest(Long userId, Long profileId) {
        ensureAssigned(currentUser.get().getId(), userId);
        RelationshipManagerSuggestion suggestion = new RelationshipManagerSuggestion();
        suggestion.setManager(currentUser.get());
        suggestion.setCustomer(findUser(userId));
        suggestion.setSuggestedProfile(profileRepository.findById(profileId).orElseThrow(() -> new ResourceNotFoundException("Profile not found")));
        suggestionRepository.save(suggestion);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", suggestion.getId());
        response.put("profileId", profileId);
        response.put("createdAt", suggestion.getCreatedAt());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<?> suggestions(Long userId) {
        ensureAssigned(currentUser.get().getId(), userId);
        return suggestionRepository.findByManagerIdAndCustomerIdOrderByCreatedAtDesc(currentUser.get().getId(), userId).stream()
                .map(s -> Map.of("id", s.getId(), "suggestedMatrimonyId", s.getSuggestedProfile().getUser().getMatrimonyId(), "createdAt", s.getCreatedAt()))
                .toList();
    }

    @Override
    @Transactional
    public void assign(Long managerId, Long userId) {
        User manager = findUser(managerId);
        if (manager.getRole() != Role.RELATIONSHIP_MANAGER) {
            throw new ForbiddenOperationException("User is not a relationship manager");
        }
        if (assignmentRepository.existsByManagerIdAndCustomerId(managerId, userId)) {
            return;
        }
        RelationshipManagerAssignment assignment = new RelationshipManagerAssignment();
        assignment.setManager(manager);
        assignment.setCustomer(findUser(userId));
        assignmentRepository.save(assignment);
    }

    @Override
    @Transactional
    public void unassign(Long managerId, Long userId) {
        assignmentRepository.deleteByManagerIdAndCustomerId(managerId, userId);
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void ensureAssigned(Long managerId, Long userId) {
        if (!assignmentRepository.existsByManagerIdAndCustomerId(managerId, userId)) {
            throw new ForbiddenOperationException("Customer is not assigned to this manager");
        }
    }

    private Object userResponse(User user) {
        return Map.of("id", user.getId(), "matrimonyId", user.getMatrimonyId(), "email", user.getEmail(), "mobileNumber", user.getCountryCode() + user.getMobileNumber(), "accountStatus", user.getAccountStatus());
    }
}
