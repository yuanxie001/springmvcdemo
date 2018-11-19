package us.zoom.spring.mvc.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import us.zoom.spring.domian.User;
@Component
public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {

        return clazz.equals(User.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user= (User) target;
        if (StringUtils.isBlank(user.getName())){
            errors.reject("name","name must not null");
        }
        if (StringUtils.containsAny(user.getName()," ")){
            errors.reject("name","name can't have blank");
        }
    }
}
