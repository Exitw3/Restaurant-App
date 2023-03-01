package com.codegym.service.staff;

import com.codegym.model.Staff;
import com.codegym.model.dto.StaffAvatarDTO;
import com.codegym.model.dto.StaffDTO;
import com.codegym.service.IGeneralService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IStaffService extends IGeneralService<Staff> {
    Optional<Staff> findByEmail(String email);
    Optional<Staff> findByEmailAndIdIsNot(String email, Long id);
    List<Staff> findAllByDeletedIsFalse();

    List<StaffDTO> getAllStaffDTO();
    StaffDTO getStaffDTOById(Long id);

    StaffDTO saveWithAvatar(Staff staff, MultipartFile avatarFile);
    StaffDTO updateWithAvatar(Staff staff, MultipartFile avatarFile, StaffAvatarDTO staffAvatarDTO);

}
