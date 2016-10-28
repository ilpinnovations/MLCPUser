package innovations.tcs.com.mlcpuser.RecyclerAdapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import innovations.tcs.com.mlcpuser.Beans.MyCarsBean;
import innovations.tcs.com.mlcpuser.R;

public class MyCarsRecyclerAdapter extends RecyclerView.Adapter<MyCarsRecyclerAdapter.RecycleViewHolder> {

    LayoutInflater layoutInflater;
    ArrayList<MyCarsBean> myCarsBeansList = new ArrayList<MyCarsBean>();

    public MyCarsRecyclerAdapter(Context context, ArrayList<MyCarsBean> myCarsBeansList) {

        this.layoutInflater = LayoutInflater.from(context);
        this.myCarsBeansList = myCarsBeansList;
    }

    @Override
    public MyCarsRecyclerAdapter.RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View cardView;
        cardView = layoutInflater.inflate(R.layout.mycar_card, parent, false);
        RecycleViewHolder recycleViewHolder = new RecycleViewHolder(cardView);

        return recycleViewHolder;
    }

    @Override
    public void onBindViewHolder(MyCarsRecyclerAdapter.RecycleViewHolder holder, int position) {
        final MyCarsBean currentData = myCarsBeansList.get(position);
        holder.value.setText(currentData.getMyCarsName());
    }

    @Override
    public int getItemCount() {
        return myCarsBeansList.size();
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