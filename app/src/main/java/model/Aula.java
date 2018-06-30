package model;

import android.content.Context;

import java.util.Date;

public class Aula {
    private int codigo;
    private String tema;
    private Date data;

    public Aula(int codigo) {
        this.codigo = codigo;
    }

    public void apaga(Context context) {
        SQLiteHelper.apagaAula(context, this);
    }

    public void carrega(Context context) {
        SQLiteHelper.buscaAula(context, this);
    }

    public void salva(Context context, Turma turma) {
        if (codigo == -1)
            //é uma nova - CREATE
            SQLiteHelper.criaAula(context, turma, this);
        else
            //edição - UPDATE
            SQLiteHelper.editaAula(context, turma, this);
    }

    @Override
    public String toString() {
        return tema;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }
}
