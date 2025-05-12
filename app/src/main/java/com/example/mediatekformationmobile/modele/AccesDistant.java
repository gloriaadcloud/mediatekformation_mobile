package com.example.mediatekformationmobile.modele;

import android.util.Log;

import com.example.mediatekformationmobile.controleur.Controle;
import com.example.mediatekformationmobile.outils.AccesREST;
import com.example.mediatekformationmobile.outils.AsyncResponse;
import com.example.mediatekformationmobile.outils.MesOutils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

public class AccesDistant implements AsyncResponse {

    private static final String SERVERADDR = "http://apimediatekformation.gloriaadonsou.com/";
    private static AccesDistant instance;
    private Controle controle;

    /**
     * constructeur oruvé
     */
    private AccesDistant(){
        controle = Controle.getInstance();
    }

    /**
     * Création d'une instance unique de la classe
     * @return instance unique de la classe
     */
    public static AccesDistant getInstance(){
        if(instance == null){
            instance = new AccesDistant();
        }
        return instance;
    }

    /**
     * retour du serveur distant
     * @param output
     */
    @Override
    public void processFinish(String output) {
        Log.d("serveur", "************" + output);
        try {
            JSONObject retour = new JSONObject(output);
            String code = retour.getString("code");
            String message = retour.getString("message");
            String result = retour.getString("result");
            if(!code.equals("200")){
                Log.d("erreur", "************ retour serveur code="+code+" result="+result);
            }else{
                try{
                    JSONArray resultJson = new JSONArray(result);
                    ArrayList<Formation> lesFormations = new ArrayList<Formation>();
                    for(int k=0;k<resultJson.length();k++) {
                        JSONObject info = new JSONObject(resultJson.get(k).toString());
                        int id = Integer.parseInt(info.getString("id"));
                        Date publishedAt = MesOutils.convertStringToDate(info.getString("published_at"),
                                "yyyy-MM-dd hh:mm:ss");
                        String title = info.getString("title");
                        String description = info.getString("description");
                        String videoId = info.getString("video_id");
                        Formation formation = new Formation(id, publishedAt, title, description, videoId);
                        lesFormations.add(formation);
                    }
                    controle.setLesFormations(lesFormations);
                }catch (JSONException ex){
                    ex.printStackTrace();

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * envoi de données vers le serveur distant
     * @param operation
     * @param table
     * @param lesDonneesJSON
     */
    public void envoi(String operation, String table, JSONObject lesDonneesJSON) {
        Log.d("Api", "envoi: opération=" + operation + ", table=" + table);
        AccesREST accesDonnees = new AccesREST();
        accesDonnees.delegate = this;
        String requesMethod = null;

        switch (operation) {
            case "tous":
                requesMethod = "GET";
                break;
            case "motcle":
                requesMethod = "GET";
                break;
            default:
        requesMethod = null;
        break;
        }

        if (requesMethod != null) {
            accesDonnees.setRequestMethod(requesMethod);

            StringBuilder urlBuilder = new StringBuilder(SERVERADDR);
            urlBuilder.append("index.php?table=").append(table);

            if (operation.equals("motcle") && lesDonneesJSON != null) {
                try {
                    // Format direct de l'URL pour correspondre à votre URL fonctionnelle
                    urlBuilder.append("&champs=").append(lesDonneesJSON.toString());
                } catch (Exception e) {
                    Log.e("AccesDistant", "Erreur de formatage JSON: " + e.getMessage());
                }
            }

            String url = urlBuilder.toString();
            Log.d("Api", "URL construite: " + url);

            // Exécution de la requête directement avec l'URL complète
            accesDonnees.execute(url);
        }
    }

}
