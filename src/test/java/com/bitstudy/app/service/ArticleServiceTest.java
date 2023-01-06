package com.bitstudy.app.service;

import com.bitstudy.app.repository.ArticleRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


/** 서비스 비즈니스 로직은 슬라이스 테스트 기능 사용 안하고 만들어볼거임
 *  스프링부트 어플리케이션 컨텍스트가 뜨는데 걸리는 시간을 없애려고한다.
 *  디펜던시가 추가되야 하는 부분에는 Mocking을 하는 방식으로 한다.
 *  그래서 많이 사용하는 라이브러리가 mockito라는게 있다.(스프링 테스트 패키지에 내장되어 있음.)
 *
 *  @ExtendWith(MockitoExtension.class) 쓰면된다.
 * */
//@WebMvcTest controller가 아니라 쓸수없다.
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    /* Mock을 주입하는 거에다가 @InjectMocks를 달아줘야한다. 그외의 것들한테는 @Mock 달아준다. */
    @InjectMocks
    private ArticleService sut; // sut - system under test. 테스트 짤때 사용하는 이름 중 하나, 이건 테스트 대상이다 라는 뜻

    @Mock
    private ArticleRepository articleRepository;
}