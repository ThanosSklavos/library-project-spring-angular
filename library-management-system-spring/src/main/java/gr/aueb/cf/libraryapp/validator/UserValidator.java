package gr.aueb.cf.libraryapp.validator;

import gr.aueb.cf.libraryapp.dto.UserDTO;
import gr.aueb.cf.libraryapp.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserValidator implements Validator {

    private final IUserService userService;

    @Autowired
    public UserValidator(IUserService userService) {
        this.userService = userService;

    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UserDTO.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO userToRegister = (UserDTO) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "empty");
        if (userToRegister.getUsername().length() < 3 || userToRegister.getUsername().length() > 32) {
            errors.rejectValue("username", "size");
        }
        if (userService.usernameExists(userToRegister.getUsername())) {
            errors.rejectValue("username", "duplicate");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty");
        UserDTO userDTO = (UserDTO) target;
        String email = userDTO.getEmail();

        if (!StringUtils.isEmpty(email)) {
            Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
            Matcher matcher = pattern.matcher(email);

            if (!matcher.matches()) {
                errors.rejectValue("email", "Invalid email format");
            } else if (userService.emailExists(email)) {
                errors.rejectValue("email", "duplicate");
            }
        }

        if (userService.emailExists(userToRegister.getEmail())) {
            errors.rejectValue("email", "duplicate");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "empty");
        if (userToRegister.getPassword().length() < 3 || userToRegister.getPassword().length() > 32) {
            errors.rejectValue("password", "size");
        }
    }
}

