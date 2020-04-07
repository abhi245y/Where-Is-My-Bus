package com.abhi.testingwhb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abhi.testingwhb.model.BusListModel;

import java.util.List;

public class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.ViewPager2ViewHolder>{
    private List<BusListModel> busListModelArrayList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    public ViewPager2Adapter(List<BusListModel> busListModelArrayList) {
        this.busListModelArrayList = busListModelArrayList;
    }

    @NonNull
    @Override
    public ViewPager2ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        ViewPager2ViewHolder vpv = new ViewPager2ViewHolder(v,mListener);
        return vpv;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPager2ViewHolder holder, int position) {
        holder.setViewPager2Data(busListModelArrayList.get(position));

    }

    @Override
    public int getItemCount() {
        return busListModelArrayList.size();
    }


    public static class ViewPager2ViewHolder extends RecyclerView.ViewHolder{

        TextView duration, distance, origin, destination, busnum, fair,interStops;

        public ViewPager2ViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            duration = itemView.findViewById(R.id.textViewDuration);
            distance = itemView.findViewById(R.id.textViewDistance);
            origin = itemView.findViewById(R.id.textViewOrigin);
            destination = itemView.findViewById(R.id.textViewDestination);
            busnum = itemView.findViewById(R.id.textViewBusNum);
            fair = itemView.findViewById(R.id.textViewFair);
            interStops=itemView.findViewById(R.id.interStops);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public void setViewPager2Data(BusListModel busListModel){

            duration.setText(busListModel.getDuration());
            distance.setText(busListModel.getDistance());
            origin.setText(busListModel.getOrigin());
            destination.setText(busListModel.getDestination());
            busnum.setText(busListModel.getBusNo());
            fair.setText(busListModel.getFair());
            interStops.setText(busListModel.getIntermediateStops());
        }
    }


}
