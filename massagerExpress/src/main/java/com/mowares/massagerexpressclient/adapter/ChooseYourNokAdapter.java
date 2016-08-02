package com.mowares.massagerexpressclient.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mowares.massagerexpressclient.ChooseYourNok;
import com.mowares.massagerexpressclient.ListService;
import com.mowares.massagerexpressclient.R;
import com.mowares.massagerexpressclient.models.MyThings;
import com.mowares.massagerexpressclient.utils.AndyUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;

/**
 * Created by mobilefirst on 11/3/16.
 */
public class ChooseYourNokAdapter extends RecyclerView.Adapter<ChooseYourNokAdapter.ViewHolder> {
    public DisplayImageOptions options;

    private ArrayList<MyThings> listMakeup = new ArrayList<MyThings>();
    private ArrayList<MyThings> listHairs = new ArrayList<MyThings>();
    private ArrayList<MyThings> listNails = new ArrayList<MyThings>();
    protected ArrayList<MyThings> subTypeMakeUp = new ArrayList<>();
    protected ArrayList<MyThings> subTypeHairs = new ArrayList<>();
    protected ArrayList<MyThings> subTypeNails = new ArrayList<>();
    private Context mContext;
    private int adapterLength;

    public ChooseYourNokAdapter(ChooseYourNok chooseYourNok,
                                ArrayList<MyThings> listWalkerMakeup,
                                ArrayList<MyThings> listWalkerHairs,
                                ArrayList<MyThings> listWalkerNails, ArrayList<MyThings> subtypeMakeup,
                                ArrayList<MyThings> subtypeHairs,
                                ArrayList<MyThings> subtypeNails, int adapterLength) {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        this.listMakeup = listWalkerMakeup;
        this.listHairs = listWalkerHairs;
        this.listNails = listWalkerNails;
        this.subTypeMakeUp = subtypeMakeup;
        this.subTypeHairs = subtypeHairs;
        this.subTypeNails = subtypeNails;
        this.mContext = chooseYourNok;
        this.adapterLength = adapterLength;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.choose_your_noke_raw_items, viewGroup, false);

        //  mContext.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (position == 0) {
            //pos ==0
            if (listMakeup.size() > 0) {
                if (!TextUtils.isEmpty(listMakeup.get(0).WALKER_FGIMAGE)) {
                    AndyUtils.LoadImageView(listMakeup.get(0).WALKER_FGIMAGE, viewHolder.ivCenter, options);
                    viewHolder.ivCenter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // subTypeMakeUp
                            Intent listServices = new Intent(mContext.getApplicationContext(), ListService.class);
                            listServices.putExtra("subTypeMakeup", subTypeMakeUp);

                            listServices.putExtra("check_array", 1);
                            mContext.startActivity(listServices);
                        }
                    });


                }
                if (!TextUtils.isEmpty((listMakeup.get(0).WALKER_BGIMAGE))) {
                    AndyUtils.LoadImageView(listMakeup.get(0).WALKER_BGIMAGE, viewHolder.ivBase, options);

                }
            } else if (listNails.size() > 0) {
                if (!TextUtils.isEmpty(listNails.get(0).WALKER_FGIMAGE)) {
                    AndyUtils.LoadImageView(listNails.get(0).WALKER_FGIMAGE, viewHolder.ivCenter, options);
                    viewHolder.ivCenter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // subTypeNails
                            Intent listServices = new Intent(mContext.getApplicationContext(), ListService.class);
                            listServices.putExtra("subTypeNails", subTypeNails);
                            listServices.putExtra("check_array", 2);
                            mContext.startActivity(listServices);
                        }
                    });
                }
                if (!TextUtils.isEmpty((listNails.get(0).WALKER_BGIMAGE))) {
                    AndyUtils.LoadImageView(listNails.get(0).WALKER_BGIMAGE, viewHolder.ivBase, options);
                }
            } else if (listHairs.size() > 0) {
                if (!TextUtils.isEmpty(listHairs.get(0).WALKER_FGIMAGE)) {
                    AndyUtils.LoadImageView(listHairs.get(0).WALKER_FGIMAGE, viewHolder.ivCenter, options);
                    viewHolder.ivCenter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // subTypeHairs
                            Intent listServices = new Intent(mContext.getApplicationContext(), ListService.class);
                            listServices.putExtra("subTypeHairs", subTypeHairs);
                            listServices.putExtra("check_array", 3);
                            mContext.startActivity(listServices);
                        }
                    });
                }
                if (!TextUtils.isEmpty((listHairs.get(0).WALKER_BGIMAGE))) {
                    AndyUtils.LoadImageView(listHairs.get(0).WALKER_BGIMAGE, viewHolder.ivBase, options);
                }
            }
        }//else data set
        if (position == 1) {
            if (listMakeup.size() > 0) {
                if (listNails.size() > 0) {

                    if (!TextUtils.isEmpty(listNails.get(0).WALKER_FGIMAGE)) {
                        AndyUtils.LoadImageView(listNails.get(0).WALKER_FGIMAGE, viewHolder.ivCenter, options);
                        viewHolder.ivCenter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // subTypeNails
                                Intent listServices = new Intent(mContext.getApplicationContext(), ListService.class);
                                listServices.putExtra("subTypeNails", subTypeNails);
                                listServices.putExtra("check_array", 2);
                                mContext.startActivity(listServices);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty((listNails.get(0).WALKER_BGIMAGE))) {
                        AndyUtils.LoadImageView(listNails.get(0).WALKER_BGIMAGE, viewHolder.ivBase, options);
                    }
                } else {
                    if (listHairs.size() > 0) {
                        if (!TextUtils.isEmpty(listHairs.get(0).WALKER_FGIMAGE)) {
                            AndyUtils.LoadImageView(listHairs.get(0).WALKER_FGIMAGE, viewHolder.ivCenter, options);
                            viewHolder.ivCenter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // subTypeHairs
                                    Intent listServices = new Intent(mContext.getApplicationContext(), ListService.class);
                                    listServices.putExtra("subTypeHairs", subTypeHairs);
                                    listServices.putExtra("check_array", 3);
                                    mContext.startActivity(listServices);
                                }
                            });
                        }
                        if (!TextUtils.isEmpty((listHairs.get(0).WALKER_BGIMAGE))) {
                            AndyUtils.LoadImageView(listHairs.get(0).WALKER_BGIMAGE, viewHolder.ivBase, options);
                        }
                    }
                }
            } else if (listNails.size() > 0) {
                if (listHairs.size() > 0) {
                    if (!TextUtils.isEmpty(listHairs.get(0).WALKER_FGIMAGE)) {
                        AndyUtils.LoadImageView(listHairs.get(0).WALKER_FGIMAGE, viewHolder.ivCenter, options);
                        viewHolder.ivCenter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // subTypeHairs
                                Intent listServices = new Intent(mContext.getApplicationContext(), ListService.class);
                                listServices.putExtra("subTypeHairs", subTypeHairs);
                                listServices.putExtra("check_array", 3);
                                mContext.startActivity(listServices);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty((listHairs.get(0).WALKER_BGIMAGE))) {
                        AndyUtils.LoadImageView(listHairs.get(0).WALKER_BGIMAGE, viewHolder.ivBase, options);
                    }
                }

            } else if (listHairs.size() > 0) {
                if (!TextUtils.isEmpty(listHairs.get(0).WALKER_FGIMAGE)) {
                    AndyUtils.LoadImageView(listHairs.get(0).WALKER_FGIMAGE, viewHolder.ivCenter, options);
                    viewHolder.ivCenter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // subTypeHairs
                            Intent listServices = new Intent(mContext.getApplicationContext(), ListService.class);
                            listServices.putExtra("subTypeHairs", subTypeHairs);
                            listServices.putExtra("check_array", 3);
                            mContext.startActivity(listServices);
                        }
                    });
                }
                if (!TextUtils.isEmpty((listHairs.get(0).WALKER_BGIMAGE))) {
                    AndyUtils.LoadImageView(listHairs.get(0).WALKER_BGIMAGE, viewHolder.ivBase, options);
                }
            }

        }
        if (position == 2) {
            if (listMakeup.size() > 0) {
                if (listNails.size() > 0) {
                    if (listHairs.size() > 0) {
                        if (!TextUtils.isEmpty(listHairs.get(0).WALKER_FGIMAGE)) {
                            AndyUtils.LoadImageView(listHairs.get(0).WALKER_FGIMAGE, viewHolder.ivCenter, options);
                            viewHolder.ivCenter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // subTypeMakeUp
                                    Intent listServices = new Intent(mContext.getApplicationContext(), ListService.class);
                                    listServices.putExtra("subTypeHairs", subTypeHairs);
                                    listServices.putExtra("check_array", 3);
                                    mContext.startActivity(listServices);
                                }
                            });
                        }
                        if (!TextUtils.isEmpty((listHairs.get(0).WALKER_BGIMAGE))) {
                            AndyUtils.LoadImageView(listHairs.get(0).WALKER_BGIMAGE, viewHolder.ivBase, options);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return adapterLength;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView ivBase, ivCenter;
        protected FrameLayout flimage;

        public ViewHolder(View view) {
            super(view);
            DisplayMetrics displaymetrics = mContext.getResources().getDisplayMetrics();
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;
            this.ivBase = (ImageView) view.findViewById(R.id.ivbase);
            this.ivCenter = (ImageView) view.findViewById(R.id.ivcenter);
            this.flimage = (FrameLayout) view.findViewById(R.id.fl_image);
            this.ivBase.getLayoutParams().height = (int) (height / 3.3);
            this.ivBase.getLayoutParams().width = width;
            this.flimage.getLayoutParams().height = (int) (height / 3.3);
            this.flimage.getLayoutParams().width = width;
            this.ivCenter.getLayoutParams().height = ((this.ivBase.getLayoutParams().height) - 100);
        }
    }
}

