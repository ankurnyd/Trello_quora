package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.ConstantValues;
import com.upgrad.quora.service.common.GenericErrorCode;
import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerBusinessService {

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {

        return answerDao.createAnswer(answerEntity);

    }

    public UserEntity answerEditOwner(String authorization, String answerId, GenericErrorCode err3, GenericErrorCode err4) throws AnswerNotFoundException, AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.fetchAuthToken(authorization);
        UserEntity userEntityLoggedIn =  userAuthTokenEntity.getUser();

        AnswerEntity answerEntity = answerDao.getAnswerbyId(answerId);

        UserEntity userEntityAnswer = answerEntity.getUser();

        if (answerEntity == null){
            throw new AnswerNotFoundException(err4.getCode(), err4.getDefaultMessage());
        }

        else if (userEntityAnswer.getUuid() != userEntityLoggedIn.getUuid()){
            throw new AuthorizationFailedException(err3.getCode(),err3.getDefaultMessage());
        }

        return userEntityAnswer;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity getAnswerbyId(String answerId) {

        return answerDao.getAnswerbyId(answerId);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void editAnswer(AnswerEntity answerEntity) {
        answerDao.editAnswer(answerEntity);
    }

    public UserEntity answerAdminDeleteCheck(String authorization, String answerId, GenericErrorCode err3, GenericErrorCode err4) throws AnswerNotFoundException, AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.fetchAuthToken(authorization);
        UserEntity userEntityLoggedIn =  userAuthTokenEntity.getUser();

        AnswerEntity answerEntity = answerDao.getAnswerbyId(answerId);

        UserEntity userEntityAnswer = answerEntity.getUser();

        if (answerEntity == null){
            throw new AnswerNotFoundException(err4.getCode(), err4.getDefaultMessage());
        }


        else if ( (userEntityAnswer.getUuid() != userEntityLoggedIn.getUuid()) & (userEntityLoggedIn.getRole() == ConstantValues.DEFAULT_USER_ROLE) ){
            throw new AuthorizationFailedException(err3.getCode(),err3.getDefaultMessage());
        }

        return userEntityAnswer;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity removeAnswer(AnswerEntity answerEntity) {
        return answerDao.removeAnswer(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<AnswerEntity> getAllAnswersByQuestionID(QuestionEntity questionEntity) {
        return answerDao.getAllAnswersByQuestionID(questionEntity);
    }
}
