package binar.and3.kelompok1.secondhand.ui.jualform

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import binar.and3.kelompok1.secondhand.databinding.ActivityJualFormBinding
import binar.and3.kelompok1.secondhand.ui.menu.daftarjual.DaftarJualFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class JualFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJualFormBinding
    private val viewModel: JualFormViewModel by viewModels()
    private val progressDialog: ProgressDialog by lazy { ProgressDialog(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJualFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        window.statusBarColor = Color.WHITE

        bindView()
        bindViewModel()
    }

    private fun bindView() {
        val getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri ->
                uri.let {
                    val type = this.contentResolver.getType(it)
                    val tempFile = File.createTempFile("temp-", null, null)
                    val inputStream = this.contentResolver.openInputStream(uri)

                    tempFile.outputStream().use { inputStream?.copyTo(it) }

                    val requestBody: RequestBody = tempFile.asRequestBody(type?.toMediaType())
                    val body = MultipartBody.Part.createFormData("image", tempFile.name, requestBody)

                    // viewModel.onChangeImage(body)
                }
            }
        binding.etNamaProduk.doAfterTextChanged {
            viewModel.onChangeName(it.toString())
        }
        binding.etHargaProduk.doAfterTextChanged {
            viewModel.onChangeBasePrice(it.hashCode())
        }
        binding.etDeskripsi.doAfterTextChanged {
            viewModel.onChangeDescription(it.toString())
        }
        binding.etKategori.doAfterTextChanged {
            it?.let { it1 -> viewModel.onChangeCategoryIds(it1.toList()) }
        }
        binding.ivFotoProduk.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.btnTerbitkan.setOnClickListener {
            // viewModel.onValidate()
        }
    }

    private fun bindViewModel() {
        viewModel.shouldShowLoading.observe(this) {
            if (it) {
                progressDialog.setMessage("Loading...")
                progressDialog.show()
            } else {
                progressDialog.hide()
            }
        }
        viewModel.shouldShowError.observe(this) {
            val snackbar = Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
            snackbar.view.setBackgroundColor(Color.RED)
            snackbar.show()
        }
        viewModel.shouldOpenDaftarJual.observe(this) {
            if (it) {
                val intent = Intent(applicationContext, DaftarJualFragment::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

}