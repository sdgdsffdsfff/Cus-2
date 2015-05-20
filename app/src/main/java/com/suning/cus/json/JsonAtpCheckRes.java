package com.suning.cus.json;

import com.suning.cus.bean.AtpCheckResponseList;

import java.util.List;

/**
 * Created by 15010551 on 2015/3/30.
 */

public class JsonAtpCheckRes extends JsonBase {
    /**
     * {
     "isSuccess": "S",
     "atpCheckResponseList": [
     {
     " commodity": "P019806228"(配件编码)
     " commodityNumber": "15"(数量)
     " price": "5" (销售价)
     " freedomCount": "5"(W库总配件数)
     " usedCount": "5" (W库已用配件数)
     " total": "10" (W工人总配件数)
     "atpResult":"5天内到货"(ATP检查消息描述)      },
     {
     " commodity": "P019806228"(配件编码)
     " commodityNumber": "15"(数量)
     " price": "5" (销售价)
     " freedomCount": "5"(W库总配件数)
     " usedCount": "5" (W库已用配件数)
     " total": "10" (W工人总配件数)
     "atpResult":"5天内到货"(ATP检查消息描述)
     },
     ]
     }
     */

    private List<AtpCheckResponseList> atpCheckResponseList;

    public List<AtpCheckResponseList> getAtpCheckResponseList() {
        return atpCheckResponseList;
    }

    public void setAtpCheckResponseList(List<AtpCheckResponseList> atpCheckResponseList) {
        this.atpCheckResponseList = atpCheckResponseList;
    }

}
