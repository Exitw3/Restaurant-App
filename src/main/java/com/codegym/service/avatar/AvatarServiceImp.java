package com.codegym.service.avatar;

import com.codegym.model.Avatar;
import com.codegym.repository.AvatarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AvatarServiceImp implements IAvatarService{

    @Autowired
    private AvatarRepository avatarRepository;

    @Override
    public Avatar getAvatarByCustomer_Id(Long id) {
        return avatarRepository.getAvatarByCustomer_Id(id);
    }

    @Override
    public Avatar getById(String id) {
        return avatarRepository.getById(id);
    }

}
