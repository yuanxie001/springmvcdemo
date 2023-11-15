package store.xiaolan.spring.mvc.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Controller;



public class PrimWelcomeController extends WelComeController {
    @Override
    public Object index(HttpServletRequest request){
        return "I am new one";
    }
}
