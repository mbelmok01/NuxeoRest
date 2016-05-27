package org.ulr.NuxeoRest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import biz.source_code.base64Coder.Base64Coder;

public class Section {

	private static String credentials = "xxx:xxx";
	private HttpURLConnection connection;
	private static String baseURL = "http://host:port/nuxeo/api/v1/path";

	private String entity_type;
	private String repository;
	private String uid;
	private static String path;
	private String type;
	private String state; 
	private String parentRef; 
	private Boolean isCheckedOut;
	private String changeToken;
	private String title;
	private String lastModified;
	private List<String> facets;
	private Boolean hasChildren;
	private List<Section> childrens;
	public ArrayList<String> listOfAllSections = new ArrayList<String>();

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

	public ArrayList<String> getListOfAllSections(){
		return this.listOfAllSections;
	}

	public void addToListOfAllSections(String pathToSection){
		this.listOfAllSections.add(pathToSection);
	}

	/*
	 * on propose un constructeur vide, ca peut servir
	 */
	public Section(){

	}
	
	/*
	 * deuxieme constructeur
	 */
	public Section(JSONObject obj) throws IOException{

		this.entity_type = (String) obj.get("entity-type");
		this.repository = (String) obj.get("repository");
		this.uid = (String) obj.get("uid");
		this.path = (String) obj.get("path");
		this.type = (String) obj.get("type");
		this.state = (String) obj.get("state"); 
		this.parentRef = (String) obj.get("parentRef"); 
		this.isCheckedOut = (Boolean) obj.get("isCheckedOut");
		this.changeToken = (String) obj.get("changeToken");
		this.title = (String) obj.get("title");
		this.lastModified = (String) obj.get("lastModified");
		this.childrens = setChildrens(obj);
	}

	public ArrayList<Section> setChildrens(JSONObject jsonObj) throws IOException{

		ArrayList<Section> sub_sections = null;

		if(jsonObj.getString("type").equalsIgnoreCase("Section")){
			JSONObject sections = getRestCall(jsonObj.getString("path"));;
			this.addToListOfAllSections(jsonObj.getString("path"));

			try{
				sub_sections = getChildrens(sections);

			}catch(Exception e){
//				System.out.println("L'objet : " + jsonObj.getString("uid") + " n'a pas de fils" );
			}
		}

		return sub_sections;
	}

	public static ArrayList<Section> getChildrens(JSONObject jsonObj) throws IOException{

		JSONObject contextParameters = (JSONObject) jsonObj.get("contextParameters");

		JSONObject childrens = (JSONObject) contextParameters.get("children");

		JSONArray entries = (JSONArray) childrens.get("entries");

		ArrayList<Section> sections = getListOfSectionObject(entries);
		return sections;
	}
	
	/*
	 * est-ce que l'objet courant possde des sous sections
	 */
	public Boolean hasChildren(){

		Boolean r;

		if(this.childrens.size()>0){
			r = true;
		} else{
			r = false;
		}
		return r;

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
	
	/*
	 * affiche quelques informations a propos de l'objet courant
	 */
	public String toString(){
		String str = "uid : " + this.getUid() + ", path : " + this.getPath() + ", type : " + this.getType();

		return str;
	}
	
	
	public static JSONObject getRestCall(String path) {

		// path = "http://docdev:8080/nuxeo/api/v1/path/default-domain/sections";

		String str ="";
		try {


			System.out.println("URL dans Section : " + baseURL+path);
			String encodedURL = baseURL+path.replace(" ", "%20");
			URL url = new URL (encodedURL);
			String encoding = Base64Coder.encodeString(credentials);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setRequestProperty  ("Authorization", "Basic " + encoding);

			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("X-NXenrichers.document", "children");
			InputStream content = (InputStream)connection.getInputStream();

			BufferedReader in = new BufferedReader (new InputStreamReader (content));

			String line;
			while ((line = in.readLine()) != null) {
				str += line;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		JSONObject jsonObj = new JSONObject(str);
		return jsonObj;
	}



	//	public Boolean hasChildren(){
	//
	//		Boolean child = false;
	//		try {
	//			String response = getRestCallOnSection();
	//			JSONObject jsonobject = new JSONObject(response);
	//
	//			jsonobject = (JSONObject) jsonobject.get("contextParameters");
	//			jsonobject = (JSONObject) jsonobject.get("children");
	//			JSONArray entries = (JSONArray) jsonobject.get("entries");
	//
	//			ArrayList<Section> childrens = getListOfSectionObject(entries);
	//
	//			for(Section s : childrens){
	//				if (s.getType().equalsIgnoreCase("Section")){
	//					child = true;
	//				}
	//			}
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//
	//		return child;
	//	}
	//
	//
	//
	//	public static JSONObject getChildrens(JSONObject jsonObj){
	//
	//		JSONObject contextParameters = (JSONObject) jsonObj.get("contextParameters");
	//
	//		JSONObject childrens = (JSONObject) contextParameters.get("children");
	//
	//		JSONArray entries = (JSONArray) childrens.get("entries");
	//		
	//	}

	
}
