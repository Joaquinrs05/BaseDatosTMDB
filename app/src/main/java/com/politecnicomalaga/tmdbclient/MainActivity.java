package com.politecnicomalaga.tmdbclient;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.politecnicomalaga.tmdbclient.control.MoviesSeriesRVAdapter;
import com.politecnicomalaga.tmdbclient.control.MoviesViewModel;
import com.politecnicomalaga.tmdbclient.data.RequestClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MoviesViewModel vmodel = new ViewModelProvider(this).get(MoviesViewModel.class);

        vmodel.getResults().observe(this, movieSerieItems -> {
            RecyclerView mRecyclerView = findViewById(R.id.rvMain);
            MoviesSeriesRVAdapter mAdapter = new MoviesSeriesRVAdapter(this, movieSerieItems);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            updatePagina(vmodel.getPagina()); // Actualizamos el número de página
            totalPaguina(vmodel.getTotalPag());
        });

        Button bMovie = findViewById(R.id.btSearchMovies);
        bMovie.setOnClickListener(v -> {
            vmodel.loadData(RequestClient.TipoBusqueda.MOVIES);
            updatePagina(vmodel.getPagina()); // Actualizamos el número de página
        });

        Button bSeries = findViewById(R.id.btSearchSeries);
        bSeries.setOnClickListener(v -> vmodel.loadData(RequestClient.TipoBusqueda.SERIES));

        Button siguiente = findViewById(R.id.botonsiguiente);
        siguiente.setOnClickListener(v -> {
            vmodel.siguientePag();
            updatePagina(vmodel.getPagina()); // Actualizamos el número de página
        });

        Button anterior = findViewById(R.id.botonatras);
        anterior.setOnClickListener(v -> {
            vmodel.antPag();
            updatePagina(vmodel.getPagina()); // Actualizamos el número de página
        });

        Button avanzar = findViewById(R.id.avanzartres);
        avanzar.setOnClickListener(v -> {
            vmodel.avanzarPag();
            updatePagina(vmodel.getPagina()); // Actualizamos el número de página
        });

        Button retroceder = findViewById(R.id.retrocedertres);
        retroceder.setOnClickListener(v -> {
            vmodel.siguientePag();
            updatePagina(vmodel.getPagina()); // Actualizamos el número de página
        });

    Button verde = findViewById(R.id.verde);
    verde.setOnClickListener( v -> {
    bSeries.setBackgroundColor(Color.GREEN);
    bMovie.setBackgroundColor(Color.GREEN);
    anterior.setBackgroundColor(Color.GREEN);
    siguiente.setBackgroundColor(Color.GREEN);

    });

    Button rojo = findViewById(R.id.rojo);
    rojo.setOnClickListener( v -> {
        bSeries.setBackgroundColor(Color.RED);
        bMovie.setBackgroundColor(Color.RED);
        anterior.setBackgroundColor(Color.RED);
        siguiente.setBackgroundColor(Color.RED);
    });

    }

    private void updatePagina(int pagina) {
        TextView Pag = findViewById(R.id.Pagina);
        Pag.setText("Página: " + pagina); // Método para actualizar el texto del número de página
    }

    private void totalPaguina(int totalPaginas) {
        Button total = findViewById(R.id.total);
        total.setText("Total de Páginas: " + totalPaginas); // Método para actualizar el texto del número total de páginas
    }
}
