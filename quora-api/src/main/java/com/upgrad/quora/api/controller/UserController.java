package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.common.ConstantValues;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ModelMapper modelMapper;


    @RequestMapping(method = RequestMethod.POST, path= "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

        //mAP USER ENTITY DATAMODEL TO REQUEST HEADER
        //UserEntity userEntity = new UserEntity();
        //userEntity.setUuid(UUID.randomUUID().toString());
        //userEntity.setFirstName(signupUserRequest.getFirstName());
        //userEntity.setLastName(signupUserRequest.getLastName());
        //userEntity.setUserName(signupUserRequest.getUserName());
        //userEntity.setEmail(signupUserRequest.getEmailAddress());
        //userEntity.setPassword(signupUserRequest.getPassword());
        //userEntity.setSalt("1234abc");
        //userEntity.setCountry(signupUserRequest.getCountry());
        //userEntity.setAboutMe(signupUserRequest.getAboutMe());
        //userEntity.setDob(signupUserRequest.getDob());
        //userEntity.setRole(ConstantValues.DEFAULT_USER_ROLE);
        //userEntity.setContactnumber(signupUserRequest.getContactNumber());

        //MAP USER ENTITY DATAMODEL TO REQUEST HEADER
        UserEntity userEntity = modelMapper.map(signupUserRequest, UserEntity.class);
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setRole(ConstantValues.DEFAULT_USER_ROLE);

        //Verification of signedup user
        userBusinessService.verificationSignUp(userEntity.getUserName(), userEntity.getEmail());

        //Sign up user and send response back
        UserEntity createdUserEntity = userBusinessService.signup(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status(ConstantValues.USER_REGISTRATION_OUTPUT);
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);

    }

    @RequestMapping(method = RequestMethod.POST, path = "/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {


        UserAuthTokenEntity userAuthToken = userBusinessService.userAuthenticate(authorization);

        SigninResponse signinResponse = new SigninResponse().id(userAuthToken.getUuid()).message(ConstantValues.LOGIN_OUTPUT);

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthToken.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.POST, path = "/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {

        UserEntity user = userBusinessService.userSignOut(authorization);

        SignoutResponse signoutResponse = new SignoutResponse().id(user.getUuid()).message(ConstantValues.LOGOUT_OUTPUT);
        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }


}
