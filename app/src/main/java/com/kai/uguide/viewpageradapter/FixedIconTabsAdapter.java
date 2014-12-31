package com.kai.uguide.viewpageradapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.astuetz.viewpager.extensions.TabsAdapter;
import com.astuetz.viewpager.extensions.ViewPagerTabButton;
import com.kai.uguide.R;

public class FixedIconTabsAdapter implements TabsAdapter {

    private Activity mContext;

    private int[] mIcons = {
            R.drawable.groups, R.drawable.contacts, R.drawable.favourites
    };

    public FixedIconTabsAdapter(Activity ctx) {
        this.mContext = ctx;
    }

    @Override
    public View getView(int position) {
        ViewPagerTabButton tab;

        LayoutInflater inflater = mContext.getLayoutInflater();
        switch (position) {
            case 0:
                tab = (ViewPagerTabButton) inflater.inflate(R.layout.tab_fixed_icon, null);
                break;
            case 1:
                tab = (ViewPagerTabButton) inflater.inflate(R.layout.tab_fixed_icon_1, null);
                break;
            case 2:
                tab = (ViewPagerTabButton) inflater.inflate(R.layout.tab_fixed_icon_2, null);
                break;
            default:
                tab = (ViewPagerTabButton) inflater.inflate(R.layout.tab_fixed_icon, null);
        }

        if (position < mIcons.length)
            tab.setCompoundDrawablesWithIntrinsicBounds(null, mContext.getResources().getDrawable(mIcons[position]), null, null);

        return tab;
    }

}