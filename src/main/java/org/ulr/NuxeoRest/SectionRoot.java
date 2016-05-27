package org.ulr.NuxeoRest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class SectionRoot {
	
	private String entity_type;
	private String repository;
	private String uid;
	private String path;
	private String type;
	private String state; 
	private String parentRef; 
	private Boolean isCheckedOut;
	private String changeToken;
	private String title;
	private String lastModified;
	private List<String> facets;
	private List<List<String>> contextParameters;
	
	public SectionRoot(JSONObject jsonObject) throws IOException{
		
		JSONArray childrens = getChildrens(jsonObject);
		ArrayList<Section> liste = getListOfSectionObject(childrens);
	}
	
	public String getEntity_type() {
		return entity_type;
	}

	public String getRepository() {
		return repository;
	}

	public String getUid() {
		return uid;
	}

	public String getPath() {
		return path;
	}

	public String getType() {
		return type;
	}

	public String getState() {
		return state;
	}

	public String getParentRef() {
		return parentRef;
	}

	public Boolean getIsCheckedOut() {
		return isCheckedOut;
	}

	public String getChangeToken() {
		return changeToken;
	}

	public String getTitle() {
		return title;
	}

	public String getLastModified() {
		return lastModified;
	}

	public List<String> getFacets() {
		return facets;
	}

	public List<List<String>> getContextParameters() {
		return contextParameters;
	}

	public void setEntity_type(String entity_type) {
		this.entity_type = entity_type;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setParentRef(String parentRef) {
		this.parentRef = parentRef;
	}

	public void setIsCheckedOut(Boolean isCheckedOut) {
		this.isCheckedOut = isCheckedOut;
	}

	public void setChangeToken(String changeToken) {
		this.changeToken = changeToken;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public void setFacets(List<String> facets) {
		this.facets = facets;
	}

	public void setContextParameters(List<List<String>> contextParameters) {
		this.contextParameters = contextParameters;
	}
	
	
	
	
	/*
	 * transforme le morceau de json qui correspond aux childrens en objets Section, puis les mets dans une liste
	 */
	public static ArrayList<Section> getListOfSectionObject(JSONArray childrens) throws IOException{
		ArrayList<Section> liste = new ArrayList<Section>();
		
		for(int i = 0; i< childrens.length(); i++){
			JSONObject obj = (JSONObject) childrens.get(i);
			Section s = new Section(obj);
			liste.add(s);
		}
		return liste;
	}
	
	
	public static JSONArray getChildrens(JSONObject jsonObj){

		JSONObject contextParameters = (JSONObject) jsonObj.get("contextParameters");

		JSONObject childrens = (JSONObject) contextParameters.get("children");

		JSONArray entries = (JSONArray) childrens.get("entries");
		return entries;
	}
	
}