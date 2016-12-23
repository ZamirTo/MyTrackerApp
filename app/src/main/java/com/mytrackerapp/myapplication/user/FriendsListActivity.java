package com.mytrackerapp.myapplication.user;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mytrackerapp.myapplication.R;
import com.mytrackerapp.myapplication.json.NewUser;
import com.mytrackerapp.myapplication.model.newUserModel;


public class FriendsListActivity extends Activity {
  
  private ListView mainListView ;
  private newUserModel[] friendsList;
  private ArrayAdapter<newUserModel> listAdapter ;
  private DatabaseReference mDatabase;
  private Button getFriendListBtn;
  private Button getFriendsLocation;
  private ArrayList<newUserModel> modelItems;


  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    getFriendsLocation = (Button)findViewById(R.id.getFriendsLocationBtn);
    getFriendsLocation.setEnabled(false);
    getFriendListBtn = (Button)findViewById(R.id.getFriendsListBtn);
    getFriendListBtn.setEnabled(false);
    mDatabase = FirebaseDatabase.getInstance().getReference();
    mDatabase = mDatabase.getRoot().child("Users");
    modelItems = new ArrayList<newUserModel>();
    // Find the ListView resource.
    mainListView = (ListView) findViewById( R.id.mainListView );
    mDatabase.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
          NewUser post = postSnapshot.getValue(NewUser.class);
          modelItems.add(new newUserModel(post.getName(),post.getLocation(),false));
//          System.out.println("DONE");
          getFriendListBtn.setEnabled(true);
        }
      }
      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

    // When item is tapped, toggle checked properties of CheckBox and friendsList.
    mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick( AdapterView<?> parent, View item,
                               int position, long id) {
        newUserModel friend = listAdapter.getItem(position);
        friend.toggleChecked();
        FriendViewHolder viewHolder = (FriendViewHolder) item.getTag();
        viewHolder.getCheckBox().setChecked(friend.isChecked());
      }
    });
  }

    /**
     * select friend to show its location
     * @param v
     */
  public void onClickFriends(View v){
    if(modelItems.size()!=0) {
      // Set our custom array adapter as the ListView's adapter.
      listAdapter = new FriendsArrayAdapter(this, modelItems);
      mainListView.setAdapter(listAdapter);
      getFriendsLocation.setEnabled(true);
    }
  }

    /**
     * open map activity with all the selected friends location
     * @param v
     */
  public void onClickChecked(View v){
    if(modelItems.size()!=0) {
      int counterCheck = 0;
      int index = 0;
      Intent intentBundle = new Intent(FriendsListActivity.this, MapsActivity.class);
      Bundle bundle = new Bundle();
      for (int i = 0; i < listAdapter.getCount(); i++) {
        if (listAdapter.getItem(i).isChecked()) {
          counterCheck++;
        }
      }
      if(counterCheck == 0)
        return;
      String[] cords = new String[counterCheck*2];
      for (int i = 0; i < listAdapter.getCount(); i++) {
        if (listAdapter.getItem(i).isChecked()) {
          cords[index++] = listAdapter.getItem(i).getName();
          cords[index++] = listAdapter.getItem(i).getCordinates();
        }
      }
      bundle.putStringArray("fcords", cords);
      intentBundle.putExtras(bundle);
      startActivity(intentBundle);
    }
  }


  /** Holds child views for one row. */
  private static class FriendViewHolder {
    private CheckBox checkBox ;
    private TextView textView ;
    public FriendViewHolder() {}
    public FriendViewHolder(TextView textView, CheckBox checkBox ) {
      this.checkBox = checkBox ;
      this.textView = textView ;
    }
    public CheckBox getCheckBox() {
      return checkBox;
    }
    public void setCheckBox(CheckBox checkBox) {
      this.checkBox = checkBox;
    }
    public TextView getTextView() {
      return textView;
    }
    public void setTextView(TextView textView) {
      this.textView = textView;
    }
  }

  /** Custom adapter for displaying an array of Friends objects. */
  private static class FriendsArrayAdapter extends ArrayAdapter<newUserModel> {

    private LayoutInflater inflater;

    public FriendsArrayAdapter( Context context, List<newUserModel> friendList ) {
      super( context, R.layout.simplerow, R.id.rowTextView, friendList );
      // Cache the LayoutInflate to avoid asking for a new one each time.
      inflater = LayoutInflater.from(context) ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      // Friend to display
      newUserModel friend = (newUserModel) this.getItem( position );

      // The child views in each row.
      CheckBox checkBox ;
      TextView textView ;

      // Create a new row view
      if ( convertView == null ) {
        convertView = inflater.inflate(R.layout.simplerow, null);

        // Find the child views.
        textView = (TextView) convertView.findViewById( R.id.rowTextView );
        checkBox = (CheckBox) convertView.findViewById( R.id.CheckBox01 );

        // Optimization: Tag the row with it's child views, so we don't have to
        // call findViewById() later when we reuse the row.
        convertView.setTag( new FriendViewHolder(textView,checkBox) );

        // If CheckBox is toggled, update the Friend it is tagged with.
        checkBox.setOnClickListener( new View.OnClickListener() {
          public void onClick(View v) {
            CheckBox cb = (CheckBox) v ;
            newUserModel friend = (newUserModel) cb.getTag();
            friend.setChecked( cb.isChecked() );
          }
        });
      }
      // Reuse existing row view
      else {
        // Because we use a ViewHolder, we avoid having to call findViewById().
        FriendViewHolder viewHolder = (FriendViewHolder) convertView.getTag();
        checkBox = viewHolder.getCheckBox() ;
        textView = viewHolder.getTextView() ;
      }

      // Tag the CheckBox with the frined it is displaying, so that we can
      // access the frined in onClick() when the CheckBox is toggled.
      checkBox.setTag( friend );

      // Display frined data
      checkBox.setChecked( friend.isChecked() );
      textView.setText( friend.getName() );

      return convertView;
    }

  }

  public Object onRetainNonConfigurationInstance() {
    return friendsList;
  }
}