package ua.dragunov.watchly.service.api;

import ua.dragunov.watchly.model.dto.ActorCreationRequest;
import ua.dragunov.watchly.model.dto.ActorPreviewResponse;
import ua.dragunov.watchly.model.dto.ActorResponse;
import ua.dragunov.watchly.model.dto.ActorUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface ActorService {

    Optional<ActorResponse> getActorById(long id);

    List<ActorPreviewResponse> getAllActors();

    ActorResponse createActor(ActorCreationRequest request);

    ActorResponse updateActor(long id, ActorUpdateRequest request);

    void deleteActorById(long id);

    List<ActorPreviewResponse> getActorsByMediaItemId(long mediaItemId);

    Optional<ActorResponse> findByExternalIdAndSource(String externalId, long apiSourceId);

    void deleteAllByApiSourceId(long apiSourceId);

    ActorResponse getOrFetchByExternalId(String externalId, long apiSourceId);
}