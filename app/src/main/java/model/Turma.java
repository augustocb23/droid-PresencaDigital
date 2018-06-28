package model;

import android.content.Context;

import java.util.ArrayList;

public class Turma {
    private String nome;
    private int codigo;
    private ArrayList<Aluno> alunos = new ArrayList<>();
    private ArrayList<Aula> aulas = new ArrayList<>();

    public Turma(int i) {
        codigo = i;
    }

    public static ArrayList<Turma> lista(Context c) {
        return SQLiteHelper.listaTurmas(c);
    }

    public static Turma carrega(Context context, int codigo) {
        return SQLiteHelper.buscaTurma(context, codigo);
    }

    @Override
    public String toString() {
        return nome;
    }

    public void salva(Context context) {
        if (codigo == -1) {
            //é uma nova turma - CREATE
            SQLiteHelper.criaTurma(context, this);
        } else {
            //TODO turma já existente - UPDATE
        }
    }

    public void buscaFrequencias(Context context, int aula) {
        SQLiteHelper.buscaFrequencias(context, this, aula);
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

    public void setNome(String nome) {
        this.nome = nome;
    }
}
