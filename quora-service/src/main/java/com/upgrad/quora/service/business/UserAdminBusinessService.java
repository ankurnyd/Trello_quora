package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.ConstantValues;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static com.upgrad.quora.service.common.GenericErrorCode.*;
import static com.upgrad.quora.service.common.GenericErrorCode.USR_001_ADMIN;

@Service
public class UserAdminBusinessService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(final UserEntity userEntity){

        String password = userEntity.getPassword();
        if(password == null){
            userEntity.setPassword("proman@123");
        }
        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeUser(final String uuid,final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.fetchAuthToken(authorizationToken);
        UserEntity userEntity = userDao.getUser(uuid);

        if (userAuthTokenEntity.getUser().getRole().equals(ConstantValues.DEFAULT_USER_ROLE)) {
            throw new AuthorizationFailedException(ATHR_003_ADMIN.getCode(), ATHR_003_ADMIN.getDefaultMessage());
        }

        else if (userEntity == null) {
            throw new UserNotFoundException(USR_001_ADMIN.getCode(), USR_001_ADMIN.getDefaultMessage());
        }

        else if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException(ATHR_001_ADMIN.getCode(), ATHR_001_ADMIN.getDefaultMessage());
        }

        else if (userAuthTokenEntity.getLogoutAt() != null || userAuthTokenEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException(ATHR_002_ADMIN.getCode(), ATHR_002_ADMIN.getDefaultMessage());
        }

        else if (userAuthTokenEntity.getUser().getRole().equals(ConstantValues.DEFAULT_USER_ROLE)) {
            throw new AuthorizationFailedException(ATHR_003_ADMIN.getCode(), ATHR_003_ADMIN.getDefaultMessage());
        }

        userDao.removeUser(userEntity);

    }

}
