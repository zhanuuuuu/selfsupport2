package com.hlyf.selfsupport.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hlyf.selfsupport.R;
import com.hlyf.selfsupport.domin.SMGGoodsInfo;

import java.util.List;


public class ShoppingCartAdapter extends BaseAdapter {

    private List<SMGGoodsInfo> shoppingCartBeanList;

    private Context context;

    private ModifyCountInterface modifyCountInterface;

    public ShoppingCartAdapter(Context context) {
        this.context = context;
    }

    public ShoppingCartAdapter(List<SMGGoodsInfo> shoppingCartBeanList, Context context) {
        this.shoppingCartBeanList = shoppingCartBeanList;
        this.context = context;

    }

    public void setShoppingCartBeanList(List<SMGGoodsInfo> shoppingCartBeanList) {
        this.shoppingCartBeanList = shoppingCartBeanList;
        notifyDataSetChanged();
    }

    public void setModifyCountInterface(ModifyCountInterface modifyCountInterface) {
        this.modifyCountInterface = modifyCountInterface;
    }

    @Override
    public int getCount() {
        return shoppingCartBeanList == null ? 0 : shoppingCartBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return shoppingCartBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.goodsitem, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final SMGGoodsInfo shoppingCartBean = shoppingCartBeanList.get(position);
        if(position==0){
            holder.LayoutGoodsItem.setBackground(context.getResources().getDrawable(R.drawable.bg_shadow_corners_rectangle));
            holder.itemLeftDivider.setVisibility(View.VISIBLE);
        }else{
            holder.LayoutGoodsItem.setBackground(context.getResources().getDrawable(R.drawable.bg_shadow_corners_rectangle_whith));
            holder.itemLeftDivider.setVisibility(View.GONE);
        }
        holder.itemGoodName.setText(shoppingCartBean.getcGoodsName());
        holder.itemGoodPrice.setText("$ "+(int)(1+Math.random()*(10-1+1)));
        holder.itemGoodNum.setText(""+shoppingCartBean.getQty());
        holder.itemGoodMoney.setText("$ "+(int)(1+Math.random()*(10-1+1)));

        //增加按钮
        holder.itemGoodAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modifyCountInterface!=null)
                modifyCountInterface.doIncrease(position, holder.itemGoodNum);//暴露增加接口
            }
        });

        //删减按钮
        holder.itemGoodDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modifyCountInterface!=null)
                modifyCountInterface.doDecrease(position, holder.itemGoodNum);//暴露删减接口
            }
        });
        //删除弹窗
        holder.itemRightClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alert = new AlertDialog.Builder(new ContextThemeWrapper(context,R.style.AlertDialogCustom)).create();
                alert.setTitle("操作提示");
                alert.setMessage("您确定要将这些商品从购物车中移除吗？");
                alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(modifyCountInterface!=null)
                                modifyCountInterface.childDelete(position);//删除 目前只是从item中移除
                            }
                        });
                alert.show();
            }
        });

        return convertView;
    }


    //初始化控件
    class ViewHolder {

        private RelativeLayout LayoutGoodsItem;
        private LinearLayout itemRightClear;
        private LinearLayout itemLeftDivider;
        private TextView itemGoodName;
        private TextView itemGoodPrice;
        private TextView itemGoodDelete;
        private TextView itemGoodNum;
        private TextView itemGoodAdd;
        private TextView itemGoodMoney;

        public ViewHolder(View itemView) {
            LayoutGoodsItem = (RelativeLayout)itemView.findViewById( R.id.Layout_goods_item );
            itemRightClear = (LinearLayout)itemView.findViewById( R.id.item_right_clear );
            itemLeftDivider = (LinearLayout)itemView.findViewById( R.id.item_left_divider );
            itemGoodName = (TextView)itemView.findViewById( R.id.item_good_name );
            itemGoodPrice = (TextView)itemView.findViewById( R.id.item_good_price );
            itemGoodDelete = (TextView)itemView.findViewById( R.id.item_good_delete );
            itemGoodNum = (TextView)itemView.findViewById( R.id.item_good_num );
            itemGoodAdd = (TextView)itemView.findViewById( R.id.item_good_add );
            itemGoodMoney = (TextView)itemView.findViewById( R.id.item_good_money );
        }
    }

    public interface ModifyCountInterface {
        /**
         * 增加操作
         *
         * @param position      元素位置
         * @param showCountView 用于展示变化后数量的View
         */
        void doIncrease(int position, View showCountView);

        /**
         * 删减操作
         *
         * @param position      元素位置
         * @param showCountView 用于展示变化后数量的View
         */
        void doDecrease(int position, View showCountView);

        /**
         * 删除子item
         *
         * @param position
         */
        void childDelete(int position);
    }
}
