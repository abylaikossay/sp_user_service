package kz.smart.plaza.users.services.v1.impl;

import kz.smart.plaza.users.models.entities.User;
import kz.smart.plaza.users.models.entities.UserAttempt;
import kz.smart.plaza.users.repositories.UserAttemptsRepository;


import kz.smart.plaza.users.services.v1.UserAttemptServiceV1;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class UserAttemptServiceImplV1 implements UserAttemptServiceV1 {

    private UserAttemptsRepository userAttemptsRepository;


    @Override
    public UserAttempt getUserAttemptByUserId(User user) {
        UserAttempt userAttempt = userAttemptsRepository.findDistinctByUser_Id(user.getId());
        if(userAttempt != null) {
            return userAttempt;
        } else {
            UserAttempt userAttemptNew = new UserAttempt();
            userAttemptNew.setUser(user);
            userAttemptNew.setSmsAttempts(0);
            return userAttemptNew;
        }
    }

    @Override
    public void save(UserAttempt userAttempt) {
        userAttemptsRepository.save(userAttempt);
    }

    @Override
    public void checkAllUsersTime() {
        List<UserAttempt> userAttemptList = userAttemptsRepository.getAllUsers();
        userAttemptList.forEach( userAttempt -> {
            long diff = new Date().getTime() -  userAttempt.getUpdatedAt().getTime();
            long diffMinutes = diff / (60 * 1000);
            if (diffMinutes > 60){
                userAttempt.setSmsAttempts(0);
                userAttemptsRepository.save(userAttempt);
            }
        });

    }
}
