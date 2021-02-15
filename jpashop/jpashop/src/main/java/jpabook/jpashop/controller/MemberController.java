package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        //Controller 에서 View 로 넘어갈 때 데이터 실어서 넘김.
        //memberForm 는 빈껍데기지만 validation 뭐 이런거해줘서 들고간다.
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    //아래 코드 public String create(@Valid MemberForm form, BindingResult result) { 에 대한 설명

    //아래 form 변수에 오류(필수써야되는거안쓰다던지)가 있으면 튕김. 컨트롤러로 코드 안넘어감.
    //@valid 다음에 BindingResult 가 있으면 오류가 result 에 담겨서 public String create 가 실행이 된다.
    //그러면  if (result.hasErrors()) 에 걸리게 되고 애러를 createMemberForm.html 로 다시보내고 얘는 그걸 뿌림. (spring boot thymeleaf 의 thymeleaf-spring 기능임)
    //홈페이지가서 이름에 아무것도 안넣고 submit 버튼누르면 "회원 이름은 필수 입니다."가 뜨는데 이건 MemberForm.java 에서 @NotEmpty(message = "회원 이름은 필수 입니다.") 한 메세지임.
    //이건 애러를 createMemberForm.html 로 보내면 얘는 그걸 #fields 로 받았고. fieldError 는 위에 html 위에 있는데 빨간색 껍대기로 만드는것.(이줄은 애러있으면 그냥 색깔 넣으려고 한 것)
    //그밑에 <p> 테그는 메세지를 넣기 위한 것. fields 에 hasError 로 이름이 있으면 th:errors="*{name}" 하는데 이는 name 필드에 대한 애러메세지를 출력하라는 것.
    //실패했는데 밑에 정보들(도시,거리,우편번호)이 남아있는 이유는 보낸 form 정보를 다시 가져가기 때문 (애러있는 result 포함해서).

    //@Valid 는 MemberForm 이 @NotEmpty(message = "회원 이름은 필수 입니다.") validation 쓰는걸 알려줌.

    //둘이 비슷한데 Member member 대신 MemberForm form 넣는 이유. 화면에서 넘어오는 validation 이랑 실제 도메인이 원하는 validation 이 다를 수 있다.
    //그렇다고 도메인 Member 에 화면처리기능 추가하자니 Member 가 지저분해짐. 차라리 화면에 fit 한 것을 만들고 그걸로 데이터 받자.
    //Entity 는 최대한 순수하게 유지해야됨!!

    @PostMapping(value = "/members/new")
    public String create(@Valid MemberForm form, BindingResult result) { //홈페이지에서 html 의 ${MemberForm}에 넣은 값이 넘어옴. 위에 주석 설명추가.
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }
        //그냥 이거 안하고 MemberForm 클래스에서 private Address address;해도된다.
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);
        memberService.join(member);//저장됨.
        return "redirect:/";//첫번째 페이지(http://localhost:8080/)로 넘어가게됨.
    }

    //원래 위에서 가져올 때 Member member 대신 MemberForm form 쓴 것 처럼
    //뿌릴때도 List<Member> 대신 변환해서 화면에 꼭 필요한 데이터들만 출력하게 하는게 좋음.
    //근데 여기선 출력하는데 Entity 손안대니까 그냥 Member 반환한거임.
    //API 를 만들때는 이유불문하고 Entity 를 외부로 반환하면 안됨. 1.스펙변함, 2. 중요정보노출
    @GetMapping(value = "/members")
    public String list(Model model) {//model 로 view 에 객체 전달.
        List<Member> members = memberService.findMembers();//findMembers 하면 JPA 에서 JPQL 짜서 모든 맴버 조회한다.
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
