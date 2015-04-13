package com.android.khel247.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.khel247.R;
import com.android.khel247.model.PeopleItem;

import java.util.List;

/**
 * Created by Misaal on 11/12/2014.
 */
public class InviteListAdapter extends ArrayAdapter {

    private final Context context;

    private final List<String> invitedMembers;
    private final boolean isInvitingMore;
    private List<PeopleItem> peopleList;


    public InviteListAdapter(Context context, List<PeopleItem> items, List<String> invitedMembers,
                             boolean isInviting){
        super(context, R.layout.invite_people_list_item, items);
        this.context = context;
        this.peopleList = items;
        this.invitedMembers = invitedMembers;
        this.isInvitingMore = isInviting;
    }


    @Override
    public int getCount() {
        return peopleList.size();
    }

    @Override
    public PeopleItem getItem(int index) {
        return peopleList.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(int index, View view, final ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            // inflates view using the LayoutInflater. null passed for view group to show that
            // there are no hierarchical parents?
            view = inflater.inflate(R.layout.invite_people_list_item, null);
        }

        // get components to insert into the listview row
        TextView txtName = (TextView) view.findViewById(R.id.invite_people_fullname_txt);
        TextView txtUsername = (TextView) view.findViewById(R.id.invite_people_username_txt);

        // get the item that holds the data of each member in the list
        final PeopleItem item = getItem(index);

        // display fullname to the view
        txtName.setText(item.getFullName());
        final String username = item.getUsername();
        // display username
        txtUsername.setText(item.getUsername());

        // the button used to invite people to a game
        ImageButton inviteBtn = (ImageButton) view.findViewById(R.id.invite_people_btn);

        InviteBtnListener listener = new InviteBtnListener(item);

        boolean isInvited = invitedMembers.contains(username);
        if(isInvited){
            listener.setInvited(true);
            if(isInvitingMore)
                inviteBtn.setEnabled(false);
        }else{
            inviteBtn.setEnabled(true);
        }
        updateInviteIcon(inviteBtn, isInvited);

        inviteBtn.setOnClickListener(listener);

        return view;
    }


    /**
     * returns the username of the person corresponding to the item at index position in the list
     * @param index : position of the item in the list
     * @return : person's username
     */
    public String getUsernameOfPerson(int index){
        return peopleList.get(index).getUsername();
    }

    /**
     * returns the fullname of the person corresponding to the item at index position in the list
     * @param index : position of the item in the list
     * @return : fullname of the person to whom the item corresponds
     */
    public String getFullNameOfPerson(int index){
        return peopleList.get(index).getFullName();
    }


    /**
     * updates the image of the button according to the input boolean flag
     * @param isInvited
     */
    private void updateInviteIcon(ImageButton button, boolean isInvited) {
        if(isInvited)
            button.setImageResource(R.drawable.checkmark_36);
        else
            button.setImageResource(R.drawable.abc_ic_menu_paste_mtrl_am_alpha);
    }

    public List<String> getInvitedMembers() {
        return invitedMembers;
    }


    private class InviteBtnListener implements View.OnClickListener{

        private final PeopleItem item;

        private boolean invited;


        public InviteBtnListener(PeopleItem item){
            this.item = item;
            this.invited = false;
        }

        public void setInvited(boolean isInvited){this.invited = isInvited;}

        @Override
        public void onClick(View view) {
            String username = item.getUsername();
            if(invited){
                uninviteAction(username);
                invited = false;
            } else{
                inviteAction(username);
                invited = true;
            }
            updateInviteIcon((ImageButton)view, invited);
        }

        /**
         * removes the user from the list of invited people
         * @param username
         */
        private void uninviteAction(String username) {
            if(invitedMembers.contains(username)){
                invitedMembers.remove(username);
            }
        }


        /**
         * adds the user to the list of invited people
         * @param username
         */
        private void inviteAction(String username) {
            if(!invitedMembers.contains(username)){
                invitedMembers.add(username);
            }
        }

    }
}
