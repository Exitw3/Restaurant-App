package com.codegym.service.avatarStaff;

import com.codegym.model.StaffAvatar;

public interface IStaffAvatarService {
    StaffAvatar getStaffAvatarByStaff_Id(Long id);
    StaffAvatar getById(String id);
}
