package vn.vhc.live.erp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import vn.vhc.live.R;

public class MangaListAdapter extends ArrayAdapter<DataUI> implements SectionIndexer
{
    public ArrayList<DataUI> items;
    public ArrayList<DataUI> filtered;
    private Context context;
    private HashMap<String, Integer> alphaIndexer;
    private String[] sections = new String[0];
    private Filter filter;
    private boolean enableSections;

    public MangaListAdapter(Context context, int textViewResourceId, ArrayList<DataUI> items, boolean enableSections)
    {
        super(context, textViewResourceId, items);
        this.filtered = items;
        this.items = filtered;
        this.context = context;
        this.filter = new MangaNameFilter();
        this.enableSections = enableSections;

        if(enableSections)
        {
            alphaIndexer = new HashMap<String, Integer>();
            for(int i = items.size() - 1; i >= 0; i--)
            {
                DataUI element = items.get(i);
                String firstChar = element.get_title().substring(0, 1).toUpperCase();
                if(firstChar.charAt(0) > 'Z' || firstChar.charAt(0) < 'A')
                    firstChar = "@";

                alphaIndexer.put(firstChar, i);
            }

            Set<String> keys = alphaIndexer.keySet();
            Iterator<String> it = keys.iterator();
            ArrayList<String> keyList = new ArrayList<String>();
            while(it.hasNext())
                keyList.add(it.next());

            Collections.sort(keyList);
            sections = new String[keyList.size()];
            keyList.toArray(sections);
        }
    }
	/*
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View view = convertView;
	        if (view == null) {
	            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            view = inflater.inflate(R.layout.item, null);
	        }
	
	        DataUI item = items.get(position);
	        if (item!= null) {
	            // My layout has only one TextView
	            TextView itemView = (TextView) view.findViewById(R.id.ItemView);
	            if (itemView != null) {
	                // do whatever you want with your string and long
	                itemView.setText(String.format("%s %d", item.reason, item.long_val));
	            }
	         }
	
	        return view;
	    }
	
    @Override
    public void notifyDataSetInvalidated()
    {
        if(enableSections)
        {
            for (int i = items.size() - 1; i >= 0; i--)
            {
                Manga element = items.get(i);
                String firstChar = element.getName().substring(0, 1).toUpperCase();
                if(firstChar.charAt(0) > 'Z' || firstChar.charAt(0) < 'A')
                    firstChar = "@";
                alphaIndexer.put(firstChar, i);
            }

            Set<String> keys = alphaIndexer.keySet();
            Iterator<String> it = keys.iterator();
            ArrayList<String> keyList = new ArrayList<String>();
            while (it.hasNext())
            {
                keyList.add(it.next());
            }

            Collections.sort(keyList);
            sections = new String[keyList.size()];
            keyList.toArray(sections);

            super.notifyDataSetInvalidated();
        }
    }
	*/
    public int getPositionForSection(int section)
    {
        if(!enableSections) return 0;
        String letter = sections[section];

        return alphaIndexer.get(letter);
    }

    public int getSectionForPosition(int position)
    {
        if(!enableSections) return 0;
        int prevIndex = 0;
        for(int i = 0; i < sections.length; i++)
        {
            if(getPositionForSection(i) > position && prevIndex <= position)
            {
                prevIndex = i;
                break;
            }
            prevIndex = i;
        }
        return prevIndex;
    }

    public Object[] getSections()
    {
        return sections;
    }

    @Override
    public Filter getFilter()
    {
        if(filter == null)
            filter = new MangaNameFilter();
        return filter;
    }

    private class MangaNameFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // NOTE: this function is *always* called from a background thread, and
            // not the UI thread.
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<DataUI> filt = new ArrayList<DataUI>();
                ArrayList<DataUI> lItems = new ArrayList<DataUI>();
                synchronized (items)
                {
                    Collections.copy(lItems, items);
                }
                for(int i = 0, l = lItems.size(); i < l; i++)
                {
                    DataUI m = lItems.get(i);
                    //if(m.get_title().toLowerCase().contains(constraint))  filt.add(m);
                    if(isContainNoVietnamese(m.get_title().toLowerCase(),constraint.toString()))
                    	filt.add(m);
                    
                }
                result.count = filt.size();
                result.values = filt;
            }
            else
            {
                synchronized(items)
                {
                    result.values = items;
                    result.count = items.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.
            filtered = (ArrayList<DataUI>)results.values;
            notifyDataSetChanged();
        }
        private boolean isContainNoVietnamese(String sContent,String sFilter)
        {
        	UtilErp  utilErp=(new UtilErp());
        	boolean isFilter=utilErp.TrimVietnameseMark(sContent).indexOf(utilErp.TrimVietnameseMark(sFilter))!=-1;
        	Log.v("Lamdaicaxxx", "Filter:"+sContent+"==>"+sFilter+"===>"+isFilter);
        	return isFilter;// UtilErp.TrimVietnameseMark(sContent).contains(UtilErp.TrimVietnameseMark(sFilter));
        }
    }
    
}