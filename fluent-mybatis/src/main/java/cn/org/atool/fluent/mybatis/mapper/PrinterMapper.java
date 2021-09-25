package cn.org.atool.fluent.mybatis.mapper;

import cn.org.atool.fluent.mybatis.base.BatchCrud;
import cn.org.atool.fluent.mybatis.base.IEntity;
import cn.org.atool.fluent.mybatis.base.crud.IQuery;
import cn.org.atool.fluent.mybatis.base.crud.IUpdate;
import cn.org.atool.fluent.mybatis.base.entity.IMapping;
import cn.org.atool.fluent.mybatis.base.mapper.IEntityMapper;
import cn.org.atool.fluent.mybatis.base.mapper.IWrapperMapper;
import cn.org.atool.fluent.mybatis.base.provider.SqlProvider;
import lombok.Getter;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.SqlSessionTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static cn.org.atool.fluent.mybatis.If.isBlank;
import static cn.org.atool.fluent.mybatis.mapper.FluentConst.*;
import static cn.org.atool.fluent.mybatis.utility.MybatisUtil.field;
import static cn.org.atool.fluent.mybatis.utility.MybatisUtil.value;
import static org.apache.ibatis.builder.annotation.ProviderContextKit.newProviderContext;

/**
 * PrinterMapper: 不实际执行sql语句, 只输出sql语句
 *
 * @author darui.wu
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class PrinterMapper implements IWrapperMapper {

    private IWrapperMapper delegate;

    private Class<? extends IWrapperMapper> klass;

    private final int mode;

    private Configuration configuration;

    private TypeHandlerRegistry typeHandlerRegistry;

    @Getter
    private final List<String> sql = new ArrayList<>();

    private PrinterMapper(int mode, IWrapperMapper mapper) {
        this.mode = mode;
        this.delegate(mapper);
    }

    @Override
    public int insert(IEntity entity) {
        return this.simulate(M_Insert, map(Param_EW, entity), SqlProvider::insert);
    }

    @Override
    public int insertBatch(Collection entities) {
        return this.simulate(M_InsertBatch, map(Param_List, entities), SqlProvider::insertBatch);
    }

    @Override
    public int insertWithPk(IEntity entity) {
        return this.simulate(M_insertWithPk, map(Param_EW, entity), SqlProvider::insertWithPk);
    }

    @Override
    public int insertBatchWithPk(Collection entities) {
        return this.simulate(M_insertBatchWithPk, map(Param_List, entities), SqlProvider::insertBatchWithPk);
    }

    @Override
    public int insertSelect(String[] fields, IQuery query) {
        Map<String, Object> map = this.map(Param_EW, query, Param_Fields, fields);
        return this.simulate(M_insertSelect, map, SqlProvider::insertSelect);
    }

    @Override
    public int updateBy(IUpdate... updates) {
        return this.simulate(M_updateBy, map(Param_EW, updates), SqlProvider::updateBy);
    }

    @Override
    public List listEntity(IQuery query) {
        this.simulate(M_listEntity, map(Param_EW, query), SqlProvider::listEntity);
        return Collections.emptyList();
    }

    @Override
    public List<Map<String, Object>> listMaps(IQuery query) {
        this.simulate(M_listMaps, map(Param_EW, query), SqlProvider::listMaps);
        return Collections.emptyList();
    }

    @Override
    public List listObjs(IQuery query) {
        this.simulate(M_listObjs, map(Param_EW, query), SqlProvider::listObjs);
        return Collections.emptyList();
    }

    @Override
    public int count(IQuery query) {
        return this.simulate(M_count, map(Param_EW, query), SqlProvider::count);
    }

    @Override
    public int countNoLimit(IQuery query) {
        return this.simulate(M_countNoLimit, map(Param_EW, query), SqlProvider::countNoLimit);
    }

    @Override
    public int delete(IQuery query) {
        return this.simulate(M_delete, map(Param_EW, query), SqlProvider::delete);
    }

    @Override
    public void batchCrud(BatchCrud crud) {
        this.simulate(M_batchCrud, map(Param_EW, crud), SqlProvider::batchCrud);
    }

    @Override
    public void callProcedure(String procedure, Object parameter) {
        this.addSQL(parameter, () -> "{CALL " + procedure + "}");
    }

    @Override
    public IMapping mapping() {
        return this.delegate.mapping();
    }

    /* ============================================= */
    private PrinterMapper delegate(IWrapperMapper mapper) {
        this.delegate = mapper;
        this.klass = (Class) this.delegate.getClass().getInterfaces()[0];
        SqlSessionTemplate session = value(this.delegate, f_proxyHandler, f_sqlSession);
        configuration = session == null ? null : session.getConfiguration();
        typeHandlerRegistry = configuration == null ? null : configuration.getTypeHandlerRegistry();
        return this;
    }

    private int simulate(String method, Map map, BiFunction<Map, ProviderContext, String> simulator) {
        ProviderContext context = newProviderContext(this.klass, this.method(method));
        this.addSQL(map, () -> simulator.apply(map, context));
        return 1;
    }

    private Map<String, Object> map(Object... kvs) {
        Map<String, Object> map = new HashMap<>();
        for (int index = 1; index < kvs.length; index += 2) {
            map.put((String) kvs[index - 1], kvs[index]);
        }
        return map;
    }

    private static Map<String, Method> methods = null;

    private static final Field f_proxyHandler = field(Proxy.class, "h");

    private static final Field f_sqlSession = field(MapperProxy.class, "sqlSession");

    private Method method(String methodName) {
        if (methods == null) {
            methods = new HashMap<>(16);
            for (Method m : IEntityMapper.class.getDeclaredMethods()) {
                methods.put(m.getName(), m);
            }
        }
        return methods.get(methodName);
    }

    private <P> void addSQL(P object, Supplier<String> supplier) {
        String text = supplier.get();
        if (isBlank(text) || mode == 2) {
            this.sql.add(text);
            return;
        }
        TextSqlNode textSqlNode = new TextSqlNode(text);
        RawSqlSource sqlSource = new RawSqlSource(configuration, textSqlNode, object.getClass());
        BoundSql boundSql = sqlSource.getBoundSql(object);
        if (mode == 0) {
            this.sql.add(boundSql.getSql());
            return;
        }
        Map<String, Object> parameters = this.getParameters(boundSql);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String expression = "#{" + entry.getKey() + "}";
            Object value = entry.getValue();

            text = this.replaceBy(text, expression, value);
        }
        this.sql.add(text);
    }

    private String replaceBy(String text, String expression, Object value) {
        if (value == null) {
            text = text.replace(expression, "null");
        } else if (value.getClass().isPrimitive() || value instanceof Number || value instanceof Boolean) {
            text = text.replace(expression, String.valueOf(value));
        } else {
            text = text.replace(expression, "'" + value + "'");
        }
        return text;
    }

    private Map<String, Object> getParameters(BoundSql boundSql) {
        Map<String, Object> values = new HashMap<>();
        for (ParameterMapping pm : boundSql.getParameterMappings()) {
            if (pm.getMode() == ParameterMode.OUT) {
                continue;
            }
            values.put(pm.getProperty(), parseParameterValue(boundSql, pm));
        }
        return values;
    }

    private Object parseParameterValue(BoundSql boundSql, ParameterMapping pm) {
        Object parameterObject = boundSql.getParameterObject();
        String property = pm.getProperty();
        if (boundSql.hasAdditionalParameter(property)) {
            return boundSql.getAdditionalParameter(property);
        } else if (parameterObject == null) {
            return null;
        } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
            return parameterObject;
        } else {
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            return metaObject.getValue(property);
        }
    }

    private static final ThreadLocal<PrinterMapper> local = new ThreadLocal<>();

    /**
     * 主动设置PrinterMapper
     *
     * @param mode   打印SQL模式
     * @param mapper 原生mapper实例
     * @return PrinterMapper
     */
    public static IWrapperMapper set(int mode, IWrapperMapper mapper) {
        local.set(new PrinterMapper(mode, mapper));
        return local.get();
    }

    /**
     * 清除线程中的PrinterMapper实例
     */
    public static void clear() {
        local.remove();
    }

    /**
     * 返回线程中的PrinterMapper
     *
     * @return PrinterMapper
     */
    public static IWrapperMapper get(IWrapperMapper mapper) {
        PrinterMapper printerMapper = local.get();
        return printerMapper == null ? mapper : printerMapper.delegate(mapper);
    }
}