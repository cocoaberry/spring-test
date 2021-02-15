package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") //이거안해주면 관례로 Order 가지고 order 라고 지어버림. (테이블 이름 지어주는 어노테이션임)
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 이러면 다른데서 new Order 이렇게 못함. createOrder 로만할수있게 강제.
public class Order {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //Order 와 Member 는 다대일 관계여서.
    @JoinColumn(name = "member_id") //매핑을 뭐로 할거냐! 이렇게하면 member_id 가 FK가 된다.
    private Member member; //member 가 연관관계의 주인이므로 member 가 변경되면 FK인 member_id 은 다른 member 값으로 변경된다!

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) //OrderItem 의 order 에 매핑된다.
    private List<OrderItem> orderItems = new ArrayList<>();// 이렇게 컬랙션은 필드에서 바로 초기화 하는게 안전함.
    //cascade 를 하면, Order 를 저장하면 orderItems 도 저장된다. persist 를 전파한다고 보면된다. ALL 해서 지울때도 한번에 라고 하는데.
/*  persist(orderItemA)
    persist(orderItemB)  여야 하는데 cascade 하면 persist(order)만 하면 된다.
    persist(orderItemC)
    persist(order)*/

    //즉 cascade 해서 delivery 객체 세팅하면 order 저장할 때 delivery 도 같이 persist 해준다는 뜻.
    //모든 entity 는 저장하고 싶으면 persist 각자해줘야 하지만 한번에 해주는 방법임.
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; //주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]

    //==연관관계 메서드==//
    //양방향이라서 양쪽에 다 값 세팅해줘야 함.
    public void setMember(Member member) {
        this.member = member; // Order 의 private Member member 와
        member.getOrders().add(this); // Member 의 private List<Order> orders 를 전부 세팅해주는 것.
        //Order 에 주문자가 정해진 것이고, orders 는 주문자인 맴버의 주문리스트에 주문이 추가 된 것이고.
    }
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem); //orderItems 에 값 넣어주고
        orderItem.setOrder(this); //넘어온 orderItem 에도 this 넣어주고.
    }
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==/
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        //createOrderItem 을 여기서 해도 됨. 따로 안빼고.
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem i: orderItems) {
            order.addOrderItem(i);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    //주문 취소
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem: orderItems) { //this.orderItems 와 같다. 어차피 보라색인거보면 클래스변수인거 아니까. this 안붙여도 알지.
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    //전체 주문 가격 조회
    public int getTotalPrice() {    //얘는 Order 의 getTotalPrice 이고

        int totalPrice = 0;//책1 3권, 책2 5권 총 합
        for(OrderItem orderItem: orderItems) {//각 책들 가격합을 각각 구함.
            totalPrice += orderItem.getToTalPrice(); //얘는 OrderItem 의 getTotalPrice 임. 둘이 다른거야.
        }
        return totalPrice;

        //위코드 for 에서 alt+enter 하고 sum 어쩌고 클릭하면 아래코드로 바꿔줌.
        //OrderItem::getToTalPrice 는 OrderItem 의 getTotalPrice 임. 둘이 다른거야.
        /*
        int totalPrice = orderItems.stream()
                            .mapToInt(OrderItem::getToTalPrice)
                            .sum();
        return totalPrice;
        */
    }












}
//연관관계의 주인을 정하자!!
//Order 의 회원을 바꿀 때 Order class 의 member 를 바꾸냐, Member class 의 orders 를 바꾸냐?
//JPA 는 뭘 보고 확인을 해야 하냐? 서로 참조할 수 있는 변수가 있어서 양방향 참조이다! 근데 FK는 한개란 말이다 ㅠㅠ.
//Member - Order 관계를 바꾸고 싶으면 FK를 변경해야 하는데
//Order class 의 member, Member class 의 orders 중 어디 값이 변경되었을 때 FK 값을 바꿔야 하지?
//Member 의 order list 에는 값을 넣었는데 Order 의 member 에는 값을 안넣었다던가. 그 반대도.
//JPA 는 뭘 믿고 값을 넣어줘야 하냐! 도대체 누가(연관관계의 주인)! "저 변경되었으니 저쪽도 변경해주세요 FK님" 라고 말할것인가!
//연관관계의 주인은 FK가 가까운 곳으로 하면 된다.
//Order 에 FK가 있으니 Order 테이블이 더 가까움.
//Order 를 주인으로 잡았으면 이제 Order 가 바뀌면 다른 테이블도 바뀜. 반대는 x.
//주인인 Order 의 private Member member;는 그대로, 주인아닌얘는 Member 의 private List<Order> orders = new ArrayList<>();에는 (mappedBy = "member")를 붙임.


// Order - Delivery 일대일 관계라서 FK를 어디둬든됨. 주로 조회를 많이하는 테이블에 둔다. 그래서 Orders 테이블에 delivery_id(FK)를 둠.
// 그래서 Order 테이블의 delivery 필드랑 Delivery 테이블의 order 필드 중에 FK와 가까운 Order 테이블에 있는 Delivery 를 연관관계의 주인으로 잡는다.


