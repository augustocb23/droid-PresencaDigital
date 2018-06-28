package augusto.aulas;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import model.Aluno;

class AdapterFaltas extends BaseAdapter {
    private final ArrayList<Aluno> alunos;
    private final Activity act;

    AdapterFaltas(ArrayList<Aluno> alunos, Activity act) {
        this.alunos = alunos;
        this.act = act;
    }

    @Override
    public Object getItem(int position) {
        return alunos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return alunos.get(position).getCodigo();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = act.getLayoutInflater().inflate(R.layout.student_checkbox, parent, false);
        final Aluno aluno = alunos.get(position);
        //definindo nome e presença
        CheckBox checkbox = view.findViewById(R.id.student_checkbox);
        checkbox.setChecked(aluno.isPresent());
        checkbox.setText(aluno.getNome());
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                aluno.setPresent(isChecked);
            }
        });
        //preenchendo o total de faltas
        TextView faltas = view.findViewById(R.id.student_absences);
        if (aluno.getPresencas() == null)
            faltas.setText(null);
        else
            faltas.setText(String.format(view.getResources().getString(R.string.student_absence), aluno.getPresencas()));
        return view;
    }

    @Override
    public int getCount() {
        return alunos.size();
    }
}
