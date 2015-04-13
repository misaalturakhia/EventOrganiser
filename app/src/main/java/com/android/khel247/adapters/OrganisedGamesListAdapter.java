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

/**
 * Created by Misaal on 20/12/2014.
 */
public class OrganisedGamesListAdapter extends ArrayAdapter{

    private final Context context;
    private List<GameData> mGameList;

    public OrganisedGamesListAdapter(Context context, List<GameData> items){
        super(context, R.layout.organised_game_list_item, items);
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
            view = inflater.inflate(R.layout.organised_game_list_item, null);
        }

        final GameData gameData = getItem(position);
        Location location = gameData.getLocation();
        Date date = gameData.getDateTime();

        String locationName = location.getName();
        populateTextView(view, R.id.list_game_location_text, locationName);

        String dateText = UtilityMethods.getDateStringFromDate(date);
        populateTextView(view, R.id.list_game_date_text, dateText);

        String timeText = UtilityMethods.getTimeStringFromDate(date);
        populateTextView(view, R.id.list_game_time_text, timeText);

        String statusText = gameData.getStatus();
        populateTextView(view, R.id.list_game_status_text, statusText);

        String modeText = createModeText(gameData.isPublic());
        populateTextView(view, R.id.list_game_mode_text, modeText);

        String formatText = gameData.getFormat();
        populateTextView(view, R.id.list_game_format_text, formatText);

        TextView identifierTextView = populateTextView(view, R.id.list_game_identifier_text, null);
        identifierTextView.setText(gameData.getName());

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
     *
     * @param isPublic
     * @return
     */
    private String createModeText(boolean isPublic) {
        if(isPublic)
            return GameConstants.GAME_MODE_PUBLIC;
        else
            return GameConstants.GAME_MODE_PRIVATE;
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
