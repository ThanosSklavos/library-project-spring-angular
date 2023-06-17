package gr.aueb.cf.libraryapp.authentication;

import gr.aueb.cf.libraryapp.dto.LoginDTO;
import gr.aueb.cf.libraryapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final IUserService userService;
    private final MessageSource messageSource;

    @Autowired
    public CustomAuthenticationProvider(IUserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    private MessageSourceAccessor accessor;

    @PostConstruct
    private void init() {
        accessor = new MessageSourceAccessor(messageSource, Locale.getDefault());
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        LoginDTO loginDTO = new LoginDTO(username, password, 0L);

        if (!userService.usernameExists(username) || userService.isUserValid(loginDTO).getLoggedInUserId() == null) {
            return null;
        } else {
            List<GrantedAuthority> authorities = new ArrayList<>();
            if (username.equals("admin")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password, authorities);

            return usernamePasswordAuthenticationToken;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }


}
