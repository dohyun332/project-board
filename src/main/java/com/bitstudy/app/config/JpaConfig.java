package com.bitstudy.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/** @Configuration 이라고 하면 설정파일을 만들기 위한 어노테이션 or Bean을 등록하기 위한 어노테이션
 *  이걸 쓰면 JpaConfig 클래스는 Configuration bean이 된다.
 *  간단히 말해서 @Configuration달아놓으면 시스템이 "이거 설정파일이야, Bean으로 등록해줘"라는 뜻
 */
@Configuration
// 안달아주면 컴퓨터가 config 파일인지 모른다.
/** JPA에서 auditing 가능하게 하는 어노테이션
 *  jpa auditing 이란: Spring Data jpa에서 자동으로 값을 넣어주는 기능.
 *         jpa에서 자동으로 세팅하게 해줄때 사용하는 기능인데, 특정 데이터를 보고 있다가 생성, 수정되면 자동으로 값을 넣어주는 기능
 */
@EnableJpaAuditing
public class JpaConfig {

    // 사람이름 정보 넣어주려고 만드는 config 설정
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("bitstudy");
        // 이렇게하면 앞으로 JPA Auditing 할때마다 사람이름은 이걸로 넣게된다.
        // TODO: 나중에 스프링 시큐리티로 인증기능 붙일때 수정필요.
    }
    // 다른 인스턴스변수나 함수가 있다면 @Bean 달린애만 bean에 등록해줘 하는 것
}
