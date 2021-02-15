package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//Entity 는 최대한 순수하게 유지해야됨!! 오직 핵심 비즈니스 로직에만 dependency 가 있도록!
@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue // @Id - pk 로 만듬, @GeneratedValue - 항상 값 보장
    @Column(name = "member_id") //이거 안해주면 밑에 이름 그대로 id 라고 되어버림.
    private Long id;

    private String name;

    @Embedded //아래변수가 내장타입(Address.java)을 포함한다라고 어노테이션으로 알려줌. 사실 @Embeddable 이랑 @Embedded 아무거나 한개만 하면 되김함.
    private Address address;

    //Member 와 Order 는 일대다 관계여서. 나는 Order 의 member 필드에 의해 매핑된거야!
    //나는 매핑하는얘가 아니라 member 에 의해 매핑된 거울일 뿐이야. 읽기전용이야! orders 에 값넣는다고 FK 값은 변경안돼.
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
