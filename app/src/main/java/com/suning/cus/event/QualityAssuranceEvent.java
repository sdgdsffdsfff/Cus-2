package com.suning.cus.event;

import com.suning.cus.json.JsonQualityAssurance;

/**
 * Created by 15010551 on 2015/4/18.
 */
public class QualityAssuranceEvent {

    public JsonQualityAssurance jsonQualityAssurance;

    public QualityAssuranceEvent(JsonQualityAssurance jsonQualityAssurance) {
        this.jsonQualityAssurance = jsonQualityAssurance;
    }
}
