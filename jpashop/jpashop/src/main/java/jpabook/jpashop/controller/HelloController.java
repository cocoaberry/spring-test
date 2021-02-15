package jpabook.jpashop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * Controller 에서 model 에 데이터를 담아 View 에 넘길 수 있다.
 * return 은 화면이름이다. resources/templates 에 있는 view 이름을 지정해주면 된다.(.html 때고)
 * 거기에 없으면 spring boot 가 static 에서 index.html 실행하는 것 같다.
 * attributeName 은 hello.html 에 있는 ${속성이름}의 속성이름과 동일해야 되고 거기에 attributeValue 를 대입한다.
 */
@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model){
        model.addAttribute("data", "hello!!!");
        return "hello";
    }
}
