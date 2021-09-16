package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.common.ConstantValues;
import com.upgrad.quora.service.common.GenericErrorCode;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.POST, path= "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion (@RequestHeader("authorization") final String authorization, final QuestionRequest questionRequest) throws AuthenticationFailedException, AuthorizationFailedException {

        GenericErrorCode err1 = GenericErrorCode.ATHR_001_CREATEQUESTION;
        GenericErrorCode err2 = GenericErrorCode.ATHR_002_CREATEQUESTION;

        UserEntity userEntity = questionBusinessService.userAuthenticateSignin(authorization, err1, err2);

        //Map question request to entity
        QuestionEntity questionEntity = modelMapper.map(questionRequest, QuestionEntity.class);
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setUser(userEntity);
        questionEntity.setDate(ZonedDateTime.now());

        //Call service to create new question
        QuestionEntity createdQuestionEntity = questionBusinessService.createQuestion(questionEntity);

        //Create required response
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status(ConstantValues.QUESTION_CREATED);

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);

    }

    @RequestMapping(method = RequestMethod.GET, path= "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions (@RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException, AuthorizationFailedException {

        GenericErrorCode err1 = GenericErrorCode.ATHR_001_GETALLQUESTION;
        GenericErrorCode err2 = GenericErrorCode.ATHR_002_GETALLQUESTION;

        UserEntity userEntity = questionBusinessService.userAuthenticateSignin(authorization, err1, err2);


        //Call service to get all questions
        List<QuestionEntity> createdQuestionEntityList = questionBusinessService.getAllQuestions();

        //Rsponse to store responses and list of responses for final list sharing
        List<QuestionDetailsResponse> entities = new ArrayList<QuestionDetailsResponse>();

        for(QuestionEntity questionEntity : createdQuestionEntityList){
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
            questionDetailsResponse.id(questionEntity.getUuid()).content(questionEntity.getContent());
            entities.add(questionDetailsResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(entities, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.PUT, path= "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent (@RequestHeader("authorization") final String authorization, @PathVariable("questionId") String questionId, QuestionEditRequest questionEditRequest)
            throws AuthorizationFailedException, InvalidQuestionException {

        GenericErrorCode err1 = GenericErrorCode.ATHR_001_EDITALLQUESTION;
        GenericErrorCode err2 = GenericErrorCode.ATHR_002_EDITALLQUESTION;
        GenericErrorCode err3 = GenericErrorCode.ATHR_003_EDITALLQUESTION;
        GenericErrorCode err4 = GenericErrorCode.QUES_001_EDITALLQUESTION;


        //Call service to check exceptions
        UserEntity userEntity = questionBusinessService.userAuthenticateSignin(authorization, err1, err2);
        UserEntity userEntity1 = questionBusinessService.questionEditOwner(authorization, questionId ,err3 ,err4);


        //Call service to get question
        QuestionEntity createdQuestionEntity = questionBusinessService.getQuestionbyId(questionId);

        //Add new content to existing entity
        createdQuestionEntity.setContent(questionEditRequest.getContent());

        //Call service to edit question in db
        questionBusinessService.editQuestion(createdQuestionEntity);


        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(createdQuestionEntity.getUuid()).status(ConstantValues.QUESTION_EDITED);

        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);

    }


    @RequestMapping(method = RequestMethod.DELETE, path= "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion  (@RequestHeader("authorization") final String authorization, @PathVariable("questionId") String questionId)
            throws AuthorizationFailedException, InvalidQuestionException {

        GenericErrorCode err1 = GenericErrorCode.ATHR_001_DELETEQUESTION;
        GenericErrorCode err2 = GenericErrorCode.ATHR_002_DELETEQUESTION;
        GenericErrorCode err3 = GenericErrorCode.ATHR_003_DELETEQUESTION;
        GenericErrorCode err4 = GenericErrorCode.QUES_001_DELETEQUESTION;


        //Call service to check exceptions
        UserEntity userEntity = questionBusinessService.userAuthenticateSignin(authorization, err1, err2);
        UserEntity userEntity2 = questionBusinessService.adminDeleteCheck(authorization, questionId ,err3 ,err4);


        //Call service to get question
        QuestionEntity createdQuestionEntity = questionBusinessService.getQuestionbyId(questionId);

        //Call service to remove question in db
        QuestionEntity removedQuestionEntity = questionBusinessService.removeQuestion(createdQuestionEntity);


        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(removedQuestionEntity.getUuid()).status(ConstantValues.QUESTION_DELETED);

        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);

    }


    @RequestMapping(method = RequestMethod.GET, path= "question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser  (@RequestHeader("authorization") final String authorization, @PathVariable("userId") String userId)
            throws AuthenticationFailedException, AuthorizationFailedException, UserNotFoundException {

        GenericErrorCode err1 = GenericErrorCode.ATHR_001_GETALLQUESTIONBYUSER;
        GenericErrorCode err2 = GenericErrorCode.ATHR_002_GETALLQUESTIONBYUSER;
        GenericErrorCode err3 = GenericErrorCode.USR_001_GETALLQUESTIONBYUSER;

        UserEntity userEntity = questionBusinessService.userAuthenticateSignin(authorization, err1, err2);
        UserEntity userEntity1 = questionBusinessService.userExistCheck(userId, err3);

        //Call service to get all questions
        List<QuestionEntity> createdQuestionEntityList = questionBusinessService.getAllQuestionsByUserID(userEntity);

        //Rsponse to store responses and list of responses for final list sharing
        List<QuestionDetailsResponse> entities = new ArrayList<QuestionDetailsResponse>();

        for(QuestionEntity questionEntity : createdQuestionEntityList){
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
            questionDetailsResponse.id(questionEntity.getUuid()).content(questionEntity.getContent());
            entities.add(questionDetailsResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(entities, HttpStatus.OK);

    }

}

