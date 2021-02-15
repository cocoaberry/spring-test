package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService { //ItemService 는 단순하게 ItemRepository 에 위임만 클래스이다. 굳이 필요할까?
                           //그냥 이 코드 없애고 controller 에서 바로 ItemRepository 로 접근하는건 어떨까?

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }


    //준영속성엔티티 - 변경감지기능사용
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) { //itemParam: 파리미터로 넘어온 준영속 상태의 엔티티
        Item findItem = itemRepository.findOne(itemId); // findOne 으로 찾아온 엔티티는 영속상태임. 값세팅하기만 하면됨.
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
        //사실 의미있는 addStock 이런걸 해야지 set 깔면 안됨. 나중에 추적할때 정말 힘듬. set 쓰지말자는거.
        //return findItem;
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
