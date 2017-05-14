package com.example.newbiechen.ireader.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newbiechen.ireader.R;
import com.example.newbiechen.ireader.model.bean.BookDetailBean;
import com.example.newbiechen.ireader.model.bean.BookListBean;
import com.example.newbiechen.ireader.model.bean.CollBookBean;
import com.example.newbiechen.ireader.model.bean.HotCommentBean;
import com.example.newbiechen.ireader.model.local.CollBookManager;
import com.example.newbiechen.ireader.presenter.BookDetailPresenter;
import com.example.newbiechen.ireader.presenter.contract.BookDetailContract;
import com.example.newbiechen.ireader.ui.adapter.BookListAdapter;
import com.example.newbiechen.ireader.ui.adapter.HotCommentAdapter;
import com.example.newbiechen.ireader.ui.base.BaseRxActivity;
import com.example.newbiechen.ireader.utils.Constant;
import com.example.newbiechen.ireader.utils.StringUtils;
import com.example.newbiechen.ireader.widget.itemdecoration.DefaultItemDecoration;

import java.util.List;

import butterknife.BindView;

/**
 * Created by newbiechen on 17-5-4.
 */

public class BookDetailActivity extends BaseRxActivity<BookDetailContract.Presenter>
        implements BookDetailContract.View {
    private static final String TAG = "BookDetailActivity";
    private static final String EXTRA_BOOK_ID = "extra_book_id";

    @BindView(R.id.book_detail_iv_cover)
    ImageView mIvCover;
    @BindView(R.id.book_detail_tv_title)
    TextView mTvTitle;
    @BindView(R.id.book_detail_tv_author)
    TextView mTvAuthor;
    @BindView(R.id.book_detail_tv_type)
    TextView mTvType;
    @BindView(R.id.book_detail_tv_word_count)
    TextView mTvWordCount;
    @BindView(R.id.book_detail_tv_lately_update)
    TextView mTvLatelyUpdate;
    @BindView(R.id.book_list_cb_chase)
    CheckBox mCbChase;
    @BindView(R.id.book_detail_cb_read)
    CheckBox mCbRead;
    @BindView(R.id.book_detail_tv_follower_count)
    TextView mTvFollowerCount;
    @BindView(R.id.book_detail_tv_retention)
    TextView mTvRetention;
    @BindView(R.id.book_detail_tv_day_word_count)
    TextView mTvDayWordCount;
    @BindView(R.id.book_detail_tv_brief)
    TextView mTvBrief;
    @BindView(R.id.book_detail_tv_more_comment)
    TextView mTvMoreComment;
    @BindView(R.id.book_detail_rv_hot_comment)
    RecyclerView mRvHotComment;
    @BindView(R.id.book_detail_rv_community)
    RelativeLayout mRvCommunity;
    @BindView(R.id.book_detail_tv_community)
    TextView mTvCommunity;
    @BindView(R.id.book_detail_tv_posts_count)
    TextView mTvPostsCount;
    @BindView(R.id.book_list_tv_recommend_book_list)
    TextView mTvRecommendBookList;
    @BindView(R.id.book_detail_rv_recommend_book_list)
    RecyclerView mRvRecommendBookList;

    /************************************/
    private HotCommentAdapter mHotCommentAdapter;
    private BookListAdapter mBookListAdapter;
    private CollBookBean mCollBookBean;
    /*******************************************/
    private String mBookId;
    private boolean isBriefOpen = false;

    public static void startActivity(Context context,String bookId){
        Intent intent = new Intent(context, BookDetailActivity.class);
        intent.putExtra(EXTRA_BOOK_ID, bookId);
        context.startActivity(intent);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_book_detail;
    }

    @Override
    protected BookDetailContract.Presenter bindPresenter() {
        return new BookDetailPresenter();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        if (savedInstanceState != null){
            mBookId = savedInstanceState.getString(EXTRA_BOOK_ID);
        }
        else {
            mBookId = getIntent().getStringExtra(EXTRA_BOOK_ID);
        }
    }

    @Override
    protected void setUpToolbar(Toolbar toolbar) {
        super.setUpToolbar(toolbar);
        getSupportActionBar().setTitle("书籍详情");
    }

    @Override
    protected void initClick() {
        super.initClick();

        //可伸缩的TextView
        mTvBrief.setOnClickListener(
                (view) -> {
                    if (isBriefOpen){
                        mTvBrief.setMaxLines(4);
                        isBriefOpen = false;
                    }
                    else{
                        mTvBrief.setMaxLines(8);
                        isBriefOpen = true;
                    }
                }
        );

        mCbChase.setOnCheckedChangeListener(
                (btn,isChecked) ->{
                    //点击存储
                    if (isChecked){
                        mCollBookBean.setUpdate(isChecked);
                        CollBookManager.getInstance()
                                .saveCollBook(mCollBookBean);

                        mCbChase.setText(getResources().getString(R.string.nb_book_detail_give_up));

                        //修改背景
                        Drawable drawable = getResources().getDrawable(R.drawable.shape_common_gray_corner);
                        mCbChase.setBackground(drawable);
                        //设置图片
                        mCbChase.setCompoundDrawables(ContextCompat.getDrawable(this,R.drawable.ic_book_list_delete),null,
                                null,null);
                    }
                    else {
                        //放弃点击
                        mCollBookBean.setUpdate(isChecked);
                        CollBookManager.getInstance()
                                .deleteCollBook(mCollBookBean);

                        mCbChase.setText(getResources().getString(R.string.nb_book_detail_chase_update));

                        //修改背景
                        Drawable drawable = getResources().getDrawable(R.drawable.selector_btn_book_list);
                        mCbChase.setBackground(drawable);
                        //设置图片
                        mCbChase.setCompoundDrawables(ContextCompat.getDrawable(this,R.drawable.ic_book_list_add),null,
                                null,null);
                    }
                }
        );
    }

    @Override
    protected void processLogic() {
        super.processLogic();
        mPresenter.refreshBookDetail(mBookId);
    }

    @Override
    public void finishRefresh(BookDetailBean bean) {
        //封面
        Glide.with(this)
                .load(Constant.IMG_BASE_URL+bean.getCover())
                .placeholder(R.drawable.ic_book_loading)
                .error(R.drawable.ic_load_error)
                .centerCrop()
                .into(mIvCover);
        //书名
        mTvTitle.setText(bean.getTitle());
        //作者
        mTvAuthor.setText(bean.getAuthor());
        //类型
        mTvType.setText(bean.getMajorCate());

        //总字数
        mTvWordCount.setText(getResources().getString(R.string.nb_book_word,bean.getWordCount()/10000));
        //更新时间
        mTvLatelyUpdate.setText(StringUtils.dateConvert(bean.getUpdated(),Constant.FORMAT_BOOK_DATE));
        //追书人数
        mTvFollowerCount.setText(bean.getFollowerCount()+"");
        //存留率
        mTvRetention.setText(bean.getRetentionRatio()+"%");
        //日更字数
        mTvDayWordCount.setText(bean.getSerializeWordCount()+"");
        //简介
        mTvBrief.setText(bean.getLongIntro());
        //社区
        mTvCommunity.setText(getResources().getString(R.string.nb_book_detail_community, bean.getTitle()));
        //帖子数
        mTvPostsCount.setText(getResources().getString(R.string.nb_book_detail_posts_count,bean.getPostCount()));
        mCollBookBean = CollBookManager.getInstance().getCollBook(bean.get_id());
        //判断是否收藏
        if (mCollBookBean != null){
            mCbChase.setChecked(true);
        }
        else {
            mCollBookBean = bean.getCollBookBean();
        }
    }

    @Override
    public void finishHotComment(List<HotCommentBean> beans) {
        if (beans.isEmpty()) return;
        mHotCommentAdapter = new HotCommentAdapter();
        mRvHotComment.setLayoutManager(new LinearLayoutManager(this));
        mRvHotComment.addItemDecoration(new DefaultItemDecoration(this));
        mRvHotComment.setAdapter(mHotCommentAdapter);
        mHotCommentAdapter.addItems(beans);
    }

    @Override
    public void finishRecommendBookList(List<BookListBean> beans) {
        if (beans.isEmpty()){
            mTvRecommendBookList.setVisibility(View.GONE);
            return;
        }
        //推荐书单列表
        mBookListAdapter = new BookListAdapter();
        mRvRecommendBookList.setLayoutManager(new LinearLayoutManager(this));
        mRvRecommendBookList.addItemDecoration(new DefaultItemDecoration(this));
        mRvRecommendBookList.setAdapter(mBookListAdapter);
        mBookListAdapter.addItems(beans);
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {

    }

    /*******************************************************/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_BOOK_ID,mBookId);
    }
}
