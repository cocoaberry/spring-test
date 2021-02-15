package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) //spring 이랑 같이 엮어서 실행할래!
@SpringBootTest //spring boot 띄운상태에서 테스트 하겠다.
@Transactional //default rollback.
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    //@Rollback(false)
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("kim");
        //when
        Long savedId = memberService.join(member);
        //then
        //이렇게 하면 rollback(false)안해도 결과창에 insert 문 나옴.
        //em.flush(); 가 영속성 컨텍스트에 있는 변경/등록 내용을 DB에 반영하는 거라서. 하지만 반영하더라도 rollback 이니 뭐.
        em.flush();
        assertEquals(member, memberRepository.findOne(savedId));
        //이게 가능한 이유는 JPA 에서 같은 트랜잭션에서 Entity id 값(pk값)이 똑같으면
        //같은 persistence context 에서 똑같은 얘로 관리.
        //2,3개 생기지 않고 만든 1개로 계속 쓰니까 같지.
    }

    @Test(expected = IllegalStateException.class) //이 테스트 코드에서 IllegalStateException.class 일어나면 통과.
    public void 중복_회원_예외() throws Exception { //throws Exception : 예외가 생기면 밖으로 통과시켜라.
        //given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        //when
        memberService.join(member1);
        memberService.join(member2); //예외가 발생해야한다!


        fail("예외가 발생해야 한다."); //이 코드가 실행되면 예외 발생. 오면안되는 것. 이 코드 실행되는지 판별할때 사용.
    }
}