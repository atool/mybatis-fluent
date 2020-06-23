package cn.org.atool.fluent.mybatis.method.partition;

import cn.org.atool.fluent.mybatis.method.metadata.DbType;
import cn.org.atool.fluent.mybatis.method.normal.SelectList;

import static cn.org.atool.fluent.mybatis.method.model.StatementId.Method_SelectListFromSpec;

/**
 * SelectSpec : 从指定表（分表）查询
 *
 * @author darui.wu
 */
public class SelectSpec extends SelectList {
    public SelectSpec(DbType dbType) {
        super(dbType);
    }

    @Override
    public String statementId() {
        return Method_SelectListFromSpec;
    }

    @Override
    protected boolean isSpecTable() {
        return true;
    }
}