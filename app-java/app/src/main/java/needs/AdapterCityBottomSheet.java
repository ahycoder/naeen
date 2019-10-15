package needs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import ir.naeen.yousefi.R;

public class AdapterCityBottomSheet extends RecyclerView.Adapter<AdapterCityBottomSheet.ViewHolder> {
  public interface onItemSelectedListener{
    void onItem(StructCity item);
  }
  private onItemSelectedListener listener;
  private ArrayList<StructCity> list;
  public AdapterCityBottomSheet(ArrayList<StructCity> list, onItemSelectedListener listener) {
    this.list = list;
    this.listener=listener;
  }

  @Override
  public int getItemCount() {
    return list.size();
  }
  @Override
  public AdapterCityBottomSheet.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_bottom_sheet_item,parent, false);
    AdapterCityBottomSheet.ViewHolder viewHolder = new AdapterCityBottomSheet.ViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(AdapterCityBottomSheet.ViewHolder holder, int position) {
    final StructCity item = list.get(position);
    holder.txtItemCustomBottomSheet.setText(""+ item.name);
    holder.txtItemCustomBottomSheet.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        listener.onItem(item);
      }
    });

  }
  class ViewHolder extends RecyclerView.ViewHolder {
    private TextView txtItemCustomBottomSheet;

    public ViewHolder(View view) {
      super(view);
      txtItemCustomBottomSheet =view.findViewById(R.id.txtItemCustomBottomSheet);

    }
  }
}