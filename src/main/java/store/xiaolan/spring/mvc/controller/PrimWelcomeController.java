package store.xiaolan.spring.mvc.controller;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

@Controller
@Primary
public class PrimWelcomeController extends WelComeController {
    @Override
    public Object index(HttpServletRequest request){
        return "I am new one";
    }
}
