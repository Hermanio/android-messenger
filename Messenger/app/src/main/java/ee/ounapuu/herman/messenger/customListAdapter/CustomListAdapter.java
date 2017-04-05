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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ee.ounapuu.herman.messenger.CustomObjects.TopicListModel;
import ee.ounapuu.herman.messenger.R;

public class CustomListAdapter extends ArrayAdapter<TopicListModel> {

    private final Activity context;
    private final ArrayList<TopicListModel> itemname;


    private StorageReference mStorageRef;


    public CustomListAdapter(Activity context, ArrayList<TopicListModel> itemname) {
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

        txtTitle.setText(itemname.get(position).getTitle());

        Calendar timestamp = Calendar.getInstance();
        timestamp.setTimeInMillis(itemname.get(position).getLastActivity());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        extratxt.setText("Last activity "+ dateFormat.format(timestamp.getTime()) + "\nParticipants "+itemname.get(position).getParticipantCount());
        setIconToItem(itemname.get(position).getTitle(), imageView);
        return rowView;

    }

    private void setIconToItem(String topic, ImageView imageView) {
        StorageReference imgRef = mStorageRef.child(topic + ".jpg");
        Glide.with(getContext()).using(new FirebaseImageLoader()).
                load(imgRef).into(imageView);
    }
}