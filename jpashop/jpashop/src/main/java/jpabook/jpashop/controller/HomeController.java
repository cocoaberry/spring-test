package jpabook.jpashop.controller;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@Slf4j //lombok, 아래 Logger log 안하고 log.info 하게 해주는 것.
public class HomeController {

    //Logger log = LoggerFactory.getLogger(getClass());
    @RequestMapping("/")
    public String home() {
        log.info("home controller");
        //콘솔결과창에 "2021-02-14 17:32:52.151  INFO 27160 --- [nio-8080-exec-1] j.jpashop.controller.HomeController      : home controller" 찍힘
        return "home";
    }
}