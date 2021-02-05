package com.cloudhubs.logparser.controller;

import com.cloudhubs.logparser.model.ClassMethods;
import com.cloudhubs.logparser.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RequestMapping("/crawler")
@RestController
public class Crawler {

    @Autowired
    CrawlerService crawlerService;


    @GetMapping("/findAllJava")
    public List<ClassMethods> findAllJavaClass(@RequestParam String  directoryName) throws IOException {
        return crawlerService.listMethodCalls(new File(directoryName));
    }
}
