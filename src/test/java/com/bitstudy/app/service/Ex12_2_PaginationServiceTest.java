package com.bitstudy.app.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;


/* SpringBootTest는 전체 테스트, 테스트는 속도때문에 최대한 간단히
webEnvironment: 기본값 Mock, Mocking한 웹 환경 구성(NONE : 별도의 웹컨피그레이션을 하지 않아 가벼운 부트테스트할수 있음)
classes = PaginationService.class 설정클래스를 지정하여 이것만 불러서 쓰겠다.(즉 더 가볍게 할수 있다.)
*  */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PaginationService.class)
class Ex12_2_PaginationServiceTest {

    /* 그냥 @SpringBootTest 쓸거면 new로 */
//    private final PaginationService sut = new PaginationService();

    private final PaginationService sut;
    public Ex12_2_PaginationServiceTest(@Autowired PaginationService sut) {
        this.sut = sut;
    }

    /* 중요(지금과 같은 테스트는 이놈밖에 없다.)
     *  페이지네이션 시 현재 페이지가 가운데로 가게 하는 복잡한 경우의수 테스트 시 사용
     *  여러 argument 등록해놓고 한번에 여러번 돌릴 수 있는 테스트 애너테이션
     *  그걸 제외하면 @Test와 같음(그래서 여기에 @Test 생략함)
     *  @ParameterizedTest 사용 시 테스트를 위해서 들어가는 값이나 객체들이 필요한데 그걸
     *  Source라고 한다.
     *  Source종류 3가지
     *  1) ValueSource - 같은 타입의 여러가지 단순함 값(literal value)들을 테스트 할때 사용
     *                   ex) @ValueSource(ints={0, 101}) 0~100까지만먹음
     *  2) CsvSource - comma(,)로 구분되는 값을 사용
     *                   ex) @CsvSource({"10, true", "100, false"})
     *  3) MethodSource - 메소드들에서 리턴되는 값을 인자로 사용
     *                    * 왠만하면 이걸로 사용하면됨
     *                    입력값이 별도의 메서드로 있어야한다.
     * */
    @ParameterizedTest(name="[{index}] 현재페이지:{0}, 총페이지: {1} => {2}")
    /* 그냥 @ParameterizedTest만 써도 되는데 출력시 로그에 지저분하게 나온다. name을 ㅣㅇ용해서 출력포맷을 설정할 수 있다. (@DisplayName 같은거임)
      {0} {1} {2} 의미는
      givenCurrPageNumberAndTotalPages_whenReturnBarList 매서드의 매개변수 순서
      {0} currentPageNumber
      {1} totalPages
      {2} expected
    */
    @MethodSource
    /* 이걸써야 같은 이름으로 된 Arguments메서드 가져온다. @MethodSource("연결할 메소드명") 써주면 되는데, 생략하면 해당 테스트 메서드와 같은이름것 가져온다. */
    @DisplayName("현재 페이지 번호와 총 페이지 수를 주면, 페이징 바 리시트 만들기")

    void givenCurrPageNumberAndTotalPages_whenReturnBarList(int currentPageNumber, int totalPages, List<Integer> expected) {
        // Given

        // When
        List<Integer> actual = sut.getPaginationBarNumbers(currentPageNumber, totalPages);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> givenCurrPageNumberAndTotalPages_whenReturnBarList() {
        /** arguments는 딥다이브해서 import static org.junit.jupiter.params.provider.Arguments.arguments를 import
         * argument(0, 10, List.of(0,1,2,3,4)에서 매개변수 순서는 위에
         * givenCurrPageNumberAndTotalPages_whenReturnBarList 메서드의 매개변수 순서에 맞춘거
         * 현재 페이지가 1페이지(여기선 0)고, 전체 페이지수는 10이라고 설정
         * 현재 이상태로 돌리면 나와야 하는(expect)결과는 (0,1,2,3,4)로 예상된다.
         * 전체 아티클수가 100개니까 10개씩하면 총 10페이지이고, 한페이지당 몇개씩 보여주는거는
         * ArticleController의 articles() 메서드에서 @PageableDefault(size=10) 부분에 설정해놓음
         * 주의!: 1페이지는 0으로 표시되어 -1씩으로 생각해야함
         */
        return Stream.of(
                arguments(0, 10, List.of(0,1,2,3,4)),
                arguments(1, 10, List.of(0,1,2,3,4)),
                arguments(2, 10, List.of(0,1,2,3,4)),
                arguments(3, 10, List.of(1,2,3,4,5)),
                arguments(4, 10, List.of(2,3,4,5,6)),
                arguments(5, 10, List.of(3,4,5,6,7)),
                arguments(6, 10, List.of(4,5,6,7,8)),
                arguments(7, 10, List.of(5,6,7,8,9)),
                arguments(8, 10, List.of(6,7,8,9)),
                arguments(9, 10, List.of(7,8,9))
                );
    }

    @DisplayName("현재 설정되어 있는 페이지네이션 바의 길이 알아내기")
    @Test
    void givenNothing_whenCallMethod() {
        // Given

        // When
        int barLength = sut.currentBartLength();

        // Then
        assertThat(barLength);
    }
}