/**
 * Copyright 2011 Pablo Mendes, Max Jakob
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

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dbpedia.spotlight.exceptions.AnnotationException;
import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.Text;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple web service-based annotation client for DBpedia Spotlight.
 *
 * @author pablomendes, Joachim Daiber
 */

public class DBpediaSpotlightClient extends AnnotationClient {

	//private final static String API_URL = "http://jodaiber.dyndns.org:2222/";
        private final static String API_URL = "http://spotlight.dbpedia.org/";
//        private final static String API_URL = "http://spotlight.sztaki.hu:2231/";
	private static final double CONFIDENCE = 0.0;
	private static final int SUPPORT = 0;

	@Override
	public List<DBpediaResource> extract(Text text) throws AnnotationException {

        LOG.info("Querying API.");
		String spotlightResponse;
		try {                        
			GetMethod getMethod = new GetMethod(API_URL + "rest/annotate/?" +
					"confidence=" + CONFIDENCE
					+ "&support=" + SUPPORT
					+ "&text=" + URLEncoder.encode(text.text(), "utf-8"));
			getMethod.addRequestHeader(new Header("Accept", "application/json"));

			spotlightResponse = request(getMethod);                        
		} catch (UnsupportedEncodingException e) {
			throw new AnnotationException("Could not encode text.", e);
		}

		assert spotlightResponse != null;

		JSONObject resultJSON = null;
		JSONArray entities = null;

		try {
                    //Imprimir resuesta del servicio web
//                        System.out.println("\nSPOTLIGHT RESPONSE: \n"+spotlightResponse);
                        resultJSON = new JSONObject(spotlightResponse);                        
			entities = resultJSON.getJSONArray("Resources");                        
		} catch (JSONException e) {
                        System.out.println("No_se_hallaron_conceptos");
			throw new AnnotationException("Received invalid response from DBpedia Spotlight API.");                        
		}

		LinkedList<DBpediaResource> resources = new LinkedList<DBpediaResource>();
                System.out.println("Cantidad_Conceptos");
                System.out.println(entities.length());
		for(int i = 0; i < entities.length(); i++) {
			try {
				JSONObject entity = entities.getJSONObject(i);
                                System.out.println(entity.getString("@surfaceForm"));
                                System.out.println(entity.getString("@URI"));                                
				resources.add(
						new DBpediaResource(entity.getString("@URI"),
								Integer.parseInt(entity.getString("@support"))));

			} catch (JSONException e) {
                            LOG.error("JSON exception "+e);
                        }

		}
                
		return resources;
	}
        
        public static void main(String[] args) throws Exception {

            DBpediaSpotlightClient c = new DBpediaSpotlightClient ();      
            
            try{
                File archivoEntrada = new File("Archivos_Texto_Conceptos/Texto.txt");
                File archivoSalida = new File("Archivos_Texto_Conceptos/Conceptos_Encontrados.list");        
                c.evaluate(archivoEntrada, archivoSalida);
                System.out.println("Exitosamente Finalizado");
            }catch(Exception e){
                System.out.println("Error en la ruta de los archivos. "+e.getMessage());
            }               
        }                
}
