
package com.politecnicomalaga.tmdbclient.data;

import com.google.gson.Gson;
import com.politecnicomalaga.tmdbclient.control.MoviesViewModel;
import com.politecnicomalaga.tmdbclient.model.MovieResultSet;
import com.politecnicomalaga.tmdbclient.model.MovieSerieItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Clase que implementa el control entre los que necesitan la información (ViewModel)
// y los datos de la API Rest TMDB. Cómo las peticiones son asíncronas
// se encargará de recibir la respuesta desde Internet de la API Rest
// y convertir el JSON a objetos
public class RequestClient {

    //IMAGES:
    /*
     *    https://image.tmdb.org/t/p/w500/h4FuqnkBrZ6Wxi4TEeB99QvL590.jpg
     *    el w500 significa de ancho 500px. Por defecto las fotos son formato 2x3.
     *    Es decir, con width 500, el height es 750
     *
     * */

    private static final String URL = "https://api.themoviedb.org/3/search/movie?query=%query%&include_adult=false&language=en-US&page=1";
    private static final String URLAllMovies = "https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=false&language=en-US&page=*p*&sort_by=vote_average.desc&without_genres=99,10755&vote_count.gte=200";
    private static final String URLAllSeries = "https://api.themoviedb.org/3/discover/tv?include_adult=false&language=en-US&page=*p*&sort_by=vote_average.desc&vote_count.gte=200";


    //Atributos
    private boolean error;
    private List<MovieSerieItem> listData;
    private MovieResultSet movieResultSet;
    private MoviesViewModel vmInstance;

    //Vamos a distinguir tres tipos de búsquedas, mejores pelis, mejores series, pelis/series con título que contenga
    //el parámetro "query" dentro
    public enum TipoBusqueda {
        MOVIES,
        SERIES,
        QUERY
    }

    //Métodos

    public RequestClient(MoviesViewModel vmInstance) {
        error = false;
        this.vmInstance = vmInstance;
        listData = new ArrayList<>();
    }

    //Método usado para seleccionar el tipo de búsqueda que pediremos a TMDB
    public void getDataFromRESTAPI(TipoBusqueda tipo, String query, int pagina) {
        //Reseteamos el modo error para que no se guarden "errores" previos
        error = false;
        //Instanciamos un objeto de acceso a datos, el que sabe hacer request de los datos
        DataMovieAccess myDMA = new DataMovieAccess(this);
        error = false;
        String queryURL;
        if (tipo == TipoBusqueda.MOVIES) {
            queryURL = URLAllMovies.replace("*p*", String.valueOf(pagina));
        } else if (tipo == TipoBusqueda.SERIES) {
            queryURL = URLAllSeries.replace("*p*", String.valueOf(pagina));
        } else
            queryURL = URL.replace("%query%",query);

        myDMA.requestData(queryURL);
    }

    //Método para cambiar el JSON (String) a un objeto de tipo MovieResultSet (nuestro modelo)
    public void setDataFromRESTAPI(String json) {
        //Si estamos en modo error, no hacemos nada
        if (error) return;

        //Procesar el JSON para pasarlo a objetos del modelo, una lista de películas o series
        Gson gson = new Gson();
        MovieResultSet result = gson.fromJson(json, MovieResultSet.class);

        //del MovieResultSet nos interesa por ahora la lista de pelis/series
        listData = Arrays.asList(result.getResults());
        movieResultSet = result;
        //Notificamos al viewmodel
        this.vmInstance.setData();
    }

    public void activateError() {
        error = true;
    }

    public boolean isError() {
        return error;
    }

    public List<MovieSerieItem> getListData() {
        return listData;
    }

    public MovieResultSet getMovieResultSet() {
        return movieResultSet;
    }
}

