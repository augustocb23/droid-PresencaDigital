package augusto.aulas;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.IOException;

import augusto.aulas.AlunoDialog.DialogListener;
import model.Aluno;
import model.Turma;

public class TurmaActivity extends AppCompatActivity implements DialogListener {
    private static final int FILE_CHOOSE = 0;
    Turma turma;
    ArrayAdapter<Aluno> adapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_CHOOSE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        try {
                            //lê o JSON e adiciona os alunos à turma
                            turma.importaJson(getContentResolver().openInputStream(uri),
                                    getResources().getString(R.string.name));
                        } catch (IOException e) {
                            String erro = getResources().getString(R.string.json_error)
                                    + e.getClass().getName();
                            Toast.makeText(getApplicationContext(), erro,
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                    //atualiza a lista
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turma);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        turma = new Turma(getIntent().getIntExtra("codigo", -1));

        //configura o botão de adicionar aluno
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new AlunoDialog();
                dialog.show(getFragmentManager(), "AlunoDialog");
            }
        });
        if (turma.getCodigo() != -1) {
            //carrega o nome da turma
            turma = Turma.carrega(getApplicationContext(), turma.getCodigo());
            EditText tema = findViewById(R.id.class_name);
            tema.setText(turma.toString());
        }

        //configura a lista de alunos
        ListView lista = findViewById(R.id.list_class);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, turma.getAlunos());
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(TurmaActivity.this, R.string.hold_to_remove,
                        Toast.LENGTH_SHORT).show();
            }
        });
        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //busca o aluno
                final Aluno aluno = turma.getAlunos().get(position);
                if (aluno.getCodigo() != -1) {
                    //se o aluno estiver cadastrado no banco, pede confirmação
                    AlertDialog.Builder builder = new AlertDialog.Builder(TurmaActivity.this);
                    builder.setTitle(getString(R.string.student_remove)).
                            setMessage(String.format(getString(R.string.student_remove_question),
                                    aluno.toString(), turma.toString()))
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //adiciona à lista de removidos (para excluir do banco)
                                    turma.getRemovidos().add(aluno);
                                    //remove da lista
                                    turma.getAlunos().remove(aluno);
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(R.string.no, null);
                    builder.create().show();
                } else {
                    //se não tem cadastro, remove direto
                    turma.getAlunos().remove(aluno);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.turma, menu);
        return true;
    }

    @Override
    public void onDialogPositiveClick(AlunoDialog dialog) {
        EditText name = dialog.getDialog().findViewById(R.id.student_name);
        turma.getAlunos().add(new Aluno(name.getText().toString()));
        adapter.notifyDataSetChanged();
        if (turma.getCodigo() != -1) {
            Snackbar.make(this.findViewById(R.id.list_class),
                    R.string.student_add, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.json_import:
                //configura a Activity de seleção de arquivo
                final Intent i = new Intent(getApplicationContext(), FilePickerActivity.class);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                i.putExtra(FilePickerActivity.EXTRA_START_PATH,
                        Environment.getExternalStorageDirectory().getPath());
                //diálogo de instruções
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.json_select)).
                        setMessage(String.format(getString(R.string.json_format), getString(R.string.name)))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //inicia a Activity ao tocar em OK
                                startActivityForResult(i, FILE_CHOOSE);
                            }
                        });
                builder.create().show();
                return true;
            case R.id.save:
                EditText nome = this.findViewById(R.id.class_name);
                if (nome.getText().length() == 0) {
                    Toast.makeText(TurmaActivity.this, R.string.class_name_add, Toast.LENGTH_SHORT).show();
                    return false;
                }
                turma.setNome(nome.getText().toString());
                if (turma.getAlunos().isEmpty()) {
                    Toast.makeText(TurmaActivity.this, R.string.class_empty, Toast.LENGTH_SHORT).show();
                    return false;
                }
                turma.salva(this.getApplicationContext());
                Intent result = new Intent();
                result.putExtra("codigo", turma.getCodigo());
                setResult(turma.getCodigo() != -1 ? RESULT_OK : RESULT_CANCELED, result);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //só ativa a opção de importar se não tiver nenhum aluno
        menu.findItem(R.id.json_import).setEnabled(turma.getAlunos().isEmpty());
        //exibe o botão excluir
        menu.findItem(R.id.delete).setVisible(turma.getCodigo() != -1);
        return super.onPrepareOptionsMenu(menu);
    }
}

