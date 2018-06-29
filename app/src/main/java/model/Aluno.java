package model;

import java.util.Objects;

public class Aluno {
    private int codigo;
    private String nome;
    //usados somente pela classe Aula
    private boolean presente = false;
    private Integer presencas;

    public Aluno(String n) {
        codigo = -1;
        nome = n;
    }

    //define que um aluno é igual outro se o código deles forem iguais
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Aluno a = (Aluno) o;
        return Objects.equals(codigo, a.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return nome;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public Integer getPresencas() {
        return presencas;
    }

    public void setPresencas(Integer presencas) {
        this.presencas = presencas;
    }

    public boolean isPresente() {
        return presente;
    }

    public void setPresente(boolean presente) {
        this.presente = presente;
    }
}
