package cn.org.atool.fluent.mybatis.segment;

import cn.org.atool.fluent.mybatis.base.IEntity;
import cn.org.atool.fluent.mybatis.base.IQuery;
import cn.org.atool.fluent.mybatis.base.IWrapper;
import cn.org.atool.fluent.mybatis.exception.FluentMybatisException;
import cn.org.atool.fluent.mybatis.metadata.TableMeta;
import cn.org.atool.fluent.mybatis.metadata.TableMetaHelper;
import cn.org.atool.fluent.mybatis.segment.model.ParameterPair;
import cn.org.atool.fluent.mybatis.segment.model.WrapperData;
import lombok.Getter;

import static cn.org.atool.fluent.mybatis.utility.MybatisUtil.notNull;

/**
 * 查询条件封装
 *
 * @param <E>  对应的实体类
 * @param <W>  更新器或查询器
 * @param <NQ> 对应的嵌套查询器
 * @author darui.wu
 */
public abstract class BaseWrapper<
    E extends IEntity,
    W extends IWrapper<E, W, NQ>,
    NQ extends IQuery<E, NQ>
    >
    implements IWrapper<E, W, NQ> {
    private static final long serialVersionUID = 2674302532927710150L;

    /**
     * 表别名
     */
    @Getter
    protected String alias = "";

    @Getter
    protected final WrapperData wrapperData;

    protected BaseWrapper(String table, Class<E> entityClass, Class queryClass) {
        this(table, new ParameterPair(), entityClass, queryClass);
    }

    protected BaseWrapper(String table, ParameterPair parameters, Class<E> entityClass, Class queryClass) {
        notNull(entityClass, "entityClass must not null,please set entity before use this method!");
        this.wrapperData = new WrapperData(table, parameters, entityClass, queryClass);
    }

    @Override
    public W last(String lastSql) {
        this.wrapperData.last(lastSql);
        return (W) this;
    }

    /**
     * 是否有主键字段
     *
     * @return true: 有主键字段
     */
    protected boolean hasPrimary() {
        return true;
    }

    /**
     * 判断字段是否在范围内
     *
     * @param column 字段
     * @return 如果不是合法字段，抛出异常
     * @throws FluentMybatisException 字段校验异常
     */
    protected abstract void validateColumn(String column) throws FluentMybatisException;

    protected TableMeta getTableMeta() {
        return TableMetaHelper.getTableInfo(this.getWrapperData().getEntityClass());
    }
}