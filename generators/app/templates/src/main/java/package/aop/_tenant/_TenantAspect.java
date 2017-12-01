package <%=packageName%>.aop.<%= tenantNameLowerFirst %>;

import <%=packageName%>.security.SecurityUtils;
import <%=packageName%>.repository.UserRepository;
import <%=packageName%>.domain.User;
import com.starbucks.inventory.service.dto.UserDTO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Filter;
import java.util.Optional;

@Aspect
@Component
public class <%= tenantNameUpperFirst %>Aspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private final String fieldName =  "<%= tenantNameSpinalCased %>Id";

    private final Logger log = LoggerFactory.getLogger(<%= tenantNameUpperFirst %>Aspect.class);

    /**
     * Run method if if a user is created or updated
     * sets the tenant on the user
     */
    @AfterReturning(value = "execution(* <%= packageName %>.service.UserService.createUser(..)) || execution(* <%= packageName %>.service.UserService.updateUser(..))", returning = "user")
    public void afterExecution(JoinPoint joinPoint, User user) throws Throwable {
        Optional<String> login = SecurityUtils.getCurrentUserLogin();

        if(login.isPresent()) {
            User loggedInUser = userRepository.findOneByLogin(login.get()).get();

            if (loggedInUser.get<%= tenantNameUpperFirst %>() != null) {
                user.set<%= tenantNameUpperFirst %>(loggedInUser.get<%= tenantNameUpperFirst %>());
            }
            else{
                UserDTO userDTO = (UserDTO)joinPoint.getArgs()[0];
                user.set<%= tenantNameUpperFirst %>(userDTO.get<%= tenantNameUpperFirst %>());
            }
        }
    }

    /**
     * Run method if User service is hit.
     * Filter users based on which <%= tenantNameLowerCase %> the user is associated with.
     * Skip filter if user has no <%= tenantNameLowerCase %>
     */
    <%- tenantisedEntityServices %>
    public void beforeExecution() throws Throwable {
        Optional<String> login = SecurityUtils.getCurrentUserLogin();

        if(login.isPresent()) {
			User user = userRepository.findOneByLogin(login.get()).get();

			if (user.get<%= tenantNameUpperFirst %>() != null) {
				Filter filter = entityManager.unwrap(Session.class).enableFilter("<%= tenantNameUpperCase %>_FILTER");
				filter.setParameter(fieldName, user.get<%= tenantNameUpperFirst %>().getId());
			}
		}
    }
}
