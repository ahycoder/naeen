package needs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import ir.naeen.yousefi.R;

public class AdapterNeedCatBottomSheet extends RecyclerView.Adapter<AdapterNeedCatBottomSheet.ViewHolder> {
  public interface onItemNeedCatSelectedListener{
    void onItem(StructNeedsCategory item,int position);
  }
  private onItemNeedCatSelectedListener listener;
  private ArrayList<StructNeedsCategory> list;
  public AdapterNeedCatBottomSheet(ArrayList<StructNeedsCategory> list, onItemNeedCatSelectedListener listener) {
    this.list = list;
    this.listener=listener;
  }

  @Override
  public int getItemCount() {
    return list.size();
  }
  @Override
  public AdapterNeedCatBottomSheet.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_bottom_sheet_item,parent, false);
    AdapterNeedCatBottomSheet.ViewHolder viewHolder = new AdapterNeedCatBottomSheet.ViewHolder(view);

    return viewHolder;
  }




  @Override
  public void onBindViewHolder(AdapterNeedCatBottomSheet.ViewHolder holder, int position) {
    final StructNeedsCategory item = list.get(position);
    holder.txtItemCustomBottomSheet.setText(""+ item.name);
    holder.txtItemCustomBottomSheet.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v)
      {
        listener.onItem(item,position);
      }
    });

  }
  class ViewHolder extends RecyclerView.ViewHolder {
    private TextView txtItemCustomBottomSheet;
    private ImageView imgArrowtItemCustomBottomSheet;

    public ViewHolder(View view) {
      super(view);
      txtItemCustomBottomSheet =view.findViewById(R.id.txtItemCustomBottomSheet);
      imgArrowtItemCustomBottomSheet =view.findViewById(R.id.imgArrowtItemCustomBottomSheet);


    }
  }
  public void clear() {
    int size = list.size();
    list.clear();
    notifyItemRangeRemoved(0, size);
  }
}