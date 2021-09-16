package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity getAnswerbyId(String answerId) {
        try {
            return entityManager.createNamedQuery("answerByUuid", AnswerEntity.class).setParameter("uuid", answerId).getSingleResult();
        }
        catch (NoResultException nre){
            return null;
        }
    }

    public void editAnswer( AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
    }

    public AnswerEntity removeAnswer(final AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
        return answerEntity;
    }

    public List<AnswerEntity> getAllAnswersByQuestionID(final QuestionEntity questionEntity) {
        try {
            return entityManager.createNamedQuery("answerByQuestion", AnswerEntity.class).setParameter("question",questionEntity).getResultList();
        }
        catch (NoResultException nre){
            return null;
        }

    }
}
