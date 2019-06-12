package br.com.catlangos.eventando.Mapas;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import br.com.catlangos.eventando.R;
import br.com.catlangos.eventando.evento.Evento;
import br.com.catlangos.eventando.evento.VisualizarEventoActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.*;
import java.util.HashMap;

public class MapsBuscarEvento extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private HashMap<Marker, Evento> eventos = new HashMap<>();
    private LocationManager locationManager;
    public final static int MAP_PERMISSION = 1;
    public final static String CODIGO_DE_BUSCA = "CODIGO_DE_BUSCA";
    public final static String TODOS = "TODOS";
    public final static String CATEGORIA = "CATEGORIA";
    public final static String CATEGORIA_SELECIONADA = "CATEGORIA_SELECIONADA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("/Eventos");

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MAP_PERMISSION);
        }else{
            getLastLocation();
        }

        String tipoDeBusca = getIntent().getStringExtra(CODIGO_DE_BUSCA);

        switch (tipoDeBusca){
            case TODOS:
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ref : dataSnapshot.getChildren()){
                            Evento evento = ref.getValue(Evento.class);
                            if(evento != null){
                                LatLng latLng = new LatLng(evento.getLatitude(), evento.getLongitude());
                                Marker marcador = mMap.addMarker(new MarkerOptions().position(latLng));
                                eventos.put(marcador, evento);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;

            case CATEGORIA:
                String categoriaSelecionada = getIntent().getStringExtra(CATEGORIA_SELECIONADA);
                Query query = reference.orderByChild("categoria").equalTo(categoriaSelecionada);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ref : dataSnapshot.getChildren()){
                            Evento evento = ref.getValue(Evento.class);
                            if(evento != null){
                                LatLng latLng = new LatLng(evento.getLatitude(), evento.getLongitude());
                                Marker marcador = mMap.addMarker(new MarkerOptions().position(latLng));
                                eventos.put(marcador, evento);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;

        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Evento evento = eventos.get(marker);
                Intent intent = new Intent(getBaseContext(), VisualizarEventoActivity.class);
                intent.putExtra(Evento.EVENTO, evento);
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MAP_PERMISSION){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else{
                Toast.makeText(MapsBuscarEvento.this, "É necessário permitir a localização para selecionar local", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getLastLocation(){
        try{
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            LatLng actualLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actualLocation, 13));
        }catch(Exception e){
            LatLng actualLocation = new LatLng(-15.7896196,-47.8911385);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actualLocation, 13));
        }
    }
}
