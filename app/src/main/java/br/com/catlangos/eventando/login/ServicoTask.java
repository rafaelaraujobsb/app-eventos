package br.com.catlangos.eventando.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ServicoTask extends AsyncTask<Void, Void, String> {
    private Context httpContext;
    private View view;
    private ProgressDialog progressDialog;
    private String resultadoAPI;
    private String linkRequestAPI;
    private Integer idUsuario;
    private List<Interesses> interesses;

    public ServicoTask(Context ctx, String linkAPI, View view, Integer idUsuario, List<Interesses> interesses) {
        for(Interesses interesse : interesses) {
            System.out.println(interesse);
        }
        this.httpContext = ctx;
        this.linkRequestAPI = linkAPI;
        this.view = view;
        this.idUsuario = idUsuario;
        this.interesses = interesses;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(httpContext, "Processando a Solicitação", "Por favor aguarde");
    }

    @Override
    protected String doInBackground(Void... params) {
        String result= null;

        String wsURL = linkRequestAPI;
        URL url;
        try {
            url = new URL(wsURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            // Criar o objeto JSON para enviar por POST
            JSONObject parametrosPost = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            parametrosPost.put("id_usuario", idUsuario);
            for(int i = 0; i < this.interesses.size(); i++) {
                jsonArray.put(this.interesses.get(i).getName());
            }
            parametrosPost.put("interesses", jsonArray);

            // Definindo os parametros de conexão
            urlConnection.setReadTimeout(15000 /* milisegundos */);
            urlConnection.setConnectTimeout(15000 /* milisegundos */);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.writeBytes(parametrosPost.toString());
            wr.flush();
            wr.close();

            int responseCode=urlConnection.getResponseCode();// Código da resposta
            if(responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                StringBuffer sb = new StringBuffer("");
                String linea="";
                while ((linea = in.readLine())!= null){
                    sb.append(linea);
                    break;
                }
                in.close();
                result = sb.toString();
            }
            else{
                result = new String("Error: "+ responseCode);
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
        resultadoAPI = s;
        Snackbar.make(view, resultadoAPI, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
