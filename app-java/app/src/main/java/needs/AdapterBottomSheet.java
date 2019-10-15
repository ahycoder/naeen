package needs;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import ir.naeen.yousefi.R;

public class AdapterBottomSheet extends RecyclerView.Adapter<AdapterBottomSheet.ViewHolder> {
  public interface onItemSelectedListener{
    void onItem(String item);
  }
  private onItemSelectedListener listener;
  private ArrayList<String> list;
  public AdapterBottomSheet(ArrayList<String> list, onItemSelectedListener listener) {
    this.list = list;
    this.listener=listener;
  }

  @Override
  public int getItemCount() {
    Log.i("TAG", "list.size() : " + list.size());
    return list.size();
  }
  @Override
  public AdapterBottomSheet.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_bottom_sheet_item,parent, false);
    AdapterBottomSheet.ViewHolder viewHolder = new AdapterBottomSheet.ViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(AdapterBottomSheet.ViewHolder holder, int position) {
    final String item = list.get(position);
    holder.txtItemCustomBottomSheet.setText(""+ item);
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