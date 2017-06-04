package project.mvoucher;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import protect.gift_card_guard.R;

class GiftCardCursorAdapter extends CursorAdapter
{
    public GiftCardCursorAdapter(Context context, Cursor cursor)
    {
        super(context, cursor, 0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.giftcard_layout, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView storeField = (TextView) view.findViewById(R.id.store);
        TextView cardIdField = (TextView) view.findViewById(R.id.cardId);

        GiftCard giftCard = GiftCard.toGiftCard(cursor);

        storeField.setText(giftCard.store);
        String cardIdText = String.format("%1$s: %2$s", "ID", giftCard.cardId);
        cardIdField.setText(cardIdText);
    }
}
