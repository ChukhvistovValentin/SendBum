package com.megatron.sendbum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class ContactArrayAdapter
        extends ArrayAdapter<Contact> {
    public ContactArrayAdapter(Context paramContext, int paramInt, List<Contact> paramList) {
        super(paramContext, paramInt, paramList);
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
        View localView = LayoutInflater.from(getContext()).inflate(R.layout.contact_row, paramViewGroup, false);
        Contact localContact = (Contact) getItem(paramInt);
        ((TextView) localView.findViewById(R.id.title)).setText(localContact.Name);
        ((TextView) localView.findViewById(R.id.phone)).setText(localContact.Phone);
        return localView;
    }
}
