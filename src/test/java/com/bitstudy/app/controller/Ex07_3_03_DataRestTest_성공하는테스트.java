package com.bitstudy.app.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 컨트롤러에 대한 테스트는 모두 슬라이스 테스트할거다
  슬라이스 테스트 : 유닛테스트, 기능별(레이어별)로 잘라서 특정부분(기능)만 테스트하는것
  - 통합 테스트 에너테이션
    @SpringBootTest - 스프링이 관리하는 모든 빈을 등록시켜서 테스트하기 때문에 무겁다.
                      * 테스트할 가볍게 하기 위해서 @WebMvcTest를 사용해서 web레이어 관련 빈들만 등록한 상태로 테스트를 할 수도 있다.
                         단, web레이어 관련된 빈들만 등록되므로 Service는 등록되지 않는다. 그래서 Mock 관련 어노테이션을 이용해서
                         가짜로 만들어줘야한다.
  - 슬라이스 테스트 에너테이션
    1) @WebMvcTest - 슬라이스 테스트에서 대표적인 어노테이션, 컨테이너로 건드리는 녀석
                     - Controller를 테스트 할 수 있드록 관련 설정을 제공해준다.
                       @WebMvcTest를 선언하면 web과 관련된 Bean만 주입되고, MockMvc를 알아볼 수 있게 한다.
                       * MockMvc는 웹 어플리케이션을 어플리케이션 서버에 배포하지않고, 가짜로 테스트용 MVC 환경을 만들어서 요청 및 전송, 응답기능을 제공해주는 유틸리티 클래스.
 *             간단히 말하면, 내가 컨트롤러 테스트 하고 싶을때 실제 서버에 올리지 않고 테스트용으로 시뮬레이션해서 내가
 *             MVC가 되도록 해주는 클래스 그냥 컨트롤러 슬라이스 테스트 한다고 하면 @WebMvcTest랑 MockMvc쓰면됨
 *  2) @DataJpaTest - JPA 레포지토리 테스트 할때 사용
 *                    @Entity가 있는 엔티티클래스들을 스캔해서 테스트를 위한 JPA레포지토리들을 설정
 *                    * @Component 나 @ConfigurationProperties Bean들은 무시
 *  3) @RestClientTest - (클라이언트 입장에서의) API 연동 테스트
 *                       테스트 코드 내에서 Mock서버를 띄울 수 있다.(response, request에 대한 사전 정의가 가능)

 */

//@WebMvcTest
@SpringBootTest /* 이것만 있으면 MockMvc를 알아볼수가 없어서 @AutoConfigureMockMvc도 같이넣기 */
@AutoConfigureMockMvc
@Transactional /* 테스트를 돌리면 Hibernate부분에 select 쿼리문이 나오면서 실제 DB를 건드리는데, 테스트 끝난이후에 DB를 롤백시키는 용도*/
public class Ex07_3_03_DataRestTest_성공하는테스트 {
    private final MockMvc mvc; // 1) MockMvc 생성(빈 준비)

    public Ex07_3_03_DataRestTest_성공하는테스트(@Autowired MockMvc mvc) { // 2) MockMvc에게 요청에 대한 정보를 입력
        this.mvc = mvc;
    }

    /* [api] - 게시글 리스트 전체 조회 */
    @DisplayName("[api] - 게시글 리스트 전체 조회 ")
    @Test
    void articleAll() throws Exception {
        mvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    /* [api] - 게시글 리스트 단건 조회 */
    @DisplayName("[api] - 게시글 리스트 단건 조회 ")
    @Test
    void articleOne() throws Exception {
        mvc.perform(get("/api/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    ////////////////////////////////////////////////////
    @DisplayName("[api] - 댓글 리스트 전체 조회 ")
    @Test
    void articleCommentAll() throws Exception {
        mvc.perform(get("/api/articleComments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

    @DisplayName("[api] - 댓글 단건 조회 ")
    @Test
    void articleCommentOne() throws Exception {
        mvc.perform(get("/api/articleComments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
    }

}
