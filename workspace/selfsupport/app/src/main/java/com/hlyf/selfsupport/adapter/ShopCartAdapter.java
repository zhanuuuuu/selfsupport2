package com.hlyf.selfsupport.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hlyf.selfsupport.R;
import com.hlyf.selfsupport.customui.CommonDialog;
import com.hlyf.selfsupport.domin.BLBGoodsInfo;
import com.hlyf.selfsupport.domin.SMGGoodsInfo;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;


public class ShopCartAdapter extends BaseAdapter {

    private List<BLBGoodsInfo> shoppingCartBeanList;

    private Context context;

    private ModifyCountInterface modifyCountInterface;

    public ShopCartAdapter(Context context) {
        this.context = context;
    }

    public ShopCartAdapter(List<BLBGoodsInfo> shoppingCartBeanList, Context context) {
        this.shoppingCartBeanList = shoppingCartBeanList;
        this.context = context;

    }

    public void setShoppingCartBeanList(List<BLBGoodsInfo> shoppingCartBeanList) {
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
        final BLBGoodsInfo shoppingCartBean = shoppingCartBeanList.get(position);
        //判断是否选中
        if(shoppingCartBean.getSelect()){
            holder.LayoutGoodsItem.setBackground(context.getResources().getDrawable(R.drawable.bg_shadow_corners_rectangle));
            holder.itemLeftDivider.setVisibility(View.VISIBLE);
        }else{
            holder.LayoutGoodsItem.setBackground(context.getResources().getDrawable(R.drawable.bg_shadow_corners_rectangle_whith));
            holder.itemLeftDivider.setVisibility(View.GONE);
        }
        //判断是否是称重
        if(shoppingCartBean.getIsWeight()){
            holder.shopitem_edit.setVisibility(View.GONE);
            holder.shopitem_edit_wight.setVisibility(View.VISIBLE);
            holder.tv_weight.setText(""+shoppingCartBean.getWeight()+"kg");
        }else {
            holder.shopitem_edit.setVisibility(View.VISIBLE);
            holder.shopitem_edit_wight.setVisibility(View.GONE);
            holder.itemGoodNum.setText(""+shoppingCartBean.getQty());
        }
        holder.itemGoodName.setText(shoppingCartBean.getName());
        holder.itemGoodPrice.setText("¥ "+new BigDecimal(shoppingCartBean.getBasePrice())
                .divide(new BigDecimal(100),2,BigDecimal.ROUND_UP)+"/"+shoppingCartBean.getUnit());
        holder.itemGoodMoney.setText("¥ "+
                new BigDecimal(shoppingCartBean.getAmount()-shoppingCartBean.getDiscountAmount())
        .divide(new BigDecimal(100),2,BigDecimal.ROUND_UP));

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

                final CommonDialog dialog = new CommonDialog(context);
                dialog.setMessage("您确定要将这些商品从购物车中移除吗？")
                        .setImageResId(R.drawable.x)
                        //.setTitle("提示")
                        .setSingle(false).setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                    @Override
                    public void onPositiveClick() {
                        dialog.dismiss();
                        if(modifyCountInterface!=null)
                            modifyCountInterface.childDelete(position);//删除 目前只是从item中移除
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



    //初始化控件
    class ViewHolder {

        private RelativeLayout LayoutGoodsItem;
        private LinearLayout itemRightClear;
        private LinearLayout itemLeftDivider;
        private LinearLayout shopitem_edit;
        private TextView itemGoodName;
        private TextView itemGoodPrice;
        private TextView itemGoodDelete;
        private TextView itemGoodNum;
        private TextView itemGoodAdd;
        private TextView itemGoodMoney;

        private LinearLayout shopitem_edit_wight;
        private TextView tv_weight;

        public ViewHolder(View itemView) {
            shopitem_edit_wight= (LinearLayout)itemView.findViewById( R.id.shopitem_edit_wight );
            tv_weight=(TextView)itemView.findViewById( R.id.tv_weight );
            shopitem_edit= (LinearLayout)itemView.findViewById( R.id.shopitem_edit );
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
