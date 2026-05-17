package com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.filters;

import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.entities.User;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.repository.UserRepository;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.service.JWTService;
import com.code.airbnb.app.airbnbhotelmanagementandbookingsystem.security.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserRepository userRepository;

    // Transports Exceptions occuring in SecurityFilterChain to DispatcherServlet to resolve them.
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;




    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Catch the Token stored in header in Key-Value pair "Authorization" : "Bearer fjgunehsfjunsefn8utjhngejht023t2t3.55rsgretertg.53454j"
        final String token = request.getHeader("Authorization");
        if(token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            // Since there was no token or not an authorization token, this means its a public route. No Auth needed.
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Bearer jdfogj834utu9325fds -> split -> ([""],["jdfogj834utu9325fds"])
            String jwt = token.split("Bearer ", 2)[1];
            Long userId = jwtService.getUserIDFromJWTToken(jwt);
            User user = userRepository.findById(userId).orElse(null);

            // Checking if id is not null and SecurityContext does not have anything
            if(user != null && SecurityContextHolder.getContext().getAuthentication() == null){
                // Creating an authentication token
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                        = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                // Putting the User into the Spring Security Context Holder
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        catch (Exception e) {
            SecurityContextHolder.clearContext();
            handlerExceptionResolver.resolveException(request, response, null, e);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
