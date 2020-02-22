package in.nishant.auctionportaladmin.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;
import in.nishant.auctionportaladmin.R;

public class CustomToast {
    public static void show(Activity activity, int type, String msg){
        View view = activity.getLayoutInflater().inflate(R.layout.layout_toast,null,false);
        CardView cardView = view.findViewById(R.id.toast_parent);
        TextView txtName = view.findViewById(R.id.toast_txtToast);
        ImageView imageView = view.findViewById(R.id.toast_imgToast);
        if(type==0) {
            imageView.setImageResource(R.drawable.ic_close_24dp);
            cardView.setCardBackgroundColor(activity.getResources().getColor(R.color.colorLightRed));
        }
        txtName.setText(msg);
        Toast toast = new Toast(activity);
        toast.setView(view);
        toast.show();
    }
}
