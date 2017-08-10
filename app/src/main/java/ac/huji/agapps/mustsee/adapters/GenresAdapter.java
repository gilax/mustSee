package ac.huji.agapps.mustsee.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ac.huji.agapps.mustsee.R;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Genre;
import ac.huji.agapps.mustsee.mustSeeApi.jsonClasses.Genres;

public class GenresAdapter extends ArrayAdapter<Genre> {

    private List<Genre> genreList;

    public GenresAdapter(Context context, int textViewResourceId, Genres genres) {
        super(context, textViewResourceId, genres.getGenres());
        this.genreList = new ArrayList<Genre>();
        this.genreList.addAll(genres.getGenres());
    }

    public List<Genre> getAllItems() {
        return genreList;
    }

    private class ViewHolder {
        TextView genre_name;
        CheckBox checkBox;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.genre_check_box, null);

            holder = new ViewHolder();
            holder.genre_name = (TextView) convertView.findViewById(R.id.genre_name);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.genre_checkbox);
            holder.checkBox.setChecked(genreList.get(position).getPreferred());
            convertView.setTag(holder);

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    genreList.get(position).setPreferred(!genreList.get(position).getPreferred());
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Genre genre = genreList.get(position);
        holder.genre_name.setText(genre.getName());
        holder.checkBox.setChecked(genre.getPreferred());
        holder.checkBox.setTag(genre);
        return convertView;
    }
}