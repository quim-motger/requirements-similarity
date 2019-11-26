package com.quim.tfm.similarity.service;

import com.quim.tfm.similarity.entity.Requirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MathService {

    @Autowired
    BM25FService bm25FService;

    public double partialDerivativeForWF(String key, Requirement q, Requirement irrel, Requirement rel) {
        double b, d;
        switch (key) {
            case "WF1":
                b = bm25FService.bm25f_textPairUnigram(q, irrel);
                d = bm25FService.bm25f_textPairUnigram(q, rel);
                break;
            case "WF2":
                b = bm25FService.bm25f_textPairBigram(q, irrel);
                d = bm25FService.bm25f_textPairBigram(q, rel);
                break;
            case "WF3":
                b = bm25FService.projectScore(q.getProject(), irrel.getProject());
                d = bm25FService.projectScore(q.getProject(), rel.getProject());
                break;
            case "WF4":
                b = bm25FService.typeScore(q.getType(), irrel.getType());
                d = bm25FService.typeScore(q.getType(), rel.getType());
                break;
            case "WF5":
                b = bm25FService.componentScore(q.getComponents(), irrel.getComponents());
                d = bm25FService.componentScore(q.getComponents(), rel.getComponents());
                break;
            case "WF6":
                b = bm25FService.priorityScore(q.getPriority(), irrel.getPriority());
                d = bm25FService.priorityScore(q.getPriority(), rel.getPriority());
                break;
            default:
                b = bm25FService.versionsScore(q.getVersions(), irrel.getVersions());
                d = bm25FService.versionsScore(q.getVersions(), rel.getVersions());
                break;
        }

        //e^(sim(q,irrel) - sim(q,rel))
        double ef = Math.exp(bm25FService.sim(q, irrel) - bm25FService.sim(q, rel));
        //(b-d)*log(e)*e^(sim(q,irrel) - sim(q,rel))
        double num = (b - d) * Math.log(Math.exp(1)) * ef;
        //e^(sim(q,irrel) - sim(q,rel)) + 1
        double den = ef + 1;
        return num / den;
    }

    public double partialDerivativeForBDF2(Requirement q, Requirement irrel, Requirement rel) {
        return 0;
    }

    public double partialDerivativeForBDF1(Requirement q, Requirement irrel, Requirement rel) {
        return 0;
    }

    public double partialDerivativeForBSF2(Requirement q, Requirement irrel, Requirement rel) {
        return 0;
    }

    public double partialDerivativeForBSF1(Requirement q, Requirement irrel, Requirement rel) {
        return 0;
    }

    public double partialDerivativeForWDF2(Requirement q, Requirement irrel, Requirement rel) {
        return 0;
    }

    public double partialDerivativeForWDF1(Requirement q, Requirement irrel, Requirement rel) {
        return 0;
    }

    public double partialDerivativeForWSF2(Requirement q, Requirement irrel, Requirement rel) {
        return 0;
    }

    public double partialDerivativeForWSF1(Requirement q, Requirement irrel, Requirement rel) {
        return 0;
    }

    public double partialDerivativeForK3F2(Requirement q, Requirement irrel, Requirement rel) {
        return 0;
    }

    public double partialDerivativeForK3F1(Requirement q, Requirement irrel, Requirement rel) {
        return 0;
    }

    public double partialDerivativeForK1F2(Requirement q, Requirement irrel, Requirement rel) {
        return 0;
    }

    public double partialDerivativeForK1F1(Requirement q, Requirement irrel, Requirement rel) {
        return 0;
    }

}
