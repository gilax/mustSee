package ac.huji.agapps.mustsee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Genre;

public class GenresAdapter extends ArrayAdapter<Genre> {

    private List<Genre> genreList;

    private HashMap<Long, Genre> mGenreIsChecked;

    public GenresAdapter(Context context, int textViewResourceId, List<Genre> genreList) {
        super(context, textViewResourceId, genreList);
        this.genreList = new ArrayList<Genre>();
        this.genreList.addAll(genreList);

        mGenreIsChecked = new HashMap<Long, Genre>();
    }

    private class ViewHolder {
        TextView genre_name;
        CheckBox checkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.genre_check_box, null);

            holder = new ViewHolder();
            holder.genre_name = (TextView) convertView.findViewById(R.id.genre_name);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.genre_checkbox);
            convertView.setTag(holder);

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    Genre genre = (Genre) cb.getTag();

                    if(mGenreIsChecked.get(genre.getId()) != null)
                          mGenreIsChecked.remove(genre.getId());
                    else
                        mGenreIsChecked.put(genre.getId(), genre);
//                    perhaps add a function that can tell us if a genre was selected
//                    genre.setSelected(cb.isChecked());
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        boolean isSelected = false;

        Genre genre = genreList.get(position);
        holder.genre_name.setText(genre.getName());
        if(mGenreIsChecked.get(genre.getId()) != null)
            isSelected = true;
        holder.checkBox.setChecked(isSelected);
        holder.checkBox.setTag(genre);
        return convertView;
    }

    public HashMap<Long, Genre> getChecked() {
        return mGenreIsChecked;
    }
}