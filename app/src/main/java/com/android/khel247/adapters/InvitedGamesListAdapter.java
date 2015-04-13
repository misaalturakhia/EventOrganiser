package com.android.khel247.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.khel247.R;
import com.android.khel247.constants.GameConstants;
import com.android.khel247.model.GameData;
import com.android.khel247.model.Location;
import com.android.khel247.utilities.UtilityMethods;

import java.util.Date;
import java.util.List;

/** Tasked with displaying the items on the list of attending games and invited games
 * Created by Misaal on 08/01/2015.
 */
public class InvitedGamesListAdapter extends ArrayAdapter{


    private final Context context;
    private List<GameData> mGameList;

    /**
     * Constructor
     * @param context
     * @param items
     */
    public InvitedGamesListAdapter(Context context, List<GameData> items){
        super(context, R.layout.invited_game_list_item, items);
        this.context = context;
        this.mGameList = items;
    }

    @Override
    public int getCount() {
        return mGameList.size();
    }

    @Override
    public GameData getItem(int position) {
        return mGameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            // inflates view using the LayoutInflater. null passed for view group to show that
            // there are no hierarchical parents?
            view = inflater.inflate(R.layout.invited_game_list_item, null);
        }

        final GameData gameData = getItem(position);
        Location location = gameData.getLocation();
        Date date = gameData.getDateTime();

        String locationName = location.getName();
        populateTextView(view, R.id.list_invited_game_location_text, locationName);

        String dateText = UtilityMethods.getDateStringFromDate(date);
        populateTextView(view, R.id.list_invited_game_date_text, dateText);

        String timeText = UtilityMethods.getTimeStringFromDate(date);
        populateTextView(view, R.id.list_invited_game_time_text, timeText);

        String statusText = gameData.getStatus();
        populateTextView(view, R.id.list_invited_game_status_text, statusText);

        TextView identifierTextView = populateTextView(view, R.id.list_invited_game_organiser_text, null);
        identifierTextView.setText(getOrganiserText(gameData));

        return view;
    }

    /**
     * Checks if the organiser name is empty or null. if it is, it sets the username as the text
     * @param data
     * @return
     */
    private String getOrganiserText(GameData data) {
        String organiserName = data.getOrganiserName();
        if(UtilityMethods.isEmptyOrNull(organiserName)){
            return data.getOrganiserUsername();
        }else return organiserName;
    }


    /**
     * finds the required textview using the input resourceId and sets the paramter text to it
     * @param view
     * @param resourceId
     * @param text
     * @return
     */
    private TextView populateTextView(View view, int resourceId, String text){
        TextView textView = (TextView)view.findViewById(resourceId);
        if(!UtilityMethods.isEmptyOrNull(text))
            textView.setText(text);
        return textView;
    }


}
