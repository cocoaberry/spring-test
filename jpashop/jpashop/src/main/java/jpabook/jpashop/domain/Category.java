package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item", //중간 테이블(CATEGORY_ITEM) 필요. name 은 테이블 이름.
            joinColumns = @JoinColumn(name = "category_id"), // 중간 테이블에 있는 카테고리 id 임.
            inverseJoinColumns = @JoinColumn(name = "item_id")) //중간 테이블에 있는 아이템 id 임.
    private List<Item> items = new ArrayList<>();

    //카테고리 구조 tree
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    //==연관관계 메서드==//
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }

}
// 객체는 컬랙션 컬랙션이 있어서 다대다가 가능한데 관계형 DB 는 컬랙션관계를 양쪽에 가질수있는게 아니라서 1:N, N: 1로 풀어내는 중간 테이블 필요.
// 실무에서는 못쓴다. 단지 각 테이블의 id만 필드로 갖고 더 필드를 추가하는게 불가능한데 실무에선 단순하게 매핑하고 끝나는 경우 없음.
// 중간에 등록한 날짜라도 넣어줘야 하고 이런거 때문에.
