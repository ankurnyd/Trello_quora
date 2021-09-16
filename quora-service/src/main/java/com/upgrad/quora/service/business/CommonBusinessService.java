package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

import static com.upgrad.quora.service.common.GenericErrorCode.*;

@Service
public class CommonBusinessService {

    @Autowired
    UserDao userDao;

    public UserEntity fetchUser(final String uuid, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.fetchAuthToken(authorizationToken);

        UserEntity userEntity = userDao.getUser(uuid);
        if (userEntity == null) {
            throw new UserNotFoundException(USER_001_COMMON.getCode(), USER_001_COMMON.getDefaultMessage());
        }

        else if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException(ATHR_001_COMMON.getCode(), ATHR_001_COMMON.getDefaultMessage());
        }

        else if (userAuthTokenEntity.getLogoutAt() != null || userAuthTokenEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException(ATHR_002_COMMON.getCode(), ATHR_002_COMMON.getDefaultMessage());
        }

        return userEntity;
    }

}
