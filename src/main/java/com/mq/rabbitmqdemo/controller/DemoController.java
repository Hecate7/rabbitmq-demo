package com.mq.rabbitmqdemo.controller;

import com.mq.rabbitmqdemo.helloWorld.Send;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @Autowired
    private Send send;

    @RequestMapping("/send")
    public void Send(@RequestParam("message")String message){
        send.sendMessage(message);
    }
}
