package br.com.catlangos.eventando;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;

public class Mapa extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PlacesClient placesClient;
    private List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG);

    private LatLng latLngSelecionado;
    private AutocompleteSupportFragment placesFragment;
    private Geocoder geocoder;
    private List<Address> enderecos;
    private Button btnConfirmar;

    public static String LATITUDE = "latitude";
    public static String LONGITUDE = "longitude";
    public final static String BUSCAR = "BUSCAR";
    public final static String CRIAR = "CRIAR";
    public final static String TIPO = "TIPO";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        btnConfirmar = findViewById(R.id.btnSelecionarLocal);
        mMap = googleMap;

        inicializaPlaces();
        setUpPlaceAutoComplete();
        configurarBtnLocal();
        LatLng iesb = new LatLng(-15.8349281, -47.9128294);
        mMap.addMarker(new MarkerOptions().position(iesb).title("Marcador IESB").draggable(true));
        setLatLngSelecionado(iesb);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(iesb, 15));
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
                setLatLngSelecionado(latLng);
            }
        });

        geocoder = new Geocoder(this);
    }

    private void configurarBtnLocal() {
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getBaseContext(), CriarEventoFragment.class);
                Intent intent = new Intent();
                intent.putExtra(LATITUDE, getLatLngSelecionado().latitude);
                intent.putExtra(LONGITUDE, getLatLngSelecionado().longitude);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void setUpPlaceAutoComplete() {
        placesFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.places_autocomplete_fragment);
        placesFragment.setPlaceFields(placeFields);
        placesFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                try {
                    mMap.clear();
                    LatLng latLng = place.getLatLng();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    setLatLngSelecionado(latLng);
                    Toast.makeText(Mapa.this, "" + place.getName(), Toast.LENGTH_LONG).show();
                } catch (NullPointerException e) {
                    Toast.makeText(Mapa.this, "Local não encontrado no mapa", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(Mapa.this, "Erro interno", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(Mapa.this, "" + status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void inicializaPlaces() {
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(this);
    }

    public LatLng getLatLngSelecionado() {
        return latLngSelecionado;
    }

    public void setLatLngSelecionado(LatLng latLngSelecionado) {
        this.latLngSelecionado = latLngSelecionado;
    }
}
