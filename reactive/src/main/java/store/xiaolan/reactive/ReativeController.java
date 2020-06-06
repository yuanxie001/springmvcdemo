package store.xiaolan.reactive;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ReativeController {

    @GetMapping("/test")
    public Flux<String> getList(){
        Flux<String> just = Flux.just("zoom", "teams", "google meet", "webx", "message room");

        return just;
    }





}
