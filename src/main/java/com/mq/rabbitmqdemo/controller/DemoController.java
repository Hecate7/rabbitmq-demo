package com.mq.rabbitmqdemo.controller;

import com.mq.rabbitmqdemo.helloWorld.Send;
import com.mq.rabbitmqdemo.workQueues.NewTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @Autowired
    private Send send;
    @Autowired
    private NewTask newTask;

    @RequestMapping("/send")
    public void Send(@RequestParam("message")String message){
        send.sendMessage(message);
    }

    @RequestMapping("/newTask")
    public void newTask(@RequestParam("message")String message){
        newTask.NewTask(message);
    }
}
