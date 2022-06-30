package binar.and3.kelompok1.secondhand.ui.menu.akun

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import binar.and3.kelompok1.secondhand.databinding.FragmentAkunBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class AkunFragment : Fragment() {

    private var _binding: FragmentAkunBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AkunViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAkunBinding.inflate(inflater, container, false)

        viewModel.getProfile()
        bindViewModel()

        return binding.root
    }

    private fun bindView() {
        val getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri ->
                uri.let {
                    val type = requireActivity().contentResolver.getType(it)

                    val tempFile = File.createTempFile("temp-", null, null)
                    val inputStream = requireActivity().contentResolver.openInputStream(uri)

                    tempFile.outputStream().use { inputStream?.copyTo(it) }

                    val requestBody: RequestBody = tempFile.asRequestBody(type?.toMediaType())
                    val body = MultipartBody.Part.createFormData("image", tempFile.name, requestBody)

                    // viewModel
                }
            }

    }

    private fun bindViewModel() {
        viewModel.shouldShowProfile.observe(viewLifecycleOwner) {
            Glide.with(requireContext())
                .load(it.imageUrl)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(12)))
                .into(binding.ivImageAccount)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}