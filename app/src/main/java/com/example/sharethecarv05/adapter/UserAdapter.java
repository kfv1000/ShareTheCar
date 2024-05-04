package com.example.sharethecarv05.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharethecarv05.R;
import com.example.sharethecarv05.user.User;

import java.util.List;

public class UserAdapter extends ArrayAdapter {
    private Context context;

    private View view;

    private LayoutInflater layoutInflater;

    private TextView userView;
    private TextView userTipeView;

    private User userObj;

    private List<User> usersLst;
    private ImageView imgTrash;
    public UserAdapter(Context adapterContext, int xmlFileResource, int layoutResource,
                       List<User> userLst) {
        super(adapterContext, xmlFileResource, layoutResource, userLst);

        this.context = adapterContext;
        this.usersLst = userLst;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        this.layoutInflater = ((Activity)this.context).getLayoutInflater();

        this.view = layoutInflater.inflate(R.layout.user, parent, false);
        //שומר את המיקיום להציג את המידה
        this.userView = (TextView)view.findViewById(R.id.textViewUserName);
        this.userTipeView = (TextView)view.findViewById(R.id.textViewUserTipe);
        this.imgTrash = (ImageView)view.findViewById(R.id.imgTrash);
        //מקבל את האיבר הנוחחי
        this.userObj = usersLst.get(position);
        //מציג את המידה של איבר
        this.userView.setText(userObj.getUsername());
        this.userTipeView.setText(userObj.getType());
        //מצג את הפרך בצבע הנכון לפי מצב המסך
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
            imgTrash.setImageResource(R.drawable.trashw);
        else
            imgTrash.setImageResource(R.drawable.trash);

        return this.view;
    }
}
