package model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int VERSAO = 1;
    private static final String DB = "main";
    private static final String TURMA = "turma";
    private static final String ALUNO = "aluno";
    private static final String AULA = "aula";
    private static final String ALUNO_COD = "cod_aluno";
    private static final String ALUNO_NOME = "nome_aluno";
    private static final String TURMA_COD = "cod_turma";
    private static final String TURMA_NOME = "nome_turma";
    private static final String AULA_COD = "cod_aula";
    private static final String AULA_TEMA = "tema_aula";
    private static final String AULA_DATA = "data_aula";
    private static final String FREQUENCIA = "frequencia";

    private SQLiteHelper(Context c) {
        this(c, DB, null, VERSAO);
    }

    private SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void buscaFrequencias(Context context, Turma turma, int aula) {
        SQLiteDatabase conn = new SQLiteHelper(context).getReadableDatabase();
        String[] args = {String.valueOf(aula), "", String.valueOf(turma.getCodigo()), ""};
        for (Aluno aluno : turma.getAlunos()) {
            args[1] = String.valueOf(aluno.getCodigo());
            args[3] = String.valueOf(aluno.getCodigo());
            Cursor c = conn.rawQuery("SELECT EXISTS (" +
                    "SELECT * FROM frequencia WHERE cod_aula = ? AND cod_aluno = ?)," +
                    "(SELECT COUNT(cod_aula) FROM aula WHERE cod_turma = ?) -" +
                    "(SELECT COUNT(cod_aula) FROM frequencia WHERE cod_aluno = ?)", args);
            if (c.moveToNext()) {
                aluno.setPresent(c.getInt(0) != 0);
                aluno.setPresencas(c.getInt(1));
            }
            c.close();
        }
    }

    public static Turma buscaTurma(Context context, int codigo) {
        Turma turma = new Turma(codigo);
        SQLiteDatabase conn = new SQLiteHelper(context).getReadableDatabase();

        //busca o nome da turma
        String[] colunas_turma = {TURMA_NOME};
        Cursor c = conn.query(TURMA, colunas_turma, TURMA_COD + "=" + codigo, null,
                null, null, null);
        int nome = c.getColumnIndex(TURMA_NOME);
        if (c.moveToNext())
            turma.setNome(c.getString(nome));
        c.close();

        //busca os alunos
        String[] colunas_aluno = {ALUNO_COD, ALUNO_NOME};
        c = conn.query(ALUNO, colunas_aluno, TURMA_COD + "=" + codigo, null,
                null, null, ALUNO_NOME);
        int[] alunos = {c.getColumnIndex(ALUNO_NOME), c.getColumnIndex(ALUNO_COD)};
        while (c.moveToNext()) {
            Aluno aluno = new Aluno(c.getString(alunos[0]));
            aluno.setCodigo(c.getInt(alunos[1]));
            turma.getAlunos().add(aluno);
        }
        c.close();

        //busca as aulas
        String[] colunas_aula = {AULA_COD, AULA_DATA, AULA_TEMA};
        c = conn.query(AULA, colunas_aula, TURMA_COD + "=" + codigo, null,
                null, null, AULA_DATA);
        int[] aulas = {
                c.getColumnIndex(AULA_COD), c.getColumnIndex(AULA_TEMA), c.getColumnIndex(AULA_DATA)
        };
        while (c.moveToNext()) {
            Aula aula = new Aula(c.getInt(aulas[0]));
            aula.setTema(c.getString(aulas[1]));
            aula.setData(new Date(c.getLong(aulas[2])));
            turma.getAulas().add(aula);
        }
        c.close();

        return turma;
    }

    public static void criaAula(Context context, Turma turma, Aula aula) {
        SQLiteDatabase conn = new SQLiteHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        //cadastra a aula, salvando o código
        values.put(AULA_TEMA, aula.toString());
        values.put(AULA_DATA, aula.getData().getTime());
        values.put(TURMA_COD, turma.getCodigo());
        aula.setCodigo((int) conn.insert(AULA, null, values));
        values.clear();
        //cadastra as frequências
        for (Aluno aluno : turma.getAlunos()) {
            if (aluno.isPresent()) {
                values.put(AULA_COD, aula.getCodigo());
                values.put(ALUNO_COD, aluno.getCodigo());
                conn.insert(FREQUENCIA, null, values);
                values.clear();
            }
        }
        conn.close();
    }

    public static void criaTurma(Context c, Turma turma) {
        SQLiteDatabase conn = new SQLiteHelper(c).getWritableDatabase();
        ContentValues values = new ContentValues();
        //cadastra a turma, salvando o código
        values.put(TURMA_NOME, turma.toString());
        turma.setCodigo((int) conn.insert(TURMA, null, values));
        values.clear();
        //insere os alunos
        for (Aluno aluno : turma.getAlunos()) {
            values.put(ALUNO_NOME, aluno.toString());
            values.put(TURMA_COD, turma.getCodigo());
            conn.insert(ALUNO, null, values);
            values.clear();
        }
        conn.close();
    }

    public static ArrayList<Turma> listaTurmas(Context context) {
        SQLiteDatabase conn = new SQLiteHelper(context).getWritableDatabase();
        ArrayList<Turma> turmas = new ArrayList<>();

        //busca as turmas
        String[] colunas = {TURMA_NOME, TURMA_COD};
        Cursor c = conn.query(TURMA, colunas, null, null, null,
                null, TURMA_NOME);
        int codigo = c.getColumnIndex(TURMA_COD);
        int nome = c.getColumnIndex(TURMA_NOME);
        while (c.moveToNext()) {
            Turma turma = new Turma(c.getInt(codigo));
            turma.setNome(c.getString(nome));
            turmas.add(turma);
        }
        c.close();
        return turmas;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS aluno (" +
                "cod_aluno INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " nome_aluno VARCHAR(255) NOT NULL, " +
                "cod_turma INTEGER NOT NULL  CONSTRAINT aula_turma_cod_turma_fk " +
                "REFERENCES turma (cod_turma))");
        db.execSQL("CREATE TABLE IF NOT EXISTS turma (" +
                "cod_turma INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " nome_turma VARCHAR(255))");
        db.execSQL("CREATE TABLE IF NOT EXISTS aula (" +
                "cod_aula INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "cod_turma INTEGER NOT NULL  CONSTRAINT aula_turma_cod_turma_fk " +
                "REFERENCES turma (cod_turma), " +
                " data_aula DATETIME NOT NULL, tema_aula TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS frequencia (cod_aluno INTEGER NOT NULL  " +
                "CONSTRAINT frequencia_aluno_cod_aluno_fk REFERENCES aluno (cod_aluno), " +
                "cod_aula INTEGER NOT NULL  CONSTRAINT frequencia_aula_cod_aula_fk " +
                "REFERENCES aula (cod_aula), " +
                " CONSTRAINT frequencia_cod_aluno_cod_aula_pk PRIMARY KEY (cod_aluno, cod_aula))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
