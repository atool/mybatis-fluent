package cn.org.atool.fluent.mybatis.entity;

import cn.org.atool.fluent.mybatis.generate.ATM;
import cn.org.atool.fluent.mybatis.generate.entity.UserEntity;
import cn.org.atool.fluent.mybatis.mapper.EntityHelperFactory;
import org.junit.jupiter.api.Test;
import org.test4j.junit5.Test4J;

import java.util.HashMap;
import java.util.Map;

public class EntityTest extends Test4J {
    @Test
    void copy() {
        UserEntity user = new UserEntity()
            .setUserName("fluent mybatis")
            .setAge(3)
            .setVersion("1.3.0")
            .copy();
        want.object(user).eqDataMap(ATM.DataMap.user.entity()
            .userName.values("fluent mybatis")
            .age.values(3)
            .version.values("1.3.0"));
    }

    @Test
    void toEntityMap() {
        Map<String, Object> user = new UserEntity()
            .setUserName("fluent mybatis")
            .setAge(3)
            .setVersion("1.3.0")
            .toEntityMap();
        want.object(user).eqDataMap(ATM.DataMap.user.entity()
            .userName.values("fluent mybatis")
            .age.values(3)
            .version.values("1.3.0"));
    }

    @Test
    void toColumnMap() {
        Map<String, Object> user = new UserEntity()
            .setUserName("fluent mybatis")
            .setAge(3)
            .setVersion("1.3.0")
            .toColumnMap();
        want.object(user).eqDataMap(ATM.DataMap.user.table()
            .userName.values("fluent mybatis")
            .age.values(3)
            .version.values("1.3.0"));
    }

    @Test
    void toEntity() {
        Map<String, Object> map = new HashMap<>();
        map.put("userName", "fluent mybatis");
        map.put("age", 3);
        map.put("version", "1.3.0");

        UserEntity user = EntityHelperFactory.getInstance(UserEntity.class).toEntity(map);
        want.object(user).eqDataMap(ATM.DataMap.user.entity()
            .userName.values("fluent mybatis")
            .age.values(3)
            .version.values("1.3.0"));
    }
}
