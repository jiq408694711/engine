package com.example.drools.engine.test.initializer.general;

import com.example.drools.engine.engine.initializer.general.AbstractDataInitializer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by jiyiqin on 2018/5/20.
 */
@Component
public class BindCardDataInitializer extends AbstractDataInitializer {

    @Override
    public String dataKey() {
        return "hasBindCard";
    }

    @Override
    public Object initialize(Map<String, Object> data) {
        return true;
    }
}
