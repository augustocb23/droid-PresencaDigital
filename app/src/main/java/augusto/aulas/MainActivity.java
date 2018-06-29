package augusto.aulas;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import model.Turma;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Turma turma;
    ArrayList<Turma> lista_turmas;
    AdapterAulas adapter;

    private void carregaTurma() {
        //exibe o nome no título
        Toolbar titulo = findViewById(R.id.toolbar);
        titulo.setTitle(turma.toString());
        //configura a lista de aulas
        ListView lista = findViewById(R.id.list_lessons);
        adapter = new AdapterAulas(turma.getAulas(), this);
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), AulaActivity.class);
                i.putExtra("codigo", turma.getAulas().get(position).getCodigo());
                i.putExtra("turma", turma.getCodigo());
                startActivity(i);
            }
        });
        //configura os dados no cabeçalho da barra lateral
        NavigationView navigationView = findViewById(R.id.nav_view);
        TextView name = navigationView.getHeaderView(0).findViewById(R.id.class_name);
        name.setText(turma.toString());
        TextView count = navigationView.getHeaderView(0).findViewById(R.id.lessons_count);
        count.setText(String.format(getResources().getString(R.string.lessons_count), turma.getAulas().size()));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //configura o botão para adicionar aula
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AulaActivity.class);
                i.putExtra("turma", turma.getCodigo());
                startActivity(i);
            }
        });

        //configura o menu lateral
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //adiciona as turmas ao menu
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu turmas = navigationView.getMenu().findItem(R.id.turmas).getSubMenu();
        lista_turmas = Turma.lista(getApplicationContext());
        for (Turma turma : lista_turmas) {
            MenuItem item = turmas.add(Menu.NONE, turma.getCodigo(), Menu.NONE, turma.toString());
            item.setIcon(R.drawable.ic_group_black_24dp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Toolbar titulo = findViewById(R.id.toolbar);
        titulo.setTitle("Carregando...");

        if (id == R.id.action_class_add) {
            Intent i = new Intent(this, TurmaActivity.class);
            i.putExtra("codigo", -1);
            startActivity(i);
            //TODO esperar o usuário cadastrar a turma
            Toast.makeText(this.getApplicationContext(), R.string.class_created, Toast.LENGTH_SHORT).show();
        } else {
            turma = Turma.carrega(this.getApplicationContext(), id);
            carregaTurma();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //carrega a primeira turma
        if (lista_turmas.isEmpty()) {
            //se não houver nenhuma, abre a Activity Turmas
            Intent i = new Intent(this, TurmaActivity.class);
            i.putExtra("codigo", -1);
            startActivity(i);
            Toast.makeText(this.getApplicationContext(), R.string.class_first, Toast.LENGTH_SHORT).show();
        } else {
            turma = Turma.carrega(this.getApplicationContext(), lista_turmas.get(0).getCodigo());
            carregaTurma();
        }
    }
}
