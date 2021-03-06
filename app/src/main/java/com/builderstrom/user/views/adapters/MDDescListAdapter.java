package com.builderstrom.user.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.builderstrom.user.R;
import com.builderstrom.user.data.retrofit.api.RestClient;
import com.builderstrom.user.data.retrofit.modals.MetaValues;
import com.builderstrom.user.utils.CommonMethods;
import com.builderstrom.user.utils.GlideApp;
import com.builderstrom.user.views.activities.BaseActivity;
import com.builderstrom.user.views.activities.ImageFullScreenActivity;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class MDDescListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ATTACHMENT = 1;
    private static final int DESCRIPTION = 0;
    private static final int SIGNATURE = 2;


    private Context mContext;
    private List<List<MetaValues>> valuesList;
    private LayoutInflater mLayoutInflater;

    MDDescListAdapter(Context mContext, List<List<MetaValues>> valuesList) {
        this.mContext = mContext;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.valuesList = valuesList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemView) {
        switch (itemView) {
            case ATTACHMENT:
                return new AttachmentHolder(mLayoutInflater.inflate(R.layout.row_description_attachment, viewGroup, false));
            case SIGNATURE:
                return new SignatureHolder(mLayoutInflater.inflate(R.layout.row_description_attachment, viewGroup, false));
            default:
                return new DescriptionHolder(mLayoutInflater.inflate(R.layout.row_description, viewGroup, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        List<MetaValues> metaList = valuesList.get(position);
        switch (getItemViewType(position)) {
            case ATTACHMENT:
                ((AttachmentHolder) viewHolder).tvDescription.setText(CommonMethods.spannedText(metaList.get(0).getField_label(), 0, metaList.get(0).getField_label().length()));
                GlideApp.with(mContext).load(CommonMethods.decodeUrl(metaList.get(0).getValue())).apply(new RequestOptions().placeholder(R.drawable.ic_file)).into(((AttachmentHolder) viewHolder).ivDescription);
                break;
            case SIGNATURE:
                ((SignatureHolder) viewHolder).tvDescription.setText(CommonMethods.spannedText(metaList.get(0).getField_label(), 0, metaList.get(0).getField_label().length()));
                GlideApp.with(mContext).load(RestClient.getBaseImageUrl() + CommonMethods.decodeUrl(metaList.get(0).getValue())).apply(new RequestOptions().placeholder(R.drawable.ic_file)).into(((SignatureHolder) viewHolder).ivDescription);
                ((SignatureHolder) viewHolder).itemView.setOnClickListener(v -> openFullImage(RestClient.getBaseImageUrl() + CommonMethods.decodeUrl(metaList.get(0).getValue())));
                break;
            case DESCRIPTION:
                ((DescriptionHolder) viewHolder).tvDescription.setText(CommonMethods.spannedText(TextUtils.concat(metaList.get(0).getField_label(), " - ", getValueString(metaList)).toString(), 0, metaList.get(0).getField_label().length()));
                break;
        }
    }

    private void openFullImage(String imageUrl) {
        if (CommonMethods.isNetworkAvailable(mContext)) {
            if (!imageUrl.isEmpty()) {
                Intent intent = new Intent(mContext, ImageFullScreenActivity.class);
                intent.putExtra("imageUrl", imageUrl);
                mContext.startActivity(intent);
            }

//            if (!imageLink.isEmpty()) {
//                Intent intent = new Intent(mContext, ImageFullScreenActivity.class);
//                intent.putExtra("imageUrl", imageLink);
//                if (CommonMethods.hasLollipop()) {
//                    ActivityOptionsCompat options = ActivityOptionsCompat.
//                            makeSceneTransitionAnimation((Activity) mContext, imageView, "profile");
//                    mContext.startActivity(intent, options.toBundle());
//                } else {
//                    mContext.startActivity(intent);
//                }
//            }
        } else {
            ((BaseActivity) mContext).errorMessage(mContext.getResources().getString(R.string.no_internet), null, false);
        }
    }

    @Override
    public int getItemCount() {
        return valuesList.size();
    }

    private String getValueString(List<MetaValues> metaList) {
        StringBuilder value = new StringBuilder();
        switch (metaList.get(0).getField_type()) {
            case "textarea":
            case "text":
            case "date":
            case "select":
            case "radio":
                value = new StringBuilder(metaList.get(0).getValue());
                break;
            case "checkbox":
                for (MetaValues metaValues : metaList) {
                    value.append(metaValues.getValue()).append(" ");
                }
                break;
            case "checkbox+input":
                for (MetaValues metaValues : metaList) {
                    value.append(metaValues.getValue()).append("  ").append(metaValues.getCheck_input()).append(" ");
                }
                break;
            case "select+input":
                value.append(metaList.get(0).getValue()).append(" ").append(metaList.get(0).getCheck_input()).append(" ");
                break;
        }

        return value.toString();
    }


    @Override
    public int getItemViewType(int position) {
        switch (valuesList.get(position).get(0).getField_type()) {
            case "file":
                return ATTACHMENT;
            case "signature":
                return SIGNATURE;

            default:
                return DESCRIPTION;
        }
    }

    class SignatureHolder extends RecyclerView.ViewHolder {
        private TextView tvDescription;
        private ImageView ivDescription;

        public SignatureHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivDescription = itemView.findViewById(R.id.ivDescription);
        }
    }

    class AttachmentHolder extends RecyclerView.ViewHolder {
        private TextView tvDescription;
        private ImageView ivDescription;

        public AttachmentHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivDescription = itemView.findViewById(R.id.ivDescription);
        }
    }

    class DescriptionHolder extends RecyclerView.ViewHolder {
        private TextView tvDescription;

        public DescriptionHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }


}
