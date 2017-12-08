package com.heizi.jtshop.block.maidan;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.heizi.jtshop.R;
import com.heizi.mycommon.adapter.CommonAdapter;
import com.heizi.mycommon.adapter.ViewHolderHelper;
import com.heizi.mycommon.utils.ImageFactory;
import com.heizi.mycommon.utils.Utils;
import com.heizi.mycommon.view.MyGridView;

import java.util.ArrayList;

/**
 * 产品标准尺码
 *
 * @date 2016-8-11 上午10:55:34
 * @description TODO
 */
public class ViewSize extends LinearLayout implements
        View.OnClickListener {
    private Activity mContext;
    private PopupWindow aPopuwindow;
    private View showView;
    private ImageView iv_btn_close, iv_reduce, iv_plus, iv_product;
    private TextView tv_num, tv_price, tv_kuncun;
    private Button btn_sure;
    private int num = 1;// 产品数量

    private String goods_id;

    // 尺码选择
    private MyGridView gridView;
    private CommonAdapter adapter;
    private ArrayList<ModelSize> listData = new ArrayList<>();

    String imgurl = "", goods_storage = "", price = "";//库存和图片地址、价格

    private ModelSize modelSize;

    /**
     * callback sizeid给diy页面
     */
    private ViewSizeCallback callback;

    public interface ViewSizeCallback {
        public void call(ModelSize modelSize, int num);

    }

    public void setCallback(ViewSizeCallback callback) {
        this.callback = callback;
    }

    public ViewSize(Activity context) {
        super(context);
        mContext = context;
    }

    public void dismiss() {
        Utils.closeInputMethod(mContext, iv_btn_close);
        aPopuwindow.dismiss();
    }

    public void setData(ArrayList<ModelSize> list) {
        if (listData.size() == 0)
            listData.addAll(list);
    }


    public int getCount() {
        return listData.size();
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    // 从底部浮出
    public void ShowPop1(View v) {
        showView = ((LayoutInflater) mContext
                .getSystemService(mContext.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.pw_select_size, null);
        aPopuwindow = new PopupWindow(showView,
                LayoutParams.FILL_PARENT,
                LayoutParams.MATCH_PARENT, true);
        aPopuwindow.setAnimationStyle(R.style.popwin_anim_style);
        // 使其聚集 ，要想监听菜单里控件的事件就必须要调用此方法
        aPopuwindow.setFocusable(true);
        // 设置允许在外点击消失
        aPopuwindow.setOutsideTouchable(false);
        // 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        aPopuwindow.setBackgroundDrawable(getResources().getDrawable(
                R.mipmap.prompt_popupwindow_bg));
        // 软键盘不会挡着popupwindow
        aPopuwindow
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 设置菜单显示的位置
        aPopuwindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        iv_btn_close = (ImageView) showView.findViewById(R.id.iv_btn_close);
        iv_btn_close.setOnClickListener(this);
        iv_reduce = (ImageView) showView.findViewById(R.id.iv_reduce);
        iv_reduce.setOnClickListener(this);
        iv_plus = (ImageView) showView.findViewById(R.id.iv_plus);
        iv_plus.setOnClickListener(this);
        btn_sure = (Button) showView.findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(this);
        gridView = (MyGridView) showView.findViewById(R.id.gridView);
        tv_num = (TextView) showView.findViewById(R.id.tv_num);
        tv_num.setText(num + "");
        tv_price = (TextView) showView.findViewById(R.id.tv_price);
        tv_kuncun = (TextView) showView.findViewById(R.id.tv_kuncun);
        iv_product = (ImageView) showView.findViewById(R.id.iv_product);

        adapter = new CommonAdapter(mContext, listData, R.layout.item_size) {
            @Override
            public void getView(int position, ViewHolderHelper holder) {
                TextView tv_size = holder.findViewById(R.id.tv_size);
                tv_size.setText(listData.get(position).getValue());
                if (listData.get(position).getState() == 0) {
                    tv_size.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_gray));
                    tv_size.setTextColor(getResources().getColor(R.color.black2));
                } else {
                    tv_size.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_main));
                    tv_size.setTextColor(getResources().getColor(R.color.white));
                }
            }
        };
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < listData.size(); i++) {
                    if (i == position) {
                        modelSize = listData.get(i);
                        modelSize.setState(1);
                    } else {
                        listData.get(i).setState(0);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        if (listData.size() == 0) {
            getData();
        } else {
            gridView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            measureListHeight();
        }

        tv_price.setText(price);
        tv_kuncun.setText("库存: " + goods_storage);
        ImageFactory.displayImage(imgurl, iv_product, 0, 0);


    }

    private void getData() {
        geneItems();
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        measureListHeight();
    }

    private void geneItems() {
        for (int i = 0; i != 10; ++i) {
            ModelSize modelSize = new ModelSize();
            modelSize.setId(i);
            modelSize.setValue("Test XScrollView item " + (i));
            listData.add(modelSize);
        }
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.iv_btn_close:
                dismiss();
                break;
            case R.id.iv_reduce:
                if (num != 1) {
                    --num;
                    tv_num.setText(num + "");
                }
                break;
            case R.id.iv_plus:
                ++num;
                tv_num.setText(num + "");
                break;
            case R.id.btn_sure:
                if (modelSize == null) {
                    Utils.toastShow(mContext, "请选择尺码");
                } else if (num == 0) {
                    Utils.toastShow(mContext, "请选择要购买的数量");
                } else {
                    // call给diy页面
                    if (callback != null) {
                        callback.call(modelSize, num);
                    }
                    dismiss();
                }
                break;
            default:
                break;
        }
    }

    public String getGoods_storage() {
        return goods_storage;
    }

    public void setGoods_storage(String goods_storage) {
        this.goods_storage = goods_storage;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    protected void measureListHeight() {
//        ListAdapter listAdapter = gridView.getAdapter();
//        if (listAdapter == null) {
//            return;
//        }
//        // 固定列宽，有多少列
//        int numColumns = 2; //5
//        int totalHeight = 0;
//        // 计算每一列的高度之和
//        for (int i = 0; i < listAdapter.getCount(); i += numColumns) {
//            // 获取gridview的每一个item
//            View listItem = listAdapter.getView(i, null, gridView);
//            listItem.measure(0, 0);
//            // 获取item的高度和
//            totalHeight += listItem.getMeasuredHeight();
//        }
//        // 获取gridview的布局参数
//        ViewGroup.LayoutParams params = gridView.getLayoutParams();
//        params.height = totalHeight;
//        gridView.setLayoutParams(params);
    }
}
