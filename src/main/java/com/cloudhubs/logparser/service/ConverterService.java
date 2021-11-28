package com.cloudhubs.logparser.service;

import com.cloudhubs.logparser.model.LogModelDAO;
import com.cloudhubs.logparser.repository.ModelRepository;
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
    ModelRepository testRepository;

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
                LogModelDAO testModel = new LogModelDAO();
                testModel.setCaseId(itr.nextToken());
                caseIdSet.add(testModel.getCaseId());
                testModel.setEventId(itr.nextToken());
                testModel.setActivity(itr.nextToken());
                //testModel.setOther(itr.nextToken());
                testRepository.save(testModel);

            }
            createXes();
            createDot();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<LogModelDAO> getALL() {
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

                Query query = em.createNamedQuery("LogModelDAO.findByCaseId").setParameter("caseId", s);
                List<LogModelDAO> testList = query.getResultList();
                for (LogModelDAO t : testList) {
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

    public  void createDot(){
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter("BusinessModel.dot");
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder graph = new StringBuilder();
        graph.append("strict digraph G {").append("\n");



        for (String s : caseIdSet) {
                Query query = em.createNamedQuery("LogModelDAO.findByCaseId").setParameter("caseId", s);
                List<LogModelDAO> testList = query.getResultList();
                graph.append("Start");
                for (LogModelDAO t : testList) {
                    graph.append(" -> "+t.getActivity().replace(" ","_").replace("'","_"));
                }
                graph.append(";\n");
        }
        graph.append("}\n");
        try {
            //System.out.println(graph.toString());
            myWriter.write(graph.toString());
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}