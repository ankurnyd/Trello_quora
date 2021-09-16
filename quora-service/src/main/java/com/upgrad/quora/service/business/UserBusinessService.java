package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.ConstantValues;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;

import static com.upgrad.quora.service.common.GenericErrorCode.*;
import static com.upgrad.quora.service.common.GenericErrorCode.SGOR_001;


@Service
public class UserBusinessService {


    @Autowired
    private UserAdminBusinessService userAdminBusinessService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity)
    {
        return userAdminBusinessService.createUser(userEntity);
    }

    public void verificationSignUp(String userName, String email) throws SignUpRestrictedException {
        UserEntity newuserEntity = userDao.getUserByUserName(userName);
        UserEntity newuserEntity1 = userDao.getUserByEmail(email);
        if(newuserEntity != null){
            throw new SignUpRestrictedException(SGUR_001.getCode(), SGUR_001.getDefaultMessage());
        }

        else if(newuserEntity1 != null){
            throw new SignUpRestrictedException(SGUR_002.getCode(), SGUR_002.getDefaultMessage());
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity userAuthenticate(String authorization) throws AuthenticationFailedException {

        String user = "";
        String pass = "";

        //Decode basic password
        try{
            byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodedText = new String(decode);
            String[] decodedArray = decodedText.split(":");
            user = decodedArray[0];
            pass = decodedArray[1];
        } catch(Exception e){
            throw new AuthenticationFailedException(ATH_001.getCode(), ATH_001.getDefaultMessage());
        }


        //check for empty user
        UserEntity userEntity = userDao.getUserByUserName(user);
        if (userEntity == null) {
            throw new AuthenticationFailedException(ATH_001.getCode(), ATH_001.getDefaultMessage());
        }

        //check for password match
        final String encryptPass = cryptographyProvider.encrypt(pass, userEntity.getSalt());
        if(encryptPass.equals(userEntity.getPassword())){
            //on successful match create new user token
            UserAuthTokenEntity userAuthTokenEntity = generateUserAuthTokenEntity(userEntity,encryptPass);
            return userAuthTokenEntity;
        } else {
            throw new AuthenticationFailedException(ATH_002.getCode(), ATH_002.getDefaultMessage());
        }

    }

    private UserAuthTokenEntity generateUserAuthTokenEntity(UserEntity userEntity, String encryptPass) {

        //Create new jwt object
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptPass);

        UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();


        //Token generation
        userAuthToken.setUser(userEntity);
        userAuthToken.setUuid(UUID.randomUUID().toString());
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expiresAt = now.plusHours(ConstantValues.EXPIRE_TIME);
        userAuthToken.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
        userAuthToken.setLoginAt(now);
        userAuthToken.setExpiresAt(expiresAt);
        userDao.createAuthToken(userAuthToken);

        return userAuthToken;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity userSignOut(final String authorization) throws SignOutRestrictedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.fetchAuthToken(authorization);

        if(userAuthTokenEntity == null){
            throw new SignOutRestrictedException(SGOR_001.getCode(), SGOR_001.getDefaultMessage());
        }

        else if (userAuthTokenEntity.getLogoutAt() != null) {
            throw new SignOutRestrictedException(SGOR_001.getCode(), SGOR_001.getDefaultMessage());
        }
        userAuthTokenEntity.setLogoutAt(ZonedDateTime.now());
        userAuthTokenEntity = userDao.editAuthToken(userAuthTokenEntity);

        return userAuthTokenEntity.getUser();
    }


}

