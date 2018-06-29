package model;

import android.content.Context;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Turma {
    private String nome;
    private int codigo;
    private ArrayList<Aluno> alunos = new ArrayList<>();
    private ArrayList<Aula> aulas = new ArrayList<>();
    //usado somente pela classe Turma
    private ArrayList<Aluno> removidos;

    public Turma(int i) {
        codigo = i;
    }

    public static Turma carrega(Context context, int codigo) {
        return SQLiteHelper.buscaTurma(context, codigo);
    }

    public static ArrayList<Turma> lista(Context c) {
        return SQLiteHelper.listaTurmas(c);
    }

    public void apaga(Context context) {
        SQLiteHelper.apagaTurma(context, this);
    }

    public void buscaFrequencias(Context context, int aula) {
        SQLiteHelper.buscaFrequencias(context, this, aula);
    }

    public void importaJson(InputStream stream, String field) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
        reader.beginArray();
        while (reader.hasNext()) {
            //para cada objeto encontrado
            reader.beginObject();
            //busca o nome do campo
            String name = reader.nextName();
            //se for igual ao informado, adiciona à lista
            if (name.contentEquals(field))
                alunos.add(new Aluno(reader.nextString()));
            reader.endObject();
        }
        reader.endArray();
    }

    public void salva(Context context) {
        if (codigo == -1) {
            //é uma nova turma - CREATE
            SQLiteHelper.criaTurma(context, this);
        } else {
            //turma já existente - UPDATE
            SQLiteHelper.editaTurma(context, this);
        }
    }

    @Override
    public String toString() {
        return nome;
    }

    public ArrayList<Aluno> getAlunos() {
        return alunos;
    }

    public ArrayList<Aula> getAulas() {
        return aulas;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public ArrayList<Aluno> getRemovidos() {
        if (removidos == null)
            removidos = new ArrayList<>();
        return removidos;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
