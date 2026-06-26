package com.nancyimmo.bailleur.services;

import org.springframework.stereotype.Service;
import com.nancyimmo.bailleur.repositories.LandlordRepository;
import com.nancyimmo.bailleur.security.CurrentUser;
import com.nancyimmo.bailleur.dto.LandlordDto;
import com.nancyimmo.bailleur.models.LandlordModel;

@Service
public class LandlordService {

    private final LandlordRepository landlordRepository;
    private final CurrentUser currentUser;

    public LandlordService(LandlordRepository landlordRepository, CurrentUser currentUser) {
        this.landlordRepository = landlordRepository;
        this.currentUser = currentUser;
    }

    public LandlordDto createLandlord(LandlordDto landlordDto) {
        LandlordModel model = toEntity(landlordDto);
        return toDto(landlordRepository.save(model));
    }

    /** Ne renvoie que le compte connecté (isolation : un bailleur ne voit pas les autres). */
    public java.util.List<LandlordDto> findAll() {
        LandlordModel me = currentUser.landlord();
        return me == null ? java.util.List.of() : java.util.List.of(toDto(me));
    }

    public void delete(Long id) {
        // Suppression réservée au compte connecté (voir aussi /api/auth/me).
        LandlordModel me = currentUser.landlord();
        if (me != null && me.getId().equals(id)) {
            landlordRepository.deleteById(id);
        }
    }

    public LandlordDto findById(Long id) {
        LandlordModel me = currentUser.landlord();
        return (me != null && me.getId().equals(id)) ? toDto(me) : null;
    }

    public LandlordDto update(Long id, LandlordDto landlordDto) {
        // On ne met à jour QUE le compte connecté, et on NE touche PAS au mot de passe (absent du DTO).
        LandlordModel me = currentUser.landlord();
        if (me == null || !me.getId().equals(id)) {
            return null;
        }
        me.setFirstName(landlordDto.getFirstName());
        me.setLastName(landlordDto.getLastName());
        me.setEmail(landlordDto.getEmail());
        me.setPhone(landlordDto.getPhone());
        me.setStreet(landlordDto.getStreet());
        me.setCity(landlordDto.getCity());
        me.setZipCode(landlordDto.getZipCode());
        me.setCountry(landlordDto.getCountry());
        return toDto(landlordRepository.save(me));
    }

    private LandlordDto toDto(LandlordModel model) {
        return new LandlordDto(
                model.getId(),
                model.getFirstName(),
                model.getLastName(),
                model.getEmail(),
            model.getPhone(),
                model.getStreet(),
                model.getCity(),
                model.getZipCode(),
                model.getCountry());
    }

    private LandlordModel toEntity(LandlordDto dto) {
        LandlordModel model = new LandlordModel();
        model.setFirstName(dto.getFirstName());
        model.setLastName(dto.getLastName());
        model.setEmail(dto.getEmail());
        model.setPhone(dto.getPhone());
        model.setStreet(dto.getStreet());
        model.setCity(dto.getCity());
        model.setZipCode(dto.getZipCode());
        model.setCountry(dto.getCountry());
        return model;
    }

}
