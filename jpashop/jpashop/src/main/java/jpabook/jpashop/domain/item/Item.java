package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item { //구현체 Album, Book, Movie 만들거니 추상클래스로!

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==//
    /**
     *  Item entity 가 해결할 수 있는 것은 entity 안에 비즈니스 로직을 넣는게 좋다.
     *  stockQuantity 이 Item entity 에 있으니. (data(stockQuantity) 를 가지고 있는 쪽에 비즈니스 로직 있는게 좋다.)
     *
     *  원래는 setter 말고 addStock, removeStock 등을 통해서 조절해야 한다.
     */

    //stock 증가
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    //stock 감소
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }

}
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)는
//상속관계 매핑이므로 상속관련전략을 부모 클래스에서 잡아줘야 함. 우리는 싱글테이블블략.
//싱글테이블 전략이므로 DB 입장에서 구분하기 위해 Book, Album, Movie 에서 DiscriminatorValue 사용.
//구분을 위해 필드중 하나인 dtype 을 사용하겠다.