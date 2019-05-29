package br.com.catlangos.eventando;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
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
            Place.Field.ADDRESS);
    private AutocompleteSupportFragment placesFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
     }

//    @Override
//    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
//        return layoutInflater.inflate(R.layout.activity_mapa, viewGroup, false);
//        //    return super.onCreateView(layoutInflater, viewGroup, bundle);
//
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        inicializaPlaces();
        setUpPlaceAutoComplete();
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng iesb = new LatLng(-15.8349281,-47.9128294);
        mMap.addMarker(new MarkerOptions().position(iesb).title("Marcador IESB").draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(iesb, 15));
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title(latLng.toString()).draggable(true));
            }
        });

    }

    private void setUpPlaceAutoComplete() {
        placesFragment = (AutocompleteSupportFragment)getSupportFragmentManager().findFragmentById(R.id.places_autocomplete_fragment);
        placesFragment.setPlaceFields(placeFields);
        placesFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Toast.makeText(Mapa.this, ""+place.getName(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(Mapa.this, ""+status.getStatusMessage(), Toast.LENGTH_LONG);
            }
        });
    }

    private void inicializaPlaces() {
        if(!Places.isInitialized()){
            Places.initialize(this, getString(R.string.google_maps_key));
        }
        placesClient = Places.createClient(this);
    }

}
