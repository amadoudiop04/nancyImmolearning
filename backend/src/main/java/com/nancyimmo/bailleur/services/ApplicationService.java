package com.nancyimmo.bailleur.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nancyimmo.bailleur.dto.ApplicationDto;
import com.nancyimmo.bailleur.models.ApplicationModel;
import com.nancyimmo.bailleur.repositories.ApplicationRepository;
import com.nancyimmo.bailleur.repositories.PropertyRepository;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final PropertyRepository propertyRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
            PropertyRepository propertyRepository) {
        this.applicationRepository = applicationRepository;
        this.propertyRepository = propertyRepository;
    }

    public ApplicationDto create(ApplicationDto dto) {
        ApplicationModel model = new ApplicationModel();
        model.setFirstName(dto.getFirstName());
        model.setLastName(dto.getLastName());
        model.setEmail(dto.getEmail());
        model.setPhone(dto.getPhone());
        model.setMessage(dto.getMessage());
        model.setStatus("PENDING");
        model.setCreatedAt(LocalDate.now());
        if (dto.getPropertyId() != null) {
            propertyRepository.findById(dto.getPropertyId()).ifPresent(model::setProperty);
        }
        return toDto(applicationRepository.save(model));
    }

    public List<ApplicationDto> findAll() {
        return applicationRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /** Candidatures sur un bien précis — limitées aux biens du bailleur connecté. */
    public List<ApplicationDto> findByProperty(Long propertyId, String email) {
        if (email == null) {
            return List.of();
        }
        return applicationRepository.findByPropertyId(propertyId).stream()
                .filter(a -> a.getProperty() != null
                        && a.getProperty().getLandlord() != null
                        && email.equalsIgnoreCase(a.getProperty().getLandlord().getEmail()))
                .map(this::toDto).collect(Collectors.toList());
    }

    /** Candidatures déposées sur les biens appartenant au bailleur connecté (par email). */
    public List<ApplicationDto> findForLandlord(String email) {
        if (email == null) {
            return List.of();
        }
        return applicationRepository.findByProperty_Landlord_EmailOrderByCreatedAtDesc(email).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /** Vrai si la candidature porte sur un bien du bailleur connecté. */
    public boolean ownedByLandlord(Long applicationId, String email) {
        if (email == null) {
            return false;
        }
        return applicationRepository.findById(applicationId)
                .map(a -> a.getProperty() != null
                        && a.getProperty().getLandlord() != null
                        && email.equalsIgnoreCase(a.getProperty().getLandlord().getEmail()))
                .orElse(false);
    }

    public ApplicationDto updateStatus(Long id, String status) {
        return applicationRepository.findById(id)
                .map(app -> {
                    app.setStatus(status);
                    return toDto(applicationRepository.save(app));
                })
                .orElse(null);
    }

    public void delete(Long id) {
        applicationRepository.deleteById(id);
    }

    private ApplicationDto toDto(ApplicationModel model) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(model.getId());
        dto.setFirstName(model.getFirstName());
        dto.setLastName(model.getLastName());
        dto.setEmail(model.getEmail());
        dto.setPhone(model.getPhone());
        dto.setMessage(model.getMessage());
        dto.setStatus(model.getStatus());
        dto.setCreatedAt(model.getCreatedAt());
        if (model.getProperty() != null) {
            dto.setPropertyId(model.getProperty().getId());
            dto.setPropertyName(model.getProperty().getName());
        }
        return dto;
    }
}
