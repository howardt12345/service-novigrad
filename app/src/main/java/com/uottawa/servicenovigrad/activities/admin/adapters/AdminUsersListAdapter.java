package com.uottawa.servicenovigrad.activities.admin.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.uottawa.servicenovigrad.R;
import com.uottawa.servicenovigrad.user.UserAccount;

import java.util.List;

public class AdminUsersListAdapter extends ArrayAdapter<UserAccount> {
    private Activity context;
    List<UserAccount> accounts;

    public AdminUsersListAdapter(@NonNull Activity context, @NonNull List<UserAccount> accounts) {
        super(context, R.layout.layout_admin_users_listitem, accounts);
        this.context = context;
        this.accounts = accounts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_admin_users_listitem, null, true);

        TextView textViewName = listViewItem.findViewById(R.id.user_name);
        TextView textViewEmail = listViewItem.findViewById(R.id.user_email);

        UserAccount account = accounts.get(position);

        textViewName.setText(account.getName());
        textViewEmail.setText(account.getEmail());

        return listViewItem;
    }
}
