package kz.smart.plaza.users.services.v2.impl;


import kz.smart.plaza.users.models.entities.CovidUser;
import kz.smart.plaza.users.models.responses.UserResponse;
import kz.smart.plaza.users.repositories.CovidUserRepository;
import kz.smart.plaza.users.services.v2.UserServiceV2;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class UserServiceImplV2 implements UserServiceV2 {
    private CovidUserRepository covidUserRepository;


    @Override
    public String addCovidUser(CovidUser covidUser) {
        covidUserRepository.save(covidUser);
        return "Success";
    }

    @Override
    public List<CovidUser> getCovidUsers() {
        List<CovidUser> covidUsers = covidUserRepository.findAll();
        return covidUsers;
    }
}
