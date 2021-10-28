package cn.org.atool.fluent.form.setter;

import cn.org.atool.fluent.mybatis.base.IEntity;
import cn.org.atool.fluent.form.Form;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * FormFunction: 表单初始化函数
 *
 * @author darui.wu
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@FunctionalInterface
public interface FormFunction<E extends IEntity, S extends BaseFormSetter>
    extends BiFunction<Object, Form, IFormApply<E, S>> {
    /**
     * 按照entity来定义条件值
     *
     * @param entity entity
     * @return entity条件设置器
     */
    default Form with(Object entity, Consumer<IFormApply<E, S>> apply) {
        Form form = new Form();
        FormApply formApply = (FormApply) this.apply(entity, form);
        form.setEntityClass(formApply.getSetter().entityClass());
        apply.accept(formApply);
        return form;
    }
}