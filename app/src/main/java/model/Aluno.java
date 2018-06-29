package model;

public class Aluno {
    private int codigo;
    private String nome;
    //usados somente pela classe Aula
    private boolean present = false;
    private Integer absences;

    public Aluno(String n) {
        codigo = -1;
        nome = n;
    }

    @Override
    public String toString() {
        return nome;
    }

    public Integer getAbsences() {
        return absences;
    }

    public void setAbsences(Integer absences) {
        this.absences = absences;
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

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}
