package sdm.scl.ifsp.intents

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.Intent.ACTION_CHOOSER
import android.content.Intent.ACTION_DIAL
import android.content.Intent.ACTION_PICK
import android.content.Intent.ACTION_VIEW
import android.content.Intent.EXTRA_INTENT
import android.content.Intent.EXTRA_TITLE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import sdm.scl.ifsp.intents.Extras.PARAMETER_EXTRA
import sdm.scl.ifsp.intents.databinding.ActivityMainBinding
import java.security.Permission


class MainActivity : AppCompatActivity() {
    //desnecessario companion devido as mudanças
    /* companion object{
        private const val PARAMETER_REQUEST_CODE = 0
    }*/
    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    //CRIAÇAO DOS OBJETOS ABAIXO
    private lateinit var parameterArl: ActivityResultLauncher<Intent>
    private lateinit var callPhonePermissionArl: ActivityResultLauncher<String>
    private lateinit var pickImageArl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)
        setSupportActionBar(activityMainBinding.toolbarIn.toolbar)
        supportActionBar?.subtitle = localClassName

        parameterArl =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.getStringExtra(PARAMETER_EXTRA)?.also {
                        activityMainBinding.parameterTv.text = it
                    }
                }

            }
        callPhonePermissionArl =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
                if (permissionGranted) {
                    //CHAMAR  ou
                    callPhone(call = true) //call=true para o dielMi
                } else {
                    Toast.makeText(
                        this,
                        "Permission Required to Call PERMITA !!!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        pickImageArl =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resul ->
                with(resul) {
                    if (resultCode == RESULT_OK) {
                        data?.data?.also {
                            activityMainBinding.parameterTv.text = it.toString()
                            startActivity(Intent(ACTION_VIEW).apply { data = it })
                        }
                    }
                }
            }

        activityMainBinding.apply {
            parameterBt.setOnClickListener {
                val parameterIntent =
                    Intent(this@MainActivity, ParameterActivity::class.java).apply {
                        putExtra(PARAMETER_EXTRA, parameterTv.text)
                    }

                //startActivityForResult(parameterIntent, PARAMETER_REQUEST_CODE )
                parameterArl.launch(parameterIntent)
            }
        }
    }
    //codigo obsoleto
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PARAMETER_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.getStringExtra(PARAMETER_EXTRA)?.also {
                activityMainBinding.parameterTv.text = it
            }
        }
    }*/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.openActivityMi -> {
                val parameterIntent = Intent("OPEN_PARAMETER_ACTIVITY_ACTION").apply {
                    putExtra(PARAMETER_EXTRA, activityMainBinding.parameterTv.text)
                }
                parameterArl.launch(parameterIntent)
                true
            }

            R.id.viewMi -> {

                //abaixo codigo para o viewNi
                //val url = Uri.parse(activityMainBinding.parameterTv.text.toString())
                //val browserIntent = Intent(ACTION_VIEW, url)
                //startActivity(browserIntent)
                //***abaixo o codigo para a viewMi + chooseMi
                startActivity(browserIntent())

                true
            }

            R.id.callMi -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) {
                        //EXECUTA CHAMADA
                        callPhone(call = true) //calL=true para o dialMi
                    } else {
                        //SOLICITA PERMISSAO
                        callPhonePermissionArl.launch(CALL_PHONE)

                    }

                } else {
                    //CHAMAR ????
                    //callPhone() so pra a callMi ou callPhone(call = true ) para dialMi
                    callPhone(call = true)
                }
                true
            }

            R.id.dialMi -> {
                callPhone(call = false) // pq aqui é false
                true
            }

            R.id.pickMi -> {
                val imageDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                pickImageArl.launch(Intent(ACTION_PICK).apply {
                    setDataAndType(
                        Uri.parse(imageDir),
                        "image/*"
                    )
                })
                true
            }

            R.id.chooserMi -> {
                //startActivity(browserIntent())
                startActivity(
                    Intent(ACTION_CHOOSER).apply {
                        putExtra(EXTRA_TITLE, "CHOSE ......")
                        putExtra(EXTRA_INTENT, browserIntent())
                    }

                )
                true
            }

            else -> {
                false
            }
        }
    }

    //DA callMi abaixo
    private fun callPhone() {
        startActivity(
            Intent(ACTION_CALL).apply {
                "tel:${activityMainBinding.parameterTv.text}".also {
                    data = Uri.parse(it)
                }
            }
        )
    }

    //DA dialMi ou poderia só adaptar a funçao acima
    private fun callPhone(call: Boolean) {
        startActivity(
            Intent(if (call) ACTION_CALL else ACTION_DIAL).apply {
                "tel:${activityMainBinding.parameterTv.text}".also {
                    data = Uri.parse(it)
                }
            }
        )
    }

    //funçao para o chooseMi
    private fun browserIntent(): Intent {
        val url = Uri.parse(activityMainBinding.parameterTv.text.toString())
        return Intent(ACTION_VIEW, url)
    }

}