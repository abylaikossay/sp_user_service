package kz.smart.plaza.users.services.v2;


import kz.smart.plaza.users.models.entities.CovidUser;

import java.util.List;

public interface UserServiceV2 {
    String addCovidUser(CovidUser covidUser);

    List<CovidUser> getCovidUsers();
}
