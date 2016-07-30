package com.pokemon.rifatrashid.pokedex;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.NearbyPokemon;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;

import java.util.List;

import okhttp3.OkHttpClient;

public class MainActivity extends Activity {

    public static GPSTracker gpsTracker;
    private Button locate_button;
    private TextView pokemonList_text;
    public StringBuilder stringBuilder;
    private ProgressBar spinner;
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpsTracker = new GPSTracker(this);
        System.out.println(gpsTracker.getLatitude() + ", " + gpsTracker.getLongitude());
        locate_button = (Button) findViewById(R.id.locate_btn);
        pokemonList_text = (TextView) findViewById(R.id.pokemon_list_text);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.INVISIBLE);

        stringBuilder = new StringBuilder();
        okHttpClient = new OkHttpClient();

        //When user requests location
        locate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                new DownloadPokemonStream().execute(okHttpClient);

            }
        });

    }



    public class DownloadPokemonStream extends AsyncTask<OkHttpClient, Integer, StringBuilder> {

        @Override
        protected void onPreExecute() {
            //nothing really to do here!
        }

        @Override
        protected StringBuilder doInBackground(OkHttpClient... okHttpClients) {

            StringBuilder stringBuilder = new StringBuilder();
            OkHttpClient okHttpClient = okHttpClients[0];

            try {
               PokemonGo go = new PokemonGo(new PtcCredentialProvider(okHttpClient, "username", "password"), okHttpClient);
                go.getPlayerProfile();
                go.getInventories();
                // set location
                go.setLocation(-32.058087, 115.744325, 0);

                List<CatchablePokemon> catchablePokemon = go.getMap().getCatchablePokemon();
                System.out.println("Pokemon in area:" + catchablePokemon.size());
                //List<CatchablePokemon> catchablePokemons = go.getMap().getCatchablePokemon();
              //  System.out.println(catchablePokemons.size());
                //for(CatchablePokemon c : catchablePokemons){
                //    System.out.println(c.getPokemonId());
              //  }
                /*
                List<NearbyPokemon> nearbyPokemons = go.getMap().getNearbyPokemon();
                stringBuilder.append("Pokemon(s) Nearby: " + String.valueOf(nearbyPokemons.size() + "\n"));
                for (NearbyPokemon np : nearbyPokemons) {
                    stringBuilder.append(np.getPokemonId() + "\n");
                    stringBuilder.append("\t >>>" + String.valueOf(np.getDistanceInMeters()) + "\n");
                }
                */
            } catch (LoginFailedException e) {
                e.printStackTrace();
            } catch (RemoteServerException e) {
                e.printStackTrace();
            }

            return stringBuilder;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            spinner.setVisibility(View.INVISIBLE);
           // pokemonList_text.setText(result.toString());
            super.onPostExecute(result);
        }
    }
}
