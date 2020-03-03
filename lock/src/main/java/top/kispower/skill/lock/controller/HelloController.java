package top.kispower.skill.lock.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haisenbao
 * @date 2020/2/26 23:20
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping("/{somebody}")
    public String sayHelloToSomebody(@PathVariable("somebody") String somebody) {
        return String.format("Hello! %s ~", somebody);
    }

}
