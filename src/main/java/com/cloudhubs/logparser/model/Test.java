package com.cloudhubs.logparser.model;

import org.springframework.data.annotation.Id;

import javax.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(name = "Test.findByCaseId", query = "SELECT t FROM Test t where  t.caseId =: caseId")
})
public class Test {

    @javax.persistence.Id
    @Id
    @GeneratedValue
    Long id;

    @Column
    private String caseId;

    @Column
    private String eventId;

    @Column
    private String activity;

    @Column
    private String other;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    @Override
    public String toString() {
        return "\t\t<event>\n" +
                "\t\t\t<string key=\"concept:name\" value=\""+activity+"\"/>\n" +
                //"\t\t\t<string key=\"org:resource\" value=\""+other+"\"/>\n" +
                "\t\t\t<string key=\"Activity\" value=\""+activity+"\"/>\n" +
                //"\t\t\t<string key=\"Others\" value=\""+other+"\"/>\n" +
                "\t\t</event>\n";
    }
}
