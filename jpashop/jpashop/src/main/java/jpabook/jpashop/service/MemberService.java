package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //이렇게 읽기만하는건 붙이자. 데이터변경안됨. 최적화.
@RequiredArgsConstructor
public class MemberService {

    //1
    //@Autowired
    //private MemberRepository memberRepository; //이건 필드주입. 세터주입(단점은 다른곳에 써놨음), 생성자주입 있음.

    //2
    //요즘 spring 은 생성자가 1개면 생성자 파라미터에 자동으로 @Autowired 없어도 injection 해줌.
    //어차피 생성시 빼고 안바뀌니까 final 하자. 그럼 초기화(값새팅) 안하면 애러 떠서 컴파일시점에서 채크가능.
    //private final MemberRepository memberRepository;
    //public MemberService(MemberRepository memberRepository) {
    //    this.memberRepository = memberRepository; }

    //3
    //근데 lombok 을 쓴다면 @AllArgsConstructor 가 자동으로 생성자 만들어주고
    //더 좋은 @RequiredArgsConstructor 은 final 있는 필드만 가지고 생성자를 만들어줌.
    private final MemberRepository memberRepository;

    //회원 가입
    @Transactional // 얘가 우선권이라서 데이터 변경됨.
    public Long join(Member member) {
        validateDuplicateMember(member); //중복 회원 검출
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        //Exception
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }

}
