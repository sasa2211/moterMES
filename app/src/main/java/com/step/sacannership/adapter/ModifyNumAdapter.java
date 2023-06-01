package com.step.sacannership.adapter;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.step.sacannership.R;
import com.step.sacannership.model.bean.DeliveryDdataBean;
import com.step.sacannership.model.bean.DeliveryDetailBean;
import java.util.List;

public class ModifyNumAdapter extends BaseAdapter {
    private List<DeliveryDetailBean> datas;
    private Context context;
    private ModifyEditNum modifyView;

    public void setModifyView(ModifyEditNum modifyView) {
        this.modifyView = modifyView;
    }

    public ModifyNumAdapter(List<DeliveryDetailBean> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    private String data = "";

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public DeliveryDetailBean getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ModifyHolder holder;
        if (view == null){
            holder = new ModifyHolder();
            view = LayoutInflater.from(context).inflate(R.layout.modifynum_item, viewGroup, false);
            holder.materialName = view.findViewById(R.id.material_name);
            holder.materialNo = view.findViewById(R.id.material_no);
            holder.tvData = view.findViewById(R.id.tv_data);
            holder.editNum = view.findViewById(R.id.edit_num);
            view.setTag(holder);
        }else {
            holder = (ModifyHolder) view.getTag();
        }
        DeliveryDetailBean detailBean = datas.get(position);
        holder.materialNo.setText("物料编号："+detailBean.getMaterialNo());
        holder.materialName.setText("物料名称："+detailBean.getMaterialName());
        holder.tvData.setText("提交时间："+data);
        holder.editNum.setText(String.valueOf(detailBean.getQuantity()));
        holder.editNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(context)
                        .setTitle("编辑数量")
                        .setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL)
                        .setPlaceholder("编辑");
                builder.addAction("取消", (dialog, index) -> dialog.dismiss())
                        .addAction("确定", (dialog, index) -> {
                            try {
                                EditText editText = builder.getEditText();
                                modifyView.modifyNum(position, editText.getText().toString().trim());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                        });
                builder.create().show();
            }
        });

        return view;
    }
    public class ModifyHolder{
        public TextView materialName;
        public TextView materialNo;
        public TextView tvData;
        public TextView editNum;
    }
    public interface ModifyEditNum{
        void modifyNum(int position, String text);
    }
}
