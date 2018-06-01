package com.example.drools.engine.test.ws;

import com.example.drools.engine.test.service.RuleService;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiyiqin on 2018/3/28.
 */
@RequestMapping("/test")
@Controller
public class RuleController {

//    @Autowired
//    private KieSession kieSession;

    @Autowired
    private RuleService ruleService;

    @ResponseBody
    @RequestMapping("/rule4")
    public void test4() {
        Map<String, Object> map = new HashMap<>();
//        map.put("investAmount", 3);
//        map.put("firstInvest", true);
//        map.put("totalInvest", 50000);
//        map.put("totalQuit", 300);

        map.put("userId", 1110011);
        map.put("actionType", "INVEST");
        map.put("investAmount", 3);
        map.put("investPlan", "33222");
        map.put("cellPhone", "18190800520");
        map.put("realNameTime", new Date());
        map.put("registerTime", new Date());
        map.put("age", 20);
        map.put("level", 4);
        map.put("investCount", 30);

        KieHelper helper = new KieHelper();
        helper.addResource(ResourceFactory.newClassPathResource("rules/rule5.drl"), ResourceType.DRL);

//        KieBaseConfiguration config = KieServices.Factory.get().newKieBaseConfiguration();
//        config.setOption( EventProcessingOption.STREAM );

        KieBase kBase = helper.build();
        KieSession kieSession = kBase.newKieSession();
        kieSession.insert(map);
        kieSession.setGlobal("ruleService", ruleService);
        int numberOfRulesFired = kieSession.fireAllRules();
        System.out.println("触发了" + numberOfRulesFired + "条规则.");
        kieSession.dispose();
    }
}
