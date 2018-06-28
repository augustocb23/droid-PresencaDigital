package model;

public class Aluno {
    private int codigo;
    private String nome;
    private boolean present = false;
    private Integer presencas;

    public Aluno(String n) {
        codigo = -1;
        nome = n;
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

    public boolean isPresent() {
        return present;
    }

    public void setPresencas(Integer presencas) {
        this.presencas = presencas;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}
