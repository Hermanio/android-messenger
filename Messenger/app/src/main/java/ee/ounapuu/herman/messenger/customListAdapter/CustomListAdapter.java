package ee.ounapuu.herman.messenger.customListAdapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ee.ounapuu.herman.messenger.R;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;


    private StorageReference mStorageRef;


    public CustomListAdapter(Activity context, String[] itemname) {
        super(context, R.layout.custom_list, itemname);

        this.context = context;
        this.itemname = itemname;
    }

    public View getView(int position, View view, ViewGroup parent) {
        mStorageRef = FirebaseStorage.getInstance().getReference();

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_list, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        txtTitle.setText(itemname[position]);
        extratxt.setText("Placeholder description for topic " + itemname[position]);
        setIconToItem(itemname[position], imageView);
        return rowView;

    }

    private void setIconToItem(String topic, ImageView imageView) {
        StorageReference imgRef = mStorageRef.child(topic + ".jpg");
        Glide.with(getContext()).using(new FirebaseImageLoader()).
                load(imgRef).
                signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(imageView);
    }
}