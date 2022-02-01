package com.cloudhubs.logparser.controller;

import com.cloudhubs.logparser.model.ClassMethods;
import com.cloudhubs.logparser.model.MethodModel;
import com.cloudhubs.logparser.service.LogFinderService;
import com.cloudhubs.logparser.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequestMapping("/logparser")
@RestController
public class LogController {

    @Autowired
    LogFinderService logFinderService;

    @Autowired
    TestService testService;


    @GetMapping("/findAll")
    public List<MethodModel> findAllJavaClass(@RequestParam String  directoryName) throws IOException {
        logFinderService.check(new File(directoryName));
        return null;
    }

    @GetMapping("/findLogVariable")
    public Map<String,MethodModel> findLogVariable(@RequestParam String  directoryName) throws IOException {
        //testService.test(null);
       //return testService.check(new File(directoryName));
        System.out.println(directoryName);
        return logFinderService.findAllClass(new File(directoryName));
        //crawlerService.listMethodCalls(new File(directoryName));
    }
}
