package com.politecnicomalaga.tmdbclient.data;

import java.io.IOException;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Clase DataMovieAccess
 *
 * Es utilizado por la clase RequesClient. El RequestClient le proporciona
 * los datos necesarios
 *
 * Se apoyará en OkHttp (librería cliente http/https)
 * Solicita una lista JSON de películas o series, y se la pasa al client para que parsee el json
 * Su única responsabilidad es obtener una petición desde client, darle forma de petición HTTP, lanzarla,
 * gestionar las dos posibiliades (se obtuvieron datos o falló) y comunicar al client que ya tiene
 * los resultados.
 */


public class DataMovieAccess {

    //ESTADO
    //Clase utilidad que no necesita nada más que poner a funcionar la peticion HTTPs
    // y una referencia al solicitante de información

    //Un bearer key es una API_KEY que no va como parámetro clásico, sino dentro de las cabeceras HTTP (más seguro)
    //Nos la da la web TMDB después de registrarnos y solicitarla, es gratis

    private static final String API_BEARER_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMjc4OTZjNjk1OTkzOWNmMzlkYmM4OTMyNzZmOTAyNSIsInN1YiI6IjY2NDFhZTY3OTA5YWVkY2FiM2YxMWQ5MiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.pVNQPp_X0U9nnzAHvrjeT7NTCoGH2OmGwiilc7XghqU";

    //Referencia al cliente que necesita la información, como es asíncrono, lo necesitamos
    //para "informarle" cuando los datos están disponibles
    private RequestClient client;

    //COMPORTAMIENTO
    public DataMovieAccess(RequestClient client) {
        this.client = client;
    }

    public void requestData(String URL) {

        //Cliente HTTP
        OkHttpClient clientHTTP = new OkHttpClient();

        //Petición a realizar al cliente HTTP. Patrón de diseño "Builder". Es decir "poco a poco"
        Request request = new Request.Builder()
                .url(URL)  //dirección web
                .get()     //método http a utilizar
                .addHeader("accept", "application/json")  //Qué formato queremos
                .addHeader("Authorization", "Bearer " + API_BEARER_KEY) //clave de identidad
                .build();   //a construir la request!!

        //realizamos la llamada al server, pero en otro thread (con enqueue)
        //Primero, una llamada al server
        Call llamada = clientHTTP.newCall(request);
        //Ponemos la llamada en cola para que salga por la tarjeta de red que tengamos en el móvil activa, y creamos un
        //objeto anónimo CallBack (llamada de vuelta cuando están los datos)
        //Un callback tiene override de onResponse (la petición se ha atendido perfectamente) y
        //override de onFailure (evento de "algo salió mal")
        llamada.enqueue(new Callback() {
            public void onResponse(Call call, Response respuestaServer)
                    throws IOException {
                //Got answer!!!!! cogemos los datos dentro del body del mensaje
                String respuesta = respuestaServer.body().string();

                // Create a handler that associated with Looper of the main thread
                //Un manejador es un "bucle" en esencia que ejecuta uno a uno todos los procesos de la App
                Handler manejador = new Handler(Looper.getMainLooper()); //pedimos el principal de la app
                // Send a task to the MessageQueue of the main thread
                manejador.post(new Runnable() {
                    @Override
                    public void run() {
                        // Code will be executed on the main thread
                        //Este es código que realmente se ejecuta cuando se recibe la respuesta.
                        client.setDataFromRESTAPI(respuesta);
                    }
                });
            }

            public void onFailure(Call call, IOException e) {
                //Cuidado, puede que haya alguna vez un fallo en la respuesta. Entonces entra por aquí
                String respuesta = e.getMessage(); //Fijaros que nos pasan la excepción con el problema.
                //Lo típico es "sacar" el string mensaje de la exceptión y mandarla al sistema principal para
                //que se vea que ha pasado
                Handler manejador = new Handler(Looper.getMainLooper());

                // Send a task to the MessageQueue of the main thread
                manejador.post(new Runnable() {
                    @Override
                    public void run() {
                        // Code will be executed on the main thread
                        client.activateError();
                        client.setDataFromRESTAPI(respuesta);

                    }
                });
            }
        });
    }



}
