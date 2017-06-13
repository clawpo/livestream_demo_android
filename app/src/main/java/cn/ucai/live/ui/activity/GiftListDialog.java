package cn.ucai.live.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.ucai.live.I;
import cn.ucai.live.LiveHelper;
import cn.ucai.live.R;
import cn.ucai.live.data.model.Gift;
import cn.ucai.live.utils.L;

/**
 * Created by wei on 2017/3/3.
 */

public class GiftListDialog extends DialogFragment {
    private static final String TAG = "GiftListDialog";
    @BindView(R.id.rv_gift)
    RecyclerView mRvGift;
    @BindView(R.id.tv_my_bill)
    TextView mTvMyBill;
    @BindView(R.id.tv_recharge)
    TextView mTvRecharge;
    Unbinder bind;
    GiftAdapter adapter;
    View.OnClickListener mOnClickListener;

    public GiftListDialog() {
    }

//    public RoomUserManagementDialog(String chatroomId){
//        this.chatroomId = chatroomId;
//    }

    public static GiftListDialog newInstance() {
        GiftListDialog dialog = new GiftListDialog();
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gift_list, container, false);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        bind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<Gift> giftList = LiveHelper.getInstance().getGiftLists();
        L.e(TAG,"onActivityCreated,giftList="+giftList.size());
        if (giftList.size() > 0) {
            initView();
            initData(giftList);
        } else {
            //download gift list data
            LiveHelper.getInstance().getGiftListFromServer();
        }
    }

    public void setOnClickListener(View.OnClickListener listener){
        mOnClickListener = listener;
    }

    private void initView() {
        GridLayoutManager gm = new GridLayoutManager(getContext(), I.GIFT_COLUMN_COUNT);
        mRvGift.setLayoutManager(gm);
    }

    private void initData(List<Gift> giftList) {
        if (adapter==null) {
            adapter = new GiftAdapter(getContext(), giftList);
            mRvGift.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bind != null) {
            bind.unbind();
        }
    }

    class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.GiftViewHolder> {
        Context mContext;
        List<Gift> mlist;

        public GiftAdapter(Context context, List<Gift> mlist) {
            mContext = context;
            this.mlist = mlist;
        }

        @Override
        public GiftViewHolder onCreateViewHolder(ViewGroup parent, int viewTpe) {
            GiftViewHolder holder = new GiftViewHolder(View.inflate(mContext, R.layout.item_gift, null));
            return holder;
        }

        @Override
        public void onBindViewHolder(GiftViewHolder holder, int position) {
            Gift gift = mlist.get(position);
            if (gift!=null){
                holder.mTvGiftName.setText(gift.getGname());
                holder.mTvGiftPrice.setText("￥ "+gift.getGprice());
                EaseUserUtils.setAppGift(mContext,gift.getGurl(),holder.mIvGiftThumb);
                holder.itemView.setTag(gift.getId());
                holder.itemView.setOnClickListener(mOnClickListener);
            }
        }

        @Override
        public int getItemCount() {
            return mlist.size();
        }

        class GiftViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.ivGiftThumb)
            ImageView mIvGiftThumb;
            @BindView(R.id.tvGiftName)
            TextView mTvGiftName;
            @BindView(R.id.tvGiftPrice)
            TextView mTvGiftPrice;
            @BindView(R.id.layout_gift)
            LinearLayout mLayoutGift;

            GiftViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
