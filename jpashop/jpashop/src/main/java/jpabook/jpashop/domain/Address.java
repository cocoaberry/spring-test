package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable //내장타입. 어딘가에 내장될 수 있다.
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {} //기본생성자 만들어줌. JPA 에서 필요하다는듯. 손대지말자. p.29

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
// 값 타입은 기본적으로 변경되면 안됨. 생성할때만 값이 세팅하고 안바뀜.
// 만들려면 복사하거나 새로운객체 만들고 값 지정해야함.