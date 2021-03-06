package com.mq.rabbitmqdemo.controller;

import com.mq.rabbitmqdemo.basicDemo.helloWorld.Send;
import com.mq.rabbitmqdemo.basicDemo.publishSubscriber.EmitLog;
import com.mq.rabbitmqdemo.basicDemo.workQueues.NewTask;
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
    @Autowired
    private EmitLog emitLog;

    @RequestMapping("/send")
    public void Send(@RequestParam("message")String message){
        send.sendMessage(message);
    }

    @RequestMapping("/newTask")
    public void newTask(@RequestParam("message")String message){
        newTask.NewTask(message);
    }

    @RequestMapping("/emitLog")
    public void emitLog(@RequestParam("message")String message){
        emitLog.emitLog(message);
    }
}
