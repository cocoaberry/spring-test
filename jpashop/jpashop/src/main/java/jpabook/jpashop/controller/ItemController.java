package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping(value = "/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());//이렇게 했으니 html 에서 th:object="${form}" 로 받음.
        return "items/createItemForm";
    }

    @PostMapping(value = "/items/new")
    public String create(BookForm form) {
        //이건 지금 setter 다 열어놔서 이게 되는거고 실무에서는 안됨. 'static 생성자 메소드' 로 하라는 거 같음.
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        itemService.saveItem(book);
        return "redirect:/items";
    }

    /**
     * 상품 목록
     */
    @GetMapping(value = "/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("items/{itemId}/edit") //items/1/edit 에서 1은 변경될 수 있으니 path variable({}) 을 쓴것임.
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) { //path variable 여기서 넣음.
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();//form 을 업데이트하는데 BookForm 을 보낼거다. Book entity 보내는게 아니라. 왠만하면 Form 새로 만들어서 쓰자했지? entity 더러워지니.
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());
        model.addAttribute("form", form); //잘 만든 form 을 model 에 채워넣고 model 를 view 에 보냄 밑에서.
        return "items/updateItemForm";
    }

    //수정하고 다시 submit 눌럿을때
    @PostMapping("items/{itemId}/edit") //이때 itemId 노출되면 안됨. 실무에선 뭐 처리해야됨.
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form) { // updateItemForm.html 에서 ${form}의 form.
        //Form 을 받아와서 Book 으로 바꾼이유. Form 은 웹계층에서만쓰자고 잡음. 그래서 Form 을 Item Service 계층으로 넘기면 지저분해짐.
//        Book book = new Book();
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//        itemService.saveItem(book);
        //위 코드보다 밑코드가 좋음.
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
        return "redirect:/items";
    }
}
