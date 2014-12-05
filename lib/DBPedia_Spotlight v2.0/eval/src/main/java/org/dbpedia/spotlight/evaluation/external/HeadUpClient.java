/**
 * Copyright 2011 Pablo Mendes, Max Jakob, Joachim Daiber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.dbpedia.spotlight.evaluation.external;

import org.apache.commons.httpclient.methods.GetMethod;
import org.dbpedia.spotlight.exceptions.AnnotationException;
import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.Text;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * The External Clients were translated to Scala but this class was not.
 * Because the HeadUp (http://www.headup.com/) was discontinued. As result of that, this client is no more working.
 *
 * Last Tested: 08/27th/2013 by Alexandre Cançado Cardoso
 */

/**
 * @author jodaiber
 *
 * This is an AnnotationClient for HeadUpClient (http://www.headup.com/).
 *
 * The Query syntax consists of a number of piped commands,
 * entity extraction can be applied on plain text:
 *
 *  http://api.headup.com/v1?q=/str('France, which has over 1,500 ...')/x:entity
 *
 * or on websites:
 *
 *  http://api.headup.com/v1?q=/str('http://www.nytimes.com/2011/03/13/world/asia/13nuclear.html')/x:entity
 *
 * Features and ease of use:
 *
 * - currently no API key required
 * - the API returns a list of entities with their corresponding surface form
 *   in the text
 * - DBPedia entites are identified by the dbpedia: prefix
 * - Has a problem with if apostrophe ' is given within the string
 * 
 */

public class HeadUpClient extends AnnotationClient {

	final static String API_URL = "http://api.headup.com/v1?q=";
        //final static String API_URL = "http://spotlight.dbpedia.org/";

	@Override
	public List<DBpediaResource> extract(Text text) throws AnnotationException {
                
            System.out.println("\n***************************************Contador:  1\n");
		LinkedList<DBpediaResource> dbpediaResources = new LinkedList<DBpediaResource>();

		JSONArray requestData;
                System.out.println("\n***************************************Contador:  2\n");
		try {
                        System.out.println("\n***************************************Contador:  3\n");
			requestData = request(buildURL(text));
                        System.out.println("\n***************************************Contador:  4\n");
		} catch (JSONException e) {
			throw new AnnotationException("Could not read API response.",e);
		} catch (UnsupportedEncodingException e) {
			throw new AnnotationException("Could not build API request URL.",e);
		}

		if(requestData.length() == 0)
			return dbpediaResources;

		for(int i = 0; i < requestData.length(); i++) {
			try {
                                System.out.println("****LINK:   "+requestData.getJSONObject(i).getString("uri"));
				DBpediaResource dBpediaResource
						= new DBpediaResource(requestData.getJSONObject(i).getString("uri").replace("dbpedia:", ""));
				dbpediaResources.add(dBpediaResource);
			} catch (JSONException e) {
				LOG.error("Error parsing JSON.");
                e.printStackTrace();
			}
		}
		
		return dbpediaResources;
	}

	
	private String buildURL(Text text) throws UnsupportedEncodingException {

		/**
		 * The API does not support single quotation characters as such, therefore
		 * replace them with their corresponding HTML Special Entity.
		 */
		String cleanText = text.text().replace("'", "&apos;");
                System.out.println("API:  "+API_URL + "/str('" + URLEncoder.encode(cleanText, "utf-8") + "')/x:entity");
		return API_URL + "/str('" + URLEncoder.encode(cleanText, "utf-8") + "')/x:entity";
	}


	private JSONArray request(String url) throws JSONException, AnnotationException {
		GetMethod method = new GetMethod(url);
		String response = request(method);
        LOG.info("reponse: " + response);
		JSONArray json = new JSONArray(response);
		return json;
	}

    public static void main(String[] args) throws Exception {

        System.out.println("\n***************************************Inició\n"); 
        HeadUpClient client = new HeadUpClient();         

         File input = new File("F:/Pruebas/paragraphs.txt");
         File output = new File("F:/Pruebas/Spotlight.list");
         client.evaluate(input, output);
         System.out.println("\n****************************************Terminó\n");
     }

}