package augusto.aulas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import model.Aula;

public class AdapterAulas extends BaseAdapter {
    private final ArrayList<Aula> aulas;
    private final Activity act;

    AdapterAulas(ArrayList<Aula> aulas, Activity ac) {
        this.aulas = aulas;
        this.act = ac;
    }

    @Override
    public Object getItem(int position) {
        return aulas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return aulas.get(position).getCodigo();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder")
        View view = act.getLayoutInflater().inflate(R.layout.lessons_list, parent, false);
        final Aula aula = aulas.get(position);
        //define o tema
        TextView tema = view.findViewById(R.id.lesson_theme);
        tema.setText(aula.toString());
        //define a data
        TextView data = view.findViewById(R.id.lesson_date);
        data.setText(new SimpleDateFormat("EEE dd/MMM/yyyy",
                Locale.getDefault()).format(aula.getData()));
        return view;
    }

    @Override
    public int getCount() {
        return aulas.size();
    }
}
