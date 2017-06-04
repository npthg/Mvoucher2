package project.mvoucher;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.ImmutableMap;

import java.util.Calendar;
import java.util.Map;

import protect.gift_card_guard.R;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateGiftCardList();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updateGiftCardList();
    }

    private void updateGiftCardList()
    {
        final ListView cardList = (ListView) findViewById(R.id.list);
        final TextView helpText = (TextView) findViewById(R.id.helpText);
        final DBHelper db = new DBHelper(this);

        if(db.getGiftCardCount() > 0)
        {
            cardList.setVisibility(View.VISIBLE);
            helpText.setVisibility(View.GONE);
        }
        else
        {
            cardList.setVisibility(View.GONE);
            helpText.setVisibility(View.VISIBLE);
        }

        Cursor cardCursor = db.getGiftCardCursor();

        final GiftCardCursorAdapter adapter = new GiftCardCursorAdapter(this, cardCursor);
        cardList.setAdapter(adapter);

        cardList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cursor selected = (Cursor) parent.getItemAtPosition(position);
                if(selected != null)
                {
                    GiftCard giftCard = GiftCard.toGiftCard(selected);

                    Intent intent = new Intent(view.getContext(), GiftCardViewActivity.class);
                    final Bundle b = new Bundle();
                    b.putInt("id", giftCard.id);
                    b.putBoolean("update", true);
                    intent.putExtras(b);
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_add)
        {
            Intent intent = new Intent(getApplicationContext(), GiftCardViewActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}