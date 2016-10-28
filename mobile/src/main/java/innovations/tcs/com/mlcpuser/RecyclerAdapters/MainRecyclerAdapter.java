package innovations.tcs.com.mlcpuser.RecyclerAdapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import innovations.tcs.com.mlcpuser.Beans.OptionBean;
import innovations.tcs.com.mlcpuser.Interfaces.Communicator;
import innovations.tcs.com.mlcpuser.R;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.RecycleViewHolder> {

    LayoutInflater layoutInflater;
    ArrayList<OptionBean> optionBeansList = new ArrayList<OptionBean>();
    Communicator communicator;

    public MainRecyclerAdapter(Context context, ArrayList<OptionBean> optionBeansList) {

        this.layoutInflater = LayoutInflater.from(context);
        this.optionBeansList = optionBeansList;
        this.communicator = (Communicator) context;
    }

    @Override
    public MainRecyclerAdapter.RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View cardView;
        int cardNumber = viewType + 1;

        if (cardNumber > 6) {
            cardNumber = cardNumber % 6;
        }

        switch (cardNumber) {
            case 1:
                cardView = layoutInflater.inflate(R.layout.course_card1, parent, false);
                break;
            case 2:
                cardView = layoutInflater.inflate(R.layout.course_card2, parent, false);
                break;
            case 3:
                cardView = layoutInflater.inflate(R.layout.course_card3, parent, false);
                break;
            case 4:
                cardView = layoutInflater.inflate(R.layout.course_card4, parent, false);
                break;
            default:
                cardView = layoutInflater.inflate(R.layout.course_card1, parent, false);
        }

        RecycleViewHolder recycleViewHolder = new RecycleViewHolder(cardView);

        Log.d("cardNumber", String.valueOf(cardNumber));

        return recycleViewHolder;
    }

    @Override
    public void onBindViewHolder(MainRecyclerAdapter.RecycleViewHolder holder, int position) {
        final OptionBean currentData = optionBeansList.get(position);
        holder.optionName.setText(currentData.getOptionName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicator.callBack(currentData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return optionBeansList.size();
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

        TextView optionName;
        CardView cardView;

        public RecycleViewHolder(View itemView) {
            super(itemView);
            optionName = (TextView) itemView.findViewById(R.id.courseName);
            cardView = (CardView) itemView.findViewById(R.id.CV);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}