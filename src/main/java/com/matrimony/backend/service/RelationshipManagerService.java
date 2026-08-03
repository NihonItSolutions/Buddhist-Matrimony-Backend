package com.matrimony.backend.service;

import com.matrimony.backend.dto.request.AdminRequests;

import java.util.List;

public interface RelationshipManagerService {
    List<?> customers();

    Object customer(Long userId);

    Object addNote(Long userId, AdminRequests.NoteRequest request);

    List<?> notes(Long userId);

    Object suggest(Long userId, Long profileId);

    List<?> suggestions(Long userId);

    void assign(Long managerId, Long userId);

    void unassign(Long managerId, Long userId);
}
