package augusto.aulas;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import model.Aula;
import model.Turma;

public class AulaActivity extends AppCompatActivity implements DataDialog.DialogListener {
    Aula aula;
    Turma turma;
    AdapterFaltas adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aula);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //instancia a aula e busca os dados da turma
        aula = new Aula(getIntent().getIntExtra("codigo", -1));
        turma = Turma.carrega(getApplicationContext(), getIntent().getIntExtra("turma",
                -1));
        //busca as presenças de cada aluno e configura a lista
        if (aula.getCodigo() != -1) {
            aula.carrega(getApplicationContext());
            EditText tema = findViewById(R.id.lesson_theme);
            tema.setText(aula.toString());
            EditText data = findViewById(R.id.lesson_date);
            data.setText(new SimpleDateFormat("EEE dd/MM/yyyy", Locale.getDefault())
                    .format(aula.getData()));
            turma.buscaFrequencias(this.getApplicationContext(), aula.getCodigo());
        }
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
    public void onDialogPositiveClick(Date date) {
        aula.setData(date);
        EditText data = findViewById(R.id.lesson_date);
        data.setText(new SimpleDateFormat("EEE dd/MM/yyyy", Locale.getDefault())
                .format(aula.getData()));
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
                aula.salva(getApplicationContext(), turma);
                Intent result = new Intent();
                result.putExtra("codigo", turma.getCodigo());
                setResult(RESULT_OK, result);
                finish();
                break;
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.lesson_delete)).
                        setMessage(String.format(getString(R.string.lesson_delete_text), aula.toString()))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                aula.apaga(getApplicationContext());
                                Intent result = new Intent();
                                result.putExtra("codigo", turma.getCodigo());
                                result.putExtra("editado", false);
                                setResult(RESULT_OK, result);
                                finish();
                            }
                        }).setNegativeButton(R.string.no, null);
                builder.create().show();
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

    public void showDatePickerDialog(View v) {
        DialogFragment fragment = new DataDialog();
        fragment.show(getFragmentManager(), "data");
    }
}
