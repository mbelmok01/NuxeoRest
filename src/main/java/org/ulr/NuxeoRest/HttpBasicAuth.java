package org.ulr.NuxeoRest;

import java.awt.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import biz.source_code.base64Coder.Base64Coder;


public class HttpBasicAuth {

	private static String credentials = "xxx:xxx";
	private HttpURLConnection connection;
	private static String baseURL = "http://host:port/nuxeo/api/v1/";
	private static ArrayList<Section> listOfAllSections = new ArrayList<Section>();
	
	
	public static void main(String[] args) throws IOException {

		explorer("path/default-domain/sections");
		Section s = new Section();
		System.out.println(s.getListOfAllSections());
	}
	
	
	/*
	 * fonction qui fait un parcours recursif pour recuperer la liste des sections
	 */
	public static void explorer(String path) throws IOException{
		
		JSONObject sectionsJsonObj = getRestCall(path);
		
		ArrayList<Section> sub_sections = getChildrens(sectionsJsonObj);
		for(Section s : sub_sections){
			
			if(s.getType().equalsIgnoreCase("Section") && s.getState().equalsIgnoreCase("project")){
				listOfAllSections.add(s);
				if(s.hasChildren()){
					explorer(s.getPath());
				}
			}
		}	
	}
	
	
	/*
	 * fonction qui extrait le morceau de json qui correspond aux childrens
	 */
	public static ArrayList<Section> getChildrens(JSONObject jsonObj) throws IOException{
		
		JSONObject contextParameters = (JSONObject) jsonObj.get("contextParameters");
		JSONObject childrens = (JSONObject) contextParameters.get("children");
		JSONArray entries = (JSONArray) childrens.get("entries");
		ArrayList<Section> sections = getListOfSectionObject(entries);
		return sections;
	}

	
	/*
	 * transforme le morceau de json qui correspond aux childrens en objets Section, puis les mets dans une liste
	 */
	public static ArrayList<Section> getListOfSectionObject(JSONArray childrens) throws IOException{
		
		ArrayList<Section> liste = new ArrayList<Section>();
		Integer i = 0;
		while(i < childrens.length()){
			
			JSONObject obj = (JSONObject) childrens.get(i);
			if(obj.getString("type").equalsIgnoreCase("Section")){
				Section s = new Section(obj);
				liste.add(s);
			}
			i = i+1;
		}
		return liste;
	}
	
	
	/*
	 * permet de faire un appel rest sur l'api de nuxeo.
	 * retourne le resultat sous forme d'un objet json
	 */
	public static JSONObject getRestCall(String path) throws IOException{
		
		String str ="";
		try {
			System.out.println("URL dans httpBasicAuth : " + baseURL+path);
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
}

