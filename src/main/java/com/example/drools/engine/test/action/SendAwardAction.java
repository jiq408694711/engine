package com.example.drools.engine.test.action;


import com.example.drools.engine.engine.rule.DrAction;

/**
 * Created by jiyiqin on 2018/5/17.
 */
public class SendAwardAction extends DrAction {
    private String awardName;
    public SendAwardAction(String awardName) {
        this.awardName = awardName;
    }

    @Override
    public void action() {
        System.out.println("发放奖励:" + awardName);
    }
}
