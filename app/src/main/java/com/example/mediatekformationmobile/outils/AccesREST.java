package com.example.mediatekformationmobile.outils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.mediatekformationmobile.vue.FormationsActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Classe utilitaire pour faire des requêtes HTTP de manière asynchrone.
 */
public class AccesREST extends AsyncTask<String, Integer, String> {

    private static final String UTF_8 = "UTF-8";
    private static final String TAG = "AccesREST";
    private static final String HTTP_KEEP_ALIVE = "http.keepAlive";
    private static final String HTTP_KEEP_ALIVE_VALUE = "false";
    private static final String TABLE_PARAM = "?table=";

    private String ret = null;
    public AsyncResponse delegate = null;
    private String parametres = "";
    private String requestMethod = "GET";

    public AccesREST() {
        Log.d(TAG, "AccesREST instance created");
    }

    public void setParametres(String valeur) {
        try {
            if (valeur == null) {
                return;
            }

            // On utilise le format ?table=valeur
            if (parametres.equals("")) {
                // premier paramètre
                parametres = TABLE_PARAM + URLEncoder.encode(valeur, UTF_8);
            } else {
                // Si on a déjà des paramètres, on ajoute avec &
                parametres += "&" + URLEncoder.encode(valeur, UTF_8);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setRequestMethod(String method) {
        this.requestMethod = method;
    }

    @Override
    protected String doInBackground(String... urls) {
        System.setProperty(HTTP_KEEP_ALIVE, HTTP_KEEP_ALIVE_VALUE);
        BufferedReader reader = null;
        HttpURLConnection connexion = null;

        try {
            if (urls.length == 0 || urls[0] == null) {
                throw new IllegalArgumentException("URL manquante dans AccesREST");
            }
            // Utiliser directement l'URL complète sans ajouter de paramètres
            String urlComplete = urls[0];
            URL url = new URL(urlComplete);
            connexion = (HttpURLConnection) url.openConnection();
            connexion.setRequestMethod(requestMethod);
            connexion.setConnectTimeout(5000); // 5 secondes
            connexion.setReadTimeout(5000);

            InputStream stream = (connexion.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST)
                    ? connexion.getInputStream()
                    : connexion.getErrorStream();

            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            ret = sb.toString();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                if (connexion != null) connexion.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    @Override
    protected void onPostExecute(String result) {
        if (delegate != null) {
            delegate.processFinish(result);
        }

    }
}
