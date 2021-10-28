package cn.org.atool.fluent.form.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 校验工具
 *
 * @author darui.wu
 */
public class ValidKit {
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    /**
     * 对Form Object进行校验
     *
     * @param object Form对象
     * @param <T>    对象类型
     */
    public static <T> void validate(T object) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException("validate object can't be null.");
        }
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        List<String> messageList = new ArrayList<>();
        for (ConstraintViolation<T> v : violations) {
            messageList.add(v.getPropertyPath() + ": " + v.getMessage());
        }
        String message = String.join("\n", messageList);
        if (!message.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        if (object instanceof IValidation) {
            ((IValidation) object).validate();
        }
    }
}