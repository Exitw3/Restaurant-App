package com.codegym.service.avatarStaff;

import com.codegym.model.StaffAvatar;
import com.codegym.repository.StaffAvatarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StaffAvatarServiceImp implements IStaffAvatarService {

    @Autowired
    private StaffAvatarRepository staffAvatarRepository;


    @Override
    public StaffAvatar getStaffAvatarByStaff_Id(Long id) {
        return staffAvatarRepository.getStaffAvatarByStaff_Id(id);
    }

    @Override
    public StaffAvatar getById(String id) {
        return staffAvatarRepository.getById(id);
    }

}
