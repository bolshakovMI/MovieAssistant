package com.example.movieAssistant.kafka;


import com.example.movieAssistant.model.dto.event.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class ProducerController {

    @Autowired
    private KafkaTemplate<String, User> kafkaTemplate;

    @PostMapping("msg/{msgId}")
    public void sendOrder(@PathVariable String msgId, @RequestBody User user){
        kafkaTemplate.send("topic-01", msgId, user);
    }

}
