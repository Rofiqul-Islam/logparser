package com.cloudhubs.logparser.service;

import com.cloudhubs.logparser.model.Test;
import com.cloudhubs.logparser.repository.TestRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ConverterService {
    @Autowired
    TestRepository testRepository;

    @PersistenceContext
    private EntityManager em;

    private Set<String> caseIdSet = new HashSet<>();

    public String csvToXes(String filePath) {
        Reader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(filePath));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            for (CSVRecord r : csvParser) {
                // Accessing Values by Column Index
                StringTokenizer itr = new StringTokenizer(r.get(0), ";");
                Test testModel = new Test();
                testModel.setCaseId(itr.nextToken());
                caseIdSet.add(testModel.getCaseId());
                testModel.setEventId(itr.nextToken());
                testModel.setActivity(itr.nextToken());
                //testModel.setOther(itr.nextToken());
                testRepository.save(testModel);

            }
            createXes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Test> getALL() {
        return testRepository.findAll();
    }

    public void createXes() {
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter("output.xes");
            myWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                    "<!-- XES version 1.0 -->\n" +
                    "<log xes.version=\"1.0\" xmlns=\"http://code.deckfour.org/xes\" xes.creator=\"Fluxicon Nitro\">\n" +
                    "\t<extension name=\"Concept\" prefix=\"concept\" uri=\"http://code.deckfour.org/xes/concept.xesext\"/>\n" +
                    "\t<extension name=\"Time\" prefix=\"time\" uri=\"http://code.deckfour.org/xes/time.xesext\"/>\n" +
                    "\t<extension name=\"Organizational\" prefix=\"org\" uri=\"http://code.deckfour.org/xes/org.xesext\"/>\n" +
                    "\t\n" +
                    "\t<classifier name=\"Activity\" keys=\"Activity\"/>\n" +
                    "\t<classifier name=\"activity classifier\" keys=\"Activity\"/>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (String s : caseIdSet) {
            try {
                myWriter.write("\t<trace>\n" +
                        "\t\t<string key=\"concept:name\" value=\"" + s + "\"/>\n");

                Query query = em.createNamedQuery("Test.findByCaseId").setParameter("caseId", s);
                List<Test> testList = query.getResultList();
                for (Test t : testList) {
                    myWriter.write(t.toString());
                }
                myWriter.write("\n\t</trace>\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            myWriter.write("</log>");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


}