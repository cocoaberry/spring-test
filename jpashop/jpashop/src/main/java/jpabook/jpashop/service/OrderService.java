package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        //엔티티 조회 - 조회해야되서 위에 의존관계 넣은 것임.
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        //OrderItem o1 = new OrderItem(); o1.setCount(); 이렇게 쓰면 위와 같은데 나중에 헷갈리니 이거막아야됨.
        //OrderItem 에서 생성자 protected 로 해놓으면됨.

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        //delivery, orderItem 도 deliveryRepository.save 해서 JPA 넣어주고
        //createOrder 에 새팅해야 한다. 근데 아래 save 한번만 해도 됬네?
        //Order.java 에서 orderItem, delivery 에 CascadeType.ALL 을 했기 때문.
        //Order 를 persist 하면 orderItem, delivery 모두 persist 날려줌. 그래서 자동으로 된거임.
        orderRepository.save(order);
        return order.getId();
    }

    //취소
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
        //원래 로직하고(위호출하면 status, stockQuantity 변경함) 밖에서 쿼리날려서 DB에 바꿔야됨.
        // ***JPA 에선 entity 의 데이터만 바꿔주면 JPA 가 알아서 바뀐 변경포인트를 감지해서
        // DB에 업데이트 쿼리 날려줌. ㄷㄷ...
    }

    //검색
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
        //orderList(controller 에있음) -> findOrders(service 에 있음) -> findAllByString(repository 에 있음)
        //findOrders 는 바로 리턴해서 단순히 해야할 걸 findAllByString 에 위임만 하니
        //orderList 에서 바로 findAllByString 로 즉, controller 에서 바로 repository 를 불러도 괜찮다.
    }
}
