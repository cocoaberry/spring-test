package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping(value = "/order")
    public String createForm(Model model) {
        List<Member> members = memberService.findMembers(); //모든 맴버 끌고오고
        List<Item> items = itemService.findItems(); // 모든 아이템 끌고오고
        model.addAttribute("members", members);
        model.addAttribute("items", items);
        return "order/orderForm";//model 에 담아서 넘김
    }

    @PostMapping(value = "/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count) {
        orderService.order(memberId, itemId, count); //memberId가 itemId를 count 개 주문할거야. 여러개주문할수있게바꾸려면 여기고치면됨.
        return "redirect:/orders"; //만약에 주문된 결과 페이지로 갈려면 위코드를 Long orderId = 로 받고, "redirect:/orders" + orderId; 하면됨.
    }
    // 조회가 아닌 핵심비즈니스로직이 있으면 식별자만 넘기고 핵심비즈니스로직은 안에서 하면 영속컨텍스트가 존재하는 상태에서 조회 가능.
    // 여기선 안 = orderService.order, 영속컨텍스트 존재 = orderService.order 안의 Member member 와 Item item.
    // 바깥에서 가지고 온 것은 트랜잭션 없이 밖에서 조회한 것이라서 영속성상태가 끝나고 order 에 넘어가는 것임.
    // public Long order(Member member) 이런식이면 이 member 는 영속성컨텍스트 와 관계 없는 놈임.

    // controller 에서 member, item 찾아서 넘겨도 되지만, controller 에서 찾는것 보다 좋은게 entity 찾아서 넘기는게 아니라 식별자만 넘김(memberId, itemId, count).
    // orderService.order(memberId, itemId, count); 안에서 찾으면 할 수 있는게 더 많아진다. 그리고 여기 안에 있는 엔티티는 영속성임.
    /**
     * orderForm.html 의 <form role="form" action="/order" method="post"> 할 때임.
     * @RequestParam("memberId") 은  <select name="memberId" id="member" class="form-control"> 에서 보낸 것 받음.
     * 밑에 마찬가지 동일.
     */

    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {
        //Form 에서 넘어온 데이터들(상품검색조건들)이 orderSearch 에 담겨서 넘어 올거임.
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);

        return "order/orderList";
    }
    /**
     *  @ModelAttribute("orderSearch") 해놔서
     *  orderList.html 에서  <form th:object="${orderSearch}" class="form-inline"> 값이 model 에 자동으로 담김.
     *  그리고 담긴게 form 서비스하면 orderSearch 에 값이 들어옴.
     *  즉, model.addAttribute("orderSearch", orderSearch); 가 생략되어 있는 것.
     *     ㄴ orderSearch 의 memberName, orderStatus 에 값이 자동으로 들어가는 것.
     *     그럼 받은(요청하는)데이터로 findOrders 로 검색해서 해당하는 데이터 찾아서 model 에 담고 다시 orderList.html 에 보냄.
     */

    @PostMapping(value = "/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}