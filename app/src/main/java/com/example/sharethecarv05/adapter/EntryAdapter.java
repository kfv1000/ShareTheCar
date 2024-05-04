package com.example.sharethecarv05.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.sharethecarv05.R;
import com.example.sharethecarv05.activity.CarActivity;
import com.example.sharethecarv05.schedule.Entry;
import com.example.sharethecarv05.schedule.ScheduleManager;

import java.util.List;

public class EntryAdapter extends ArrayAdapter {

    private Context context;

    private View view;

    private LayoutInflater layoutInflater;

    private TextView startTime;
    private TextView user;
    private LinearLayout menubtn;

    private List<Entry> entries;
    private ImageView menu;
    String userName;

    public EntryAdapter(Context adapterContext, int xmlFileResource, int layoutResource,
                        List<Entry> entryLst,String userName) {
        super(adapterContext, xmlFileResource, layoutResource, entryLst);

        this.context = adapterContext;
        this.entries = entryLst;
        this.userName=userName;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        this.layoutInflater = ((Activity)this.context).getLayoutInflater();

        this.view = layoutInflater.inflate(R.layout.entry, parent, false);
        //שומר את המקומת שהמידה היה מיוצג
        this.startTime = (TextView)view.findViewById(R.id.textViewStartTime);
        this.user = (TextView)view.findViewById(R.id.textViewUser);
        this.menu = (ImageView) view.findViewById(R.id.btnEntryMenu);
        this.menubtn = (LinearLayout) view.findViewById(R.id.linear);
        //מסיג את האיבר הנוחחי
        Entry entryObj = entries.get(position);

        //מציג את המידה
        startTime.setText(entryObj.getDateRange().StartTimeToString()+" - "+entryObj.getDateRange().EndTimeToString());
        user.setText(entryObj.getUserName());
        //סם מאניו לזמנים של המישתמש
        if(userName.equals(entryObj.getUserName())){
            CarActivity car_activity = (CarActivity) context;
            int nightModeFlags = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            //סם את בצבה הנכון לשלוש הנקודות לפי מצב המסך
            if(nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
                menu.setImageResource(R.drawable.menunight);
            else
                menu.setImageResource(R.drawable.menu);
            //מפעיל הקשבה לפתיחת מניו
            PopupMenu popup = new PopupMenu(context, menu);
            popup.getMenuInflater().inflate(R.menu.entry_menu, popup.getMenu());
            //מקשיב ללחיצה על איבר במניו
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    int id=menuItem.getItemId();
                    //קורה לפעולה במסך של המחונית
                    if(R.id.editEntry == id){
                        car_activity.createEditEntryDialog(entryObj);
                    }//מוחק את הזמן שימוש
                    else if(R.id.deleteEnty == id){
                        //שועל אים את המישתמש אים הוא בתואך
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        ScheduleManager.DeleteEntry(entryObj);
                                        car_activity.updateListView();
                                        break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Are you sure you want to delete this time slot?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                        }
                        return false;
                    }
                });
                menubtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popup.show();
                    }
            });}
        return this.view;
    }
}
