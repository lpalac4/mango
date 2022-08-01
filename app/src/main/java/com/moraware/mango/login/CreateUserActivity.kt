package com.moraware.mango.login

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.moraware.mango.R
import com.moraware.mango.base.ViewModelActivity
import com.moraware.mango.databinding.ActivityCreateUserBinding
import com.moraware.mango.databinding.ImageViewBindingAdapter
import com.moraware.mango.main.MainActivity
import com.moraware.mango.util.Utils
import com.moraware.mango.webview.WebViewFragment
import kotlinx.android.synthetic.main.activity_create_user.*
import java.io.ByteArrayOutputStream

/**
 * Created by Luis Palacios on 8/8/18.
 */
class CreateUserActivity : ViewModelActivity<CreateUserViewModel>() {
    lateinit var binding: ActivityCreateUserBinding

    override fun setupUIAndBindViewModel(savedInstanceState: Bundle?) : ViewDataBinding {
        mViewModel = ViewModelProviders.of(this).get(CreateUserViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_user)

        var data = savedInstanceState?.getSerializable(LoginActivity.THIRD_PARTY_USER_DATA)
        if(data == null) data = intent.getSerializableExtra(LoginActivity.THIRD_PARTY_USER_DATA)

        if (data != null) {
            (data as LoginViewModel.ThirdPartyData).let {
                mViewModel.udpateWithThirdPartyData(it)
            }
        }

        mViewModel.accountCreatedEvent.observe(this, Observer { email ->
            mSharedPrefs.saveCredentials(email)
            val displayToS = !mSharedPrefs.termsOfServiceAccepted

            if(displayToS) navigateToTermsOfService() else startActivity(MainActivity.newIntent(this))
        })
        mViewModel.onSelectImageEvent.observe(this, Observer {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        })

        binding.model = mViewModel
        binding.lifecycleOwner = this
        return binding
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {

                data?.data?.let { androidUri ->
                    val javaUri = Utils.uriToURI(androidUri)
                    if (javaUri == null) {
                        mViewModel.setErrorMessage(R.string.error_pick_image)
                        return
                    }

                    mViewModel.imageSelected(javaUri)
                        image.invalidate()
                        ImageViewBindingAdapter.loadCircularImage(image, androidUri)
                } ?: mViewModel.setErrorMessage(R.string.error_pick_image)
        }
    }

    private fun uploadImageToFirebase() {
        // Get the data from an ImageView as bytes
        image.isDrawingCacheEnabled = true
        image.buildDrawingCache()
        val bitmap = (image.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
    }

    private fun navigateToTermsOfService() {
        var fragment = supportFragmentManager.findFragmentByTag(WebViewFragment.TAG)
        if(fragment == null) fragment = WebViewFragment.newInstanceForAsset(getString(R.string.terms_of_service), true)
        else (fragment as WebViewFragment).onLoadNewBundle(true, getString(R.string.terms_of_service), true)

        fragment.addConfirmCallback {
            mSharedPrefs.termsOfServiceAccepted = true
            startActivity(MainActivity.newIntent(this, true))
        }

        val fTrans = supportFragmentManager.beginTransaction()

        create_btn.visibility = View.GONE
        tos_container.visibility = View.VISIBLE
        fTrans.replace(binding.tosContainer.id, fragment, WebViewFragment.TAG)
        fTrans.commit()
    }

    companion object {
        const val PICK_IMAGE = 10002
    }
}