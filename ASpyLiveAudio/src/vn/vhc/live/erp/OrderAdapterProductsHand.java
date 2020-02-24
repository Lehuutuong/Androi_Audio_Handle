package vn.vhc.live.erp;

import java.util.ArrayList;
import java.util.List;

import vn.vhc.live.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class OrderAdapterProductsHand extends ArrayAdapter<Products> {

	public ArrayList<Products> original;
	private ArrayList<Products> fitems;
	private Filter filter;
	private Context mcontext;
	private LayoutInflater inflater;
	public static String NewNumber = "";
	public static String OdlName = "";
	public int createOrder = 0;
	public int viewisNull = 0;

	public OrderAdapterProductsHand(Context context, int textViewResourceId,
			List<Products> objects, LayoutInflater inflater) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.original = new ArrayList<Products>(objects);
		this.fitems = new ArrayList<Products>(objects);
		this.mcontext = context;
		this.inflater = inflater;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return super.getFilter();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;

		if (convertView == null) {

			holder = new ViewHolder();
			// inflater = mcontext.getLayoutInflater();
			convertView = inflater.inflate(R.layout.order_hand_product_items,
					parent, false);
			holder.txtName = (TextView) convertView
					.findViewById(R.id.txt_title_hand_products);
			holder.txtNumber = (TextView) convertView
					.findViewById(R.id.txt_number_hand_products);
		

			viewisNull = 1;
			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		if (fitems == null) {
			fitems = new ArrayList<Products>(original);
		}
		final Products products = fitems.get(position);
		holder.txtName.setText(products.ProductName);
		holder.txtNumber.setText(products.ProductNumber);
		
		return convertView;
	}

	static class ViewHolder {
		TextView txtNumber;
		TextView txtName;
		

	}
}