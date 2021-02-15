package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

   // 1
   // @PersistenceContext //spring 이 Entity Manager 를 만들어서 em 에 injection 한다. (꺼내고 쓸필요없음)
   // private EntityManager em;

   // 2, spring 데이터 JPA 가 @PersistenceContext -> @Autowired 할 수 있게 해주고 그럼? lombok 으로 @RequiredArgsConstructor 사용가능.
   // EntityManager 는  @Autowired 로 안되고 @PersistenceContext 있어야 injection 되는데 spring 데이터 JPA 가 @Autowired 되게 해줌.
    private final EntityManager em;

    public void save(Member member) {
        //영속성 컨텍스트(persistence context)에 member entity 를 넣고 transaction 이 commit 되는 시점에 DB 에 반영됨(쿼리날라감).
        //persist 해서 영속성 컨텍스트에 들어갈 때 key, value 형태로 들어가는데 key 는 Member 의 pk인 private Long id;에 값을 넣으면서 그 값이 들어감.
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id); //단건조회, (타입, pk)
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class) // (JPQL, 반환타입)
                .getResultList();
    }
    // SQL 은 table 을 대상으로 query 를 한다면, JPQL 은 객체(Entity, 위에서 Member as m)를 대해서 함.

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class) // (JPQL, 반환타입)
                .setParameter("name", name)
                .getResultList();
    }
}

















