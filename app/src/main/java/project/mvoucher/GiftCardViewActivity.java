package project.mvoucher;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;



public class GiftCardViewActivity extends AppCompatActivity
{
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private String capturedReceipt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.giftcard_view_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        final Bundle b = getIntent().getExtras();
        final int giftCardId = b != null ? b.getInt("id") : 0;
        final boolean updateGiftCard = b != null && b.getBoolean("update", false);

        final EditText storeField = (EditText) findViewById(R.id.storeName);
        final EditText cardIdField = (EditText) findViewById(R.id.cardId);
        final EditText valueField = (EditText) findViewById(R.id.value);
        final TextView receiptField = (TextView) findViewById(R.id.receiptLocation);
        final LinearLayout hasReceiptButtonLayout = (LinearLayout) findViewById(R.id.hasReceiptButtonLayout);
        final LinearLayout noReceiptButtonLayout = (LinearLayout) findViewById(R.id.noReceiptButtonLayout);

        final Button captureButton = (Button) findViewById(R.id.captureButton);
        final Button viewButton = (Button) findViewById(R.id.viewButton);
        final Button saveButton = (Button) findViewById(R.id.saveButton);
        final Button cancelButton = (Button) findViewById(R.id.cancelButton);

        final DBHelper db = new DBHelper(this);

        noReceiptButtonLayout.setVisibility(View.GONE);
        hasReceiptButtonLayout.setVisibility(View.GONE);

        if(updateGiftCard)
        {
            final GiftCard giftCard = db.getGiftCard(giftCardId);

            storeField.setText(giftCard.store);
            cardIdField.setText(giftCard.cardId);
            valueField.setText(giftCard.value);
            receiptField.setText(giftCard.receipt);

            storeField.setEnabled(false);
            cardIdField.setEnabled(false);
            valueField.setEnabled(false);


            if(giftCard.receipt.isEmpty() && capturedReceipt == null)
            {
                noReceiptButtonLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                hasReceiptButtonLayout.setVisibility(View.VISIBLE);
            }

            setTitle("Detail Gift Voucher");
        }
        else
        {
            if(capturedReceipt == null)
            {
                noReceiptButtonLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                hasReceiptButtonLayout.setVisibility(View.VISIBLE);
            }

            setTitle("Add Gift Voucher");
        }

        View.OnClickListener captureCallback = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File imageLocation = getNewImageLocation();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageLocation));
                capturedReceipt = imageLocation.getAbsolutePath();
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        };

        captureButton.setOnClickListener(captureCallback);

        viewButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(v.getContext(), ReceiptViewActivity.class);
                final Bundle b = new Bundle();

                final TextView receiptField = (TextView) findViewById(R.id.receiptLocation);

                String receipt = receiptField.getText().toString();
                if(capturedReceipt != null)
                {
                    receipt = capturedReceipt;
                }

                b.putString("receipt", receipt);
                i.putExtras(b);
                startActivity(i);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                String store = storeField.getText().toString();
                String cardId = cardIdField.getText().toString();
                String value = valueField.getText().toString();
                String receipt = receiptField.getText().toString();

                if(store.isEmpty())
                {
                    Snackbar.make(v, "Please input store", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(value.isEmpty())
                {
                    Snackbar.make(v, "Please input value", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(capturedReceipt != null)
                {
                    receipt = capturedReceipt;
                    capturedReceipt = null;
                }

                if(updateGiftCard)
                {
                    db.updateGiftCard(giftCardId, store, cardId, value, receipt);
                }
                else
                {
                    db.insertGiftCard(store, cardId, value, receipt);
                }

                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        final Bundle b = getIntent().getExtras();
        final boolean updateGiftCard = b != null && b.getBoolean("update", false);

        if(updateGiftCard)
        {
            getMenuInflater().inflate(R.menu.delete_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        final Bundle b = getIntent().getExtras();
        final int giftCardId = b != null ? b.getInt("id") : 0;

        if(id==R.id.action_delete){
            DBHelper db = new DBHelper(this);
            db.deleteGiftCard(giftCardId);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private File getNewImageLocation()
    {
        File imageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        UUID imageFilename = UUID.randomUUID();
        File receiptFile = new File(imageDir, imageFilename.toString() + ".png");

        return receiptFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_IMAGE_CAPTURE)
        {
            onResume();
        }
    }
}
