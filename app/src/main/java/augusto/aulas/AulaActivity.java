package augusto.aulas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Date;

import model.Aula;
import model.Turma;

public class AulaActivity extends AppCompatActivity {
    Aula aula;
    Turma turma;
    AdapterFaltas adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aula);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //busca os dados da turma
        aula = new Aula(getIntent().getIntExtra("codigo", -1));
        turma = Turma.carrega(getApplicationContext(), getIntent().getIntExtra("turma",
                -1));
        //busca as presenças de cada aluno
        if (aula.getCodigo() != -1)
            turma.buscaFrequencias(this.getApplicationContext(), aula.getCodigo());
        ListView lista = findViewById(R.id.lesson_students);
        adapter = new AdapterFaltas(turma.getAlunos(), this);
        lista.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.aula, menu);
        menu.findItem(R.id.delete).setVisible(aula.getCodigo() != -1);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.cancel:
                finish();
                break;
            case R.id.save:
                EditText tema = this.findViewById(R.id.lesson_theme);
                if (tema.getText().length() == 0) {
                    Toast.makeText(this, R.string.lesson_theme_add, Toast.LENGTH_SHORT).show();
                    return false;
                }
                aula.setTema(tema.getText().toString());
                EditText data = this.findViewById(R.id.lesson_date);
                if (data.getText().length() == 0) {
                    Toast.makeText(this, R.string.lesson_date_add, Toast.LENGTH_SHORT).show();
                    return false;
                }
                aula.setData(new Date()); //TODO buscar a data
                aula.salva(getApplicationContext(), turma);
                finish();
                Toast.makeText(getApplicationContext(), R.string.lesson_saved, Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //configura o título
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (aula.getCodigo() == -1)
            toolbar.setTitle(R.string.lesson_new);
        else
            toolbar.setTitle(R.string.lesson_edit);
    }
}
