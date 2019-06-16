package br.com.catlangos.eventando.home;

import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import br.com.catlangos.eventando.R;
import br.com.catlangos.eventando.evento.BuscarEventoFragment;
import br.com.catlangos.eventando.evento.CriarEventoFragment;
import br.com.catlangos.eventando.fragmentos.MenuPrincipal;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    private final static String MENU_PRINCIPAL = "MENU_PRINCIPAL";
    private final static String CRIAR_EVENTO = "CRIAR_EVENTO";
    private final static String BUSCAR_EVENTO = "BUSCAR_EVENTO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MenuPrincipal()).commit();

            navigationView.setCheckedItem(R.id.nav_home);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MenuPrincipal(), MENU_PRINCIPAL).commit();
                break;

            case R.id.nav_cadastrar_evento:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CriarEventoFragment(), CRIAR_EVENTO).commit();
                break;

            case R.id.nav_buscar_evento:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BuscarEventoFragment(), BUSCAR_EVENTO).commit();
                break;

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        CriarEventoFragment criarEventoFragment = (CriarEventoFragment)getSupportFragmentManager().findFragmentByTag(CRIAR_EVENTO);
        BuscarEventoFragment buscarEventoFragment = (BuscarEventoFragment)getSupportFragmentManager().findFragmentByTag(BUSCAR_EVENTO);

        if(criarEventoFragment != null && criarEventoFragment.isVisible()){
            voltarAoMenu();
        }else if(buscarEventoFragment != null && buscarEventoFragment.isVisible()){
            voltarAoMenu();
        }else{
            super.onBackPressed();
        }
    }

    private void voltarAoMenu(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new MenuPrincipal()).commit();
    }
}
