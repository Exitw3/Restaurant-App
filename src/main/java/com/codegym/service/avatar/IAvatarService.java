package com.codegym.service.avatar;

import com.codegym.model.Avatar;

public interface IAvatarService {
    Avatar getAvatarByCustomer_Id(Long id);
    Avatar getById(String id);
}
