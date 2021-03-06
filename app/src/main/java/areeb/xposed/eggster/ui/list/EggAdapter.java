package areeb.xposed.eggster.ui.list;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import areeb.xposed.eggster.Egg;
import areeb.xposed.eggster.R;
import areeb.xposed.eggster.preferences.PreferenceManager;

import java.util.ArrayList;

public class EggAdapter extends ArrayAdapter<Egg> {

    private Context context;
    private ArrayList<Egg> eggs;

    private String selected = null;


    public EggAdapter(Context context, ArrayList<Egg> eggs) {
        super(context, 0, eggs);
        this.context = context;
        this.eggs = eggs;

        selected = new PreferenceManager(context).getEasterEgg();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Egg egg = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.item_easter_egg, parent, false);

        switchHandler(convertView, position);
        itemHandler(convertView, egg);

        return convertView;
    }

    private void itemHandler(View convertView, final Egg egg) {
        final TextView textView = (TextView) convertView.findViewById(R.id.egg_title);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.egg_image);

        String name = egg.getName();
        textView.setText(name);

        imageView.setImageResource(egg.getDrawableRes(context));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            imageView.setElevation(0.5f);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && name.equals(Egg.N_PREVIEW.getName())) {
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    textView.setText(new String(Character.toChars(0x1F31A)));
                    imageView.setImageResource(R.drawable.logo_nm);
                    return false;
                }
            });
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_MAIN).setFlags(
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        .setClassName("areeb.xposed.eggster",
                                egg.getPackage()));
            }
        });
    }

    private void switchHandler(View convertView, int position) {
        final SwitchCompat switchCompat = (SwitchCompat) convertView.findViewById(R.id.egg_select);

        if (!PreferenceManager.isModuleActive()) {
            switchCompat.setEnabled(false);
            return;
        } else {
            switchCompat.setEnabled(true);
        }

        final Egg egg = getItem(position);

        CompoundButton.OnCheckedChangeListener mListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                PreferenceManager preferenceManager = new PreferenceManager(context);

                if (checked) {
                    preferenceManager.setEnabled(true);
                    preferenceManager.setEasterEgg(egg);

                    selected = egg.getId();
                } else {
                    preferenceManager.setEnabled(false);
                    selected = null;
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        notifyDataSetChanged();
                    }
                }, 170);

            }
        };

        switchCompat.setOnCheckedChangeListener(null);
        if (egg.getId().equals(selected)) {
            switchCompat.setChecked(true);
        } else {
            switchCompat.setChecked(false);
        }
        switchCompat.setOnCheckedChangeListener(mListener);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCompat.toggle();
            }
        });
    }
}
