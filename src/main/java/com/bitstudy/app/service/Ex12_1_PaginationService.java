package com.bitstudy.app.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

/** 할일 : 게시판 페이지 페이지네이션 서비스 구현 */

@Service
public class Ex12_1_PaginationService {

    /* 페이지네이션 바의 길이(개수) - 한번에 몇페이지씩 보여줄 숫자 */
    private static final int BAR_LENGTH = 5;

    /* 테스트에서 getPaginationBarNumbers에 값을 보내서 리턴으로 페이징 결과를 얻어낸다.
    * List<Integer>: 숫자 리스트를 받아서 뷰에 보내준다.
    * currentPageNumber: 현재 페이지 번호를 보내준다.
    * totalPages : 전체 페이지 수 구하기
    *  */
    public List<Integer> getPaginationBarNumbers(int currentPageNumber, int totalPages) {
        /* startNumber, endNumber 구하기
          현재 선택한 페이지 번호가 페이징부분의 가운데에 위치하게 만들거임
         */
        int startNumber = Math.max(0, (currentPageNumber - (BAR_LENGTH / 2)));
        // Math.max를 이용해서 공식이 음수가 될 경우에는 1페이지가 startNumber에 저장되게
        int endNumber = Math.min(totalPages, (startNumber + BAR_LENGTH));
        /* for(int i = startnumber; i<endnumber; i++) {
            }

         */

        return IntStream.range(startNumber, endNumber).boxed().toList();
        /* boxed()메서드는 IntStream같은 원시타입에 대한 스트림을 지원한다.
        *  (IntStream -> Stream<Integer>)
        *  */
    }

    /* getter 같은거 */
    public int currentBartLength() {
        return BAR_LENGTH;
    }
}
