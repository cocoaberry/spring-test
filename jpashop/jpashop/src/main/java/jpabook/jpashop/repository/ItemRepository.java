package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        //Item 은 JPA 에 저장하기 전까지 id 가 없음. (처음에 저장할 때 생김)
        //id 값이 없다. = 새로운 호출이다. = 신규등록상품
        //id 값이 있다. = 이미 DB에 등록된 것을 어디서 가져온 것. = 강제 업데이트
        if (item.getId() == null) {
            em.persist(item);
        } else {
            em.merge(item);//실무에선 안씀. 준영속엔티티 - merge 사용. public void updateItem()이랑 같음.
            //영속성엔티티에서 item 찾아서(없으면 DB 에서 가져와서 새로만듬.) public void updateItem() 처럼 모든 데이터를 바꿔치기 함. 그걸 반환해줌.
            //즉 item 은 영속성엔티티 아님. em.merge(item); 의 반환 값은 영속성엔티티임.
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
