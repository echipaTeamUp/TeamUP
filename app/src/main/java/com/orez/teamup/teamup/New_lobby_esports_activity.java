package com.orez.teamup.teamup;


import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Calendar;

public class New_lobby_esports_activity extends Activity  {

    EditText mnumber_playersEt;
    Spinner mSelectSportSpinner;
    Spinner mranks_spinner;
    Button mnewLobbyBtn;
    User user;
    TextView mtimeTV;
    RadioButton today_Rbtn;
    RadioButton tomorrow_Rbtn;
    RadioGroup radioGroup;
    int Lmonth, Lday, Lhour, Lminute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_lobby_esports_activity);


        mnumber_playersEt = (EditText) findViewById(R.id.number_playersET);
        mSelectSportSpinner = (Spinner) findViewById(R.id.sport_spinner);
        mnewLobbyBtn = (Button) findViewById(R.id.new_lobbyBtn);
        mtimeTV = (TextView) findViewById(R.id.timeTV);
        today_Rbtn = (RadioButton) findViewById(R.id.today_RBtn);
        tomorrow_Rbtn = (RadioButton) findViewById(R.id.tomorrow_RBtn);
        mranks_spinner=(Spinner) findViewById(R.id.create_rank_spinner);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        user = (User) getIntent().getSerializableExtra("User");
        //adapter pentru spinner
        mSelectSportSpinner.setAdapter(new ArrayAdapter<esports>(this, android.R.layout.simple_list_item_1, esports.values()));

        mSelectSportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               changeranks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //onclick creare lobby
        mnewLobbyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkallfields()) {
                    Calendar calendar = Calendar.getInstance();
                    if (tomorrow_Rbtn.isChecked())
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                    Lmonth = calendar.get(Calendar.MONTH);
                    Lday = calendar.get(Calendar.DAY_OF_MONTH);
                    CSGOranks csgorank;LoLranks LoLrank;
                    switch ((esports) mSelectSportSpinner.getSelectedItem()) {
                        case CSGO:
                            csgorank = (CSGOranks) mranks_spinner.getSelectedItem();
                            LoLrank = null;
                            break;
                        case LoL:
                            csgorank = null;
                            LoLrank = (LoLranks) mranks_spinner.getSelectedItem();
                            break;
                        default:
                            csgorank = (CSGOranks) mranks_spinner.getSelectedItem();
                            LoLrank = LoLranks.Bronze1;
                            break;
                    }
                    int maxlobbysize = Integer.parseInt(mnumber_playersEt.getText().toString());
                    LobbyEsports mlobby = new LobbyEsports(Lobby.getNewID(), maxlobbysize, (esports) mSelectSportSpinner.getSelectedItem(),
                            FirebaseAuth.getInstance().getUid(), Lmonth, Lday, Lhour, Lminute,csgorank,LoLrank);
                    Log.v("log", "apeleaza writetodb din new_lobby_activity");
                    mlobby.writeToDB();

                    Intent i = new Intent(New_lobby_esports_activity.this, LobbyEsportsActivity.class);
                    i.putExtra("Lobby", mlobby);
                    i.putExtra("User", user);
                    makeToast("Lobby created");
                    startActivity(i);
                    finish();
                }
            }
        });

        //onClick selectat ora
        mtimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                final int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(New_lobby_esports_activity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        //Daca ora e buna
                        if (checktime(selectedHour, selectedMinute, mcurrentTime.get(Calendar.HOUR_OF_DAY),
                                mcurrentTime.get(Calendar.MINUTE)) || tomorrow_Rbtn.isChecked()) {
                            mtimeTV.setText("Hour: " + selectedHour + ":" + selectedMinute);
                            Lhour = selectedHour;
                            Lminute = selectedMinute;
                        } else
                            makeToast("Time should be in at least an hour from now");
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        //Daca schimba data, reseteaza ora
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mtimeTV.setText(R.string.select_an_hour);
            }
        });
    }
    void changeranks() {
        switch ((esports) mSelectSportSpinner.getSelectedItem()) {
            case CSGO:
                mranks_spinner.setAdapter(new ArrayAdapter<CSGOranks>(this, android.R.layout.simple_list_item_1, CSGOranks.values()));
                break;
            case LoL:
                mranks_spinner.setAdapter(new ArrayAdapter<LoLranks>(this, android.R.layout.simple_list_item_1, LoLranks.values()));
                break;
        }

    }


    //verifica sa fie ora in cel putin o ora de la ora curenta
    public boolean checktime(int hour, int minute, int chour, int cminute) {
        if (tomorrow_Rbtn.isChecked() && chour < 23)
            return true;
        if (tomorrow_Rbtn.isChecked() && chour == 23 && hour == 0 && minute < cminute)
            return false;
        if (hour - chour > 1)
            return true;
        if (hour - chour == 1 && minute >= cminute)
            return true;
        else return false;
    }

    public boolean checkallfields() {
        int maxlobbysize;

        try {
            maxlobbysize = Integer.parseInt(mnumber_playersEt.getText().toString());
        } catch (Exception e) {
            makeToast("Max lobby size is not a valid format");
            return false;
        }

        if (maxlobbysize < 2) {
            makeToast("The max lobby size shoud be at least 2");
            return false;
        }

        if (mtimeTV.getText().toString().equals(R.string.select_an_hour)) {
            makeToast("Please select an hour");
            return false;
        }
        return true;
    }

    public void makeToast(String string) {
        Toast.makeText(New_lobby_esports_activity.this, string, Toast.LENGTH_SHORT).show();
    }
}
