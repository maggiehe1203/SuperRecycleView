package com.gwb.superrecycleview.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gwb.superrecycleview.R;
import com.gwb.superrecycleview.entity.GoodsPropertyBean;
import com.gwb.superrecycleview.ui.wedgit.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by ${GongWenbo} on 2018/3/30 0030.
 */

public class GoodsPropertyAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG          = "GoodsPropertyAdapter";
    private final        String COLOR_SELECT = "#ffffff";
    private final        String COLOR_EMPTY  = "#BBBBBB";
    private final        String COLOR_NORMAL = "#6D6D6D";

    private List<GoodsPropertyBean.AttributesBean> mAttributes;
    private List<GoodsPropertyBean.StockGoodsBean> mStockGoods;
    private Context                                mContext;
    private int                                    layoutId;
    private TextView[][]                           mTextViews;
    private HashMap<Integer, String>              sam  = new HashMap<>();
    private SimpleArrayMap<Integer, List<String>> sams = new SimpleArrayMap<>();
    private int index = 0;

    public GoodsPropertyAdapter(List<GoodsPropertyBean.AttributesBean> attributes, List<GoodsPropertyBean.StockGoodsBean> stockGoods, Context context, @LayoutRes int layoutId) {
        this.mAttributes = attributes;
        this.mStockGoods = stockGoods;
        this.mContext = context;
        this.layoutId = layoutId;
        int size = attributes.size();
        mTextViews = new TextView[size][0];
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return BaseViewHolder.createViewHolder(mContext, parent, layoutId);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        GoodsPropertyBean.AttributesBean attributesBean = mAttributes.get(position);
        // 标题
        holder.setTitle(R.id.tv_title, attributesBean.getTabName());
        // 一行具体的view
        FlowLayout flowLayout = holder.getView(R.id.flowLayout);
        List<String> attributesItem = attributesBean.getAttributesItem();
        int size = attributesItem.size();
        TextView[] textViews = new TextView[size];
        for (int i = 0; i < attributesItem.size(); i++) {
            final String property = attributesItem.get(i);
            TextView textView = getTextView(property, holder);
            flowLayout.addView(textView);
            textViews[i] = textView;
        }
        mTextViews[position] = textViews;

    }

    public TextView getTextView(final String title, final BaseViewHolder holder) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView tv = new TextView(mContext);
        lp.setMargins(10, 10, 10, 10);
        tv.setPadding(40, 20, 40, 20);
        tv.setBackgroundResource(R.drawable.normal);
        tv.setTextColor(Color.parseColor(COLOR_NORMAL));
        tv.setLayoutParams(lp);
        tv.setText(title);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getLayoutPosition();
                Log.d(TAG, "position== " + position);
                String str = sam.get(position);
                // TODO: 2018/5/6 如果点击的时候同一个,不做处理
                if (!TextUtils.isEmpty(str)) {
                    if (str.equals(title)) {
                        return;
                    } else {
                        sam.clear();
                        sam.put(position, title);
                    }
                } else {
                    List<String> list = sams.get(position);
                    if (list != null && !list.contains(title)) {
                        sam.clear();
                    }
                    sam.put(position, title);
                }

                // TODO: 2018/5/6 每一行的属性容器
                for (int i = 0; i < mAttributes.size(); i++) {
                    List<String> list = new ArrayList<>();
                    sams.put(i, list);
                }
                // TODO: 2018/5/6 每一行有的属性
                for (int i = 0; i < mStockGoods.size(); i++) {
                    GoodsPropertyBean.StockGoodsBean stockGoodsBean = mStockGoods.get(i);
                    List<GoodsPropertyBean.StockGoodsBean.GoodsInfoBean> goodsInfo = stockGoodsBean.getGoodsInfo();
                    boolean flag = false;
                    for (int j = 0; j < goodsInfo.size(); j++) {
                        GoodsPropertyBean.StockGoodsBean.GoodsInfoBean goodsInfoBean = goodsInfo.get(j);
                        String tabValue = goodsInfoBean.getTabValue();
                        List<String> list = sams.get(j);
                        Set<Integer> keySet = sam.keySet();
                        Iterator<Integer> iterator = keySet.iterator();
                        while (iterator.hasNext()) {
                            Integer key = iterator.next();
                            String arr = sam.get(key);
                            // TODO: 2018/5/7 0007 如果是选中的行，看改行可选的，如果是没选中的，那就看附和所有条件的
                            if (key == j) {
                                if (!list.contains(tabValue)) {
                                    list.add(tabValue);
                                }
                                if (arr.equals(tabValue)) {
                                    index++;
                                }
                            }
                        }
                        if (index == keySet.size()) {
                            flag = true;
                        }
                    }
                    index = 0;
                    if (flag) {
                        for (int j = 0; j < goodsInfo.size(); j++) {
                            String tabValue = goodsInfo.get(j).getTabValue();
                            List<String> list = sams.get(j);
                            if (!list.contains(tabValue)) {
                                list.add(tabValue);
                            }
                        }
                        flag = false;
                    }
                }
                // TODO: 2018/5/6 根据商品的状态绘制
                for (int i = 0; i < mTextViews.length; i++) {
                    List<String> list = sams.get(i);
                    // 之前选中的
                    String select = sam.get(i);
                    TextView[] textViews = mTextViews[i];
                    for (TextView textView : textViews) {
                        String title = textView.getText().toString();
                        if (!TextUtils.isEmpty(select) && select.equals(title)) {

                        } else if (list.contains(title)) {
                            //                            textView.setEnabled(true);
                            textView.setBackgroundResource(R.drawable.normal);
                            textView.setTextColor(Color.parseColor(COLOR_NORMAL));
                        } else {
                            //                            textView.setEnabled(false);
                            textView.setBackgroundResource(R.drawable.empty);
                            textView.setTextColor(Color.parseColor(COLOR_EMPTY));
                        }
                    }
                }

                Log.d(TAG, "onClick: sam" + sam.toString());
                Log.d(TAG, "onClick: sams" + sams.toString());

                tv.setBackgroundResource(R.drawable.select);
                tv.setTextColor(Color.parseColor(COLOR_SELECT));
            }
        });
        return tv;
    }

    @Override
    public int getItemCount() {
        return mAttributes == null ? 0 : mAttributes.size();
    }

}
