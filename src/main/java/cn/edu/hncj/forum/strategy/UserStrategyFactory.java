package cn.edu.hncj.forum.strategy;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
@Service
public class UserStrategyFactory {
    @Autowired
    private List<UserStrategy> userStrategies;

    public UserStrategy getStrategy(String type) {
        for (UserStrategy userStrategy : userStrategies) {
            if(StringUtils.equals(type,userStrategy.getSupportedType())) {
                return userStrategy;
            }
        }
        return null;
    }
}
