package site.book.project.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
@EnableWebSocketMessageBroker
public class SecurityConfig implements WebSocketMessageBrokerConfigurer {
    @Bean // 스프링 컨텍스트에서 생성, 관리하는 객체 - 필요한 곳에 의존성 주입. <bean></bean>같은.
    // 암호화 알고리즘 객체 -> Spring Security에서는 비밀번호는 반드시 암호화를 해야 함. 암호화 안되면 오류!
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {        
        // Spring Security는 GET 방식을 제외한 POST/PUT/DELETE 요청에서
        // CSRF 토큰을 요구함.
        // POST/PUT/DELETE 요청에서 CSRF ㅊ토큰을 서버로 전송하지 않으면 403(forbidden 권한없음) 에러가 발생.
        // 기능 구현을 간단히 하기 위해서 Spring Security의 CSRF 기능을 비활성화.
        http.csrf().disable(); // CSRF 비활성. 새글작성 등을 다시 쓸 수 있다.
        http.formLogin()
            .loginPage("/user/signin")
            .defaultSuccessUrl("/", true);
        
        http.logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true);

        
        // 특정 경로(URL)에 시큐리티 적용:
        // 권한을 가지고 있는(로그인한) 사용자만 접근할 수 있는 경로
        // 익명 사용자(로그인하지 않은) 사용자도 접근할 수 있는 경로
        

       http.authorizeHttpRequests() // 요청에 따른 권한 설정 시작.
            .antMatchers("/post/create", "/post/modify", "/cart", "/cart/add",  "/myPage", "/book/wishList" ,"/orderFromDetail", "/search/cart", "/search/order", "/market/create", "/market/mypage") // "/post", "/api/reply" 로 시작하는 모든 경로           .hasRole("USER") // USER 권하능ㄹ 가지고 있는 사용자만 접근 가능
            .hasRole("USER")

            .anyRequest() // 그 이외의 모든 요청
            .permitAll(); // 로그인 여부와 상관 없이 허용.

        // => 새로운 요청 경로/컨트롤러가 생길 때마다 설정 자바 코드를 변경을 해야 하는 번거로움.
        // => 컨트롤러 메서드를 작성할 때 애너테이션을 사용해서 권한 설정을 할 수 있음.
        // => (1) SecurityConfig 클래스에는 @EnableGlobalMethodSecurity 애너테이션을 사용.
        // => (2) 각각의 컨트롤러 메서드에 @PreAuthorize (이전) 또는 @PostAuthorize (이후) 애너테이션을 사용.
        
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // setHeartbeatTime은 1/1000초 단위로 연결 상태 확인해 주는 역할(1000은 1초마다 확인하겠다는 뜻) -> 읽음/안읽음 표시 기능에 쓰일 수 있음
        registry.addEndpoint("/chat").setAllowedOriginPatterns("*").withSockJS().setHeartbeatTime(1000);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지를 구독하는 요청 url prefix => 즉 메시지 받을 때 (subscribe, sub)
        registry.enableSimpleBroker("/user");

        // 메시지를 발행하는 요청 url prefix => 즉 메시지 보낼 때 (publish, pub)
        registry.setApplicationDestinationPrefixes("/app");
    }
    
    @Bean  // 로컬 폴터 이미지 불러오기 위한 config 추가
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()
                    );
        };
    }
}
