package innovations.tcs.com.mlcpuser.RecyclerAdapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import innovations.tcs.com.mlcpuser.Beans.MySlotBean;
import innovations.tcs.com.mlcpuser.R;

public class MySlotRecyclerAdapter extends RecyclerView.Adapter<MySlotRecyclerAdapter.RecycleViewHolder> {

    LayoutInflater layoutInflater;
    ArrayList<MySlotBean> mySlotBeansList = new ArrayList<MySlotBean>();

    public MySlotRecyclerAdapter(Context context, ArrayList<MySlotBean> mySlotBeansList) {

        this.layoutInflater = LayoutInflater.from(context);
        this.mySlotBeansList = mySlotBeansList;
    }

    @Override
    public MySlotRecyclerAdapter.RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View cardView;
        int cardNumber = viewType + 1;

        if (cardNumber > 6) {
            cardNumber = cardNumber % 6;
        }

        switch (cardNumber) {
            case 1:
                cardView = layoutInflater.inflate(R.layout.myslot_card1, parent, false);
                break;
            case 2:
                cardView = layoutInflater.inflate(R.layout.myslot_card2, parent, false);
                break;
            case 3:
                cardView = layoutInflater.inflate(R.layout.myslot_card3, parent, false);
                break;
            case 4:
                cardView = layoutInflater.inflate(R.layout.myslot_card4, parent, false);
                break;
            default:
                cardView = layoutInflater.inflate(R.layout.myslot_card1, parent, false);
        }

        RecycleViewHolder recycleViewHolder = new RecycleViewHolder(cardView);

        return recycleViewHolder;
    }

    @Override
    public void onBindViewHolder(MySlotRecyclerAdapter.RecycleViewHolder holder, int position) {
        final MySlotBean currentData = mySlotBeansList.get(position);
        holder.value.setText(currentData.getMySlotName());
    }

    @Override
    public int getItemCount() {
        return mySlotBeansList.size();
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