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
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.TextView;

import vn.vhc.live.R;

public class AgencyAdapter extends ArrayAdapter<DataUI> 
{
	private ArrayList<DataUI> original;
	private ArrayList<DataUI> fitems;
	private Filter filter;

	public AgencyAdapter(Context context, int textViewResourceId, ArrayList<DataUI> items) {
	        super(context, textViewResourceId, items);
	        this.original = new ArrayList<DataUI>(items);
	        this.fitems = new ArrayList<DataUI>(items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{	        
	        return super.getView(position, convertView, parent);
	}

	@Override
	public Filter getFilter()
	{
	    if (filter == null)
	        filter = new PkmnNameFilter();

	    return filter;
	}

	private class PkmnNameFilter extends Filter
	{
	        @Override
	        protected FilterResults performFiltering(CharSequence constraint)
	        {   
	            FilterResults results = new FilterResults();
	            String prefix = constraint.toString().toLowerCase();

	            if (prefix == null || prefix.length() == 0)
	            {
	                ArrayList<DataUI> list = new ArrayList<DataUI>(original);
	                results.values = list;
	                results.count = list.size();
	            }
	            else
	            {
	                final ArrayList<DataUI> list = new ArrayList<DataUI>(original);
	                final ArrayList<DataUI> nlist = new ArrayList<DataUI>();
	                int count = list.size();

	                for (int i=0; i<count; i++)
	                {
	                    final DataUI pkmn = list.get(i);
	                    final String value = pkmn.get_titleToCompare().toLowerCase();

	                    if (value.contains(prefix))
	                    {
	                        nlist.add(pkmn);
	                    }
	                }
	                results.values = nlist;
	                results.count = nlist.size();
	            }
	            return results;
	        }
	        @SuppressWarnings("unchecked")
	        @Override
	        protected void publishResults(CharSequence constraint, FilterResults results) {
	            fitems = (ArrayList<DataUI>)results.values;
	            clear();
	            int count = fitems.size();
	            for (int i=0; i<count; i++)
	            {
	            	DataUI pkmn = (DataUI)fitems.get(i);
	                add(pkmn);
	                //  notifyDataSetInvalidated();
	            }
	        }
	   }
}