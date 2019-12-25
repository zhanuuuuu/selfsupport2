package com.hlyf.selfsupport.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hlyf.selfsupport.R;
import com.hlyf.selfsupport.customui.CommonDialog;
import com.hlyf.selfsupport.domin.SysnLog;

import java.util.ArrayList;
import java.util.List;

public class SysnLogAdapter extends BaseAdapter {
    private Context context;
    List<SysnLog> SysnLogList=new ArrayList<>();
    private SysnLogListenr sysnLogListenr;

    public void setSysnLogListenr(SysnLogListenr sysnLogListenr) {
        this.sysnLogListenr = sysnLogListenr;
    }

    public SysnLogAdapter(Context context, List<SysnLog> sysnLogList) {
        this.context = context;
        SysnLogList = sysnLogList;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<SysnLog> getSysnLogList() {
        return SysnLogList;
    }

    public void setSysnLogList(List<SysnLog> sysnLogList) {
        if(sysnLogList!=null){
            SysnLogList.addAll(sysnLogList);
            //这里刷新数据
            notifyDataSetChanged();
        }


    }

    public void setSysnLogList2(List<SysnLog> sysnLogList) {
        if(sysnLogList!=null){
            SysnLogList.clear();
            SysnLogList.addAll(sysnLogList);
            //这里刷新数据
            notifyDataSetChanged();
        }


    }

    @Override
    public int getCount() {
        return SysnLogList==null ? 0:SysnLogList.size();
    }

    @Override
    public Object getItem(int position) {
        return SysnLogList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.goodsitemlog, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //设置数据
        final SysnLog sysnLog=SysnLogList.get(position);
        holder.tv_orderno.setText("订单编号:"+sysnLog.getMerchantOrderId());
        holder.tv_allNmber.setText("总件数: "+sysnLog.getNumber()+" 件");
        holder.tv_money.setText(sysnLog.getMoney());
        holder.tv_time.setText(sysnLog.getCreateTime());

        holder.bt_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CommonDialog dialog = new CommonDialog(context);
                dialog.setMessage("确认重新打印该订单？")
                        //.setImageResId(R.mipmap.ic_launcher)
               .setTitle("提示")
                        .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                    @Override
                    public void onPositiveClick() {
                        dialog.dismiss();
                        if(sysnLogListenr!=null)
                            sysnLogListenr.print(position,holder.tv_orderno,sysnLog);
                    }

                    @Override
                    public void onNegtiveClick() {
                        dialog.dismiss();

                    }
                }).show();
            }
        });
        return convertView;
    }

    class ViewHolder {

        private TextView tv_orderno;
        private TextView tv_allNmber;
        private TextView tv_money;
        private TextView tv_time;
        private Button bt_print;

        public ViewHolder(View itemView) {

            tv_orderno = (TextView)itemView.findViewById( R.id.tv_orderno );
            tv_allNmber = (TextView)itemView.findViewById( R.id.tv_allNmber );
            tv_money = (TextView)itemView.findViewById( R.id.tv_money );
            tv_time = (TextView)itemView.findViewById( R.id.tv_time );
            bt_print = (Button) itemView.findViewById( R.id.bt_print );

        }
    }

    public interface SysnLogListenr {

        /**
         * <pre>
         *     用于打印的
         * </pre>
         * @param position
         * @param view
         */
        void print(int position, View view,SysnLog sysnLog);

    }
}
