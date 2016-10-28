package innovations.tcs.com.mlcpuser.RecyclerAdapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import innovations.tcs.com.mlcpuser.Beans.ParkingInfoBean;
import innovations.tcs.com.mlcpuser.R;

public class ParkingInfoRecyclerAdapter extends RecyclerView.Adapter<ParkingInfoRecyclerAdapter.RecycleViewHolder> {

    LayoutInflater layoutInflater;
    ArrayList<ParkingInfoBean> parkingInfoBeansList = new ArrayList<ParkingInfoBean>();

    public ParkingInfoRecyclerAdapter(Context context, ArrayList<ParkingInfoBean> parkingInfoBeansList) {

        this.layoutInflater = LayoutInflater.from(context);
        this.parkingInfoBeansList = parkingInfoBeansList;
    }

    @Override
    public ParkingInfoRecyclerAdapter.RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View cardView;
        int cardNumber = viewType + 1;

        switch (cardNumber) {
            case 1:
                cardView = layoutInflater.inflate(R.layout.parkinginfo_card1, parent, false);
                break;
            case 2:
                cardView = layoutInflater.inflate(R.layout.parkinginfo_card2, parent, false);
                break;
            default:
                cardView = layoutInflater.inflate(R.layout.parkinginfo_card1, parent, false);
        }

        RecycleViewHolder recycleViewHolder = new RecycleViewHolder(cardView);

        return recycleViewHolder;
    }

    @Override
    public void onBindViewHolder(ParkingInfoRecyclerAdapter.RecycleViewHolder holder, int position) {
        final ParkingInfoBean currentData = parkingInfoBeansList.get(position);
        holder.value.setText(currentData.getParkingInfoName());
    }

    @Override
    public int getItemCount() {
        return parkingInfoBeansList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class RecycleViewHolder extends RecyclerView.ViewHolder {

        TextView value;
        CardView cardView;

        public RecycleViewHolder(View itemView) {
            super(itemView);
            value = (TextView) itemView.findViewById(R.id.value);
            cardView = (CardView) itemView.findViewById(R.id.CV);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}