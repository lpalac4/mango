package com.moraware.mango.social

import com.moraware.domain.models.UserThumbnail
import com.moraware.mango.R
import com.moraware.mango.databinding.BaseRecyclerviewDataBindingAdapter

class SocialProfilesAdapter(viewModel: SocialViewModel) : BaseRecyclerviewDataBindingAdapter(viewModel) {

    private var profilesList : MutableList<UserThumbnail> = viewModel.profiles.value ?: mutableListOf()

    fun updateModels(profiles: MutableList<UserThumbnail>?) {
        profilesList = profiles ?: mutableListOf()
        notifyDataSetChanged()
    }

    override fun getObjForPosition(position: Int): Any {
        return profilesList[position]
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.adapter_social_profile_item
    }

    override fun getItemCount(): Int {
        return profilesList.size
    }
}