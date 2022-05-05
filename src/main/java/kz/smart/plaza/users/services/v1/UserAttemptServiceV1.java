package kz.smart.plaza.users.services.v1;


import kz.smart.plaza.users.models.entities.User;
import kz.smart.plaza.users.models.entities.UserAttempt;

public interface UserAttemptServiceV1 {

    UserAttempt getUserAttemptByUserId(User user);
    void save(UserAttempt userAttempt);
    void checkAllUsersTime();




    }
