package Chapter_18_함수형_관점으로_생각하기;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunctionalExam {
    public static List<List<Integer>> subSet(List<Integer> list) {
        if( list.isEmpty() ){ //비어있다면 자신 자체가 서브 집합
            List<List<Integer>> ans = new ArrayList<>();
            ans.add(Collections.emptyList());
            return ans;
        }
        Integer first = list.get(0);
        List<Integer> rest = list.subList(1, list.size());
        List<List<Integer>> subans = subSet(rest); //빈 리스트가 아니면 하나의 요소를 꺼내고 나머지 요소의 모든 서브 집합을 찾아서
                                                    //subans로 전달 subans는 절반의 정답을 포함
        List<List<Integer>> subans2 = insertAll(first, subans); // 정답의 나머지 절반을 포함하는 subans2는 subans의 모든 리스트에 처음 꺼낸 요소를
                                                                //앞에 추가해서 만든다.
        return concat(subans, subans2); //subans.subans2를 연결하면 정답
    }

    public static List<List<Integer>> insertAll(Integer first, List<List<Integer>> lists) {
        List<List<Integer>> result = new ArrayList<>();// 기존 데이터 변경하지 않기 위해서 새로 ArrayList를 만든다.
        for (List<Integer> list : lists) {
            List<Integer> copyList = new ArrayList<>();
            copyList.add(first);
            copyList.addAll(list);
            result.add(copyList);
        }
        return result;
    }

    public static List<List<Integer>> concat( List<List<Integer>> a, List<List<Integer>> b) {
        List<List<Integer>> r = new ArrayList<>(a);// 기존 데이터 변경하지 않기 위해서 새로 ArrayList를 만든다.
        r.addAll(b);
        return r;
    }
}
