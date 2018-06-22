package com.orez.teamup.teamup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> headers;
    private HashMap<String, List<String>> headeritems;

    public MyExpandableListAdapter(Context context, List<String> listheaders, HashMap<String, List<String>> headerchilds) {
        this.context = context;
        this.headers = listheaders;
        this.headeritems = headerchilds;
    }

    @Override
    public int getGroupCount() {
        return this.headers.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headers.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.headeritems.get(this.headers.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headername = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.filter_exp_list_view_group_header, null);
        }
        TextView header = (TextView) convertView.findViewById(R.id.expHeaderTv);
        header.setText(headername);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //final String childname=(String)getChild(groupPosition,childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.filter_age_item, null);
        }
        EditText mMinAgeEt = (EditText) convertView.findViewById(R.id.minAgeEt);
        EditText mMaxAgeEt = (EditText) convertView.findViewById(R.id.maxAgeEt);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
