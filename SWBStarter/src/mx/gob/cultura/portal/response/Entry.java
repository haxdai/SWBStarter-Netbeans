/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.gob.cultura.portal.response;

import java.util.List;
import java.io.Serializable;
/**
 *
 * @author sergio.tellez
 */
public class Entry implements Serializable {

    private static final long serialVersionUID = 7680915584896844702L;

    private String _id;
    private List<Title> recordtitle;
    private String holder;
    private String catchall;
    private String description;
    
    private List<String> type;
    private List<Rights> rights;
    private List<String> creator;
    private Period periodcreated;
    private List<Identifier> identifier;
    private List<DigitalObject> digitalobject;
    private DateDocument datecreated;
    
    private String technique;
    private String collection;
    private String institution;
    
    public Entry() {
        init();
    }

    public String getTechnique() {
        return technique;
    }

    public String getInstitution() {
        return institution;
    }

    public String getCollection() {
        return collection;
    }

    public void setTechnique(String technique) {
        this.technique = technique;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public List<Rights> getRights() {
        return rights;
    }

    public void setRights(List<Rights> rights) {
        this.rights = rights;
    }

    public List<String> getCreator() {
        return creator;
    }

    public void setCreator(List<String> creator) {
        this.creator = creator;
    }

    public Period getPeriodcreated() {
        return periodcreated;
    }

    public void setPeriodcreated(Period periodcreated) {
        this.periodcreated = periodcreated;
    }

    public DateDocument getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(DateDocument datecreated) {
        this.datecreated = datecreated;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getCatchall() {
        return catchall;
    }

    public void setCatchall(String catchall) {
        this.catchall = catchall;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public List<Title> getRecordtitle() {
        return recordtitle;
    }

    public void setRecordtitle(List<Title> recordtitle) {
        this.recordtitle = recordtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DigitalObject> getDigitalobject() {
        return digitalobject;
    }

    public void setDigitalobject(List<DigitalObject> digitalobject) {
        this.digitalobject = digitalobject;
    }
    
    private void init() {
        DateDocument date = new DateDocument();
        date.setValue("");
        periodcreated = new Period();
        periodcreated.setDateend(date);
        periodcreated.setDatestart(date);
    }

    @Override
    public String toString() {
        return "Entry{" + "datecreated=" + datecreated + '}';
    }
}
