package us.zoom.spring.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.Set;

/**
 * 用来测试MatrixVariable注解的值
 * 需要在<mvc:annotation-driven enable-matrix-variables="true"/> 将enable-matrix-variables设置为true
 * 方法主要关注的是spring mvc的参数解析处理机制。
 */
@Controller
public class MatrixVariableController {
    /**
     * 测试获取单个值的场景
     * http://localhost/matrix/zhangsan;age=12
     * @param user
     * @param age
     * @return
     */
    @RequestMapping("/matrix/{user}")
    @ResponseBody
    public Object singleMatrix(@PathVariable String user, @MatrixVariable(name = "age") int age) {
        return "user:" + user + "<br> age:" + age;
    }

    /**
     * 测试拥有多个key的场景
     *http://localhost/info/123456;name=zhangsan;gender=male
     * @param id
     * @param name
     * @param gender
     * @return
     */
    @RequestMapping("/info/{id}")
    @ResponseBody
    public Object twiceMatrix(@PathVariable String id,@MatrixVariable(name = "name") String name,@MatrixVariable String gender){
        return "id:" + id + "<br> name:" + name+"<br>gender:"+gender;
    }

    /**
     * 直接用map获取所有种类的场景
     * http://localhost/infos/123456;name=zhangsan;gender=male;age=12
     * @param id
     * @param map
     * @return
     */
    @RequestMapping("/infos/{id}")
    @ResponseBody
    public Object twiceMapMatrix(@PathVariable String id,@MatrixVariable(pathVar = "id") Map map){
        StringBuilder sb = new StringBuilder("id:").append(id);
        map.forEach((key,value)->{
            sb.append("<br>").append(key).append(":").append(value);
        });
        return sb.toString();
    }

    /**
     * 获取一个类型有多个值的情况
     * http://localhost/car/bmw;colors=black,red,white
     * http://localhost/car/bmw;colors=black;colors=red;colors=white
     * @param car
     * @param colors
     * @return
     */
    @RequestMapping("/car/{car}")
    @ResponseBody
    public Object mutrixValueMatrix(@PathVariable String car,@MatrixVariable Set<String> colors){

        return car+" colors: "+colors;
    }
}
