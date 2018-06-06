package com.example.drools.engine;

import com.example.drools.engine.engine.DrRuleEngine;
import com.example.drools.engine.engine.condition.BaseCondition;
import com.example.drools.engine.engine.condition.MetaCondition;
import com.example.drools.engine.engine.condition.StreamCondition;
import com.example.drools.engine.engine.condition.enums.CompareMethod;
import com.example.drools.engine.engine.condition.enums.DurationType;
import com.example.drools.engine.engine.condition.enums.ReduceType;
import com.example.drools.engine.engine.initializer.GeneralDataInitializer;
import com.example.drools.engine.engine.initializer.StreamDataFilter;
import com.example.drools.engine.engine.rule.DrAction;
import com.example.drools.engine.engine.rule.DrCondition;
import com.example.drools.engine.engine.rule.DrRule;
import com.example.drools.engine.test.action.SendAwardAction;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jiyiqin on 2017/9/19.
 * <p>
 * * 关于SpringBootTest注解：
 * The @SpringBootTest annotation tells Spring Boot to go and look for a main
 * configuration class (one with @SpringBootApplication for instance), and use
 * that to start a Spring application context.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RuleEngineTest2 {
    @Autowired
    private GeneralDataInitializer dataInitializer;
    @Autowired
    private StreamDataFilter dataFilter;
    @Resource
    private DrRuleEngine drRuleEngine;

    @Test
    public void testBasicRule() throws Exception {
        //准备数据
        Map<String, Object> data = new HashMap<>();
        data.put("aId", 1110011);
        data.put("actionType", "INVEST");
        data.put("investAmount", 50000);
        data.put("investPlan", "33222");
        data.put("cellPhone", "18190800520");
        //构造规则
        DrCondition d1 = BaseCondition.newBaseCondition(new MetaCondition("${actionType}", CompareMethod.EQUAL,
                Arrays.asList("INVEST")));
        DrCondition d2 = BaseCondition.newBaseCondition(new MetaCondition("${investAmount}", CompareMethod.GRATER,
                Arrays.asList(10000)));
        DrCondition d3 = BaseCondition.newBaseCondition(new MetaCondition("${investPlan}", CompareMethod.IN,
                Arrays.asList("33221", "33222")));
        DrCondition d4 = BaseCondition.newBaseCondition(new MetaCondition("${cellPhone}", CompareMethod.STR_STARTS_WITH,
                Arrays.asList("181")));
        DrCondition drCondition7 = BaseCondition.newBaseCondition(new MetaCondition("${registerTime}", CompareMethod.BETWEEN,
                Arrays.asList(DateUtils.parseDate("2018-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"),
                        DateUtils.parseDate("2018-06-01 00:00:00", "yyyy-MM-dd HH:mm:ss"))));
        DrCondition drCondition8 = BaseCondition.newBaseCondition(new MetaCondition("${registerChannel}", CompareMethod.IN,
                Arrays.asList("channelA", "channelB")));
        DrCondition drCondition9 = BaseCondition.newBaseCondition(new MetaCondition("${bindCardTime}", CompareMethod.LESS_AND_EQUAL,
                Arrays.asList("DateUtils.addDays(${registerTime}, 20)")));

        //完成实名、或完成绑卡
        //"或"关系支持
        MetaCondition metaConditionA = new MetaCondition("${hasRealName}", CompareMethod.EQUAL, Arrays.asList(true));
        MetaCondition metaConditionB = new MetaCondition("${hasBindCard}", CompareMethod.EQUAL, Arrays.asList(true));
        DrCondition drCondition10 = BaseCondition.newBaseCondition(metaConditionA, metaConditionB);
        DrCondition drCondition11 = BaseCondition.newBaseCondition(new MetaCondition("${investAmount}", CompareMethod.GRATER,
                Arrays.asList(10000)));

        //构造条件
        MetaCondition filterCondition31 = new MetaCondition("${investAmount}", CompareMethod.GRATER, Arrays.asList(1000));
        StreamCondition.Reduce reduce3 = StreamCondition.newReduce("STREAM_INVEST", new StreamCondition.Duration(DurationType.LAST_DAYS, 7), Arrays.asList(filterCondition31), ReduceType.COUNT, null);
        DrCondition drCondition13 = StreamCondition.newStreamCondition(reduce3, new MetaCondition("${reduceValue}", CompareMethod.GRATER, Arrays.asList(3)));

        //存在发生在注册后20天内、且投资金额大于1w的首次投资
        //带时间限制的流式数据支持
        MetaCondition filterCondition41 = new MetaCondition("${investTime}", CompareMethod.LESS_AND_EQUAL,
                Arrays.asList("DateUtils.addDays(${registerTime}, 20)"));
        MetaCondition filterCondition42 = new MetaCondition("${investAmount}", CompareMethod.GRATER, Arrays.asList(10000));
        StreamCondition.Reduce reduce4 = StreamCondition.newReduce("STREAM_INVEST", new StreamCondition.Duration(DurationType.AHEAD_LENGTH, 1),
                Arrays.asList(filterCondition41, filterCondition42), ReduceType.COUNT, null);
        DrCondition drCondition14 = StreamCondition.newStreamCondition(reduce4, new MetaCondition("${reduceValue}", CompareMethod.GRATER,
                Arrays.asList(0)));

        //(用户大于1w的投资的总额、大于5w)
        //流式数据支持
        MetaCondition filterCondition21 = new MetaCondition("${investAmount}", CompareMethod.GRATER, Arrays.asList(10000));
        StreamCondition.Reduce reduce2 = StreamCondition.newReduce("STREAM_INVEST", null, Arrays.asList(filterCondition21),
                ReduceType.SUM, "${investAmount}");
        DrCondition drCondition12 = StreamCondition.newStreamCondition(reduce2, new MetaCondition("${reduceValue}", CompareMethod.GRATER,
                Arrays.asList(50000)));

        //设置action
        DrRule rule = new DrRule("testRule1");
        rule.setConditions(Arrays.asList(Arrays.asList(d1, d2, d3, d4, drCondition7, drCondition8, drCondition9, drCondition10, drCondition11, drCondition12, drCondition13, drCondition14)));
        rule.setActions(Arrays.asList(new SendAwardAction("award1"), new SendAwardAction("award2")));
        //运行规则
        drRuleEngine.runRuleEngine(data, Arrays.asList(rule));
    }

    public List<DrAction> runRuleEngine(Map<String, Object> data, List<DrRule> rules) throws Exception {
        KieServices kieServices = KieServices.Factory.get();
        KieHelper helper = new KieHelper();
        //转换DrRule为drools规则
        Iterator<DrRule> rulesIterator = rules.iterator();
        while (rulesIterator.hasNext()) {
            DrRule rule = rulesIterator.next();
            helper.addResource(kieServices.getResources().newByteArrayResource(parse(rule).getBytes()), ResourceType.DRL);
        }
        KieBase kieBase = helper.build();   //步骤1：构建知识库
        KieSession kieSession = kieBase.newKieSession();    //步骤2：创建到Drools的会话
        Set<String> passRuleSet = new HashSet();    //步骤3：插入数据
        kieSession.setGlobal("passRuleSet", passRuleSet); //插入执行通过的规则的收集器
        kieSession.setGlobal("dataInitializer", dataInitializer);   //插入外部基本数据加载器
        kieSession.setGlobal("dataFilter", dataFilter); //插入外部流式数据加载器
        kieSession.insert(data);   //插入准备过规则的用户数据
        int ruleFiredCount = kieSession.fireAllRules(); //步骤4：执行规则
        kieSession.dispose();
        List<DrRule> passRules = rules.parallelStream().filter(rule -> passRuleSet.contains(rule.getId())).collect(Collectors.toList());
        List<DrAction> resultActions = new ArrayList<>();
        passRules.stream().forEach(rule -> resultActions.addAll(rule.getActions()));
        return resultActions;
    }

    private String parse(DrRule rule) {
        return null;
    }
}
