package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.AuthRequestDto;
import school.faang.user_service.dto.AuthResponseDto;
import school.faang.user_service.security.JwtUserDetailsService;
import school.faang.user_service.security.jwt.JwtTokenProvider;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUserDetailsService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/user/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequest) {
        try {
            String username = authRequest.getUsername();
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, authRequest.getPassword()));
            UserDetails user = userService.loadUserByUsername(username);
            String token = jwtTokenProvider.generateToken(user.getUsername());
            AuthResponseDto response = new AuthResponseDto();
            response.setUsername(username);
            response.setToken(token);
            return ResponseEntity.ok(response);
        } catch (InternalAuthenticationServiceException | BadCredentialsException e) {
            throw new BadCredentialsException("Invalid login or password");
        }
    }
}