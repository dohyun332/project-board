package com.bitstudy.app.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
                       * MockMvc는 웹 어플리케이션을 어플리케이션 서버에 배포하지않고, 가짜로 테스트용 MVC 환경을 만들어서 요청 및
 *                       전송, 응답기능을 제공해주는 유틸리티 클래스.
 *                       간단히 말하면, 내가 컨트롤러 테스트 하고 싶을때 실제 서버에 올리지 않고 테스트용으로 시뮬레이션해서 내가
 *                       MVC가 되도록 해주는 클래스
 *                       그냥 컨트롤러 슬라이스 테스트 한다고 하면 @WebMvcTest랑 MockMvc쓰면됨
 *  2) @DataJpaTest - JPA 레포지토리 테스트 할때 사용
 *                    @Entity가 있는 엔티티클래스들을 스캔해서 테스트를 위한 JPA레포지토리들을 설정
 *                    * @Component 나 @ConfigurationProperties Bean들은 무시
 *  3) @RestClientTest - (클라이언트 입장에서의) API 연동 테스트
 *                       테스트 코드 내에서 Mock서버를 띄울 수 있다.(response, request에 대한 사전 정의가 가능)

 */

@WebMvcTest
public class Ex07_3_1_DataRestTest_실패하는테스트 {
    /** MockMvc 테스트 방법 5가지가 있다.
     * 1) MockMvc 생성(빈 준비)
     * 2) MockMvc에게 요청에 대한 정보를 입력
     * 3) 요청에 대한 응답값을 expect를 이용해서 테스트 한다.
     * 4) expect를 다 통과하면 테스틑 통과
     */

    private final MockMvc mvc; // 1) MockMvc 생성(빈 준비)

    public Ex07_3_1_DataRestTest_실패하는테스트(@Autowired MockMvc mvc) { // 2) MockMvc에게 요청에 대한 정보를 입력
        this.mvc = mvc;
    }

    /* [api] - 게시글 리스트 전체 조회 */
    @DisplayName("[api] - 게시글 리스트 전체 조회 ")
    @Test
    void articles() throws Exception {
        /** 일단 이 테스트는 실패해야 정상임, 이유는 해당 api를 찾을 수 없기 때문에
            콘솔창에 MockHttpServletRequest 부분에 URI="/api/articles" 있을거다 복사해서 브라우저에
            http://localhost:8080/api/articles 넣어보면 데이터가 제대로 나온다.
            그럼 왜 여기선 안되냐면, @WebMvcTest는 슬라이스 테스트이기 때문에 Controller외의 빈들은 로드하지않았기 때문이다.

            그래서 일단 @WebMvcTest 대신 통합테스트(@SpringBootTest)로 돌릴거다.
         */

        mvc.perform(get("api/articles"))
                .andExpect(status().isOk()) // 현재 200이 들어왔는지 확인
                                            // MockMvcResultMatchers.status
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
        // mock 관련된 자동완성이 없다면 deep dive방식(ctrl+space) 그리고 import alt+ent
        // 서버 잘갔다왔냐? content 타입이 맞느냐?

        /* 특별한 import!(deep dive)
           1) perform() 안에 get 치고 ctrl+space 누르면 딥다이트 함
              * 그냥 기본으로 나오는건 getClass()인데 그거 엔터치지말고, ctrl+space하기
                그러면 다른 방식의 추천들이 엄청나오는데 그 중에
                MockMvcRequestBuilders.get이라는거 선택할건데, 엔터 치지 말고
                Alt+enter (static import를 하기 위해) 해서 넣기
                그러면 맨 위에 import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
                생김
                * static import란 필드나 메서드를 클래스를 지정하지 않고도 코드에서 사용할 수 있도록 하는 기능.

          2) andExpect(status) 부분 설명
             status치고 ctrl+space 두세번하면 MockMvcResultMatchers.status() 나옴
             그거 alt+enter로 해서 static import 하기

          3) andExpect(content().contentType()) 부분 설명
             content 검사는 contentType 으로하고 MediaType 사용함
             valueOf안에 들어갈 content-type은 아까 HAL의 Response Headers에 있는 content-type에 있는거 복사해오기
         */
    }
}
