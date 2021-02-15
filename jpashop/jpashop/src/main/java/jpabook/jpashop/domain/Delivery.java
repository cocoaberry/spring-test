package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    //Enum 타입 사용하려면 이 어노테이션 해야되고 ORDINAL(컬럼이 숫자로 1,2), STRING(READY, COMP) 타입을 넣을 수 있는데.
    //ORDINAL 의 경우 상태가 중간에 추가되면 숫자가 밀려서 망함. 절대 쓰지말기. STRING으로만 쓰기.
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; //READY, COMP
}

