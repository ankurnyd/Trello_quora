package com.upgrad.quora.service.common;

import java.util.HashMap;
import java.util.Map;

public enum GenericErrorCode implements ErrorCode {

    /**
     * Error message: <b>An unexpected error occurred. Please contact System Administrator</b><br>
     * <b>Cause:</b> This error could have occurred due to undetermined runtime errors.<br>
     * <b>Action: None</b><br>
     */
    GEN_001("GEN-001", "An unexpected error occurred. Please contact System Administrator"),
    SGUR_001("SGR-001","Try any other Username, this Username has already been taken"),
    SGUR_002("SGR-002", "This user has already been registered, try with any other emailId"),
    ATH_001("ATH-001", "This username does not exist"),
    ATH_002("ATH-002", "Password failed"),
    SGOR_001("SGR-001", "User is not Signed in"),
    USER_001_COMMON("USR-001", "User with entered uuid does not exist"),
    ATHR_001_COMMON("ATHR-001", "User has not signed in"),
    ATHR_002_COMMON("ATHR-002", "User is signed out.Sign in first to get user details"),
    ATHR_001_ADMIN("ATHR-001", "User has not signed in"),
    ATHR_002_ADMIN("ATHR-002", "User is signed out"),
    ATHR_003_ADMIN("ATHR-003", "Unauthorized Access, Entered user is not an admin"),
    USR_001_ADMIN("USR-001", "User with entered uuid to be deleted does not exist"),
    ATHR_001_CREATEQUESTION("ATHR-001", "User has not signed in"),
    ATHR_002_CREATEQUESTION("ATHR-002", "User is signed out.Sign in first to post a question"),
    ATHR_001_GETALLQUESTION("ATHR-001", "User has not signed in"),
    ATHR_002_GETALLQUESTION("ATHR-002", "User is signed out.Sign in first to get all questions"),
    ATHR_001_EDITALLQUESTION("ATHR-001", "User has not signed in"),
    ATHR_002_EDITALLQUESTION("ATHR-002", "User is signed out.Sign in first to edit the question"),
    ATHR_003_EDITALLQUESTION("ATHR-003", "Only the question owner can edit the question"),
    QUES_001_EDITALLQUESTION("QUES-001", "Entered question uuid does not exist"),
    ATHR_001_DELETEQUESTION("ATHR-001", "User has not signed in"),
    ATHR_002_DELETEQUESTION("ATHR-002", "User is signed out.Sign in first to delete a question"),
    ATHR_003_DELETEQUESTION("ATHR-003", "Only the question owner or admin can delete the question"),
    QUES_001_DELETEQUESTION("QUES-001", "Entered question uuid does not exist"),
    ATHR_001_GETALLQUESTIONBYUSER("ATHR-001", "User has not signed in"),
    ATHR_002_GETALLQUESTIONBYUSER("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user"),
    USR_001_GETALLQUESTIONBYUSER("USR-001", "User with entered uuid whose question details are to be seen does not exist"),
    ATHR_001_CREATEANSWER("ATHR-001", "User has not signed in"),
    ATHR_002_CREATEANSWER("ATHR-002", "User is signed out.Sign in first to post an answer"),
    QUES_001_CREATEANSWER("QUES-001", "The question entered is invalid"),
    ATHR_001_EDITANSWER("ATHR-001", "User has not signed in"),
    ATHR_002_EDITANSWER("ATHR-002", "User is signed out.Sign in first to edit an answer"),
    ATHR_003_EDITANSWER("ATHR-003", "Only the answer owner can edit the answer"),
    ANS_001_EDITANSWER("ANS-001", "Entered answer uuid does not exist"),
    ATHR_001_DELETEANSWER("ATHR-001", "User has not signed in"),
    ATHR_002_DELETEANSWER("ATHR-002", "User is signed out.Sign in first to delete an answer"),
    ATHR_003_DELETEANSWER("ATHR-003", "Only the answer owner or admin can delete the answer"),
    ANS_001_DELETEANSWER("ANS-001", "Entered answer uuid does not exist"),
    ATHR_001_GETALLANSWERSTOQUESTION("ATHR-001", "User has not signed in"),
    ATHR_002_GETALLANSWERSTOQUESTION("ATHR-002", "User is signed out.Sign in first to get the answers"),
    QUES_001_GETALLANSWERSTOQUESTION("QUES-001", "User with entered uuid whose question details are to be seen does not exist") ;



    private static final Map<String, GenericErrorCode> LOOKUP = new HashMap<String, GenericErrorCode>();

    static {
        for (final GenericErrorCode enumeration : GenericErrorCode.values()) {
            LOOKUP.put(enumeration.getCode(), enumeration);
        }
    }

    private final String code;

    private final String defaultMessage;

    private GenericErrorCode(final String code, final String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

}
