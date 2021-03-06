package com.example.drools.engine.engine.condition;

import com.example.drools.engine.engine.condition.enums.DurationType;
import com.example.drools.engine.engine.condition.enums.ReduceType;
import com.example.drools.engine.engine.rule.DrCondition;

import java.util.List;

/**
 * Created by jiyiqin on 2018/5/22.
 * 流式数据条件的数据来自于针对流的操作结果
 */
public class StreamCondition extends DrCondition {
    //针对流式数据一系列操作，计算出value
    private Reduce reduce;
    //针对value的条件判断
    private MetaCondition condition;

    public static Reduce newReduce(String streamKey, Duration duration, List<MetaCondition> filterConditions, ReduceType reduceOp, String reduceKey) {
        return new Reduce(streamKey, duration, filterConditions, reduceOp, reduceKey);
    }

    public static StreamCondition newStreamCondition(Reduce streamDataResult, MetaCondition condition) {
        StreamCondition drCondition = new StreamCondition();
        drCondition.setReduce(streamDataResult);
        drCondition.setCondition(condition);
        return drCondition;
    }

    public Reduce getReduce() {
        return reduce;
    }

    public void setReduce(Reduce reduce) {
        this.reduce = reduce;
    }

    public MetaCondition getCondition() {
        return condition;
    }

    public void setCondition(MetaCondition condition) {
        this.condition = condition;
    }

    public static class Reduce {
        private String streamKey;
        private Duration duration;
        private List<MetaCondition> filterConditions;
        private ReduceType reduceOp;
        private String reduceKey;

        public Reduce(String streamKey, Duration duration, List<MetaCondition> filterConditions, ReduceType reduceOp, String reduceKey) {
            this.setStreamKey(streamKey);
            this.setDuration(duration);
            this.setFilterConditions(filterConditions);
            this.setReduceOp(reduceOp);
            this.setReduceKey(reduceKey);
        }

        public String getStreamKey() {
            return streamKey;
        }

        public void setStreamKey(String streamKey) {
            this.streamKey = streamKey;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }

        public List<MetaCondition> getFilterConditions() {
            return filterConditions;
        }

        public void setFilterConditions(List<MetaCondition> filterConditions) {
            this.filterConditions = filterConditions;
        }

        public ReduceType getReduceOp() {
            return reduceOp;
        }

        public void setReduceOp(ReduceType reduceOp) {
            this.reduceOp = reduceOp;
        }

        public String getReduceKey() {
            return reduceKey;
        }

        public void setReduceKey(String reduceKey) {
            this.reduceKey = reduceKey;
        }
    }

    public static class Duration {
        private DurationType type;
        private Integer value;
        private Long beginTime;
        private Long endTime;

        public Duration(DurationType type, Integer value) {
            this.type = type;
            this.value = value;
        }

        public Duration(DurationType type) {
            this.type = type;
        }

        public DurationType getType() {
            return type;
        }

        public void setType(DurationType type) {
            this.type = type;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public Long getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(Long beginTime) {
            this.beginTime = beginTime;
        }

        public Long getEndTime() {
            return endTime;
        }

        public void setEndTime(Long endTime) {
            this.endTime = endTime;
        }
    }
}
