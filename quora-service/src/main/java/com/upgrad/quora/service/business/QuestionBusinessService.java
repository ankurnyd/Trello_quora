package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.ConstantValues;
import com.upgrad.quora.service.common.GenericErrorCode;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class QuestionBusinessService {
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity)
    {
        return questionDao.createQuestion(questionEntity);
    }

    public UserEntity userExistCheck(String userId, GenericErrorCode err3) throws UserNotFoundException {

        UserEntity userEntity = userDao.getUser(userId);

        if (userEntity == null){
            throw new UserNotFoundException(err3.getCode(), err3.getDefaultMessage());
        }

        return userEntity;

    }


    public UserEntity userAuthenticateSignin(String authorization, GenericErrorCode err1, GenericErrorCode err2) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.fetchAuthToken(authorization);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException(err1.getCode(), err1.getDefaultMessage());
        }

        else if (userAuthTokenEntity.getLogoutAt() != null || userAuthTokenEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException(err2.getCode(), err2.getDefaultMessage());
        }

        UserEntity userEntity = userAuthTokenEntity.getUser();

        return userEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity getQuestionbyId(String questionId) {
        return questionDao.getQuestionbyId(questionId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void editQuestion(QuestionEntity questionEntity) {
        questionDao.editQuestion(questionEntity);
    }

    public UserEntity questionEditOwner(String authorization, String questionId, GenericErrorCode err3, GenericErrorCode err4) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.fetchAuthToken(authorization);
        UserEntity userEntityLoggedIn =  userAuthTokenEntity.getUser();

        QuestionEntity questionEntity = questionDao.getQuestionbyId(questionId);
        UserEntity questionOwnerEntity = questionEntity.getUser();

        if (questionEntity == null){
            throw new InvalidQuestionException(err4.getCode(), err4.getDefaultMessage());
        }

        else if(userEntityLoggedIn.getUuid() != questionOwnerEntity.getUuid()){
            throw new AuthorizationFailedException(err3.getCode(),err3.getDefaultMessage());
        }

        return userEntityLoggedIn;
    }




    public UserEntity adminDeleteCheck(String authorization, String questionId, GenericErrorCode err3, GenericErrorCode err4) throws InvalidQuestionException, AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.fetchAuthToken(authorization);
        UserEntity userEntityLoggedIn =  userAuthTokenEntity.getUser();

        QuestionEntity questionEntity = questionDao.getQuestionbyId(questionId);
        UserEntity questionOwnerEntity = questionEntity.getUser();

        if (questionEntity == null){
            throw new InvalidQuestionException(err4.getCode(), err4.getDefaultMessage());
        }

        else if( (userEntityLoggedIn.getUuid() != questionOwnerEntity.getUuid()) & (userEntityLoggedIn.getRole() == ConstantValues.DEFAULT_USER_ROLE) ){
            throw new AuthorizationFailedException(err3.getCode(),err3.getDefaultMessage());
        }

        return userEntityLoggedIn;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity removeQuestion(QuestionEntity questionEntity) {
        return questionDao.removeQuestion(questionEntity);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionsByUserID(UserEntity userEntity) {
        return questionDao.getAllQuestionsByUserID(userEntity);
    }

    public QuestionEntity checkInvalidQuestion(String questionId, GenericErrorCode err3) throws InvalidQuestionException {

        QuestionEntity questionEntity = questionDao.getQuestionbyId(questionId);

        if (questionEntity == null){
            throw new InvalidQuestionException(err3.getCode(), err3.getDefaultMessage());
        }

        return questionEntity;
    }


}
